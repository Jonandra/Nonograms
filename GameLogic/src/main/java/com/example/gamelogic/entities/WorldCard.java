package com.example.gamelogic.entities;

import com.example.engine.IEngine;
import com.example.engine.IFont;
import com.example.engine.IImage;
import com.example.engine.IInput;

public class WorldCard extends Entity{
    private Button button;
    private SizedImage tape;
    private SizedImage cardHolder;
    private SizedImage cardImage;
    private IFont textFont;
    int completedLevels;
    String completedText;
    String titeText;

    public WorldCard(IEngine engine, int x, int y, int width, int height, int completedLevels, String titleText, IImage cardHolder, IImage cardImage, IImage tape, IFont textFont) {
        super(engine);
        this.width = width;
        this.height = height;
        this.posX = x;
        this.posY = y;
        this.completedLevels = completedLevels;
        this.completedText = completedLevels+ "/20";
        this.titeText = titleText;
        this.cardHolder = new SizedImage(engine, cardHolder, x, y, width, height);
        this.cardImage = new SizedImage(engine, cardImage, x, y - (int) (height * 0.05f), (int) (height * 0.8f), (int) (height*0.8f));
        this.tape = new SizedImage(engine, tape, x, y - (int) (height * 0.4f), (int) (width * 0.5f), (int) (height * 0.2f));
        this.textFont = textFont;
        this.button = new Button(x,y,width,height, engine);
    }

    public void setCallback(IInteractableCallback callback)
    {
        this.button.setCallback(callback);
    }

    @Override
    public void update(double deltaTime) {
        button.update(deltaTime);
    }

    @Override
    public void render() {
        cardImage.render();
        cardHolder.render();
        tape.render();

        graphics.drawTextCentered(completedText, posX, posY - (int) (height * 0.39f), textFont);
        graphics.drawTextCentered(titeText, posX, posY + (int) (height * 0.38f), textFont);
    }

    @Override
    public void handleInput(int x, int y, IInput.InputTouchType touchType)
    {
        super.handleInput(x,y, touchType);
        button.handleInput(x,y,touchType);
    }

    @Override
    public void OnPointerDown(int x, int y) {

    }

    @Override
    public void OnPointerUp(int x, int y) {

    }

    @Override
    public void OnPointerMove(int x, int y) {

    }
}
