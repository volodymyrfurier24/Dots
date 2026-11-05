package sk.tuke.gamestudio.game.core;

public class Dot {
    private int posX;
    private int posY;
    private boolean isSelected;
    private Color color;

    public Dot(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.isSelected = false;
        this.color = Color.values()[(int) (Math.random() * Color.values().length)];
    }

    public Dot(int posX, int posY, Color color) {
        this.posX = posX;
        this.posY = posY;
        this.isSelected = false;
        this.color = color;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public Color getColor() {
        return color;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    void select() {
        isSelected = true;
    }

    void deselect() {
        isSelected = false;
    }

    @Override
    public String toString() {
        if (isSelected)
            return switch (color) {
                case RED -> "R";
                case GREEN -> "G";
                case BLUE -> "B";
                case YELLOW -> "Y";
                case PURPLE -> "P";
                default -> "?";
            };
        else
            return switch (color) {
                case RED -> "r";
                case GREEN -> "g";
                case BLUE -> "b";
                case YELLOW -> "y";
                case PURPLE -> "p";
                default -> "?";
            };
    }
}
