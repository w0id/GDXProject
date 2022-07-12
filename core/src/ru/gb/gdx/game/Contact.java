package ru.gb.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import java.util.SortedMap;

public class Contact implements ContactListener {
    private int count, climb;

    private boolean fatal;
    private int life = 3;

    public boolean isFatal() {
        return fatal;
    }

    public int getLife() {
        return life;
    }

    public boolean isOnGround() {
        return count>0;
    }

    public boolean isClimb() {
        return climb>0;
    }

    @Override
    public void beginContact(final com.badlogic.gdx.physics.box2d.Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa.getUserData() != null) {
            if (((String)fa.getUserData()).equals("sensor")) {
                count++;
            }
            if ((((String)fa.getUserData()).equals("left_sensor")) && (((String)fb.getUserData()).equals("rope"))) {
                climb++;
            }
            if (((String)fb.getUserData()).equals("enemy") && ((String)fa.getUserData()).equals("activator")) {
                fb.getBody().setAwake(true);
            }
            if (((String)fa.getUserData()).equals("Pers") && ((String)fb.getUserData()).equals("abyss")) {
                fatal = true;
            }
        }

        if (fb.getUserData() != null) {
            if (((String)fb.getUserData()).equals("sensor")) {
                count++;
            }
            if ((((String)fb.getUserData()).equals("left_sensor")) && (((String)fa.getUserData()).equals("rope"))) {
                climb++;
            }
            if (((String)fa.getUserData()).equals("enemy") && ((String)fb.getUserData()).equals("activator")) {
                fa.getBody().setAwake(true);
            }
            if (((String)fb.getUserData()).equals("Pers") && ((String)fa.getUserData()).equals("abyss")) {
                fatal = true;
            }
        }
    }

    @Override
    public void endContact(final com.badlogic.gdx.physics.box2d.Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa.getUserData() != null) {
            if (((String)fa.getUserData()).equals("sensor")) {
                count--;
            }
            if ((((String)fa.getUserData()).equals("left_sensor")) && (((String)fb.getUserData()).equals("rope"))) {
                climb--;
            }
            if (((String)fa.getUserData()).equals("Pers") && ((String)fb.getUserData()).equals("annihilator")) {
                life--;
            }
        }

        if (fb.getUserData() != null) {
            if (((String)fb.getUserData()).equals("sensor")) {
                count--;
            }
            if ((((String)fb.getUserData()).equals("left_sensor")) && (((String)fa.getUserData()).equals("rope"))) {
                climb--;
            }
            if (((String)fb.getUserData()).equals("Pers") && ((String)fa.getUserData()).equals("annihilator")) {
                life--;
            }
        }
    }

    @Override
    public void preSolve(final com.badlogic.gdx.physics.box2d.Contact contact, final Manifold oldManifold) {

    }

    @Override
    public void postSolve(final com.badlogic.gdx.physics.box2d.Contact contact, final ContactImpulse impulse) {

    }
}
