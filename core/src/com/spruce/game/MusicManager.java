package com.spruce.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicManager {
    public Music darkMusic;
    public Music actionMusic;

    public float progress = 0.0f;
    public Music current = null;
    public Music next = null;

    private enum State {
        Idle,
        FadingOut,
        FadingIn,
    }
    private State state = State.Idle;

    public MusicManager() {
        darkMusic = Gdx.audio.newMusic(Gdx.files.internal("music/dark.ogg"));
        actionMusic = Gdx.audio.newMusic(Gdx.files.internal("music/action.ogg"));
    }

    public void start(Music music) {
        if (current == null) {
            music.setVolume(1.0f);
            music.setLooping(true);
            music.play();
            current = music;
        } else {
            if (current == music) {
                if (state == State.FadingOut) {
                    state = State.FadingIn;
                    progress = 1.0f - progress;
                }
            } else {
                fadeOut();
                next = music;
            }
        }
    }

    public void fadeOut() {
        if (current == null) return;
        if (state == State.FadingIn) {
            progress = 1-progress;
        }
        state = State.FadingOut;
    }

    public void update(float delta) {
        if (state == State.FadingIn) {
            progress += delta;
            current.setVolume(Math.min(1.0f, progress));
            if (progress >= 1) {
                progress = 0;
                state = State.Idle;
            }
        } else if (state == State.FadingOut) {
            progress += delta;
            current.setVolume(Math.max(0.0f, 1.0f - progress));
            if (progress >= 1) {
                progress = 0;
                state = State.Idle;
                current.stop();
                if (next != null) {
                    current = next;
                    next = null;
                    current.setVolume(1.0f);
                    current.setLooping(true);
                    current.play();
                } else {
                    current = null;
                }
            }
        }
    }

    public void paused() {

    }
    public void resumed() {

    }
}
