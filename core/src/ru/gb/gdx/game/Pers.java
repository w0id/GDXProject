package ru.gb.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Pers {
    private AnimPlayer idle, jump, walkRight;
    private boolean isJump, isWalk, dir;
    private Vector2 pos;
    private int x, y;
    private Rectangle rect;

    public Pers(AnimPlayer idle, AnimPlayer jump, AnimPlayer walkRight) {
        idle = new AnimPlayer("idle.png", 1,1, 16.0f, Animation.PlayMode.LOOP);
        jump = new AnimPlayer("jump.png", 1,1, 16.0f, Animation.PlayMode.LOOP);
        walkRight = new AnimPlayer("walkRight.png", 1,8, 16.0f, Animation.PlayMode.LOOP);
//        x = Gdx.graphics.getWidth()/2;
//        y = Gdx.graphics.getHeight()/2;
        pos = new Vector2(0,0);


    }


}
