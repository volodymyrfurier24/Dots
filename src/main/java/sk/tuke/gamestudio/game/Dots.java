package sk.tuke.gamestudio.game;

import sk.tuke.gamestudio.game.consoleui.ConsoleUI;
import sk.tuke.gamestudio.game.core.Field;

public class Dots {
    public static void main(String[] args) {
        ConsoleUI console = new ConsoleUI(new Field(10, 10));
        console.play();

    }
}
