package com.oh_72.prolit.tools;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.oh_72.prolit.Prolit;
import com.oh_72.prolit.sprites.Ball;
import com.oh_72.prolit.sprites.Log;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int cDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        switch (cDef){
            case Prolit.LOG_BIT | Prolit.KNIFE_BIT:
                Gdx.app.log(Prolit.LOG_TAG, "COLLIDE KNIFE & LOG");
                if(fixtureA.getFilterData().categoryBits == Prolit.KNIFE_BIT){
                    ((Ball)fixtureA.getUserData()).setNewLog((Log) fixtureB.getUserData());
                }else{
                    ((Ball)fixtureB.getUserData()).setNewLog((Log)fixtureA.getUserData());
                }
                break;
            case Prolit.OBSTACLE_BIT | Prolit.KNIFE_BIT:
                Gdx.app.log(Prolit.LOG_TAG, "COLLIDE KNIFE & OBSTACLE");
                if(fixtureA.getFilterData().categoryBits == Prolit.KNIFE_BIT){
                    ((Ball)fixtureA.getUserData()).timeToDie();
                }else{
                    ((Ball)fixtureB.getUserData()).timeToDie();
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
