package com.oh_72.prolit.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.oh_72.prolit.Prolit;
import com.oh_72.prolit.screens.PlayScreen;

public class Log extends Sprite {

    private World world;
    public Body b2body;
    private PlayScreen screen;

    private float radius;
    private float x;
    private float y;
    private float angleV;
    private float angle;

    private Obstacle obstacle;

    private Texture texture;
    private boolean boost;

    public Log(PlayScreen screen, float radius, float x, float y, float angleV){
        this.screen = screen;
        world = screen.getWorld();
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.angleV = angleV;
        obstacle = null;
        angle = 0;
        texture = new Texture("spiral.png");
        setRegion(texture);
        setBounds(0, 0, radius * 2 / Prolit.PPM, radius * 2 / Prolit.PPM);
        define();
        this.setOriginCenter();
    }

    public void define(){
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x / Prolit.PPM, y / Prolit.PPM);
        b2body = world.createBody(bdef);
        b2body.setGravityScale(0);
        b2body.setActive(true);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(radius / Prolit.PPM);
        fdef.filter.categoryBits = Prolit.LOG_BIT;
        fdef.filter.maskBits = Prolit.KNIFE_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void createObstacle(int angle){
        obstacle = new Obstacle(screen, this, angle);
    }

    public void update(float delta){
        angle += angleV;
        angle %= 360;
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRotation(angle);
        if(obstacle != null){
            obstacle.update(delta);
        }
        //b2body.setAwake(true);
    }

    public void setBoost(boolean with){
        boost = with;
        if(with) {
            angleV *= 2;
            if(Prolit.sound) {
                Prolit.assetManager.get("sounds/spin.wav", Sound.class).loop();
            }
        }else{
            angleV /= 2;
            Prolit.assetManager.get("sounds/spin.wav", Sound.class).stop();
        }
    }

    public void setAngleV(float angleV){
        this.angleV = angleV;
    }

    public float getRadius(){
        return radius;
    }

    public float getAngle(){
        return angle;
    }

    public float getAngleV(){
        return angleV;
    }

    public float getAngleForPoint(Vector2 point){

        double radius = getDistanceToPoint(point);

        double x0 = point.x - x;
        double y0 = point.y - y;

        x0 /= radius;
        y0 /= radius;

        Gdx.app.log(Prolit.LOG_TAG, "Point x0 & y0 = " + x0 + " :: " + y0);
        Gdx.app.log(Prolit.LOG_TAG, "Point x & y = " + x + " :: " + y);
        Gdx.app.log(Prolit.LOG_TAG, "Point point.x & point.y = " + point.x + " :: " + point.y);
        Gdx.app.log(Prolit.LOG_TAG, "Point radius = " + radius);

        double alpha = 0;

        if(x0 >= 0 && y0 >= 0){
            alpha = Math.acos(x0) * 180 / Math.PI;
        }
        if(x0 <= 0 && y0 >= 0){
            alpha = Math.acos(x0) * 180 / Math.PI;
        }
        if(x0 <= 0 && y0 <= 0){
            alpha = Math.abs(Math.asin(y0) * 180 / Math.PI) + 180;
        }
        if(x0 >= 0 && y0 <= 0){
            alpha = Math.asin(y0) * 180 / Math.PI + 360;
        }

        Gdx.app.log(Prolit.LOG_TAG, "Angle = " + alpha);
        return (float)alpha;
    }

    public double getDistanceToPoint(Vector2 point){
        return Math.sqrt((x - point.x) * (x - point.x) + (y - point.y) * (y - point.y));
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public void dispose(){
        if(obstacle != null){
            obstacle.dispose();
        }
        texture.dispose();
        world.destroyBody(b2body);
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }
    public void drawObstacle(PolygonSpriteBatch polyBatch){
        if(obstacle != null) {
            obstacle.draw(polyBatch);
        }
    }


}
