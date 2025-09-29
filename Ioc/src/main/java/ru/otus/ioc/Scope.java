package ru.otus.ioc;

import java.util.function.Function;

public interface Scope {

    Object resolveDependency(String key, Object... args);

    boolean addDependency(String key, Function<Object[], Object> strategy);
}
