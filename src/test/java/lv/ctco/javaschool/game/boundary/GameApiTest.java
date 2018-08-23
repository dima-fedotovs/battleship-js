package lv.ctco.javaschool.game.boundary;

import lv.ctco.javaschool.auth.control.UserStore;
import lv.ctco.javaschool.auth.entity.domain.User;
import lv.ctco.javaschool.game.control.GameStore;
import lv.ctco.javaschool.game.entity.Game;
import lv.ctco.javaschool.game.entity.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameApiTest {

    @Mock
    EntityManager em;
    @Mock
    UserStore userStore;
    @Mock
    GameStore gameStore;
    @InjectMocks
    GameApi gameApi;

    private User user1;
    private User user2;

    @BeforeEach
    void init() {
        user1 = new User();
        user1.setUsername("user1");
        user2 = new User();
        user2.setUsername("user2");
    }

    @Test
    @DisplayName("Check game status after first player")
    void startGameFirstPlayer() {
        when(userStore.getCurrentUser())
                .thenReturn(user1);
        when(gameStore.getIncompleteGame())
                .thenReturn(Optional.empty());
        doAnswer(invocation -> {
            Game game = invocation.getArgument(0);
            assertEquals(user1, game.getPlayer1());
            assertFalse(game.isPlayer1Active());
            assertFalse(game.isPlayer2Active());
            assertEquals(GameStatus.INCOMPLETE, game.getStatus());
            return null;
        }).when(em).persist(any(Game.class));

        gameApi.startGame();

        verify(em, times(1)).persist(any(Game.class));
    }

    @Test
    @DisplayName("Check game status after second player")
    void startGameSecondPlayer() {
        Game game = new Game();
        game.setStatus(GameStatus.INCOMPLETE);
        game.setPlayer1(user1);

        when(userStore.getCurrentUser())
                .thenReturn(user2);
        when(gameStore.getIncompleteGame())
                .thenReturn(Optional.of(game));

        gameApi.startGame();

        assertEquals(user1, game.getPlayer1());
        assertEquals(user2, game.getPlayer2());
        assertTrue(game.isPlayer1Active());
        assertTrue(game.isPlayer2Active());
        assertEquals(GameStatus.PLACEMENT, game.getStatus());
    }

}