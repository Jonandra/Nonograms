package com.example.gamelogic.states;

import com.example.engine.IEngine;
import com.example.engine.IFont;
import com.example.engine.IGraphics;
import com.example.engine.IImage;
import com.example.engine.IState;
import com.example.engine.InputEvent;
import com.example.gamelogic.entities.Board;
import com.example.gamelogic.entities.Button;
import com.example.gamelogic.entities.Callback;
import com.example.gamelogic.entities.Entity;
import com.example.gamelogic.entities.IInteractableCallback;
import com.example.gamelogic.entities.LivesPanel;
import com.example.gamelogic.entities.NonogramBoard;
import com.example.gamelogic.entities.Pointer;
import com.example.gamelogic.levels.NonogramGenerator;
import com.example.gamelogic.levels.WorldLevelType;
import com.example.gamelogic.utilities.DataToAccess;
import com.example.gamelogic.utilities.Event;
import com.example.gamelogic.utilities.EventHandler;
import com.example.gamelogic.utilities.EventManager;
import com.example.gamelogic.utilities.Listener;
import com.example.gamelogic.utilities.events.DefaultEvent;
import com.example.gamelogic.utilities.events.OnDamaged;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.crypto.Data;

public class MainGameLogic extends AbstractState implements Listener {

    String level;
    NonogramBoard board;
    Button returnButton;
    Button watchVid;
    Button winReturnButton;
    Button nextLevelButton;
    IFont font;
    IFont boardFont;
    IFont congratsFont;
    IImage arrow;
    IImage search;

    IImage fullLive;
    IImage emptyLive;
    LivesPanel livesPanel;
    boolean gameWin = false;
    boolean random = true;
    IInteractableCallback returnCallback;
    IInteractableCallback watchVidCallback;
    IInteractableCallback nextLevelCallback;

    int numLevel;
    WorldLevelType type;
    int row ;


    public MainGameLogic(IEngine engine, String level) {
        super(engine);
        this.level = level;
    }



    public MainGameLogic(IEngine engine, String level, boolean random) {
        super(engine);
        this.level = level;
        this.random = random;
    }

    /*public MainGameLogic(final IEngine engine, String level, boolean random, IInteractableCallback returnCallabck) {
        super(engine);
        this.level = level;
        this.random = random;
        this.returnCallback = returnCallabck;

        final IEngine engineAux = this.engine;
        this.watchVidCallback = new IInteractableCallback() {
            @Override
            public void onInteractionOccur() {
                try {
                    livesPanel.restoreLive();
                    engineAux.showVid();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

    }*/

    public MainGameLogic(final IEngine engine, int numLevel,WorldLevelType type,  boolean random, IInteractableCallback returnCallabck) {
        super(engine);
        this.row = numLevel / 5;
        this.numLevel = numLevel;
        this.type = type;
        this.level = getLevelName(numLevel,row);



        this.random = random;
        this.returnCallback = returnCallabck;

        final IEngine engineAux = this.engine;
        this.watchVidCallback = new IInteractableCallback() {
            @Override
            public void onInteractionOccur() {
                try {
                    livesPanel.restoreLive();
                    engineAux.showVid();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

    }



    @EventHandler
    public void onDamaged(OnDamaged eventArgs)
    {
        livesPanel.takeLive();
        if(!livesPanel.isAlive())
        {
            board.clear();
            livesPanel.restoreLives();
        }
    }

    @Override
    public boolean init() {
        try {
            if(returnCallback == null)
                returnCallback = new IInteractableCallback() {
                    @Override
                    public void onInteractionOccur() {
                        try {
                            engine.setState(new WorldLevelSelectionPageLogic(engine,type));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

            nextLevelCallback = new IInteractableCallback() {
                @Override
                public void onInteractionOccur() {
                    try {

                        MainGameLogic logic = new MainGameLogic(engine,++numLevel,type,false, new IInteractableCallback() {
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
            };

            EventManager.register(this);

            font = graphics.newFont(engine.getAssetsPath() + "fonts/Roboto-Regular.ttf", 24, false);
            boardFont = graphics.newFont(engine.getAssetsPath() + "fonts/Roboto-Regular.ttf", 18, true);
            congratsFont = graphics.newFont(engine.getAssetsPath() + "fonts/Roboto-Regular.ttf", 36, true);
            arrow = graphics.newImage(engine.getAssetsPath() + "images/arrow.png");
            search = graphics.newImage(engine.getAssetsPath() + "images/search.png");
            emptyLive = graphics.newImage(engine.getAssetsPath() + "images/heart-empty.png");
            fullLive = graphics.newImage(engine.getAssetsPath() + "images/heart-full.png");

            int numLifes = 3;
            int livesPanelWidth = Math.min(LOGIC_WIDTH, LOGIC_HEIGHT) / 4;
            int livesPanelHeight = livesPanelWidth / numLifes;
            int livesPanelYPos = (LOGIC_WIDTH - 20) / 2 + LOGIC_HEIGHT / 2 + livesPanelHeight;
            int livesPanelXPos = LOGIC_WIDTH - LOGIC_WIDTH / 25 - livesPanelWidth / 2;

            if(!graphics.isPortrait())
            {
                livesPanelYPos = LOGIC_HEIGHT / 20 + livesPanelHeight / 2;
                livesPanelXPos += 40;
            }

            livesPanel = new LivesPanel(engine, livesPanelXPos, livesPanelYPos, livesPanelWidth, livesPanelHeight, numLifes, fullLive, emptyLive);
            addEntity(livesPanel);

            returnButton = new Button(25, 25, 30, 30, engine);
            returnButton.setImage(arrow);
            returnButton.setPadding(10, 10);
            returnButton.setBackgroundColor(0, 0, 0, 0);
            returnButton.setBorderSize(0);
            returnButton.setHoverColor(205, 205, 205);
            returnButton.setCallback(returnCallback);

            watchVid = new Button(25, LOGIC_HEIGHT-10, 30, 30, engine);
            watchVid.setImage(arrow);
            watchVid.setPadding(10, 10);
            watchVid.setBackgroundColor(0, 0, 0, 0);
            watchVid.setBorderSize(0);
            watchVid.setHoverColor(205, 205, 205);
            watchVid.setCallback(watchVidCallback);

            winReturnButton = new Button(LOGIC_WIDTH / 2, LOGIC_HEIGHT - 50, 100, 50, engine);
            winReturnButton.setText("Volver", font);
            winReturnButton.setBackgroundColor(0, 0, 0, 0);
            winReturnButton.setBorderSize(0);
            winReturnButton.setHoverColor(205, 205, 205);
            winReturnButton.setCallback(returnCallback);


            nextLevelButton = new Button(LOGIC_WIDTH / 2, LOGIC_HEIGHT - 15, 100, 50, engine);
            nextLevelButton.setText("Siguiente Nivel", font);
            nextLevelButton.setBackgroundColor(0, 0, 0, 0);
            nextLevelButton.setBorderSize(0);
            nextLevelButton.setHoverColor(205, 205, 205);
            nextLevelButton.setCallback(nextLevelCallback);



            int[][] level = loadLevel();
            if (level == null)
                return false;

            final int numDesbloq = numLevel+= 2;
            int boardWidth = Math.min(LOGIC_WIDTH, LOGIC_HEIGHT) - 20;
            board = new NonogramBoard(engine, level, boardWidth, 2, boardFont, new Callback() {
                @Override
                public void callback() {
                    if(numLevel+1 > 10) DataToAccess.getInstance().changeBool(type.toString()+ "Palette", true);
                    DataToAccess.getInstance().changeInt(type.toString(), Math.max(DataToAccess.getInstance().getInt(type.toString()), numDesbloq));
                }
            });
            board.setPosX(LOGIC_WIDTH / 2);
            board.setPosY(LOGIC_HEIGHT / 2);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            EventManager.unregister(this);
            return false;
        }
    }

    private int[][] loadLevel() {
        if (random) {
            int rows = Integer.parseInt(level.split("x")[0]);
            int cols = Integer.parseInt(level.split("x")[1]);
            return NonogramGenerator.GenerateLevel(rows, cols);
        } else {
            InputStream is = engine.openFile(level);
            if (is == null)
                return null;

            // Read width and height
            try {
                String file = "";
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.endsWith(" "))
                        line += " ";
                    file += line;
                }

                String[] fileDivied = file.split(" ");
                int width = Integer.parseInt(fileDivied[0]);
                int height = Integer.parseInt(fileDivied[1]);

                int[][] level = new int[width][height];
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        level[i][j] = Integer.parseInt(fileDivied[i * height + j + 2]);
                    }
                }

                br.close();
                is.close();
                return level;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void update(double deltaTime) {
        board.update(deltaTime);

        if (!board.getIsWin()) {
            returnButton.update(deltaTime);
        } else
        {
            int boardWidth = Math.min(LOGIC_WIDTH, LOGIC_HEIGHT)/2 - 20;
            board.setWidth(boardWidth);
            winReturnButton.update(deltaTime);
            nextLevelButton.update(deltaTime);
        }
        watchVid.update(deltaTime);
    }

    @Override
    public void render() {
        board.render();

        graphics.setColor(0, 0, 0);
        if (!board.getIsWin()) {
            graphics.drawText("Rendirse", 45, 33, font);

            returnButton.render();
            watchVid.render();
            livesPanel.render();
        } else {
            graphics.drawTextCentered("¡Enhorabuena!", LOGIC_WIDTH / 2, 50, congratsFont);
            winReturnButton.render();
            nextLevelButton.render();
        }
    }

    @Override
    public void handleInput(List<InputEvent> events) {
        for (InputEvent inputEvent : events) {
            int proccesedX = graphics.windowsXPositionToLogicXPosition(inputEvent.x);
            int proccesedY = graphics.windowsYPositionToLogicYPosition(inputEvent.y);

            board.handleInput(proccesedX, proccesedY, inputEvent.type);

            if (!board.getIsWin()) {
                watchVid.handleInput(proccesedX,proccesedY,inputEvent.type);
                returnButton.handleInput(proccesedX, proccesedY, inputEvent.type);
            } else {
                winReturnButton.handleInput(proccesedX, proccesedY, inputEvent.type);
                nextLevelButton.handleInput(proccesedX,proccesedY,inputEvent.type);
            }

        }
    }

    private String getLevelName( int index, int i) {
        int cells = (i + 1) * 5;
        String typeToLower = type.toString().toLowerCase();
        String filename = typeToLower + cells + "x" + cells + "-" + ((index % 5) + 1) + ".txt";
        return "levels/" + typeToLower + "/" + filename;
    }

    /*private String getNextLevel(){
         String nextLevel;
         int nextLevelNum = ++numLevel;

         int i = nextLevelNum / 5;
         int j  = nextLevelNum % 5;

        nextLevel = getLevelName( (i * 5) + j, i);

       *//*  if(j+1 < 5)
             nextLevel = getLevelName( (i * 5) + j+1, i);
         else if(i +1<4)
             nextLevel = getLevelName( ((i+1) *5) ,i);
         else
             nextLevel = getLevelName( (i * 5) + j, i);*//*

        return  nextLevel;
    }*/


}