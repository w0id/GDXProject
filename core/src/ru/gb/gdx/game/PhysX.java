package ru.gb.gdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
//import jdk.internal.vm.compiler.word.WordBase;

import java.util.Iterator;

public class PhysX {
    private final World world = new World(new Vector2(0,-9.81f), true);
    PolygonShape poly_h = new PolygonShape();

    private final Box2DDebugRenderer debugRenderer;
    private Body pers, enemy;
    public Contact cl;

    public PhysX() {
        debugRenderer = new Box2DDebugRenderer();
        cl = new Contact();
        world.setContactListener(cl);
    }

    public Body getPers() {
        return pers;
    }

    public void setPersForce(Vector2 force) {
        pers.applyForceToCenter(force,true);
    }

    public void step(){
        world.step(1/60.0f,3,3);
    }

    public void debugDraw(OrthographicCamera camera) {
        debugRenderer.render(world, camera.combined);
    }

    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
    }

    public void addObject(MapObject obj) {
        BodyDef def = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        CircleShape circle = new CircleShape();

        switch ((String) obj.getProperties().get("type")) {
            case "StaticBody":
                def.type = BodyDef.BodyType.StaticBody;
                break;
            case "DynamicBody":
                def.type = BodyDef.BodyType.DynamicBody;
                break;
            case "KinematicBody":
                def.type = BodyDef.BodyType.KinematicBody;
                break;
            default:
        }

        String name = (String) obj.getProperties().get("shape");
        switch (name) {
            case "rect":
                RectangleMapObject rect = (RectangleMapObject) obj;
                def.position.set(new Vector2(rect.getRectangle().x+rect.getRectangle().width/2, rect.getRectangle().y+rect.getRectangle().height/2));
                poly_h.setAsBox(rect.getRectangle().width/4,rect.getRectangle().height/4, new Vector2(rect.getRectangle().width/4, rect.getRectangle().height/4),0);
                fdef.shape = poly_h;
                break;
            case "circle":
                EllipseMapObject ellipseMapObject = (EllipseMapObject) obj;
                def.position.set(new Vector2(ellipseMapObject.getEllipse().x+ellipseMapObject.getEllipse().width/2, ellipseMapObject.getEllipse().y+ellipseMapObject.getEllipse().height/2));
                circle.setRadius(ellipseMapObject.getEllipse().width/2);
                fdef.shape = circle;
                break;
            default:
        }

        def.gravityScale = (float) obj.getProperties().get("gravityScale");
        fdef.restitution = (float) obj.getProperties().get("restitution");
        fdef.density = (float) obj.getProperties().get("density");
        fdef.friction = (float) obj.getProperties().get("friction");

        if (obj.getName().equals("camera")) {
            pers = world.createBody(def);
            pers.createFixture(fdef).setUserData(obj.getProperties().get("name"));

            poly_h.setAsBox(4.0f,2.0f, new Vector2(4.5f,-0.5f),0);
            fdef.shape = poly_h;
            fdef.isSensor = true;
            pers.createFixture(fdef).setUserData("sensor");

            poly_h.setAsBox(2.0f,7.25f,new Vector2(-0.5f,7.25f),0);
            fdef.shape = poly_h;
            fdef.isSensor = true;
            pers.createFixture(fdef).setUserData("left_sensor");

            circle.setRadius(100.0f);
            fdef.shape = circle;
            fdef.isSensor = true;
            pers.createFixture(fdef).setUserData("activator");

        } else {
            world.createBody(def).createFixture(fdef).setUserData(obj.getProperties().get("name"));
        }

        poly_h.dispose();
        circle.dispose();
    }

    public void addObjects(MapObjects objects) {
        BodyDef def = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape poly_h = new PolygonShape();
        CircleShape circle = new CircleShape();

        Iterator<MapObject> objectIterator = objects.iterator();
        while (objectIterator.hasNext()) {
            MapObject obj = objectIterator.next();
            fdef.isSensor = false;
            switch ((String) obj.getProperties().get("type")) {
                case "StaticBody":
                    def.type = BodyDef.BodyType.StaticBody;
                    break;
                case "DynamicBody":
                    def.type = BodyDef.BodyType.DynamicBody;
                    break;
                case "KinematicBody":
                    def.type = BodyDef.BodyType.KinematicBody;
                    break;
                default:
            }
            String name = (String) obj.getProperties().get("shape");
            switch (name) {
                case "rect":
                    RectangleMapObject rect = (RectangleMapObject) obj;
                    def.position.set(new Vector2(rect.getRectangle().x+rect.getRectangle().width/2, rect.getRectangle().y+rect.getRectangle().height/2));
                    poly_h.setAsBox(rect.getRectangle().width/2,rect.getRectangle().height/2);
                    fdef.shape = poly_h;
                    break;
                case "circle":
                    EllipseMapObject ellipseMapObject = (EllipseMapObject) obj;
                    def.position.set(new Vector2(ellipseMapObject.getEllipse().x+ellipseMapObject.getEllipse().width/2, ellipseMapObject.getEllipse().y+ellipseMapObject.getEllipse().height/2));
                    circle.setRadius(ellipseMapObject.getEllipse().width/2);
                    fdef.shape = circle;
                    break;
                default:
            }

            def.gravityScale = (float) obj.getProperties().get("gravityScale");
            def.awake = (boolean) obj.getProperties().get("awake");
            fdef.restitution = (float) obj.getProperties().get("restitution");
            fdef.density = (float) obj.getProperties().get("density");
            fdef.friction = (float) obj.getProperties().get("friction");

            if (obj.getName() == null)
                world.createBody(def).createFixture(fdef).setUserData(obj.getProperties().get("name"));
            else if (obj.getName().equals("rope"))
                world.createBody(def).createFixture(fdef).setUserData("rope");
            else if (obj.getName().equals("enemy")) {
                enemy = world.createBody(def);
                enemy.createFixture(fdef).setUserData(obj.getProperties().get("name"));

                circle.setRadius(15.0f);
                fdef.shape = circle;
                fdef.isSensor = true;
                enemy.createFixture(fdef).setUserData("annihilator");
            }

        }

        poly_h.dispose();
        circle.dispose();
    }
}
