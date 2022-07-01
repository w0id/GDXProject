package ru.gb.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class Contact implements ContactListener {
    private int count;
    private int climb;

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
        }

        if (fb.getUserData() != null) {
            if (((String)fb.getUserData()).equals("sensor")) {
                count++;
            }
            if ((((String)fb.getUserData()).equals("left_sensor")) && (((String)fa.getUserData()).equals("rope"))) {
                climb++;
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
        }

        if (fb.getUserData() != null) {
            if (((String)fb.getUserData()).equals("sensor")) {
                count--;
            }
            if ((((String)fb.getUserData()).equals("left_sensor")) && (((String)fa.getUserData()).equals("rope"))) {
                climb--;
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
