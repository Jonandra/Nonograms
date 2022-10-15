package com.example.engine;

public interface IGraphics {
    IImage newImage(String pathToImage);
    IFont newFont(String pathToFont);
    void setLogicSize(int xSize, int ySize);
    void setColor();
    void setFont();
    void drawImage(IImage image, int x, int y);
    void drawRectangle(int upperLeftX, int upperLeftY, int lowerRightX, int lowerRightY, int lineWidth);
    void fillRectangle(int upperLeftX, int upperLeftY, int lowerRightX, int lowerRightY);
    void drawLine(int fromX, int fromY, int toX, int toY);
    void drawText(String text, int x, int y, IFont font);
}
