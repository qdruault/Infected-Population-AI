package model;

import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Created by Louis on 20/05/2017.
 */
public class Human implements Steppable {

    public float immunity;
    public float health;
    public float age;
    public float fertility;
    public enum gender {
        MALE,
        FEMALE
    };
    // satiété
    public float gratification;
    public enum condition {
        SICK,
        FINE
    }

    public float x;
    public float y;


    public Human(){
        health = Constants.MAX_HEALTH;
        gratification = Constants.MAX_GRATIFICATION;
    }

    @Override
    public void step(SimState state) {
        Beings beings = (Beings) state;
        if (mustDie()){
            beings.yard.remove(this);
        }
    }

    public Boolean mustDie(){
        if (health == 0)
            return true;
        else return false;
    }
}
