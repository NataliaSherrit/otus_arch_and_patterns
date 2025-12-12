package ru.otus.auth.service;

import ru.otus.auth.dto.TokenDto;

import java.util.List;

public interface AuthService {

    String registerGame(List<String> users);

    String token(String gameId);

    TokenDto verify(String token, String gameId);
}
