package model;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Created by Louis on 20/05/2017.
 */
public class Human implements Steppable {

    public float immunity;
    public float healthPoints;
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



    @Override
    public void step(SimState state) {
        Beings beings = (Beings) state;
        if (mustDie()){
            beings.yard.remove(this);
        }
    }

    public Boolean mustDie(){
        if (healthPoints == 0)
            return true;
        else return false;
    }
}
