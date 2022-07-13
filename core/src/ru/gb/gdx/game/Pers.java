package ru.gb.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Pers {
    private AnimPlayer idle, jump, walkRight;
    private boolean isJump, isWalk, dir;
    private Vector2 pos;
    private Rectangle rect;

    public void setPos(final Vector2 pos) {
        this.pos = pos;
    }

    public Pers() {
        idle = new AnimPlayer("hero/idle.png", 1, 1, 16.0f, Animation.PlayMode.LOOP);
        jump = new AnimPlayer("hero/jump.png", 1, 1, 16.0f, Animation.PlayMode.LOOP);
        walkRight = new AnimPlayer("hero/runRight.png", 4, 1, 16.0f, Animation.PlayMode.LOOP);
        pos = new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        rect = new Rectangle(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2,walkRight.getFrame().getRegionWidth(),walkRight.getFrame().getRegionHeight());
    }

    public void setJump(final boolean jump) {
        isJump = jump;
    }

    public void setWalk(boolean walk) {isWalk = walk;}

    public void setDir(boolean dir) {this.dir = dir;}

    public TextureRegion getFrame(){
        TextureRegion tmpTex = null;
        if (!isJump && !isWalk && !dir) {
            idle.step(Gdx.graphics.getDeltaTime());
            if (idle.getFrame().isFlipX())
               idle.getFrame().flip(true, false);
            tmpTex = idle.getFrame();
        } else if (!isJump && !isWalk && dir) {
            idle.step(Gdx.graphics.getDeltaTime());
            if (!idle.getFrame().isFlipX())
              idle.getFrame().flip(true, false);
            tmpTex = idle.getFrame();
        } else  if (!isJump && isWalk && !dir) {
            walkRight.step(Gdx.graphics.getDeltaTime());
            if (walkRight.getFrame().isFlipX())
               walkRight.getFrame().flip(true, false);
            tmpTex = walkRight.getFrame();
        } else  if (!isJump && isWalk && dir) {
            walkRight.step(Gdx.graphics.getDeltaTime());
            if (!walkRight.getFrame().isFlipX())
                walkRight.getFrame().flip(true, false);
            tmpTex = walkRight.getFrame();
        } else if (isJump && !dir) {
            jump.step(Gdx.graphics.getDeltaTime());
            if (jump.getFrame().isFlipX())
                jump.getFrame().flip(true, false);
            tmpTex = jump.getFrame();
        } else if (isJump && dir) {
            jump.step(Gdx.graphics.getDeltaTime());
            if (!jump.getFrame().isFlipX())
                jump.getFrame().flip(true, false);
            tmpTex = jump.getFrame();
        }
        return tmpTex;
    }

    public Vector2 getPos() {return pos;}

    public Rectangle getRect() {return rect;}

    public void shapeDraw(ShapeRenderer renderer, OrthographicCamera camera) {
        float cx = Gdx.graphics.getWidth()/2 - ((rect.width/2) / camera.zoom);
        float cy = Gdx.graphics.getHeight()/2 - ((rect.height/2) / camera.zoom);
        float cW = rect.getWidth() / camera.zoom;
        float cH = rect.getHeight() / camera.zoom;
        renderer.rect(cx, cy, cW, cH);
    }

}

