package model;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import res.values.Constants;
import sim.engine.SimState;

/**
 * Created by Louis on 22/05/2017.
 */
public class Doctor extends Human {

    public int drugStock = Constants.MAX_DRUG_STOCK;
    public float skill; // between 0.0f and 1.0f, probability to heal, cure of vaccinate successfully

    public Doctor(){
    }
    public Doctor(float i, float a, float f, float g, float r, float s){
        super(i,a,f,g,r);
        skill = s;
    }


    @Override
    public void step(SimState state) {
        super.step(state);
        Beings beings = (Beings)state;
    }

    // Restore a few health
    public void heal(Human human ){
        if (human.health + Constants.HEAL_VALUE < Constants.MAX_HEALTH){
            human.health += Constants.HEAL_VALUE;
        } else {
            human.health = Constants.MAX_HEALTH;
        }
        this.drugStock -= Constants.HEAL_CONSUMMATION;
    }
    // Cure a human Disease
    public void cureDisease(Human human){human.condition = Condition.FINE;}
    // Vaccinate a human, increase its immunity
    public void vaccinate(Beings beings, Human human){ human.immunity += Constants.VACCINATE_EFFICIENCY * beings.random.nextFloat(false, false); }

    // Check if enough drugs are available for the operation requested
    public Boolean canHeal(){
        return drugStock > Constants.HEAL_CONSUMMATION;
    }
    public Boolean canCure(){
        return drugStock > Constants.CURE_CONSUMMATION;
    }
    public Boolean canVaccinate(){
        return drugStock > Constants.VACCINATE_CONSUMMATION;
    }

    // Check the success of an operation based on the skill level of the doctor
    public Boolean tryHeal(Beings beings){ return tryOperation(beings, Constants.HEAL_DIFFICULTY); }
    public Boolean tryCure(Beings beings){ return tryOperation(beings, Constants.CURE_DIFFICULTY); }
    public Boolean tryVaccinate(Beings beings){ return tryOperation(beings, Constants.VACCINATE_DIFFICULTY); }
    // The resulting number of the calculation must be superior to the SUCCESS_DIFFICULTY for the operation to succeed
    // The resulting number varies between 0.5f and 1f
    // The resulting number depends on the level of skill of the doctor, the difficulty of the operation and a random number between 0f (open) and 1f (open)
    public Boolean tryOperation(Beings beings, int operationSuccessLevel){ return (0.5f + (Math.pow(skill * 0.5f, operationSuccessLevel) * beings.random.nextFloat(false, false)) > Constants.SUCCESS_DIFFICULTY); }


}
