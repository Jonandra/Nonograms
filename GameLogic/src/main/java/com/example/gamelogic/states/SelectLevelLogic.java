package com.example.gamelogic.states;

import com.example.engine.IEngine;
import com.example.engine.IFont;
import com.example.engine.IGraphics;
import com.example.engine.IImage;
import com.example.engine.IState;
import com.example.engine.InputEvent;
import com.example.gamelogic.entities.Button;
import com.example.gamelogic.entities.Entity;
import com.example.gamelogic.entities.IInteractableCallback;
import com.example.gamelogic.entities.Pointer;
import com.example.gamelogic.entities.Text;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectLevelLogic extends AbstractState {
    IFont font;
    IFont fontBold;
    IImage arrow;

    Button returnButton;
    int rows = 2, cols = 3;
    String[][] texts = {{"4x4", "5x5", "5x10"}, {"8x8", "10x10", "10x15"}};
    Text selectText;

    public SelectLevelLogic(IEngine engine) {
        super(engine);
    }

    @Override
    public boolean init() {
        try {

            engine.enableBanner(false);

            fontBold = graphics.newFont(engine.getAssetsPath() + "fonts/Roboto-Regular.ttf", 24, true);
            font = graphics.newFont(engine.getAssetsPath() + "fonts/Roboto-Regular.ttf", 24, false);
            arrow = graphics.newImage(engine.getAssetsPath() + "images/arrow.png");
            int buttonSize = graphics.isPortrait() ? LOGIC_WIDTH / 5 : LOGIC_WIDTH / 8;
            int gapSize = buttonSize / 2;

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    int buttonY = graphics.isPortrait() ? 200 + (120 * (row + 1)) : 75 + (120 * (row + 1));
                    int buttonX = graphics.isPortrait() ? (gapSize + buttonSize) * (col + 1) - gapSize : 110 + (gapSize + buttonSize) * (col + 1) - gapSize;
                    Button button = new Button(buttonX, buttonY, buttonSize, buttonSize, engine);
                    button.setText(texts[row][col], fontBold);
                    final int finalRow = row;
                    final int finalCol = col;
                    button.setCallback(new IInteractableCallback() {
                        @Override
                        public void onInteractionOccur() {
                            try {
                                engine.setState(new MainGameLogic(engine, texts[finalRow][finalCol]));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    addEntity(button);
                }
            }

            returnButton = new Button(25, 25, 30, 30, engine);
            returnButton.setImage(arrow);
            returnButton.setPadding(10, 10);
            returnButton.setBackgroundColor(0, 0, 0, 0);
            returnButton.setBorderSize(0);
            returnButton.setHoverColor(205, 205, 205);
            returnButton.setCallback(new IInteractableCallback() {
                @Override
                public void onInteractionOccur() {
                    try {
                        engine.setState(new StartMenuLogic(engine));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            addEntity(returnButton);
            int textY = graphics.isPortrait() ? 200 : 100;
            selectText = new Text(engine, "Selecciona el tamaño del puzzle", font, LOGIC_WIDTH / 2, textY);
            addEntity(selectText);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void render() {
        super.render();
        graphics.setScale(0.05, 0.05);
        graphics.drawImage(arrow, 25, 25);
        graphics.setScale(1, 1);
        graphics.drawText("Volver", 45, 35, font);


    }
}
