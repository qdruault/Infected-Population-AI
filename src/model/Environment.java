package model;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Created by Louis on 01/06/2017.
 */
public class Environment implements Steppable{

    int maxFood = Constants.BASE_FOOD;

    @Override
    public void step(SimState state) {
        Beings beings = (Beings) state;
        requestFood(beings);
    }

    // Génère une quantité aléatoire de nourriture sur la grille
    private void requestFood(Beings beings) {
        beings.generateFood(maxFood);
    }
}
