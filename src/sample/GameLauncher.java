package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.List;

public class GameLauncher extends Application {
    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parameters parameters = getParameters();
        List<String> list = parameters.getRaw();
        int size = list.size(), n, m, d;
        if(size > 0) {
            n = Integer.parseInt(list.get(0));
            if (size > 1) {
                m = Integer.parseInt(list.get(1));
                if(size > 2) {
                    d = Integer.parseInt(list.get(2));
                }
                else {
                    d = 0;
                }
            }
            else {
                m = 9; d = 0;
            }
        }
        else {
            n = 6; m = 9; d = 0;
        }
        GridPane root = new GridPane();
        primaryStage.setTitle("Minesweeper");
        root.setHgap(15);
        root.setVgap(15);
        root.setPadding(new Insets(25, 25, 25, 25));

        //final MineSweeper[] game = new MineSweeper[]{new MineSweeper()};
        MineSweeper game = new MineSweeper(root, n, m, d);

        Scene scene = new Scene(root);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode().equals(KeyCode.S)) {
                game.showStats();
            }
            else if(event.getCode().equals(KeyCode.R)) {
                game.initialiseGame();
            }
            /*else if(event.getCode().equals(KeyCode.D)) {
                MineField[][] gameMines = game.getMines();
                for (int i = 0; i <= n; i++) {
                    for (int j = 0; j <= m; j++) {
                        root.getChildren().remove(gameMines[i][j]);
                    }
                }
            }*/
            /*else if(event.getCode().equals(KeyCode.N)) {
                game[0] = new MineSweeper(root, n, m, d);
                game[0].initialiseGame();
            }
            else if(event.getCode().equals(KeyCode.K)) {
                try {
                    FileOutputStream outputStream = new FileOutputStream("progress.ser");
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(game[0]);
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(event.getCode().equals(KeyCode.L)) {
                try {
                    FileInputStream inputStream = new FileInputStream("progress.ser");
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    Object object = objectInputStream.readObject();
                    //game[0].initialiseGame();
                    game[0] = new MineSweeper(root, n, m, d);
                    game[0] = (MineSweeper) object;
                    inputStream.close();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }*/
        });
        primaryStage.setScene(scene);//, 300, 275));
        primaryStage.show();
    }
}
