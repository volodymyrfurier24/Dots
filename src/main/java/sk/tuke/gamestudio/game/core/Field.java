package sk.tuke.gamestudio.game.core;

import java.util.*;

public class Field {

    private final int width;
    private final int height;
    private Dot[][] dots;
    private List<Dot> selectedDots = new ArrayList<>();
    private GameState state;
    private int moves;
    private int score;
    private Map<Color, Integer> targets;
    private Map<Color, Integer> collected;

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        dots = new Dot[width][height];
        state = GameState.PLAYING;
        moves = 30;
        score = 0;
        targets = new HashMap<>();
        collected = new HashMap<>();
    }

    public Dot getDot(int x, int y) {
        return dots[x][y];
    }

    public GameState getState() {
        return state;
    }

    public int getMoves() {
        return moves;
    }

    public int getScore() {
        return score;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Map<Color, Integer> getTargets() {
        return targets;
    }

    public Map<Color, Integer> getCollected() {
        return collected;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public void setDots(Dot[][] dots) {
        this.dots = dots;
    }

    public void generateTargets() {
        targets.put(Color.values()[(int) (Math.random() * Color.values().length)], (int) (Math.random() * 11));
        for (Color c : targets.keySet()) {
            collected.put(c, 0);
        }
    }

    public void selectDot(Dot dot) {

        if (!(dot.getPosX() - 1 > -1 && getDot(dot.getPosX() - 1, dot.getPosY()).getColor() == dot.getColor() ||
              dot.getPosX() + 1 < width && getDot(dot.getPosX() + 1, dot.getPosY()).getColor() == dot.getColor() ||
              dot.getPosY() - 1 > -1 && getDot(dot.getPosX(), dot.getPosY() - 1).getColor() == dot.getColor() ||
              dot.getPosY() + 1 < height && getDot(dot.getPosX(), dot.getPosY() + 1).getColor() == dot.getColor())
        ) {
            return;
        }


        if (selectedDots.isEmpty()) {
            dot.select();
            selectedDots.add(dot);
            return;
        }

        Dot last = selectedDots.get(selectedDots.size() - 1);

        int dx = Math.abs(dot.getPosX() - last.getPosX());
        int dy = Math.abs(dot.getPosY() - last.getPosY());

        if (dx + dy == 1 && dot.getColor() == last.getColor()) {

            if (selectedDots.contains(dot)) {
                removeAllOfColor(dot.getColor());
                selectedDots.clear();
                return;
            }

            dot.select();
            selectedDots.add(dot);
        } else
            System.out.println("Selected last dot " + Character.toString('A' + last.getPosY()) + (last.getPosX() + 1));
    }

    public void removeDots() {
        if (selectedDots.size() >= 2) {
            for (Dot dot : selectedDots) {
                dots[dot.getPosX()][dot.getPosY()] = null;
                score += 10;
                Color color = dot.getColor();
                if (targets.containsKey(color)) {
                    collected.put(color, collected.get(color) + 1);
                }
            }
            selectedDots.clear();
            if (moves < Integer.MAX_VALUE)
                moves--;
            refill();
            if (isCompleted()) {
                state = GameState.WON;
            } else if (moves < 1) {
                state = GameState.LOST;
            }
        }
    }

    private void removeAllOfColor(Color color) {

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                if (dots[x][y] != null && dots[x][y].getColor() == color) {

                    if (targets.containsKey(color)) {
                        collected.put(color, collected.get(color) + 1);
                    }

                    dots[x][y] = null;
                    score += 10;
                }
            }
        }

        if (moves < Integer.MAX_VALUE)
            moves--;
        refill();

        if (isCompleted()) {
            score += 100;
            state = GameState.WON;
        } else if (moves < 1) {
            state = GameState.LOST;
        }
    }

    private boolean isCompleted() {
        if (moves == Integer.MAX_VALUE)
            return false;
        for (Color color : targets.keySet()) {
            if (collected.get(color) < targets.get(color)) {
                return false;
            }
        }
        return true;
    }

    public void refill() {
        for (int x = 0; x < width; x++) {
            int writeRow = height - 1;
            for (int y = height - 1; y >= 0; y--) {
                if (dots[x][y] != null) {
                    if (y != writeRow) {
                        dots[x][writeRow] = dots[x][y];
                        dots[x][y] = null;
                        dots[x][writeRow].setPosY(writeRow);
                    }
                    writeRow--;
                }
            }
            for (int y = writeRow; y >= 0; y--) {
                dots[x][y] = new Dot(x, y);
            }
        }
    }

    public void generate() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                dots[x][y] = new Dot(x, y);
            }
        }
    }

    public boolean hasValidMoves() {
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width - 1; x++) {
                if (getDot(x, y).getColor() == getDot(x + 1, y).getColor() || getDot(x, y).getColor() == getDot(x, y + 1).getColor()) {
                    return true;
                }
            }
        }
        return false;
    }

    /*@Override
    public String toString() {
        StringBuilder r = new StringBuilder();
        for (int y = -1; y < height; y++) {
            if(y != -1)
                r.append(Character.toString('A' + y)).append("\t");
            for (int x = 0; x < width; x++) {
                if(y == -1)
                    r.append("\t").append(x+1);
                else
                    r.append(dots[x][y]).append("\t");
            }
            r.append("\n");
        }
        return r.toString();
    }*/
}
