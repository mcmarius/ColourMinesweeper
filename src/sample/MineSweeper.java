package sample;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.Serializable;
import java.util.Random;

/**
 * @author marius
 * @version 1.4.3
 */
class MineSweeper implements Serializable {
    private int N, M;
    private MineField mines[][];
    private boolean revealed[][], ended;
    private int difficulty;

    /*MineField[][] getMines() {
        return mines;
    }*/

    /**
     * Creates an instance of the game. The minimum level must be 0 and the maximum must be 10.
     * If <code>level</code> is more than 10 or les than 0, it is set to 10 or 0 respectively.
     * @param root the root where the tiles should be added
     * @param n number of rows
     * @param m number of columns
     * @param level difficulty level
     */
    MineSweeper(GridPane root, int n, int m, int level) {
        N = n;
        M = m;
        level = Math.min(level, 10);
        level = Math.max(level, 0);
        difficulty = 12 - level;        // maximum level is 10, so there would be 50/50 chance of hitting a mine
        mines = new MineField[N + 1][M + 1];
        revealed = new boolean[N + 1][M + 1];
        for (int i = 0; i < N + 1; i++) {
            for (int j = 0; j < M + 1; j++) {
                mines[i][j] = new MineField(j, i, 40, 40);//, i + ", " + j);
            }
        }
        initialiseGame();
        for (int i = 0; i < N + 1; i++) {
            for (int j = 0; j < M + 1; j++) {
                int finalI = i, finalJ = j;
                mines[i][j].addEventFilter(MouseEvent.MOUSE_CLICKED, event -> clickHandler(event, finalI, finalJ));
                root.add(mines[i][j], j, i, 1, 1);
            }
        }
    }

    /**
     * Randomly generates the board game. This method is called before the game starts and also
     * every time a game reset is requested.
     */
    void initialiseGame() {
        ended = false;
        for (int i = 0; i < N + 1; i++) {
            for (int j = 0; j < M + 1; j++) {
                revealed[i][j] = false;
                mines[i][j].setNumber(-2);
                mines[i][j].reset();
                mines[i][j].setFill(Color.DARKGREY);
            }
        }
        Random random = new Random();
        int number, total;
        total = 0;
        for (int i = 0; i <= N; i++) {
            for (int j = 0; j <= M; j++) {
                number = random.nextInt(difficulty);     // higher means easier difficulty
                if(number == 0) {
                    ++total;
                    mines[i][j].setMine();
                }
            }
        }

        for (int i = 0; i <= N; i++) {
            for (int j = 0; j <= M; j++) {
                if(mines[i][j].getNumber()<-1) {
                    mines[i][j].setNumber(sumNeighbours(i, j));
                }
            }
        }
        System.out.printf("Starting a new game. Probability of generating a mine: %.3f%%." +
                        " Total mines in current game: %d (so the probability is in fact %.3f%%)\n", 100./ difficulty,
                total, total * 100./ ((N+1) * (M + 1)));
    }

    /**
     * Returns the number of surrounding mines doing bounds checks with respect to fields N and M.
     * @param i row number
     * @param j column number
     * @return number of mines
     */
    private int sumNeighbours(int i, int j) {
        return (i > 0 && j > 0 ? checkMine(mines[i - 1][j - 1]) : 0) +      // bounds checking
                (i > 0 ? checkMine(mines[i - 1][j]) + (j < M ? checkMine(mines[i - 1][j + 1]) : 0) : 0) +
                (j > 0 ? checkMine(mines[i][j - 1]) : 0) + (j < M ? checkMine(mines[i][j + 1]) : 0) +
                (i < N ? (j > 0 ? checkMine(mines[i + 1][j - 1]) : 0) + checkMine(mines[i + 1][j]) +
                        (j < M ? checkMine(mines[i + 1][j + 1]) : 0) : 0);
    }

    /**
     * Does the same as {@link MineSweeper#sumNeighbours(int, int)}, except that the mines are the marked ones,
     * not necessarily the real ones.
     * @see MineSweeper#sumNeighbours
     * @param i row number
     * @param j column number
     * @return the number of marked mines
     */
    private int visibleSumNeighbours(int i, int j) {
        return (i > 0 && j > 0 ? checkVisibleMine(mines[i - 1][j - 1]) : 0) +      // bounds checking
                (i > 0 ? checkVisibleMine(mines[i - 1][j]) + (j < M ? checkVisibleMine(mines[i - 1][j + 1]) : 0) : 0) +
                (j > 0 ? checkVisibleMine(mines[i][j - 1]) : 0) + (j < M ? checkVisibleMine(mines[i][j + 1]) : 0) +
                (i < N ? (j > 0 ? checkVisibleMine(mines[i + 1][j - 1]) : 0) + checkVisibleMine(mines[i + 1][j]) +
                        (j < M ? checkVisibleMine(mines[i + 1][j + 1]) : 0) : 0);
    }

    /**
     * Because Java doesn't have implicit conversion from boolean to int, this method does it.
     * @param field The field to be checked
     * @return 1 if there is a mine or 0 otherwise
     */
    private int checkMine(MineField field) {
        return field.isTrueMine() ? 1 : 0;
    }

    /**
     * @see MineSweeper#checkMine(MineField)
     * @param field The field to be checked
     * @return 1 if the field is marked or 0 otherwise
     */
    private int checkVisibleMine(MineField field) {
        return field.isMine() ? 1 : 0;
    }

    /**
     * Left-click reveals the (i, j) field. Double (or more) left-click reveals the neighbours of that field.
     * Right-clicking the (i, j) field marks it as potential mine, so left-clicking a neighbour won't trigger that one.
     * Works only before the game has been won/lost.
     * @param event the mouse event
     * @param i row number
     * @param j column number
     */
    private void clickHandler(MouseEvent event, int i, int j) {
        if(!ended) {
            switch (event.getButton()) {
                case PRIMARY:
                    if (mines[i][j].isMine()) {
                        break;
                    }
                    mines[i][j].reveal(false);
                    revealed[i][j] = true;
                    if (mines[i][j].isTrueMine()) {
                        playSong("Wrong-answer-sound-effect.mp3");
                        endGame("Bad luck! Game over, you lost!");//, 1000);
                    }

                    if (event.getClickCount() > 1 || mines[i][j].getNumber() == 0) {
                        if (mines[i][j].getNumber() == visibleSumNeighbours(i, j))
                            revealNonExplodingNeighbours(i, j);
                    }
                    if (wonGame()) {
                        endGame("Game over, you won!");//, 5500);
                    }
                    break;
                case SECONDARY:
                    if (!revealed[i][j])
                        mines[i][j].setMarked();
                    break;
                case MIDDLE:
                    initialiseGame();
                    break;
                default:
                    System.out.println("Unknown mouse button. What have you done?");
                    break;
            }
        }
        else if(event.getButton().equals(MouseButton.MIDDLE)) {
            initialiseGame();
        }
    }

    /**
     * Taken from Stack Overflow. Plays mp3 media using JavaFX utilities.
     * @param song the name of the file relative to the working directory (or absolute path)
     */
    private void playSong(String song) {
        try {
            Media hit = new Media(new File(song).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.play();
        } catch (MediaException e) {
            System.out.println("Song not found");
        }
    }

    /**
     * When the game ends, every piece is revealed. Ta-daa!
     * @param message The message displayed (win or loss) when the game ends.
     */
    private void endGame(String message) { //, int millis) {
        ended = true;
        System.out.println(message);
        showStats();
        System.out.println();
        for (int i = 0; i <= N; i++) {
            for (int j = 0; j <= M; j++) {
                mines[i][j].reveal(true);
            }
        }

        /*try {
            Thread.sleep(millis);
            Platform.exit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Marks non-mine {@code (i, j)}'s neighbours as revealed and colours them accordingly.
     * @param i row number
     * @param j column number
     */
    private void revealNonExplodingNeighbours(int i, int j) {
        if(i > 0) {
            if(j > 0 && !mines[i - 1][j - 1].isMine()) {
                processMine(i - 1, j - 1);
            }
            if(!mines[i - 1][j].isMine()) {
                processMine(i - 1, j);
            }
            if(j < M && !mines[i - 1][j + 1].isMine()) {
                processMine(i - 1, j + 1);
            }
        }
        if(j > 0) {
            if(!mines[i][j - 1].isMine()) {
                processMine(i, j - 1);
            }
        }
        if(j < M && !mines[i][j + 1].isMine()) {
            processMine(i, j + 1);
        }
        if(i < N) {
            if(j > 0 && !mines[i + 1][j - 1].isMine()) {
                processMine(i + 1, j - 1);
            }
            if(!mines[i + 1][j].isMine()) {
                processMine(i + 1, j);
            }
            if(j < M && !mines[i + 1][j + 1].isMine()) {
                processMine(i + 1, j + 1);
            }
        }
    }

    /**
     * Either detonates or reveals the field and its' neighbours recursively if the field is zero.
     * @param i row number
     * @param j column number
     */
    private void processMine(int i, int j) {
        if(!revealed[i][j]) {
            if (mines[i][j].isTrueMine()) {
                playSong("src/sample/cling.mp3");
                endGame("Whoops! Game over, you lost!");//, 2000);
            }
            mines[i][j].reveal(false);
            revealed[i][j] = true;
            if (mines[i][j].getNumber() == 0) {
                revealNonExplodingNeighbours(i, j);
            }
        }
    }

    /**
     * The game is won when every non-mine field is discovered (clicked directly or not).
     * @return true if everything has been clicked
     */
    private boolean wonGame() {
        for (int i = 0; i <= N; i++) {
            for (int j = 0; j <= M; j++) {
                if(!revealed[i][j] && !mines[i][j].isTrueMine()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Shows statistics for the current game, if available (if the player clicked something).
     */
    void showStats() {
        int total = 0, mineCount = 0, trueMineCount = 0;
        for (int i = 0; i <= N; i++) {
            for (int j = 0; j <= M; j++) {
                if(revealed[i][j]) {
                    ++total;
                }
                if(mines[i][j].isMine()) {
                    ++mineCount;
                }
                if(mines[i][j].isTrueMine()) {
                    ++trueMineCount;
                }
            }
        }
        if(total > 0) {
            System.out.printf("You completed %.3f%% and used %d/%d flags\n",
                    (total + mineCount) * 100. / ((N+1) * (M+1)), mineCount, trueMineCount);
            if(ended) {   // + time elapsed should be included in the score in the future (1./time)
                System.out.println("Your score is " + ((wonGame() ? 1000 : 0) + (trueMineCount - mineCount) * 10 +
                        trueMineCount + N * M) + " points");
            }
        }
        else {
            System.out.println("Stats unavailable");
        }
    }
}
