package ru.otus.ioc;

import ru.otus.adapter.AdapterGenerator;
import ru.otus.adapter.AdapterGeneratorImplementation;
import ru.otus.command.Command;
import ru.otus.domain.UObject;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class StrategyBasedOnScopes implements Strategy {
    private final ThreadLocal<String> currentScope;
    private final ThreadLocal<Map<String, Scope>> scopes;
    public static final String DEFAULT_SCOPE_NAME = "ROOT";
    private Scope defaultScope = null;

    public StrategyBasedOnScopes() {
        this.currentScope = ThreadLocal.withInitial(() -> DEFAULT_SCOPE_NAME);
        this.scopes = ThreadLocal.withInitial(HashMap::new);
    }

    @Override
    public Object resolve(String key, Object[] args) {
        if ("Scopes.Default".equals(key)) {
            return defaultScope;
        } else {
            try {
                Scope scope = getCurrentOrDefaultScope();
                return scope.resolveDependency(key, args);
            } catch (NullPointerException e) {
                throw new IllegalStateException("Scope is not exist");
            }
        }
    }

    private String getCurrentOrDefaultScopeName() {
        String scope;
        scope = currentScope.get();
        if (scope == null) scope = DEFAULT_SCOPE_NAME;
        return scope;
    }

    private Scope getCurrentOrDefaultScope() {
        return getScope(getCurrentOrDefaultScopeName());
    }

    private Scope getScope(String scope) {
        if (DEFAULT_SCOPE_NAME.equals(scope)) {
            return defaultScope;
        }
        return scopes.get().get(scope);
    }

    public class InitScopeBasedIoCCommand implements Command {


        @Override
        public synchronized void execute() {
            if (defaultScope != null) {
                return;
            }

            Map<String, Function<Object[], Object>> dependencies = new ConcurrentHashMap<>();

            Scope scope = new ChildScope(
                    dependencies,
                    new DefaultScope(IoC.resolve("IoC.Default")));

            dependencies.put("Scopes.Storage", args -> new ConcurrentHashMap<String, Function<Object[], Object>>());

            dependencies.put("Scopes.New", args -> (Command) () -> {

                String newScopeName = (String) args[1];
                if (newScopeName == null) {
                    throw new IllegalArgumentException("Not valid scope name");
                }
                if (scopes.get().containsKey(newScopeName)) {
                    throw new IllegalArgumentException(String.format("Scope %s already exists", newScopeName));
                }

                String parentScopeName = (String) args[0];
                Scope parentScope;
                if (parentScopeName == null || (parentScope = getScope(parentScopeName)) == null) {
                    throw new IllegalArgumentException(String.format("Parent scope %s not found", parentScopeName));
                }
                Map<String, Function<Object[], Object>> storage = IoC.resolve("Scopes.Storage");

                ChildScope childScope = new ChildScope(storage, parentScope);
                scopes.get().put((String) args[1], childScope);
            });

            dependencies.put("Scopes.Current", args -> getCurrentOrDefaultScopeName());

            dependencies.put("Scopes.Current.Set", args -> (Command) () -> {
                String scopeName = (String) args[0];
                if (getScope(scopeName) == null) {
                    throw new IllegalArgumentException(String.format("Scope %s not found", scopeName));
                }
                StrategyBasedOnScopes.this.currentScope.set(scopeName);
            });

            dependencies.put("IoC.Register", args -> {
                return (Command) () -> {
                    Scope currentScope = getCurrentOrDefaultScope();
                    boolean success;
                    if (currentScope == null) {
                        success = false;
                    } else {
                        success = currentScope.addDependency((String) args[0], (Function<Object[], Object>) args[1]);
                    }
                    if (!success) {
                        throw new IllegalArgumentException("Can not register dependency");
                    }
                };
            });

            dependencies.put("Adapter.Generator.Create", args -> new AdapterGeneratorImplementation());

            dependencies.put("Adapter", args -> {
                AdapterGenerator adapterGenerator;
                try {
                    adapterGenerator = IoC.resolve("Adapter.Generator");
                } catch (Throwable ex) {
                    adapterGenerator = null;
                }
                if (adapterGenerator == null) {
                    synchronized (AdapterGenerator.class) {
                        try {
                            adapterGenerator = IoC.resolve("Adapter.Generator");
                        } catch (Throwable ex) {
                            AdapterGenerator adapterGeneratorNew = IoC.resolve("Adapter.Generator.Create");
                            ((Command) IoC.resolve("IoC.Register", "Adapter.Generator", (Function<Object[], Object>) args1 -> adapterGeneratorNew)).execute();
                            adapterGenerator = adapterGeneratorNew;
                        }
                    }
                }
                return adapterGenerator.resolve((Class<?>) args[0], (UObject) args[1]);
            });

            defaultScope = scope;

            ((Command) IoC.resolve("IoC.SetupStrategy", StrategyBasedOnScopes.this)).execute();

            StrategyBasedOnScopes.this.currentScope.set(DEFAULT_SCOPE_NAME);
        }
    }
}
