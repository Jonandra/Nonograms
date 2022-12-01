package com.example.gamelogic.states;

import com.example.engine.IEngine;
import com.example.engine.IFont;
import com.example.engine.IImage;
import com.example.gamelogic.entities.Button;
import com.example.gamelogic.entities.IInteractableCallback;
import com.example.gamelogic.entities.SizedImage;
import com.example.gamelogic.entities.Text;
import com.example.gamelogic.levels.WorldLevelType;
import com.example.gamelogic.utilities.Color;

public class WorldLevelSelectionPageLogic extends AbstractState {
    Button returnButton;
    IImage arrow;
    SizedImage backgroundImg;
    Button levels[][];
    IFont tittleFont; // TODO: Crear una entidad que sea simplemente un texto
    String text;
    WorldLevelType type;
    Text tittleText;

    protected WorldLevelSelectionPageLogic(IEngine engine, WorldLevelType type) {
        super(engine);
        text = type.toString();
        this.type = type;
    }

    @Override
    public boolean init() {
        try {
            tittleFont = graphics.newFont(engine.getAssetsPath() + "fonts/Roboto-Regular.ttf", 24, true);
            tittleText = new Text(engine, text, tittleFont, LOGIC_WIDTH / 2, (LOGIC_HEIGHT / 2) - 100);
            tittleText.setBackgroundColor(new Color(255,255,255,169));
            tittleText.setBackgruondSize(LOGIC_WIDTH, -1);

            int width = 0, height = 0;
            createBackground();
            addEntity(backgroundImg);
            addEntity(tittleText);

            int xPos = 0;
            int yPos = 100;

            width = (LOGIC_WIDTH - (5 * 10)) / 5;
            height = width;
            int spacing = (LOGIC_WIDTH - (5 * width)) / 6;

            levels = new Button[4][5];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 5; j++) {
                    // Calculate the x and y position of the button
                    xPos = (j * width) + ((j + 1) * spacing) + width / 2;
                    yPos = (i * height) + ((i + 1) * spacing) + (LOGIC_HEIGHT / 2);

                    // Create the button
                    levels[i][j] = new Button(xPos, yPos, width, height, engine);
                    levels[i][j].setBorderSize(4);
                    levels[i][j].setBorderColor(183, 210, 79);
                    levels[i][j].setBackgroundColor(240, 240, 240, 255);
                    levels[i][j].setPadding(5, 5);

                    final String level = getLevelName(type, i);
                    levels[i][j].setCallback(new IInteractableCallback() {
                        @Override
                        public void onInteractionOccur() {
                            try {
                                MainGameLogic logic = new MainGameLogic(engine, level, false, new IInteractableCallback() {
                                    @Override
                                    public void onInteractionOccur() {
                                        try {
                                            engine.setState(new WorldLevelSelectionPageLogic(engine, type));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                engine.setState(logic);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    addEntity(levels[i][j]);
                }
            }

            arrow = graphics.newImage(engine.getAssetsPath() + "images/arrow.png");
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
                        engine.setState(new WorldSelectionPageLogic(engine));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            addEntity(returnButton);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void createBackground()
    {
        IImage bg = graphics.newImage("images/" + text.toLowerCase() + ".png");
        int wWidth = graphics.getWidth();
        int wHeiht = graphics.getHeight();
        int maximum = Math.max(wWidth, wHeiht);
        backgroundImg = new SizedImage(engine, bg, LOGIC_WIDTH / 2, LOGIC_HEIGHT / 2, maximum, maximum);
    }

    private String getLevelName(WorldLevelType type, int index) {
        int cells = (index + 1) * 5;
        String typeToLower = type.toString().toLowerCase();
        String filename = typeToLower + cells + "x" + cells + "-" + (index + 1) + ".txt";
        return "levels/" + typeToLower + "/" + filename;
    }
}
