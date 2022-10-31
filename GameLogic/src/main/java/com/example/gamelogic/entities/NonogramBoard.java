package com.example.gamelogic.entities;

import com.example.engine.IEngine;
import com.example.engine.IFont;
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
    private boolean isWin;
    private float borderBoardRatio = 0.2f;
    private int initialWidth;
    private Color textColor;

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

        this.font = font;
        endTransitionLerper = new FloatLerper(borderBoardRatio, 0, 0.55f, LerpType.EaseOut);
        textColor = new Color();
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
    }

    private void RenderTextArea() {
        // Render text background
        graphics.setColor(255, 255, 255);
        graphics.fillRectangle(posX - width / 2 - borderBoardSize / 2, posY, borderBoardSize, height);
        graphics.fillRectangle(posX, posY - height / 2 - borderBoardSize / 2, width, borderBoardSize);

        graphics.setColor(textColor.r, textColor.g, textColor.b, textColor.a);

        // Render rows text from right to left
        for (int row = 0; row < rows; row++) {
            String text = rowsText[row];
            int textWidth = graphics.getStringWidth(text, font);
            int textPosX = posX - width / 2 - borderBoardSize / 2 + (borderBoardSize - textWidth) / 2;
            int textPosY = getCellPosY(row);
            graphics.drawTextCentered(text, textPosX, textPosY, font);
        }

        // Render cols text from bottom to top
        for (int col = 0; col < cols; col++) {
            String text = colsText[col];
            String[] texts = text.split(" ");

            int numOfTexts = texts.length;
            for (int i = 0; i < numOfTexts; i++) {
                String textToRender = texts[(numOfTexts - 1) - i];
                int textHeight = graphics.getFontHeight(font);
                int textPosX = getCellPosX(col);
                int textPosY = posY - height / 2 - borderBoardSize / 2 + (borderBoardSize - textHeight) / 2 - (i * textHeight);
                graphics.drawTextCentered(textToRender, textPosX, textPosY, font);
            }

        }
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
        nonogramCellStates[row][col] = (nonogramCellStates[row][col] + 1) % numOfStates;
    }

    private void setColorGivenState(int state) {
        switch (state) {
            case 0:
                graphics.setColor(123, 123, 123);
                break;
            case 1:
                graphics.setColor(123, 123, 255);
                break;
            case 2:
                graphics.setColor(23, 23, 23);
                break;
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

    private boolean checkWin() {
        return true;
    }

    public void checkSolution() {
        isWin = checkWin();
    }

    @Override
    public void update(double deltaTime) {
        if (isWin) {
            this.endTransitionLerper.update(deltaTime);
            borderBoardRatio = endTransitionLerper.getValue();
            setWidth(initialWidth);
            textColor.a = (int) (255 * (1.0f - endTransitionLerper.getProgress()));
            borderColor.a = (int) (255 * (1.0f - endTransitionLerper.getProgress()));
            super.init();
        }
    }

    @Override
    public void OnPointerDown(int x, int y)
    {
        this.posX += borderBoardSize / 2;
        this.posY += borderBoardSize / 2;

        super.OnPointerDown(x, y);

        this.posX -= borderBoardSize / 2;
        this.posY -= borderBoardSize / 2;
    }
}
