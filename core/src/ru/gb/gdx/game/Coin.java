package ru.gb.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Coin {
    private AnimPlayer animPlayer;
    private Vector2 position;
    private Rectangle rectangle;
    private Sound sound;
    private int state;
    private float time;

    public Rectangle getRectangle() {return rectangle;}

    public void setState() {
        sound.play(0.5f,1,0);
        time = 1;
        state=1;
    }

    public Coin(Vector2 position) {
        animPlayer = new AnimPlayer("Full Coinss.png", 8, 1, 10, Animation.PlayMode.LOOP);
        this.position = new Vector2(position);
        rectangle = new Rectangle(position.x, position.y, animPlayer.getFrame().getRegionWidth(), animPlayer.getFrame().getRegionWidth());
        sound = Gdx.audio.newSound(Gdx.files.internal("77fae3ab5b341cd.mp3"));
    }

    public int draw(SpriteBatch batch, OrthographicCamera camera){
        animPlayer.step(Gdx.graphics.getDeltaTime());
        float cx = (position.x - camera.position.x)/camera.zoom + Gdx.graphics.getWidth()/2;
        float cy = (position.y - camera.position.y)/camera.zoom + Gdx.graphics.getHeight()/2;

        batch.draw(animPlayer.getFrame(), cx, cy);
        if (time>0) {
            time -= Gdx.graphics.getDeltaTime();
        }
        if (time<0) {
            state=1;
        }
        return state;
    }

    public void shapeDraw(ShapeRenderer renderer, OrthographicCamera camera) {
        float cx = (rectangle.x - camera.position.x)/camera.zoom + Gdx.graphics.getWidth()/2;
        float cy = (rectangle.y - camera.position.y)/camera.zoom + Gdx.graphics.getHeight()/2;
        renderer.rect(cx, cy, rectangle.getWidth(), rectangle.getHeight());
    }

    public boolean isOverlaps(Rectangle heroRect, OrthographicCamera camera){
        float cx = (rectangle.x - camera.position.x)/camera.zoom + Gdx.graphics.getWidth()/2;
        float cy = (rectangle.y - camera.position.y)/camera.zoom + Gdx.graphics.getHeight()/2;
        Rectangle rect = new Rectangle(cx, cy, rectangle.width, rectangle.height);
        return rect.overlaps(heroRect);
    }

    public void dispose(){
        animPlayer.dispose();
        sound.dispose();
    }
}

