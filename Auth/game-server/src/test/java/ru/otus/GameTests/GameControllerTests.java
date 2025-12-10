package ru.otus.GameTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.game.GameApplication;
import ru.otus.game.dto.Message;
import ru.otus.game.dto.TokenDto;
import ru.otus.game.service.AuthService;
import ru.otus.game.service.GameService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GameApplication.class)
@AutoConfigureMockMvc
public class GameControllerTests {

    @MockBean
    private AuthService authService;

    @MockBean
    private GameService gameService;

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldThrowIfNotCorrectToken() throws Exception {
        mvc.perform(post("/game/receive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new Message("", "", "", new Object[]{})))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());

        mvc.perform(post("/game/receive")
                        .header(HttpHeaders.AUTHORIZATION, "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new Message("", "", "", new Object[]{})))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldNotAcceptCommandIfNotValidToken() throws Exception {
        String token = UUID.randomUUID().toString();
        String gameId = UUID.randomUUID().toString();
        when(authService.verify(argThat(m -> m.getGameId().equals(gameId)), eq(token)))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        mvc.perform(post("/game/receive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(mapper.writeValueAsString(new Message(gameId, "", "", new Object[]{})))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(gameService, times(0)).receive(any(), any());
    }

    @Test
    public void shouldAcceptCommandIfValidToken() throws Exception {
        String token = UUID.randomUUID().toString();
        String gameId = UUID.randomUUID().toString();
        TokenDto metadata = new TokenDto("user1", gameId);
        when(authService.verify(argThat(m -> m.getGameId().equals(gameId)), eq(token))).thenReturn(metadata);
        when(gameService.receive(any(), any())).thenReturn(true);

        mvc.perform(post("/game/receive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(mapper.writeValueAsString(new Message(gameId, "", "", new Object[]{})))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(gameService, times(1)).receive(
                argThat(m -> m.getGameId().equals(gameId)),
                argThat(t -> t.getGameId().equals(metadata.getGameId()) && t.getUserName().equals(metadata.getUserName()))
        );
    }

}
