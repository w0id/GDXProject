package ru.gb.gdx.game;

import com.badlogic.gdx.Game;
import ru.gb.gdx.game.screens.InScreen;

public class Main extends Game {
    @Override
    public void dispose() {

    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(final int width, final int height) {
        super.resize(width, height);
    }

    @Override
    public void create() {
        this.setScreen(new InScreen(this));
    }
}
