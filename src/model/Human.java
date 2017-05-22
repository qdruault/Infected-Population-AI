package model;

import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Created by Louis on 20/05/2017.
 */
public class Human implements Steppable {

    public float age; // between 0.0f (birth) and a random float, each step a person has a probability of dying based on its age
    public float health; // between 0.0f (death) and MAX_HEALTH
    public float immunity; // between 0.0f and MAX_IMMUNITY
    public float fertility; // between 0.0f and MAX_FERTILITY
    // satiété
    public float gratification; // between 0f and MAX_GRATIFICATION
    public float rottingPerceptionChance; // between 0.0f and 1.0f


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
    public Human(float i, float a, float f, float g, float r){
        health = Constants.MAX_HEALTH;
        gratification = Constants.MAX_GRATIFICATION;
        immunity = i;
        age = a;
        fertility = f;
        rottingPerceptionChance = r;
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
