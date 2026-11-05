package sk.tuke.gamestudio.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.tuke.gamestudio.game.core.Color;
import sk.tuke.gamestudio.game.core.Dot;
import sk.tuke.gamestudio.game.core.Field;
import sk.tuke.gamestudio.game.core.GameState;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {

    private Field field;

    @BeforeEach
    void setup() {
        field = new Field(5, 5);
        field.generate();
    }

    @Test
    void testFieldInitialization() {
        assertEquals(5, field.getWidth());
        assertEquals(5, field.getHeight());
        assertEquals(GameState.PLAYING, field.getState());
        assertEquals(30, field.getMoves());
        assertEquals(0, field.getScore());
    }

    @Test
    void testGenerateFillsField() {
        for (int x = 0; x < field.getWidth(); x++) {
            for (int y = 0; y < field.getHeight(); y++) {
                assertNotNull(field.getDot(x, y));
            }
        }
    }

    @Test
    void testGenerateTargets() {
        field.generateTargets();

        Map<Color, Integer> targets = field.getTargets();
        Map<Color, Integer> collected = field.getCollected();

        assertFalse(targets.isEmpty());
        for (Color c : targets.keySet()) {
            assertEquals(0, collected.get(c));
        }
    }

    @Test
    void testRemoveDotsIncreasesScore() {
        Dot d1 = new Dot(0, 0);
        Dot d2 = new Dot(1, 0);

        d2.setColor(d1.getColor());

        Dot[][] custom = new Dot[5][5];
        custom[0][0] = d1;
        custom[1][0] = d2;

        field.setDots(custom);

        field.selectDot(d1);
        field.selectDot(d2);
        field.removeDots();

        assertTrue(field.getScore() >= 20);
    }

    @Test
    void testMovesDecreaseAfterRemove() {
        int initialMoves = field.getMoves();

        Dot d1 = field.getDot(0, 0);
        Dot d2 = field.getDot(1, 0);
        d2.setColor(d1.getColor());

        field.selectDot(d1);
        field.selectDot(d2);
        field.removeDots();

        assertEquals(initialMoves - 1, field.getMoves());
    }

    @Test
    void testRefillFillsEmptySpaces() {
        Dot[][] custom = new Dot[5][5];
        field.setDots(custom);

        field.refill();

        for (int x = 0; x < field.getWidth(); x++) {
            for (int y = 0; y < field.getHeight(); y++) {
                assertNotNull(field.getDot(x, y));
            }
        }
    }

    @Test
    void testHasValidMovesTrue() {
        Dot d1 = new Dot(0, 0);
        Dot d2 = new Dot(1, 0);

        d2.setColor(d1.getColor());

        Dot[][] custom = new Dot[5][5];
        custom[0][0] = d1;
        custom[1][0] = d2;

        field.setDots(custom);

        assertTrue(field.hasValidMoves());
    }

    @Test
    void testHasValidMovesFalse() {
        field = new Field(2, 2);
        Dot[][] custom = new Dot[2][2];

        Dot d1 = new Dot(0, 0);
        Dot d2 = new Dot(1, 0);
        Dot d3 = new Dot(0, 1);
        Dot d4 = new Dot(1, 1);

        d1.setColor(Color.values()[0]);
        d2.setColor(Color.values()[1]);
        d3.setColor(Color.values()[2]);
        d4.setColor(Color.values()[3]);

        custom[0][0] = d1;
        custom[1][0] = d2;
        custom[0][1] = d3;
        custom[1][1] = d4;

        field.setDots(custom);

        assertFalse(field.hasValidMoves());
    }

    @Test
    void testGameLostWhenMovesZero() {
        field.getTargets().put(Color.RED, 5);
        field.getCollected().put(Color.RED, 0);

        field.setMoves(1);

        Dot d1 = field.getDot(0, 0);
        Dot d2 = field.getDot(1, 0);
        d1.setColor(Color.BLUE);
        d2.setColor(Color.BLUE);

        field.selectDot(d1);
        field.selectDot(d2);
        field.removeDots();

        assertEquals(GameState.LOST, field.getState());
    }

    @Test
    void testGameWonWhenTargetsMet() {
        field.generateTargets();

        Color targetColor = field.getTargets().keySet().iterator().next();
        int targetAmount = field.getTargets().get(targetColor);

        field.getCollected().put(targetColor, targetAmount);

        field.setMoves(1);

        Dot d1 = field.getDot(0, 0);
        Dot d2 = field.getDot(1, 0);
        d2.setColor(d1.getColor());

        field.selectDot(d1);
        field.selectDot(d2);
        field.removeDots();

        assertEquals(GameState.WON, field.getState());
    }
}