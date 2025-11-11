package ru.otus.ioc;

import java.util.function.Function;

public class DefaultScope implements Scope{
    private final Strategy strategy;

    public DefaultScope(Strategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public Object resolveDependency(String key, Object... args) {
        return strategy.resolve(key, args);
    }

    @Override
    public boolean addDependency(String key, Function<Object[], Object> strategy) {
        return false;
    }
}
