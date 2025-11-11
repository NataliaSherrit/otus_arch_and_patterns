package ru.otus.test;

import ru.otus.adapter.AdapterGenerator;
import ru.otus.adapter.AdapterGeneratorImplementation;

public  class AdapterProvider {
    public AdapterGenerator generate() {
        return new AdapterGeneratorImplementation();
    }
}
