package ru.otus.ioc;

import java.util.Map;
import java.util.function.Function;

public class ChildScope implements Scope{
    private final Map<String, Function<Object[], Object>> dependencies;
    private final Scope parentScope;

    ChildScope(Map<String, Function<Object[], Object>> dependencies, Scope parentScope) {
        this.dependencies = dependencies;
        this.parentScope = parentScope;
    }

    @Override
    public Object resolveDependency(String key, Object... args) {
        Function<Object[], Object> func = dependencies.get(key);
        if (func != null) {
            return func.apply(args);
        }
        return parentScope.resolveDependency(key, args);
    }

    @Override
    public boolean addDependency(String key, Function<Object[], Object> strategy) {
        dependencies.put(key, strategy);
        return true;
    }
}
