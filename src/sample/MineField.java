package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

class MineField extends Rectangle implements Serializable {
    private static final long serialVersionUID = 5357L;
    //private Text text;
    private int number;
    private boolean isMine, isMarked;

    MineField(double x, double y, double width, double height) {
        super(x, y, width, height);
        isMine = isMarked = false;
    }

    int getNumber() {
        return number;
    }

    void setNumber(int number) {
        this.number = number;
    }

    boolean isMine() {
        return /*isMine || */isMarked;
    }

    boolean isTrueMine() {
        return isMine;
    }

    void setMine() {
        number = -1;
        isMine = true;
    }

    void setMarked() {
        isMarked = !isMarked;
        if(isMarked)
            setFill(Color.INDIANRED);
        else setFill(Color.DARKGREY);
    }

    void reveal(boolean done) {
        if(isMarked && !done) {
            return;
        }
        switch (number) {
            case -1:
                setFill(Color.FIREBRICK);
                break;
            case 0:
                setFill(Color.YELLOW);
                break;
            case 1:
                setFill(Color.GOLD);
                break;
            case 2:
                setFill(Color.BURLYWOOD);
                break;
            case 3:
                setFill(Color.YELLOWGREEN);
                break;
            case 4:
                setFill(Color.TEAL);
                break;
            case 5:
                setFill(Color.DARKSLATEGRAY);
                break;
            case 6:
                setFill(Color.BLUE);
                break;
            case 7:
                setFill(Color.TOMATO);
                break;
            case 8:
                setFill(Color.ORANGERED);
                break;
            default:
                break;
        }
    }

    void reset() {
        isMine = isMarked = false;
    }

    /*MineField(double x, double y, double width, double height, String name) {
        super(x, y, width, height);
        text = new Text(x, y, name);

    }*/

    /*Text getText() {
        return text;
    }*/
}
