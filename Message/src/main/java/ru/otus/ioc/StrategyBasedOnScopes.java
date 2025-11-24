package ru.otus.ioc;

import ru.otus.command.*;
import ru.otus.domain.UObject;

import java.util.HashMap;
import java.util.Map;
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
            Map<String, GameCommand> games = new ConcurrentHashMap<>();
            dependencies.put("Games.GetById", args -> games.get((String) args[0]));
            dependencies.put("Games.CreateQueue", args -> new CommandConcurrentQueue());

            dependencies.put("Games.Create", args -> {
                String gameId = (String) args[0];
                return games.compute(gameId, (id, oldGame) -> {
                    if (oldGame != null) {
                        throw new IllegalArgumentException("Game already exists");
                    }
                    CommandQueue commandQueue = IoC.resolve("Games.CreateQueue", gameId);
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.CommandQueue", id), (Function<Object[], Object>) args1 -> commandQueue)).execute();
                    Map<String, UObject> objects = new ConcurrentHashMap<>();
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.Objects.Add", id), (Function<Object[], Object>) args1 -> objects.put((String) args1[0], (UObject) args1[1]))).execute();
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.Objects.Get", id), (Function<Object[], Object>) args1 -> objects.get((String) args1[0]))).execute();
                    Map<String, Boolean> allowedOperations = new ConcurrentHashMap<>();
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.AllowedOperations.Add", id), (Function<Object[], Object>) args1 -> allowedOperations.put((String) args1[0], true))).execute();
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.AllowedOperations.Remove", id), (Function<Object[], Object>) args1 -> allowedOperations.remove((String) args1[0]))).execute();
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.AllowedOperations.Get", id), (Function<Object[], Object>) args1 -> allowedOperations.getOrDefault((String) args1[0], false))).execute();
                    return new GameCommand(gameId);
                });
            });

            dependencies.put("InterpretCommand", args -> new InterpreterCommand((String) args[0], (String) args[1], (String) args[2], (Object[]) args[3]));


            defaultScope = scope;

            ((Command) IoC.resolve("IoC.SetupStrategy", StrategyBasedOnScopes.this)).execute();

            StrategyBasedOnScopes.this.currentScope.set(DEFAULT_SCOPE_NAME);
        }
    }
}
