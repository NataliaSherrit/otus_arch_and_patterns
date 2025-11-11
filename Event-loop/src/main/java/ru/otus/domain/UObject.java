package ru.otus.domain;

public interface UObject {
    Object getProperty(String key);

    void setProperty(String key, Object value);
}
