import engine.cell.Color;
import engine.Game;
import engine.Key;

import java.util.Arrays;

public class Game2048 extends Game {

    private static final int SIDE = 4;
    private int[][] gameField = new int[SIDE][SIDE];
    private boolean isGameStopped = false;
    private int score = 0;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
        drawScene();
    }

    private void createGame() {
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField[i].length; j++) {
                gameField[i][j] = 0;
            }
        }
        createNewNumber();
        createNewNumber();
    }

    private void drawScene() {
        for (int y = 0; y < gameField.length; y++) {
            for (int x = 0; x < gameField[y].length; x++) {
                setCellColoredNumber(x,y,gameField[y][x]);
            }
        }
    }

    private void createNewNumber() {

        int x, y;
        do {
            x = getRandomNumber(SIDE);
            y = getRandomNumber(SIDE);

        } while (gameField[y][x] != 0);

        if (getRandomNumber(10) == 9) {
            gameField[y][x] = 4;
        } else {
            gameField[y][x] = 2;
        }

        if (getMaxTileValue() == 2048) {
            win();
        }

    }

    private Color getColorByValue(int value) {
        //0, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048.
        switch (value) {
            case 0:
                return Color.BISQUE;
                //break;
            case 2:
                return Color.WHITE;
                //break;
            case 4:
                return Color.HONEYDEW;
            case 8:
                return Color.ORANGE;
            case 16:
                return Color.DARKORANGE;
            case 32:
                return Color.ORANGERED;
            case 64:
                return Color.BROWN;
            case 128:
                return Color.DARKGRAY;
            case 256:
                return Color.KHAKI;
            case 512:
                return Color.PINK;
            case 1024:
                return Color.NAVY;
            case 2048:
                return Color.SILVER;

                default: return Color.BISQUE;
        }
    }

    private void setCellColoredNumber(int x, int y, int value) {
        Color currentColor = getColorByValue(value);
        String stringValue = Integer.toString(value);
        if (value == 0) {
            setCellValueEx(x,y,currentColor,"");
        } else {
            setCellValueEx(x,y,currentColor,stringValue);
        }

    }

    private boolean compressRow(int[] row) {
        boolean somethingChanged = false;

        for (int i = 0; i < row.length; i++) {
            if (row[i] != 0) {
                for (int j = 0; j < i; j++) {
                    if (row[j] == 0) {
                        row[j] = row[i];
                        row[i] = 0;
                        somethingChanged = true;
                        break;
                    }
                }
            }
        }

        return somethingChanged;
    }

    private boolean mergeRow(int[] row) {
        boolean somethingChanged = false;

        for (int i = 0; i < row.length; i++) {
            if (i+1 < row.length) {
                if (row[i] == row[i+1] && row[i] != 0) {
                    row[i] += row[i+1];
                    row[i+1] = 0;
                    somethingChanged = true;
                    score += row[i];
                    setScore(score);
                }
            }
        }

        return somethingChanged;
    }

    @Override
    public void onKeyPress(Key key) {
        if (isGameStopped && key == Key.SPACE) {
            isGameStopped = false;
            score = 0;
            setScore(score);
            createGame();
            drawScene();
        } else if (isGameStopped) {
            return;
        }

        if (!canUserMove()) {
            gameOver();
        }


        switch (key) {
            case UP:
                moveUp();
                drawScene();
                break;
            case DOWN:
                moveDown();
                drawScene();
                break;
            case LEFT:
                moveLeft();
                drawScene();
                break;
            case RIGHT:
                moveRight();
                drawScene();
                break;
        }
    }

    private void moveLeft() {
        boolean somethingChanged = false;

        for(int i = 0; i < gameField.length; i++) {

            boolean firstCompress = compressRow(gameField[i]);
            boolean firstMerge = mergeRow(gameField[i]);
            boolean secondCompress = compressRow(gameField[i]);

            if (firstCompress || firstMerge || secondCompress) {
                somethingChanged = true;
            }
        }

        if (somethingChanged) {
            createNewNumber();
        }

    }

    private void moveRight() {
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }

    private void moveUp() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }

    private void moveDown() {
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    private void rotateClockwise() {

        int[][] copyArray = new int[SIDE][SIDE];

        for (int i = 0; i < gameField.length; i++) {
            copyArray[i] = Arrays.copyOf(gameField[i], gameField[i].length);
        }

        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField[i].length; j++) {
                gameField[i][j] = copyArray[gameField[i].length - 1 -j][i];
            }
        }

    }

    private int getMaxTileValue() {
        int maxValue = gameField[0][0];

        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField[i].length; j++) {
                if (gameField[i][j] > maxValue) {
                    maxValue = gameField[i][j];
                }
            }
        }

        return maxValue;
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.GREEN,"ПОБЕДА! ПОБЕДА! ВМЕСТО ОБЕДА!",Color.WHITE,30);
    }

    private boolean canUserMove() {
        boolean userCanMove = false;

        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField[i].length; j++) {
                if (gameField[i][j] == 0) {
                    userCanMove = true;
                    break;
                }
            }
            if (userCanMove) break;
        }

        if (!userCanMove) {
            //Horizontal merge check
            for (int i = 0; i < gameField.length; i++) {
                for (int j = 0; j < gameField[i].length; j++) {
                    if (j+1 < gameField[i].length) {
                        if (gameField[i][j] == gameField[i][j+1] && gameField[i][j] != 0) {
                            userCanMove = true;
                            break;
                        }
                    }
                }
                if (userCanMove) break;
            }

            //Vertical merge check
            if (!userCanMove) {
                for (int i = 0; i < gameField.length; i++) {
                    for (int j = 0; j < gameField[i].length; j++) {
                        if (j+1 < gameField.length) {
                            if (gameField[j][i] == gameField[j + 1][i] && gameField[j][i] != 0) {
                                userCanMove = true;
                                break;
                            }
                        }
                    }
                    if (userCanMove) break;
                }
            }
        }

        return userCanMove;
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.RED,"НУ КАК ТАК, БРО ?! ПОРАЖЕНИЕ !!!",Color.WHITE,30);
    }

}
