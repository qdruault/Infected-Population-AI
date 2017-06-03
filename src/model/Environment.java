package model;

import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

/**
 * Created by Louis on 01/06/2017.
 */
public class Environment implements Steppable{

    int maxFood = Constants.BASE_FOOD;
    int maxMedicine = Constants.BASE_MEDICINE;

    Beings beings;
    @Override
    public void step(SimState state) {
        beings = (Beings) state;
        generateFood(maxFood);
    }

    // Generate a random food quantity on the yard
    public void generateFood(int max) {
        int result = beings.random.nextInt(max + 1);

        for (int i = 0; i < result; i++){
            Int2D pos = beings.getFreeLocation();
            Food food = new Food(beings.random.nextInt(Constants.MAX_NUTRITIONAL_PROVISION), beings.random.nextInt(Constants.MAX_FOOD_QUANTITY));
            beings.yard.set(pos.x, pos.y, food);
            food.setX(pos.x);
            food.setY(pos.y);
        }
    }

    // Generate a random medicine quantity on the yard
    public void generateMedicine(int max){
        int result = beings.random.nextInt(max + 1);

        for (int i = 0; i < result; i++){
            Int2D pos = beings.getFreeLocation();
            Medicine medicine = new Medicine();
            beings.yard.set(pos.x, pos.y, medicine);
            medicine.setX(pos.x);
            medicine.setY(pos.y);
        }
    }

}
