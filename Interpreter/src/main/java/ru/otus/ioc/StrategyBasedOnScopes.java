package ru.otus.ioc;

import ru.otus.command.*;
import ru.otus.domain.Game;
import ru.otus.domain.UObject;
import ru.otus.interpreter.CommandInterpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class StrategyBasedOnScopes implements Strategy {
    static final String DEFAULT_SCOPE_NAME = "ROOT";

    private final ThreadLocal<String> currentScope;
    private final ThreadLocal<Map<String, Scope>> scopes;
    private Scope rootScope = null;

    public StrategyBasedOnScopes() {
        this.currentScope = ThreadLocal.withInitial(() -> DEFAULT_SCOPE_NAME);
        this.scopes = ThreadLocal.withInitial(HashMap::new);
    }

    @Override
    public Object resolve(String key, Object... args) {
        if ("Scopes.Root".equals(key)) {
            return rootScope; // корневой скоуп доступен из любого потока
        } else {
            Scope scope = getCurrentOrRootScope();
            if (scope == null) {
                throw new IllegalStateException("Scope not found");
            }
            return scope.resolveDependency(key, args);
        }
    }
    private String getCurrentOrRootScopeName() {
        String scope = currentScope.get();
        if (scope == null) {
            scope = DEFAULT_SCOPE_NAME;
        }
        return scope;
    }

    private Scope getCurrentOrRootScope() {
        return getScope(getCurrentOrRootScopeName());
    }

    private Scope getScope(String scope) {
        if (DEFAULT_SCOPE_NAME.equals(scope)) {
            return rootScope;
        }
        return scopes.get().get(scope);
    }

    public class InitScopeBasedIoCCommand implements Command {

        @Override
        public synchronized void execute() {
            if (rootScope != null) {
                return;
            }

            Map<String, Function<Object[], Object>> dependencies = new ConcurrentHashMap<>();

            Scope scope = new ChildScope(
                    dependencies,
                    new DefaultScope(IoC.resolve("IoC.Default")) // крайний скоуп с дефолтной стратегией
            );

            dependencies.put("Scopes.Storage", args -> {
                return new ConcurrentHashMap<String, Function<Object[], Object>>();
            });

            dependencies.put("Scopes.New", args -> {
                return (Command) () -> {
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
                };
            });

            dependencies.put("Scopes.Current", args -> {
                return getCurrentOrRootScopeName();
            });

            dependencies.put("Scopes.Current.Set", args -> {
                return (Command) () -> {
                    String scopeName = (String) args[0];
                    if (getScope(scopeName) == null) {
                        throw new IllegalArgumentException(String.format("Scope %s not found", scopeName));
                    }
                    StrategyBasedOnScopes.this.currentScope.set(scopeName);
                };
            });

            dependencies.put("IoC.Register", args -> {
                return (Command) () -> {
                    Scope currentScope = getCurrentOrRootScope();
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
            Map<String, Game> games = new ConcurrentHashMap<>();

            dependencies.put("Games.Create", args -> {
                String gameId = (String) args[0];
                return games.compute(gameId, (id, oldGame) -> {
                    if (oldGame != null) {
                        throw new IllegalArgumentException("Game already exists");
                    }
                    Map<String, Class<Command>> actionTypes = new ConcurrentHashMap<>();
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.Actions.Types.Add", id), (Function<Object[], Object>) args1 -> actionTypes.put((String) args1[0], (Class<Command>) args1[1]))).execute();
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.Actions.Types.Get", id), (Function<Object[], Object>) args1 -> actionTypes.get((String) args1[0]))).execute();
                    Map<String, Function<Object[], Command>> actions = new ConcurrentHashMap<>();
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.Actions.Commands.Add", id), (Function<Object[], Object>) args1 -> actions.put((String) args1[0], (Function<Object[], Command>) args1[1]))).execute();
                    ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.Actions.Commands.Get", id), (Function<Object[], Object>) args1 -> {
                        Function<Object[], Command> commandFunction = actions.get((String) args1[0]);
                        if (commandFunction == null) {
                            return null;
                        }
                        return commandFunction.apply((Object[]) args1[1]);
                    })).execute();
                    return new Game(gameId);
                });
            });


            dependencies.put("InterpretCommand", args -> new InterpretCommand((String) args[0], (UObject) args[1]));

            dependencies.put("Interpreter.Command.Execute", args -> new CommandInterpreter((String) args[0]));

            rootScope = scope;

            ((Command) IoC.resolve("IoC.SetupStrategy", StrategyBasedOnScopes.this)).execute();

            StrategyBasedOnScopes.this.currentScope.set(DEFAULT_SCOPE_NAME);
        }
    }
}
