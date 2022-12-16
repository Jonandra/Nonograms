package com.example.engine;

import java.io.InputStream;

public interface IEngine {
    IGraphics getGraphics();
    IAudio getAudio();
    IState getState();
    String getAssetsPath();
    InputStream openFile(String filename);
    boolean supportsTouch();
    void enableBanner(boolean enable);
    void showVid();


    void render();
    void update(double elapsedTime);
    void handleInput();
    <T> void setSavedValue(String key,T value);
    <T> Boolean hasSavedValue(String key);
    <T> T getSavedValue(String key, Class<T> classType);

    // Esto podria estar definido en una clase abstracta, pero de esta forma
    // podemos hacer el efecto de fade, que en desktop será distinto que en android
    // Ademas, lo ideal es cambiar el estado al final del bucle de juego, cambiarlo de golpe puede dar problemas
    void setState(IState state) throws Exception;
}
