package ru.gb.gdx.game.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WinScreen implements Screen, InputProcessor {
    Texture texture;
    private Music music;
    final Game game;
    SpriteBatch batch;

    public WinScreen(final Game game) {
        this.game = game;
        batch = new SpriteBatch();
        texture = new Texture("win.jpg");
        music = Gdx.audio.newMusic(Gdx.files.internal("Freesound_Victory_Celebration_Movie_Score_wav_by_FunWithSound.mp3"));
        music.setLooping(false);
        music.setVolume(0.025f);
        music.play();
    }

    @Override
    public boolean keyDown(final int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(final int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(final char character) {
        return false;
    }

    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(final float amountX, final float amountY) {
        return false;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(final float delta) {
        batch.begin();
        batch.draw(texture,0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            dispose();
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void resize(final int width, final int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        music.stop();
        music.dispose();
        texture.dispose();
        batch.dispose();
    }
}
