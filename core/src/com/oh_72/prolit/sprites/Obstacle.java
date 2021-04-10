package com.oh_72.prolit.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.oh_72.prolit.Prolit;
import com.oh_72.prolit.screens.PlayScreen;

import java.util.ArrayList;
import java.util.Random;

public class Obstacle extends Sprite implements Disposable {

    private PlayScreen screen;
    private World world;

    public Body b2body;

    private Log log;
    private float angle;

    private float turnAngle;
    private Random random;

    private TextureRegion texture;
    private PolygonRegion polygonRegion;
    private PolygonSprite sprite;

    private ShapeRenderer shapeRenderer;
    private float currentA;

    private float constK;

    public Obstacle(PlayScreen screen, Log log, float angle, TextureAtlas atlas){
        this.screen = screen;
        this.world = screen.getWorld();
        this.log = log;
        this.angle = angle;

        constK = 290.f / 250.f;

//        Gdx.app.log(Prolit.LOG_TAG, "create obstacle");
        texture = atlas.findRegion("obstacle");
//        Gdx.app.log(Prolit.LOG_TAG, "texture is setted");
        random = new Random();
        turnAngle = random.nextFloat() * 360;
        setRegion(texture);


        define();
        calculatePoint();

        setBounds(0, 0, (log.getRadius() + Prolit.OBSTACLE_R) * 2 / Prolit.PPM,
                (log.getRadius() + Prolit.OBSTACLE_R) * 2 / Prolit.PPM);

        shapeRenderer = new ShapeRenderer();
    }

    private void calculatePoint(){
        currentA = log.getAngle() + turnAngle;

        sprite.rotate(log.getAngleV());

        float x = log.getX() / Prolit.PPM;
        float y = log.getY() / Prolit.PPM;

        b2body.setTransform(x, y, (float) (currentA * Math.PI / 180));
        //setPosition(b2body.getPosition().x, b2body.getPosition().y);
    }

    private void define(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bodyDef);
        b2body.setGravityScale(0);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        Vector2 vertices[] = getArc(angle, log.getRadius() / Prolit.PPM + Prolit.OBSTACLE_R / Prolit.PPM);
        shape.set(vertices);
        Gdx.app.log(Prolit.LOG_TAG, "count vertex: " + shape.getVertexCount());
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Prolit.OBSTACLE_BIT;
        fixtureDef.filter.maskBits = Prolit.KNIFE_BIT;

        b2body.createFixture(fixtureDef).setUserData(this);
    }

    private Vector2[] getArc(float angle, float radius){
        radius *= Prolit.PPM;
        Gdx.app.log(Prolit.LOG_TAG, "Arc radius = " + radius);
        ArrayList<Vector2> list = new ArrayList<Vector2>();
        list.add(new Vector2(0, 0));

        double first = 90 - angle / 2;
        double second = first + angle;
        double current = first;
        double step = angle / 6;

        Gdx.app.log(Prolit.LOG_TAG, "angle range = " + first + " :: " + second);

        Vector2 crop1 = new Vector2(0, radius);
        Vector2 crop2 = new Vector2(0, radius);

        while (current <= second){
            float x = (float) (Math.cos(current * Math.PI / 180) * radius);
            float y = (float) (Math.sin(current * Math.PI / 180) * radius);

            if(x < crop1.x){
                crop1.x = x;
            }
            if(x > crop2.x){
                crop2.x = x;
            }
            if(y < crop2.y){
                crop2.y = y;
            }

            list.add(new Vector2(x , y ));
            current += step;
        }

        Vector2 vertices[] = new Vector2[list.size()];
        float points[] = new float[list.size() * 2];

        float constW = texture.getRegionWidth() / ((log.getRadius() + Prolit.OBSTACLE_R) * 2 );

        for(int i = 0; i < list.size(); i++){
            vertices[i] = new Vector2(list.get(i).x / Prolit.PPM, list.get(i).y / Prolit.PPM);
            points[i * 2] = (list.get(i).x + radius) * constW;
            points[i * 2 + 1] = (list.get(i).y + radius) * constW;
            Gdx.app.log(Prolit.LOG_TAG, "Arc point #" + i + " = " + vertices[i].x + " :: " + vertices[i].y);
        }

        polygonRegion = new PolygonRegion(new TextureRegion(texture), points, new EarClippingTriangulator().computeTriangles(points).toArray());

        sprite = new PolygonSprite(polygonRegion);

        float rad = log.getRadius() * constK;

        Gdx.app.log(Prolit.LOG_TAG, "Obstacle Rad = " + rad + " :: " + (log.getRadius() + Prolit.OBSTACLE_R) + " :: " + constK);

        sprite.setBounds(0, 0, (rad) / Prolit.PPM * 2, (rad)  / Prolit.PPM * 2);
        sprite.setPosition(log.getX() / Prolit.PPM - (rad) / Prolit.PPM,
                log.getY() / Prolit.PPM - (rad) / Prolit.PPM);
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        sprite.setRotation(turnAngle);

        //TextureRegion region = new TextureRegion(texture, vertices);

        return vertices;
    }

    public void update(float delta){
        calculatePoint();
        Gdx.app.log(Prolit.LOG_TAG, "width = " + texture.getRegionWidth());
        setPosition(log.getX() / Prolit.PPM, log.getY() / Prolit.PPM);

    }

    public void draw(PolygonSpriteBatch batch) {
        //super.draw(batch);
        this.sprite.draw(batch);
        //drawLine();
    }

    private void drawLine(){
        Gdx.gl.glLineWidth(5);
        shapeRenderer.setProjectionMatrix(screen.getGameCam().combined);
        shapeRenderer.begin((ShapeRenderer.ShapeType.Filled));
        shapeRenderer.arc(log.getX() / Prolit.PPM, log.getY() / Prolit.PPM,
                log.getRadius() / Prolit.PPM + Prolit.OBSTACLE_R / Prolit.PPM,
                currentA + 90 - angle / 2, angle, 10);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        world.destroyBody(b2body);
    }
}
