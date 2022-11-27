package com.example.gamelogic.entities;

import com.example.engine.IEngine;
import com.example.engine.IFont;
import com.example.engine.IImage;
import com.example.engine.ISound;
import com.example.engine.utilities.FloatLerper;
import com.example.engine.utilities.LerpType;
import com.example.gamelogic.utilities.Color;

public class NonogramBoard extends Board {
    private int[][] nonogramCellStates;
    private final int numOfStates = 3;
    private int borderBoardSize;
    private Color borderColor;
    private int borderWidth = 2;
    private int[][] solvedPuzzle;
    private String[] rowsText;
    private String[] colsText;
    private IFont font;
    private FloatLerper endTransitionLerper;
    private FloatLerper wrongTilesTimer;
    private boolean isWin;
    private float borderBoardRatio = 0.2f;
    private int initialWidth;
    private Color textColor;
    private int missingCells, badCellNumber;
    private int maxRow = 0;
    private int maxCol = 0;
    IImage blockedCell;
    ISound winSound;
    ISound selectCell;

    public NonogramBoard(IEngine engine, int[][] solvedPuzzle, int width, int gapSize, IFont font) {
        super(engine, solvedPuzzle.length, solvedPuzzle[0].length, width, gapSize);

        this.initialWidth = width;
        setWidth(width);
        this.solvedPuzzle = solvedPuzzle;
        init();

        nonogramCellStates = new int[rows][cols];
        borderColor = new Color();

        generateRowsText();
        generateColsText();
        calculateMaxRowAndMaxCol();

        this.font = font;
        endTransitionLerper = new FloatLerper(borderBoardRatio, 0, 0.55f, LerpType.EaseOut);
        wrongTilesTimer = new FloatLerper(0, 5, 10, LerpType.Linear); // Voy a usar esto como un timer
        wrongTilesTimer.setPaused(true);
        textColor = new Color();

        blockedCell = graphics.newImage(engine.getAssetsPath() + "images/blockedTile.png");
        winSound = audio.newSound(engine.getAssetsPath() + "audio/winSound.wav", "winSound");
        selectCell = audio.newSound(engine.getAssetsPath() + "audio/selectSound.wav", "selectSound");
    }

    public boolean getIsWin() {
        return isWin;
    }

    @Override
    public void setWidth(int newWidth) {
        borderBoardSize = (int) (newWidth * borderBoardRatio);
        this.width = newWidth - borderBoardSize;
    }

    private void generateRowsText() {
        rowsText = new String[rows];

        for (int row = 0; row < rows; row++) {
            int count = 0;
            String text = "";
            for (int col = 0; col < cols; col++) {
                if (solvedPuzzle[row][col] == 1) {
                    count++;
                } else {
                    if (count > 0) {
                        text += count + " ";
                        count = 0;
                    }
                }
            }
            if (count > 0) {
                text += count + " ";
            }
            rowsText[row] = text;
        }
    }

    private void generateColsText() {
        colsText = new String[cols];

        for (int col = 0; col < cols; col++) {
            int count = 0;
            String text = "";
            for (int row = 0; row < rows; row++) {
                if (solvedPuzzle[row][col] == 1) {
                    count++;
                } else {
                    if (count > 0) {
                        text += count + " ";
                        count = 0;
                    }
                }
            }
            if (count > 0) {
                text += count + " ";
            }
            colsText[col] = text;
        }
    }

    @Override
    public void render() {
        this.posX += borderBoardSize / 2;
        this.posY += borderBoardSize / 2;

        // Draw the border board
        super.render();
        RenderTextArea();
        RenderBordersStroke();

        this.posX -= borderBoardSize / 2;
        this.posY -= borderBoardSize / 2;

        renderErrorLabels();
    }

    private void renderErrorLabels() {
        if (isWin || wrongTilesTimer.getPaused())
            return;

        graphics.setColor(255, 50, 50);
        graphics.drawTextCentered("Te faltan " + missingCells + " casillas", posX, posY - height / 2 - borderBoardSize, font);
        graphics.drawTextCentered("Tienes mal " + badCellNumber + " casillas", posX, posY - height / 2 - borderBoardSize + graphics.getFontHeight(font), font);
    }

    private void RenderTextArea() {
        // Render text background
        graphics.setColor(255, 255, 255);
        graphics.fillRectangle(posX - width / 2 - borderBoardSize / 2, posY, borderBoardSize, height);
        graphics.fillRectangle(posX, posY - height / 2 - borderBoardSize / 2, width, borderBoardSize);

        graphics.setColor(textColor.r, textColor.g, textColor.b, textColor.a);

        renderRowText();
        renderColText();
    }

    private void calculateMaxRowAndMaxCol() {
        for (int row = 0; row < rows; row++) {
            int count = 0;
            for (int col = 0; col < rowsText[row].length(); col++) {
                if (rowsText[row].charAt(col) != ' ') {
                    count++;
                }
            }
            if (count > maxRow) {
                maxRow = count;
            }
        }

        for (int col = 0; col < cols; col++) {
            int count = 0;
            for (int row = 0; row < colsText[col].length(); row++) {
                if (colsText[col].charAt(row) != ' ') {
                    count++;
                }
            }
            if (count > maxCol) {
                maxCol = count;
            }
        }
    }

    private void renderRowText() {
        // We divide the row in maxRow areas, each text will be in one area from right to left
        int areaWidth = borderBoardSize / maxRow;

        // For each row, render character by character
        int rightestCellX = posX - width / 2 - areaWidth / 2;
        for (int row = 0; row < rows; row++) {
            String rowText = rowsText[row];
            int count = 0;
            for (int character = rowText.length() - 1; character >= 0; character--) {
                // If current character is not a space, we render it
                if (rowText.charAt(character) != ' ') {
                    graphics.drawTextCentered(rowText.charAt(character) + "", rightestCellX - areaWidth * count, getCellPosY(row), font);
                    count++;
                }
            }
        }
    }

    private void renderColText() {
        // The same we did for rows, but now we do it for cols from bottom to top
        int areaHeight = borderBoardSize / maxCol;

        int bottomestCellY = posY - height / 2 - areaHeight / 2;
        for (int col = 0; col < cols; col++) {
            String colText = colsText[col];
            int count = 0;
            for (int character = colText.length() - 1; character >= 0; character--) {
                if (colText.charAt(character) != ' ') {
                    graphics.drawTextCentered(colText.charAt(character) + "", getCellPosX(col), bottomestCellY - areaHeight * count, font);
                    count++;
                }
            }
        }

        // Render cols text from bottom to top
        // for (int col = 0; col < cols; col++) {
        //     String text = colsText[col];
        //     String[] texts = text.split(" ");

        //     int numOfTexts = texts.length;
        //     for (int i = 0; i < numOfTexts; i++) {
        //         String textToRender = texts[(numOfTexts - 1) - i];
        //         int textHeight = graphics.getFontHeight(font);
        //         int textPosX = getCellPosX(col);
        //         int textPosY = posY - height / 2 - borderBoardSize / 2 + (borderBoardSize - textHeight) / 2 - (i * textHeight);
        //         graphics.drawTextCentered(textToRender, textPosX, textPosY, font);
        //     }

        // }
    }

    private void RenderBordersStroke() {
        // Render borders on top of board
        graphics.setColor(borderColor.r, borderColor.g, borderColor.b, borderColor.a);
        graphics.drawRectangle(posX, posY, width, height, borderWidth);
        graphics.drawRectangle(posX - width / 2 - borderBoardSize / 2, posY, borderBoardSize, height, borderWidth);
        graphics.drawRectangle(posX, posY - height / 2 - borderBoardSize / 2, width, borderBoardSize, borderWidth);
    }

    @Override
    protected void OnCellClicked(int row, int col) {
        if (isWin)
            return;
        //System.out.println("Clicked on cell: " + row + " " + col);

        nonogramCellStates[row][col] = Math.min(nonogramCellStates[row][col] + 1, numOfStates) % numOfStates;
        isWin = updateBoardState(false);
        if (isWin)
            winSound.play();
        else
            selectCell.play();
    }

    private void setColorGivenState(int state) {
        setCellImg(null);

        switch (state) {
            case 0:
                if (!isWin)
                    graphics.setColor(123, 123, 123);
                else
                    graphics.setColor(255, 255, 255);
                break;
            case 1:
                graphics.setColor(123, 123, 255);
                break;
            case 2:
                if (!isWin) {
                    graphics.setColor(23, 23, 23);
                    setCellImg(blockedCell);
                } else
                    graphics.setColor(255, 255, 255);
                break;
            case 3:
                if (!isWin)
                    graphics.setColor(255, 123, 123);
                else
                    graphics.setColor(255, 255, 255);
        }
    }

    public void setBorderColor(int r, int g, int b) {
        borderColor.r = r;
        borderColor.g = g;
        borderColor.b = b;
        borderColor.a = 255;
    }

    @Override
    protected void OnCellRender(int row, int col) {
        setColorGivenState(nonogramCellStates[row][col]);
    }

    // Updates board state, update missingCells and badCells, also returns true if win is satisfied
    private boolean updateBoardState(boolean updateStats) {
        boolean win = true;

        if (updateStats) {
            badCellNumber = 0;
            missingCells = 0;
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (solvedPuzzle[row][col] == 1 && nonogramCellStates[row][col] != 1) {
                    if (updateStats)
                        missingCells++;
                    win = false;
                }

                if (solvedPuzzle[row][col] != 1 && (nonogramCellStates[row][col] == 1 || nonogramCellStates[row][col] == 3)) {
                    if (updateStats)
                        badCellNumber++;
                    win = false;
                }
            }
        }

        return win;
    }

    public void checkSolution() {
        if (isWin)
            return;

        updateBoardState(true);

        if (isWin) {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    if (solvedPuzzle[row][col] != 1) {
                        nonogramCellStates[row][col] = 0;
                    }
                }
            }
        } else {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    if (solvedPuzzle[row][col] != 1 && nonogramCellStates[row][col] == 1) {
                        nonogramCellStates[row][col] = 3;
                    }
                }
            }

            wrongTilesTimer.restart();
        }
    }

    @Override
    public void update(double deltaTime) {
        if (isWin) {
            if (!endTransitionLerper.isFinished()) {
                this.endTransitionLerper.update(deltaTime);
                borderBoardRatio = endTransitionLerper.getValue();
                setWidth(initialWidth);
                textColor.a = (int) (255 * (1.0f - endTransitionLerper.getProgress()));
                borderColor.a = (int) (255 * (1.0f - endTransitionLerper.getProgress()));
                super.init();
            } else {
                textColor.a = 0;
                borderColor.a = 0;
                borderBoardRatio = 0;
            }
        } else
            wrongTilesTimer.update(deltaTime);

        if (wrongTilesTimer.isFinished()) {
            wrongTilesTimer.restart();
            wrongTilesTimer.setPaused(true);

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    if (nonogramCellStates[row][col] == 3) {
                        nonogramCellStates[row][col] = 0;
                    }
                }
            }

            isWin = updateBoardState(false);
        }
    }

    @Override
    public void OnPointerDown(int x, int y) {
        this.posX += borderBoardSize / 2;
        this.posY += borderBoardSize / 2;

        super.OnPointerDown(x, y);

        this.posX -= borderBoardSize / 2;
        this.posY -= borderBoardSize / 2;
    }
}
