package model;

import java.util.concurrent.ThreadLocalRandom;

import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Int2D;

/**
 * Created by Louis on 01/06/2017.
 */
public class Environment implements Steppable{

    int maxFood = Constants.BASE_FOOD;
    int maxMedicine = Constants.BASE_MEDICINE;
    int usedFoodStat = maxFood;
    int usedMedicineStat = maxMedicine;

    Beings beings;
    
    int famineDuration = Constants.FAMINE_DURATION;
    
    @Override
    public void step(SimState state) {
        beings = (Beings) state;
        System.out.println("Dans l'environnement");
        
        // 1 chance sur 3
        int faminePossibility = ThreadLocalRandom.current().nextInt(0, 3);
        if (faminePossibility == 0){
        	famine();
        }

        
        generateFood(usedFoodStat);
//        generateMedicine(this.getMaxMedicine());
        
        
        famineDuration--;
        if (famineDuration == 0){
        	restoreFood();
        	famineDuration = Constants.FAMINE_DURATION;
        }
    }
    
    /**
     * Constructeur
     */
    public Environment(){
    }

    /**
     * Generate a random food quantity on the yard
     */
    public void generateFood(int max) {
        int result = beings.random.nextInt(max + 1);
        Stoppable stoppable;
//        System.out.println("Result food "+result);
        
        for (int i = 0; i < result; i++){
            Int2D pos = beings.getFreeLocation();
            Food food = new Food(beings.random.nextInt(Constants.MAX_NUTRITIONAL_PROVISION), beings.random.nextInt(Constants.MAX_FOOD_QUANTITY));
            beings.yard.set(pos.x, pos.y, food);
            food.setX(pos.x);
            food.setY(pos.y);
            stoppable = beings.schedule.scheduleRepeating(food);
            food.setStoppable(stoppable);
        }
    }


    // Generate a random medicine quantity on the yard
    public void generateMedicine(int max){
        Stoppable stoppable;
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
            stoppable = beings.schedule.scheduleRepeating(medicine);
            medicine.setStoppable(stoppable);
        }
    }
    
    public void famine(){
    	// R�duction de la nourriture.
    	usedFoodStat = 1;
    }
    
    public void restoreFood(){
    	// Restauration de la quantit� de nourriture normale
    	usedFoodStat = maxFood;
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

}
