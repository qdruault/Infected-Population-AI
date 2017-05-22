package model;

import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Created by Louis on 20/05/2017.
 */
public class Food implements Steppable {

    public float x;
    public float y;

    public float rottingIn = Constants.ROTTING_DURATION;
    public boolean rotten;

    public float nutritionalProvision;
    public float quantity;

    public Food(float q, float n){
        quantity = q;
        nutritionalProvision = n;
    }

    @Override
    public void step(SimState state) {
        Beings beings = (Beings) state;

        // remove if needed
        if (mustDisappear()){
            beings.yard.remove(this);
        }
        // Rotting
        if (!rotten){
            rottingIn -= 5f;
            if (rottingIn <= 0){
                rotten = true;
                nutritionalProvision *= 0.25;
            }
        }
    }

    // Consume food
    public float consume(float q){
        if(quantity > q){
            quantity -= q;
            return q;
        } else {
            q -= quantity;
            quantity = 0;
            return q;
        }
    }

    public Boolean mustDisappear(){
        if (quantity == 0)
            return true;
        else return false;
    }
}
