package ru.otus.adapter;

import ru.otus.domain.UObject;

public interface AdapterGenerator {
    <T> T resolve(Class<T> interfaceType, UObject uObject);

}
