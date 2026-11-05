package sk.tuke.gamestudio.game.consoleui;

import org.springframework.beans.factory.annotation.Autowired;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.core.Color;
import sk.tuke.gamestudio.game.core.Dot;
import sk.tuke.gamestudio.game.core.Field;
import sk.tuke.gamestudio.game.core.GameState;
import sk.tuke.gamestudio.service.*;

import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleUI {
    private final Field field;
    private String name;

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;

    private final Scanner scanner = new Scanner(System.in);

    private static final Pattern INPUT_PATTERN = Pattern.compile("((S)([A-Z]+)([1-9]+|10)|D)");
    private static final Pattern MENU_PATTERN = Pattern.compile("([1-7])");
    //private final ScoreService scoreService = new ScoreServiceJDBC();
    //private final CommentService commentService = new CommentServiceJDBC();
    //private final RatingService ratingService = new RatingServiceJDBC();
    private final int consoleWidth = 172;

    public ConsoleUI(Field field) {
        this.field = field;
    }

    public void play() {
        printHeader();
        getName();
        menu();
        printField();
        do {
            processInput();
            printField();
        } while (field.getState() == GameState.PLAYING);
        if (field.getState() == GameState.WON) {
            printWon();
            saveScore();
        } else if (field.getState() == GameState.LOST) {
            printLost();
        }
    }

    private void printHeader(){
        System.out.println("==========================================");
        System.out.println("\t\t\t\tWELCOME TO");
        System.out.println("""
                \t██████╗  ██████╗ ████████╗███████╗
                \t██╔══██╗██╔═══██╗╚══██╔══╝██╔════╝
                \t██║  ██║██║   ██║   ██║   ███████╗
                \t██║  ██║██║   ██║   ██║   ╚════██║
                \t██████╔╝╚██████╔╝   ██║   ███████║
                \t╚═════╝  ╚═════╝    ╚═╝   ╚══════╝""");
        System.out.println("==========================================");
    }
    private void getName(){
        System.out.println("Enter your name:");
        name = scanner.nextLine();
    }

    private void printMenu() {
        System.out.println("==========================================");
        System.out.println("\t1. Start New Game");
        System.out.println("\t2. Game Rules");
        System.out.println("\t3. Controls");
        System.out.println("\t4. Scores");
        System.out.println("\t5. Write comment");
        System.out.println("\t6. Rate game");
        System.out.println("\t7. Exit");
        System.out.println("==========================================");
        System.out.print("Select option: ");

    }

    private void printField() {
        //System.out.println(field);
        printFieldHeader();
        printFieldBody();
    }

    private void printFieldHeader() {
        if (field.getMoves() == Integer.MAX_VALUE)
            System.out.println("Moves: ∞ Score: " + field.getScore());
        else {
            System.out.println("Moves: " + field.getMoves() + " Score: " + field.getScore());
            for (Color color : field.getTargets().keySet()) {
                System.out.println(color + ": " + field.getCollected().get(color) + "/" + field.getTargets().get(color));
            }
        }

    }

    private void printFieldBody() {
        for (int y = -2; y < field.getHeight(); y++) {
            if (y == -1)
                System.out.print("────");
            if (y != -1 && y != -2)
                System.out.print(Character.toString('A' + y) + " │\t");
            for (int x = 0; x < field.getWidth(); x++) {
                if (y == -2)
                    System.out.print("\t" + (x + 1));
                else if (y == -1)
                    System.out.print("────");
                else {
                    Dot dot = field.getDot(x, y);
                    String color = "";
                    switch (dot.getColor()) {
                        case RED -> color = "\u001B[31m";
                        case GREEN -> color = "\u001B[32m";
                        case YELLOW -> color = "\u001B[33m";
                        case BLUE -> color = "\u001B[34m";
                        case PURPLE -> color = "\u001B[35m";
                    }
                    System.out.print(color + field.getDot(x, y) + "\u001B[0m\t");
                }
            }
            System.out.println();
        }
    }

    private void printWon() {
        System.out.println("██╗   ██╗ ██████╗ ██╗   ██╗    ██╗    ██╗ ██████╗ ███╗   ██╗\n" +
                           "╚██╗ ██╔╝██╔═══██╗██║   ██║    ██║    ██║██╔═══██╗████╗  ██║\n" +
                           " ╚████╔╝ ██║   ██║██║   ██║    ██║ █╗ ██║██║   ██║██╔██╗ ██║\n" +
                           "  ╚██╔╝  ██║   ██║██║   ██║    ██║███╗██║██║   ██║██║╚██╗██║\n" +
                           "   ██║   ╚██████╔╝╚██████╔╝    ╚███╔███╔╝╚██████╔╝██║ ╚████║\n" +
                           "   ╚═╝    ╚═════╝  ╚═════╝      ╚══╝╚══╝  ╚═════╝ ╚═╝  ╚═══╝");

        System.out.println("\n SCORE: " + field.getScore());
        System.out.println(" MOVES LEFT: " + field.getMoves());
    }

    private void printLost() {
        System.out.println("██╗   ██╗ ██████╗ ██╗   ██╗    ██╗      ██████╗ ███████╗████████╗\n" +
                           "╚██╗ ██╔╝██╔═══██╗██║   ██║    ██║     ██╔═══██╗██╔════╝╚══██╔══╝\n" +
                           " ╚████╔╝ ██║   ██║██║   ██║    ██║     ██║   ██║███████╗   ██║   \n" +
                           "  ╚██╔╝  ██║   ██║██║   ██║    ██║     ██║   ██║╚════██║   ██║   \n" +
                           "   ██║   ╚██████╔╝╚██████╔╝    ███████╗╚██████╔╝███████║   ██║   \n" +
                           "   ╚═╝    ╚═════╝  ╚═════╝     ╚══════╝ ╚═════╝ ╚══════╝   ╚═╝");

    }

    private void menu() {

        String line;
        Matcher matcher;

        while (true) {
            printMenu();
            line = scanner.nextLine().trim().toUpperCase();
            matcher = MENU_PATTERN.matcher(line);
            if (matcher.matches()) {
                System.out.println(matcher.group());
                switch (Integer.parseInt(matcher.group())) {
                    case 1:
                        startGame();
                        return;
                    case 2:
                        showRules();
                        break;
                    case 3:
                        showControls();
                        break;
                    case 4:
                        printScores();
                        break;
                    case 5:
                        enterComment();
                        break;
                    case 6:
                        enterRating();
                        break;
                    case 7:
                        System.exit(0);
                        return;
                    default:
                        System.out.println("Invalid choice.\n");

                }
            }
        }
    }
    private void startGame(){
        field.generate();
        field.generateTargets();
    }

    public static void showRules() {
        System.out.println("\n=== GAME RULES ===");

        System.out.println("Dots is a puzzle game played on a grid of colored dots.\n");

        System.out.println("Objective:");
        System.out.println("Complete tasks by connecting and removing dots.\n");

        System.out.println("Basic Mechanics:");
        System.out.println("- Select at least 2 adjacent dots of the same type/color");
        System.out.println("- Connect them in a line (horizontal or vertical)");
        System.out.println("- All selected dots will be removed from the board\n");

        System.out.println("Connections:");
        System.out.println("- Dots must be next to each other (no diagonals)");
        System.out.println("- You can connect more than 2 dots in one move");
        System.out.println("- Longer chains give better results\n");

        System.out.println("Special Move (Loop):");
        System.out.println("- If you create a closed loop (square/rectangle),");
        System.out.println("- ALL dots of that type on the board are removed!\n");

        System.out.println("Board Update:");
        System.out.println("- After removing dots, new ones fall from above");
        System.out.println("- Empty spaces are filled automatically\n");

        System.out.println("Game Goal:");
        System.out.println("- Complete specific tasks (e.g. remove 20 blue dots)");
        System.out.println("- Finish within limited moves\n");

        System.out.println("Game Ends When:");
        System.out.println("- You run out of moves");
        System.out.println("- Or you complete all tasks\n");
    }
    public static void showControls() {
        System.out.println("\n=== CONTROLS ===");
        System.out.println("Enter coordinates to place a dot:");
        System.out.println("Format: s(select) row column");
        System.out.println("Example: sa5\n");

        System.out.println("Commands:");
        System.out.println("x - Exit game");

        System.out.println("Tip:");
        System.out.println("- Invalid or occupied cell = move rejected\n");
    }
    private void processInput() {
        System.out.print("Enter command: ");
        String line = scanner.nextLine().trim().toUpperCase();
        if ("X".equals(line))
            System.exit(0);
        Matcher matcher = INPUT_PATTERN.matcher(line);
        if (matcher.matches()) {
            if (matcher.group(0).equals("D")) {
                field.removeDots();
            } else if (matcher.group(2).equals("S")) {
                if(matcher.group(3).length() == 1 && (matcher.group(4).length() == 1 || Integer.parseInt(matcher.group(4)) == 10)){
                    int x = Integer.parseInt(matcher.group(4)) - 1;
                    int y = matcher.group(3).charAt(0) - 'A';
                    if (x < field.getWidth() && y < field.getHeight()) {
                        field.selectDot(field.getDot(x, y));
                    }
                }else if(matcher.group(3).length() > 1){
                    for (int i = 0; i < matcher.group(3).length(); i++){
                        int x = Integer.parseInt(Character.toString(matcher.group(4).charAt(0))) - 1;
                        int y = matcher.group(3).charAt(i) - 'A';
                        if (x < field.getWidth() && y < field.getHeight()) {
                            field.selectDot(field.getDot(x, y));
                        }
                    }
                }else if(matcher.group(4).length() > 1){
                    for (int i = 0; i < matcher.group(4).length(); i++){
                        int x = Integer.parseInt(Character.toString(matcher.group(4).charAt(i))) - 1;
                        int y = matcher.group(3).charAt(0) - 'A';
                        if (x < field.getWidth() && y < field.getHeight()) {
                            field.selectDot(field.getDot(x, y));
                        }
                    }
                }
            }

        } else {
            System.err.println("Bad input!");
        }
    }

    private void printScores() {
        List<Score> scores = scoreService.getTopScores("dots");

        for (int i = 0; i < scores.size(); i++) {
            Score score = scores.get(i);
            System.out.printf("\t%d. %s %d\n", i + 1, score.getPlayer(), score.getPoints());
        }
    }
    private void saveScore() {
        scoreService.addScore(
                new Score("dots", name, field.getScore(), new Date()));
    }
    private void enterComment(){
        System.out.println("Enter comment: ");
        String comment = scanner.nextLine();
        commentService.addComment(new Comment(name, "dots", comment, new Date()));
    }
    private void enterRating(){
        System.out.println("Enter rating: (0-5)");
        int rating = Integer.parseInt(scanner.nextLine());
        while (rating < 0 || rating > 5) {
            System.err.println("Invalid rating!");
            rating = Integer.parseInt(scanner.nextLine());
        }
        ratingService.setRating(new Rating(name, "dots", rating, new Date()));
    }


}
