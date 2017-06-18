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

        if (mustDisappear()){
            System.out.println("Medicine  is disappearing");
            beings.yard.set(getX(), getY(), null);
            stoppable.stop();
        }
    }

    public Medicine(int _quantity, Beings beings){
        quantity = _quantity;
        this.beings = beings;
        beings.increaseNbMedicine(quantity);
    }

    public int consume(int q){
        if(quantity > q){
            quantity -= q;
            beings.decreaseNbMedicine(q);
            return q;
        } else {
            q = quantity;
            quantity = 0;
            beings.decreaseNbMedicine(q);
            return q;
        }
    }

    public Boolean mustDisappear(){
        return quantity == 0;
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
    public void setQuantity(int q) { this.quantity = q; }
    public int getQuantity() { return this.quantity; }

}
