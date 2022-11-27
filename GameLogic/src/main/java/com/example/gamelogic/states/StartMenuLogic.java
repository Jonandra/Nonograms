package com.example.gamelogic.states;

import com.example.engine.IEngine;
import com.example.engine.IFont;
import com.example.engine.ISound;
import com.example.engine.InputEvent;
import com.example.gamelogic.entities.Button;
import com.example.gamelogic.entities.IInteractableCallback;
import com.example.gamelogic.entities.Pointer;

import java.util.List;

public class StartMenuLogic extends AbstractState {
    IFont testFont;
    Button quickGame;
    Button historyMode;

    public StartMenuLogic(IEngine engine) {
        super(engine);
    }

    @Override
    public boolean init() {
        try {
            int separation = 35;
            testFont = graphics.newFont(engine.getAssetsPath() + "fonts/Antihero.ttf", 24, false);
            quickGame = new Button(LOGIC_WIDTH / 2, LOGIC_HEIGHT / 2 + separation, 300, 35, engine);
            quickGame.setText("Partida Rápida", testFont);
            quickGame.setBackgroundColor(0, 0, 0, 0);
            quickGame.setBorderSize(0);
            quickGame.setHoverColor(200, 200, 200);
            quickGame.setCallback(new IInteractableCallback() {
                @Override
                public void onInteractionOccur() {
                    try {
                        engine.setState(new SelectLevelLogic(engine));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            historyMode = new Button(LOGIC_WIDTH / 2, LOGIC_HEIGHT / 2 - separation, 300, 35, engine);
            historyMode.setText("Modo Historia", testFont);
            historyMode.setBackgroundColor(0, 0, 0, 0);
            historyMode.setBorderSize(0);
            historyMode.setHoverColor(200, 200, 200);
            historyMode.setCallback(new IInteractableCallback() {
                @Override
                public void onInteractionOccur() {
                    try {
                        engine.setState(new HistoryModeSelection(engine));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            addEntity(quickGame);
            addEntity(historyMode);

            ISound sound = audio.newMusic(engine.getAssetsPath() + "audio/bgMusic.wav", "musicBg");
            sound.setVolume(1f);
            sound.play(); //It only plays if it's not alrady playing
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void render() {
        graphics.drawTextCentered("Nonogramas", LOGIC_WIDTH / 2, 90, testFont);
        super.render();
    }
}
