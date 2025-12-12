package ru.otus.AuthTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import ru.otus.auth.AuthApplication;
import ru.otus.auth.dto.TokenDto;
import ru.otus.auth.exception.NotAuthorizedException;
import ru.otus.auth.exception.NotFoundException;
import ru.otus.auth.jwt.JwtService;
import ru.otus.auth.service.AuthService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
@SpringBootTest(classes = AuthApplication.class)
public class AuthServiceImplTests {
    @Autowired
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @Test
    public void registerGameAndReturnIdTest() {
        String gameId = authService.registerGame(List.of("user1", "user2"));
        assertThat(gameId).isNotNull().isNotEmpty();
    }

    @Test
    public void failWhenTokenIsNotAuthenticatedTest() {
        assertThatThrownBy(() -> {
            authService.token(UUID.randomUUID().toString());
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @WithMockUser(value = "user1", password = "password1")
    @Test
    public void failWhenTokenCreatingIfGameNotFoundTest() {
        String gameId = authService.registerGame(List.of("user1", "user2"));

        assertThatThrownBy(() -> {
            authService.token(gameId + "!");
        }).isInstanceOf(NotFoundException.class);
    }

    @WithMockUser(value = "user1", password = "password1")
    @Test
    public void createTokenIfParametersValidTest() {
        String gameId = authService.registerGame(List.of("user1", "user2"));
        String token = UUID.randomUUID().toString();
        when(jwtService.token(eq("user1"), eq(gameId))).thenReturn(token);

        assertThat(authService.token(gameId)).isEqualTo(token);
    }

    @WithMockUser(value = "user1", password = "password1")
    @Test
    public void verifyTokenTest() {
        String gameId = authService.registerGame(List.of("user1", "user2"));
        String token = UUID.randomUUID().toString();
        when(jwtService.verify(eq(token), eq(gameId))).thenReturn(new TokenDto("user1", gameId));

        TokenDto tokenDto = authService.verify(token, gameId);
        assertThat(tokenDto.getUserName()).isEqualTo("user1");
        assertThat(tokenDto.getGameId()).isEqualTo(gameId);
    }
}
