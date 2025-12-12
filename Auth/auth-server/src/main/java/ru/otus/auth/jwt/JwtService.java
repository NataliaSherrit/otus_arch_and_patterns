package ru.otus.auth.jwt;

import ru.otus.auth.dto.TokenDto;

public interface JwtService {

    String token(String userName, String gameId);
    TokenDto verify(String token, String gameId);
}