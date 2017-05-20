package model;

import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Created by Louis on 20/05/2017.
 */
public class Human implements Steppable {

    public float age;
    public float health;
    public float immunity;
    public float fertility;
    // satiété
    public float gratification;


    public enum Gender {
        MALE,
        FEMALE
    };
    public Gender gender;

    public enum Condition {
        SICK,
        FINE
    }
    public Condition condition;




    public float x;
    public float y;


    // Constructor used when the simulation is initialized.
    public Human(float i, float a, float f, float g){
        health = Constants.MAX_HEALTH;
        gratification = Constants.MAX_GRATIFICATION;
        immunity = i;
        age = a;
        fertility = f;
        condition = Condition.FINE;
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
