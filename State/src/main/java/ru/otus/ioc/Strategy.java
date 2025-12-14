package ru.otus.ioc;

public interface Strategy {
    Object resolve(String key, Object... args);

}
