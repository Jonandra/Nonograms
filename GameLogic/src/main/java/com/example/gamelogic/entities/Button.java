package com.example.gamelogic.entities;

import com.example.engine.IEngine;
import com.example.engine.IFont;
import com.example.engine.IImage;
import com.example.engine.utilities.FloatLerper;
import com.example.engine.utilities.LerpType;
import com.example.gamelogic.utilities.Color;

public class Button extends UIElement {
    private Color buttonColor;
    private Color borderColor;
    private Color buttonPressedColor;
    private Color textColor;

    private Color currentButtonColor;
    private int borderSize = 10;
    float scale = 1;
    FloatLerper scaleLerper;
    String buttonText;
    IFont font;
    IInteractableCallback callback;
    IImage image;
    int paddingHorizontal, paddingVertical;

    public Button(int x, int y, int width, int height, String buttonText, IFont buttonFont, IEngine engine) {
        super(engine);

        this.posX = x;
        this.posY = y;
        this.width = width;
        this.height = height;
        this.graphics = engine.getGraphics();
        this.buttonText = buttonText;
        this.font = buttonFont;
        scaleLerper = new FloatLerper(1, 1.2f, 0.1f, LerpType.EaseInOut);
        scaleLerper.setReversed(true);

        buttonColor = new Color(255, 255, 255, 255);
        borderColor = new Color(0, 0, 0, 255);
        buttonPressedColor = new Color(123, 123, 123, 255);
        textColor = new Color();
        currentButtonColor = buttonColor;
    }

    public void setImage(IImage image) {
        this.image = image;
    }

    public void setPadding(int paddingHorizontal, int paddingVertical) {
        this.paddingHorizontal = paddingHorizontal;
        this.paddingVertical = paddingVertical;
    }

    public void setCallback(IInteractableCallback callback) {
        this.callback = callback;
    }

    @Override
    public void update(double deltaTime) {
        scaleLerper.update(deltaTime);
        scale = scaleLerper.getValue();
    }

    public void setBorderColor(int r, int g, int b) {
        borderColor.r = r;
        borderColor.g = g;
        borderColor.b = b;
    }

    public void setBorderSize(int boderSize) {
        this.borderSize = boderSize;
    }

    public void setBackgroundColor(int r, int g, int b) {
        buttonColor.r = r;
        buttonColor.g = g;
        buttonColor.b = b;
    }

    public void setBackgroundColor(int r, int g, int b, int a) {
        buttonColor.r = r;
        buttonColor.g = g;
        buttonColor.b = b;
        buttonColor.a = a;
    }

    public void setPressedColor(int r, int g, int b) {
        buttonPressedColor.r = r;
        buttonPressedColor.g = g;
        buttonPressedColor.b = b;
    }

    public void setPressedColorColor(int r, int g, int b, int a) {
        buttonPressedColor.r = r;
        buttonPressedColor.g = g;
        buttonPressedColor.b = b;
        buttonPressedColor.a = a;
    }

    public void setTextColor(int r, int g, int b) {
        textColor.r = r;
        textColor.g = g;
        textColor.b = b;
    }

    public void setTextColor(int r, int g, int b, int a) {
        textColor.r = r;
        textColor.g = g;
        textColor.b = b;
        textColor.a = a;
    }


    @Override
    public void render() {
        graphics.setScale(scale, scale);

        renderBorders();
        renderBackground();
        renderImage();
        renderText();

        graphics.setScale(1, 1);
    }

    @Override
    public void OnHoverEnter() {
        scaleLerper.setReversed(false);
    }

    @Override
    public void OnHoverExit() {
        scaleLerper.setReversed(true);
    }

    @Override
    public void OnTouchDown() {
        currentButtonColor = this.buttonPressedColor;
        if (callback != null)
            callback.onInteractionOccur();
    }

    public void setText(String text) {
        this.buttonText = text;
    }

    @Override
    public void OnTouchUp() {
        currentButtonColor = this.buttonColor;
    }

    @Override
    public void OnPointerMove(int x, int y) {

    }

    private void renderBackground() {
        graphics.setColor(currentButtonColor.r, currentButtonColor.g, currentButtonColor.b, currentButtonColor.a);
        graphics.fillRectangle(posX, posY, width, height);
    }

    private void renderBorders() {
        if (this.borderSize <= 0)
            return;

        graphics.setColor(borderColor.r, borderColor.g, borderColor.b, borderColor.a);
        graphics.drawRectangle(posX, posY, (int) (width), (int) (height), borderSize);
    }

    private void renderText() {
        graphics.setColor(0, 0, 0);
        graphics.drawTextCentered(buttonText, posX, posY, font);
    }

    private void renderImage() {
        if (image != null)
        {
            double imageWidthRatio = ((width-paddingHorizontal) * scale)  / image.getWidth();
            double imageHeightRatio = ((height - paddingVertical) * scale) / image.getHeight();
            double scaleFactor = scale;

            if (imageWidthRatio < imageHeightRatio) {
                scaleFactor = imageWidthRatio;
            }
            else {
                scaleFactor = imageHeightRatio;
            }

            graphics.setScale(scaleFactor, scaleFactor);
            graphics.drawImage(image, posX, posY);
        }
        graphics.setScale(scale, scale);
    }
}
