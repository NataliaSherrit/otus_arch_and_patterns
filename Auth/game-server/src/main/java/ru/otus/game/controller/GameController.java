package ru.otus.game.controller;

import ru.otus.game.dto.Message;
import ru.otus.game.dto.TokenDto;
import ru.otus.game.service.AuthService;
import ru.otus.game.service.GameService;

@RestController
@RequestMapping("/")
public class GameController {

    private final GameService gameService;
    private final AuthService authService;

    public GameController(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }

    @PostMapping("/game/receive")
    public ResponseEntity<Boolean> receive(@RequestBody Message message, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        TokenDto metadata = authService.verify(message, token);
        return ResponseEntity.ok().body(gameService.receive(message, metadata));
    }

}
