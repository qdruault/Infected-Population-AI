package model;

import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

/**
 * Created by Louis on 03/06/2017.
 */
public class Medicine implements Steppable {

    Beings beings;
    int quantity;
    private Stoppable stoppable;

    private int x;
    private int y;
    public void setStoppable(Stoppable stoppable){ this.stoppable = stoppable; }

    @Override
    public void step(SimState simState) {
        beings = (Beings) simState;
    }

    public Medicine(){
//    	System.out.println(" un medicine de généré ");
        quantity = beings.random.nextInt(Constants.MAX_MEDICINE_QUANTITY);
    }

    // Getters ans setters
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }

}
