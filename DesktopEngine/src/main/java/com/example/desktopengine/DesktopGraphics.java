package com.example.desktopengine;

import com.example.engine.IFont;
import com.example.engine.IGraphics;
import com.example.engine.IImage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class DesktopGraphics implements IGraphics {

    Graphics2D graphics2D;
    JFrame jFrame;
    int logicSizeX, logicSizeY;
    Color currentColor;

    // Para guardar las transformaciones entre buffers
    AffineTransform currentTransform;
    AffineTransform defaultTransform;
    BufferStrategy bufferStrategy;


    public DesktopGraphics(JFrame jFrame, int wWidth, int wHeight) {
        this.jFrame = jFrame;

        jFrame.setSize(wWidth, wHeight);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setIgnoreRepaint(false);

        jFrame.setVisible(true);

        int tries = 100;
        while (tries-- > 0) {
            try {
                this.jFrame.createBufferStrategy(2);
                break;
            } catch (Exception e) {
            }
        } // while pidiendo la creación de la buffeStrategy
        if (tries == 0) {
            System.err.println("No pude crear la BufferStrategy");
            return;
        }

        this.bufferStrategy = jFrame.getBufferStrategy();

        this.graphics2D = ((Graphics2D) bufferStrategy.getDrawGraphics());
        currentColor = new Color(0, 0, 0, 0);
        currentTransform = graphics2D.getTransform();
        defaultTransform = new AffineTransform();

        // Esto es para tener en cuenta la barra de titulo de la app
        // Intente tambien que se ajustara horizontalmente pero nada, imposible
        translate(jFrame.getInsets().left, jFrame.getInsets().top);
    }

    void setGraphics2D(Graphics2D graphics) {
        this.graphics2D = graphics;
    }

    void updateGraphics()
    {
        this.graphics2D = (Graphics2D) bufferStrategy.getDrawGraphics();
    }

    public BufferStrategy getBufferStrategy() {
        return bufferStrategy;
    }

    void prepareFrame()
    {
        save();
        updateGraphics();
        restore(); // Restaurar
        clear(100, 100, 100);
    }

    void finishFrame()
    {
        bufferStrategy.getDrawGraphics().dispose();
    }

    boolean swapBuffer()
    {
        while(bufferStrategy.contentsRestored()) {
            return false; //ha ido mal
        }
        //Display Buffer
        this.bufferStrategy.show();

        return !this.bufferStrategy.contentsLost(); //ha ido bien?
    }

    @Override
    public IImage newImage(String pathToImage) {
        IImage image = null;
        try {
            image = new DesktopImage(ImageIO.read((new File(pathToImage))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }


    @Override
    public IFont newFont(String pathToFont) {
        return null;
    }

    @Override
    public void setLogicSize(int xSize, int ySize) {
        this.logicSizeX = xSize;
        this.logicSizeY = ySize;
    }

    @Override
    public void setColor(int r, int g, int b) {
        currentColor = new Color(r, g, b);
    }

    @Override
    public void setFont() {

    }

    @Override
    public void clear(int r, int g, int b) {
        graphics2D.setTransform(defaultTransform);
        graphics2D.setBackground(new Color(r, g, b, 255));
        graphics2D.clearRect(0, 0, getWidth(), getHeight());
        graphics2D.setTransform(currentTransform);
    }

    @Override
    public void drawImage(IImage image, int x, int y) {
        //this.graphics2D.drawImage(((DesktopImage) image).getImage(), x, y, null);
        this.graphics2D.drawImage(((DesktopImage)image).getImage(), x - image.getWidth()/2, y - image.getHeight()/2, null);
    }

    @Override
    public void drawRectangle(int upperLeftX, int upperLeftY, int lowerRightX, int lowerRightY, int lineWidth) {

    }

    @Override
    public void fillRectangle(int upperLeftX, int upperLeftY, int lowerRightX, int lowerRightY) {

    }

    @Override
    public void drawLine(int fromX, int fromY, int toX, int toY) {

    }

    @Override
    public void drawText(String text, int x, int y, IFont font) {

    }

    @Override
    public int getWidth() {
        return jFrame.getWidth();
    }

    @Override
    public int getHeight() {
        return jFrame.getHeight();
    }

    @Override
    public void scale(double x, double y) {
        graphics2D.scale(x, y);
        save();
    }

    @Override
    public void translate(double x, double y) {
        graphics2D.translate(x, y);
        save();
    }

    @Override
    public void rotate(double angleDegrees) {
        graphics2D.rotate(Math.toRadians(angleDegrees));
        save();
    }

    @Override
    public int logicXPositionToWindowsXPosition(int x) {
        return 0;
    }

    @Override
    public int logicYPositionToWindowsYPosition(int x) {
        return 0;
    }

    @Override
    public int windowsXPositionToLogicXPosition(int x) {
        return 0;
    }

    @Override
    public int windowsYPositionToLogicYPosition(int x) {
        return 0;
    }

    @Override
    public void setScale(double x, double y) {
        currentTransform.setToScale(x/currentTransform.getScaleX(),y/currentTransform.getScaleY());
        apply();
        save();
    }

    @Override
    public void setTranslation(double x, double y) {

        currentTransform.setToTranslation(x-currentTransform.getTranslateX(), y-currentTransform.getTranslateY());
        apply();
        save();
    }

    @Override
    public void setRotation(double angleDegrees) {
        currentTransform.setToRotation(Math.toRadians(angleDegrees));
        apply();
        save();
    }

    @Override
    public void resetScale()
    {
        setScale(1,1);
    }

    @Override
    public void resetTranslation()
    {
        setTranslation(0,0);

        // Volver a trasladar para tener en cuenta la barra
        translate(jFrame.getInsets().left, jFrame.getInsets().top);
    }

    @Override
    public void resetRotation()
    {
        setRotation(0);
    }

    @Override
    public void resetTransform()
    {
        resetTranslation();
        resetScale();
        resetRotation();
    }

    @Override
    public void save() {
        currentTransform = graphics2D.getTransform();
    }

    private void apply(){
        graphics2D.transform(currentTransform);
    }

    @Override
    public void restore() {
        if (currentTransform != null)
            graphics2D.setTransform(currentTransform);
    }
}
