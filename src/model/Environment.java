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
        System.out.println("Dans l'environnement");

        generateFood(this.getMaxFood());
//        generateMedicine(this.getMaxMedicine());
    }
    
    /**
     * Constructeur
     */
    public Environment(){
    }

    public int getMaxFood() {
		return maxFood;
	}

	public void setMaxFood(int maxFood) {
		this.maxFood = maxFood;
	}

	public int getMaxMedicine() {
		return maxMedicine;
	}

	public void setMaxMedicine(int maxMedicine) {
		this.maxMedicine = maxMedicine;
	}

	// Generate a random food quantity on the yard
    public void generateFood(int max) {
        int result = beings.random.nextInt(max + 1);
//        System.out.println("Result food "+result);
        
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
//        System.out.println("Result medecine "+result);

        for (int i = 0; i < result; i++){
            Int2D pos = beings.getFreeLocation();
            Medicine medicine = new Medicine();
//            TODO : corriger le bug ici (plante et ne met pas de médoc)
//            Peut-être parce qu'il n'y a pas de portrayals pour les médoc?
            beings.yard.set(pos.x, pos.y, medicine);
            medicine.setX(pos.x);
            medicine.setY(pos.y);
        }
    }

}
