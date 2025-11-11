package ru.otus.ioc;

public interface IoC {
    <T> T resolve(String key, Object... args);
}
