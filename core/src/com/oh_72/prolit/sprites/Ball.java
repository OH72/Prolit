package com.oh_72.prolit.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.oh_72.prolit.Prolit;
import com.oh_72.prolit.screens.PlayScreen;

public class Ball extends Sprite {

    private final static float VELOCITY = 1000;

    private PlayScreen screen;
    private World world;

    public Body b2body;

    private Animation<TextureRegion> animation;

    private com.oh_72.prolit.sprites.Log log;
    private float angle;
    private float logAngleV;
    private float currentA; // current angle

    private Vector2 velocity;

    private boolean onLog;
    private boolean toThrow;
    private boolean dead;
    private boolean toMenu;

    private TextureRegion spriteSheet;
    private TextureRegion sprite;

    private Animation<TextureRegion> deathAnimation;

    private float animTimer;

    private float maxSaveY;

    public Ball(PlayScreen screen, Log log, TextureRegion sprite, TextureAtlas atlas){
        this.screen = screen;
        this.log = log;
        this.sprite = sprite;

        angle = 0;
        animTimer = 0;
        world = screen.getWorld();

        maxSaveY = Prolit.V_HEIGHT / 2 / Prolit.PPM;
        setBounds(0, 0, Prolit.KNIFE_R * 2 / Prolit.PPM, Prolit.KNIFE_R * 2 / Prolit.PPM);

        velocity = new Vector2(0, 0);
        onLog = true;
        toThrow = false;
        dead = false;
        toMenu = false;

        spriteSheet = atlas.findRegion("deathAnimation");
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 0; i < 4; i++){
            frames.add( new TextureRegion(spriteSheet, i * 327, 0, 327, 327));
        }
        deathAnimation = new Animation<TextureRegion>(0.1f, frames);

        define();
        calculatePoint();
    }

    private void define() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(getX(), getY()));
        bodyDef.type = BodyDef.BodyType.DynamicBody;


        b2body = world.createBody(bodyDef);
        b2body.setGravityScale(0);
        b2body.setLinearVelocity(velocity);

        FixtureDef fixtureDef = new FixtureDef();
        //fixtureDef
//        PolygonShape shape = new PolygonShape();
//        Vector2 vertices[] = new Vector2[12];
//        vertices[0] = new Vector2(0 / Prolit.PPM, 0 / Prolit.PPM);
//        vertices[1] = new Vector2(30 / Prolit.PPM, 0 / Prolit.PPM);
//        vertices[2] = new Vector2(20 / Prolit.PPM, 90 / Prolit.PPM);
//        vertices[3] = new Vector2(50 / Prolit.PPM, 93 / Prolit.PPM);
//        vertices[4] = new Vector2(42 / Prolit.PPM, 150 / Prolit.PPM);
//        vertices[5] = new Vector2(30 / Prolit.PPM, 185 / Prolit.PPM);
//        vertices[6] = new Vector2(21 / Prolit.PPM, 200 / Prolit.PPM);
//        vertices[7] = new Vector2(13 / Prolit.PPM, 187 / Prolit.PPM);
//        vertices[8] = new Vector2(8 / Prolit.PPM, 174 / Prolit.PPM);
//        vertices[9] = new Vector2(4 / Prolit.PPM, 150 / Prolit.PPM);
//        vertices[10] = new Vector2(4 / Prolit.PPM, 89 / Prolit.PPM);
//        vertices[11] = new Vector2(0 / Prolit.PPM, 89 / Prolit.PPM);
        CircleShape shape = new CircleShape();
        shape.setRadius(Prolit.KNIFE_R / Prolit.PPM);
        fixtureDef.restitution = 0.5f;
        fixtureDef.filter.categoryBits = Prolit.KNIFE_BIT;
        fixtureDef.filter.maskBits = Prolit.LOG_BIT |
            Prolit.OBSTACLE_BIT;
        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);
    }

    private void calculatePoint(){
        if(toThrow){
            throwKnife();
            toThrow = false;
        }
        if(onLog) {
            float logX = log.getX();
            float logY = log.getY();
            float logR = log.getRadius() + Prolit.KNIFE_R + Prolit.KNIFE_PAD;


            currentA = angle + log.getAngle();
            float logA = (float) (currentA * Math.PI / 180);
            logAngleV = log.getAngleV();
            //currentA -= 90;

            float x = (float) (Math.cos(logA) * logR + logX) / Prolit.PPM;
            float y = (float) (Math.sin(logA) * logR + logY) / Prolit.PPM;


            setOrigin(getWidth() / 2, getHeight() / 2);
            //setPosition(getWidth() / 2, getHeight() / 2);
            rotate(logAngleV);
            //this.setTexture(texture);
            //this.setRegion(new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight()));
            //Gdx.app.log(Prolit.LOG_TAG, this.getX() + " " + getY());
            b2body.setTransform(x, y, (float) (currentA * Math.PI / 180));
        }
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        Gdx.app.log(Prolit.LOG_TAG, "Knife angle = " + currentA);
    }

    public void setNewLog(com.oh_72.prolit.sprites.Log log){
        this.log = log;
        this.angle = log.getAngleForPoint(new Vector2(getX() * Prolit.PPM, getY() * Prolit.PPM)) - log.getAngle();
        onLog = true;
        if((log.getY() + Prolit.KNIFE_R * 2) / Prolit.PPM > maxSaveY){
            maxSaveY = (log.getY() + Prolit.KNIFE_R * 2 + Prolit.KNIFE_PAD) / Prolit.PPM;
        }
        velocity.set(0, 0);
        b2body.setLinearVelocity(velocity);
        calculatePoint();
    }

    public void setThrow(){
        toThrow = true;
    }

    public void throwKnife(){
        if(onLog) {
            if(Prolit.sound) {
                Prolit.assetManager.get("sounds/jump.wav", Sound.class).play();
            }
            onLog = false;
            float angle = (float) ((log.getAngle() + this.angle) * Math.PI) / 180;
            float x = (float) (Math.cos(angle) * VELOCITY);
            float y = (float) (Math.sin(angle) * VELOCITY);
            velocity.set(x / Prolit.PPM, y / Prolit.PPM);
            b2body.setLinearVelocity(velocity);
            //body.setLinearVelocity(velocity.x, velocity.y);
            //body.setAwake(true);
            //b2body.applyLinearImpulse(new Vector2(x, y), b2body.getWorldCenter(), true);
            //body.setActive(true);
        }
    }

    public void update(float delta){
        if(!dead) {
            checkBounds();
            if(dead) {
                if(Prolit.sound) {
                    Prolit.assetManager.get("sounds/death.wav", Sound.class).play();
                }
                animTimer = 0;
                //setBounds(0, 0, Prolit.KNIFE_R * 2 / Prolit.PPM, Prolit.KNIFE_R * 2 / Prolit.PPM);
                setRotation(0);
            }
            calculatePoint();
            //setRegion(new TextureRegion(spriteSheet));
            //setTexture(spriteSheet);
            if(!onLog) {
                animTimer += delta;
            }

            setRegion(sprite);
            Gdx.app.log(Prolit.LOG_TAG, "Sprite position = " + getX() + " :: " + getY());
        }else{
            setRegion(deathAnimation.getKeyFrame(animTimer));
            if(deathAnimation.isAnimationFinished(animTimer)){
                toMenu = true;
            }
            animTimer += delta;
        }
    }

    private void checkBounds(){
        if(getX() < (0 - Prolit.KNIFE_R * 2) / Prolit.PPM){
            dead = true;
        }
        if(getX() > (Prolit.V_WIDTH  + Prolit.KNIFE_R * 2) / Prolit.PPM){
            dead = true;
        }
        if(getY() < (0 - Prolit.KNIFE_R * 2) / Prolit.PPM + screen.getGameCam().position.y - Prolit.V_HEIGHT / 2 / Prolit.PPM){
            dead = true;
        }
        if(getY() > maxSaveY + Prolit.V_HEIGHT / 2 / Prolit.PPM  + (Prolit.KNIFE_R * 2) / Prolit.PPM){
            dead = true;
        }
    }

    public void timeToDie(){
        if(Prolit.sound) {
            Prolit.assetManager.get("sounds/death.wav", Sound.class).play();
        }
        dead = true;
        animTimer = 0;
        setRotation(0);
    }

    public boolean isOnLog(){
        return onLog;
    }

    public float getMax(){
        return maxSaveY;
    }

    public boolean isToMenu(){
        return toMenu;
    }

    public Log getLog(){
        return log;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }
}
