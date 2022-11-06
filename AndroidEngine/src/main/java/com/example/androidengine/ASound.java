package com.example.androidengine;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.SoundPool;

import com.example.engine.ISound;

import java.io.IOException;

public class ASound implements ISound {

    SoundPool soundPool;
    int id;
    float volume;


    public ASound(int id,SoundPool soundPool){
        this.soundPool =soundPool;
        this.id = id;
        volume = 1;
    }

    @Override
    public void play(){ soundPool.play(id, volume, volume, 0, 0, 1);}
    @Override
    public void stop(){soundPool.stop(id);}

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public void setVolume(float volume) {
        if (volume < 0f || volume > 1f)
            throw new IllegalArgumentException("Volume not valid: " + volume);
        soundPool.setVolume(id,volume,volume);
        this.volume = volume;
    }

    @Override
    public float getVolume() {
        return volume;
    }

}
