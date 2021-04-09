package com.oh_72.prolit.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.oh_72.prolit.Prolit;
import com.oh_72.prolit.screens.PlayScreen;

import java.util.ArrayList;
import java.util.Random;

public class Logs implements Disposable {

    private final static int COUNT_COEF = 10000;
    private final static float SPEED_COEF = 1 / 2500;

    private PlayScreen screen;

    private ArrayList<Log> logs;
    private long maxHeight;
    private int nowCount;
    private int maxAngle;

    private int tempK = 1;

    private ArrayList<Integer> counts;

    private Random random;

    private long startHeight;

    public  Logs(PlayScreen screen){
        this.screen = screen;

        nowCount = 5;
        maxAngle = 0;

        logs = new ArrayList<Log>();
        logs.add(new Log(screen, 200 * Prolit.K, Prolit.V_WIDTH / 2, Prolit.V_HEIGHT / 2, 3));
        maxHeight = (long) (logs.get(logs.size() - 1).getY() + logs.get(logs.size() - 1).getRadius()) + Prolit.KNIFE_R * 2 + Prolit.KNIFE_PAD + Prolit.OBSTACLE_R;
        Gdx.app.log(Prolit.LOG_TAG, "maxHeightStart = " + maxHeight);

        counts = new ArrayList<Integer>();

        counts.add(nowCount);
        counts.add(nowCount);
        counts.add(nowCount);
        counts.add(nowCount);

        random = new Random();

        counts.set(0, generateNewLogs(counts.get(0) - 1) + 1);
        counts.set(1, generateNewLogs(counts.get(1)));
        counts.set(2, generateNewLogs(counts.get(2)));
        counts.set(3, generateNewLogs(counts.get(3)));

    }

    private int generateNewLogs(int count){
        tempK *= -1;

        long nextMax = maxHeight;

        int newCount = count;

        int again = 60;

        for(int i = 0; i < count; i++){

            long y = 0;
            int x = 0;
            int radius = 0;

            int iteration = 0;

            do {
                Gdx.app.log(Prolit.LOG_TAG, "#bug Try spawn new Log");
                if(iteration > again){
                    break;
                }
                iteration++;
                radius = (int) (random.nextInt(Prolit.MAX_LOG_R - Prolit.MIN_LOG_R) + Prolit.MIN_LOG_R);
                y = random.nextInt(Prolit.V_HEIGHT / 2) + maxHeight + radius;
                int offset = Prolit.KNIFE_R * 2 + Prolit.KNIFE_PAD + radius;
                x = random.nextInt(Prolit.V_WIDTH - offset * 2) + offset;
            }while(!isFree(x, y, radius, i));

            if(iteration > again){
                newCount--;
                continue;
            }

            float velocity = maxHeight * SPEED_COEF;

            if(velocity < 2){
                velocity = 2;
            }

            if(velocity > 8){
                velocity = 8;
            }

            if(random.nextInt(2) == 0) {
                velocity *= -1;
            }

            logs.add(new Log(screen, radius, x, y, velocity));

            if(random.nextInt(nowCount) <= 2 && maxAngle > 20){
                Gdx.app.log(Prolit.LOG_TAG, "new obstacle");
                logs.get(logs.size() - 1).createObstacle(random.nextInt(maxAngle - 20) + 20);
            }

            if(y + radius > nextMax){
                nextMax = y + radius;
            }
        }

        maxHeight = nextMax + Prolit.KNIFE_R * 2 + Prolit.KNIFE_PAD + Prolit.OBSTACLE_R;
        return newCount;
    }

    private boolean isFree(int x, long y, int radius, int created){

        for(int i = 0; i < created; i++){
            Log log = logs.get(logs.size() - i - 1);
            double distance = log.getDistanceToPoint(new Vector2(x, y));

            if(distance < radius + log.getRadius() + Prolit.KNIFE_R * 2 + Prolit.KNIFE_PAD * 2 + Prolit.OBSTACLE_R){
                Gdx.app.log(Prolit.LOG_TAG, "so clear");
                return false;
            }
        }
        return true;
    }

    public ArrayList<Log> getLogs(){
        return logs;
    }

    public void update(float delta){
        for(Log log : logs){
            log.update(delta);
        }
    }

    public void madeNewLogs(float camY){
        camY -= (Prolit.V_HEIGHT / Prolit.PPM) / 2;
        Gdx.app.log(Prolit.LOG_TAG, "what is higher: " +
                (logs.get(counts.get(0) - 1).getY() +
                        logs.get(counts.get(0) - 1).getRadius() * 2) / Prolit.PPM +
                " :: " + camY);
        if(logs.get(counts.get(0)).getY() / Prolit.PPM < camY) {
            nowCount = calculateCount();
            maxAngle = calculateAngle();
            Gdx.app.log(Prolit.LOG_TAG, "time to make something new and count = " + counts.get(3) + " :: " + maxAngle);
            for (int i = 0; i < counts.get(0); i++) {
                logs.get(0).define();
                logs.remove(0);
            }
            counts.remove(0);
            int addCount = nowCount + random.nextInt(4) - 2;
            if(addCount < 0){
                addCount = 1;
            }
            counts.add(addCount);
            counts.set(3, generateNewLogs(counts.get(3)));
        }
    }

    private int calculateCount(){
        int score = (int) (maxHeight / Prolit.PPM);
        if(score <= 200){return 5;}
        if(score <= 300){return 4;}
        if(score <= 400){return 3;}
        if(score <= 500){return 2;}
        return 1;
    }

    private int calculateAngle(){
        int score = (int) (maxHeight / Prolit.PPM);
        if(score <= 50){return 30;}
        if(score <= 100){return 60;}
        if(score <= 150){return 80;}
        if(score <= 200){return 110;}
        if(score <= 250){return 140;}
        if(score <= 300){return 170;}
        if(score <= 400){return 200;}
        if(score <= 500){return 250;}
        return 1;
    }

    public void draw(Batch batch){
        for(Log log : logs){
            log.draw(batch);
        }
    }

    public void drawObstacle(PolygonSpriteBatch polyBatch){
        for(Log log : logs){
            log.drawObstacle(polyBatch);
        }
    }

    @Override
    public void dispose() {
        for(Log log : logs){
            log.dispose();
        }
    }
}
