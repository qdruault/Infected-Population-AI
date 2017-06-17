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
    int shortageDuration = Constants.SHORTAGE_DURATION;
    
    int virusGap = Constants.VIRUS_GAP-1;
    
    @Override
    public void step(SimState state) {
        beings = (Beings) state;

        manageFamine();
        manageShortage();
        manageVirus();

        generateFood(usedFoodStat);
        generateMedicine(usedMedicineStat);
    }
    
    /**
     * Constructeur
     * @param beings
     */
    public Environment(Beings beings){
    	this.beings = beings;
    }

    /**
     * Generate a random food quantity on the yard
     */
    public void generateFood(int max) {
        int result = beings.random.nextInt(max + 1);
        Stoppable stoppable;
//        System.out.println("Result food "+result);
        
        for (int i = 0; i < result; i++){
            Int2D pos = beings.freeLocation();
            Food food = new Food(beings.random.nextInt(Constants.MAX_NUTRITIONAL_PROVISION), beings.random.nextInt(Constants.MAX_FOOD_QUANTITY), beings);
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
            Int2D pos = beings.freeLocation();
            Medicine medicine = new Medicine(beings.random.nextInt(Constants.MAX_MEDICINE_QUANTITY));
            beings.yard.set(pos.x, pos.y, medicine);
            medicine.setX(pos.x);
            medicine.setY(pos.y);
            stoppable = beings.schedule.scheduleRepeating(medicine);
            medicine.setStoppable(stoppable);
        }
    }
    
    public void famine(){
    	// Rï¿½duction de la nourriture.
    	usedFoodStat = maxFood / Constants.FAMINE_REDUCTION;
        famineDuration = Constants.FAMINE_DURATION;
    }
    
    public void restoreFood(){
    	// Restauration de la quantitï¿½ de nourriture normale
    	usedFoodStat = maxFood;
    }
    
    public void medicineShortage(){
    	// Rï¿½duction des medicaments.
    	usedMedicineStat = maxMedicine / Constants.SHORTAGE_REDUCTION;
        shortageDuration = Constants.SHORTAGE_DURATION;
    }
    
    public void restoreMedicine(){
    	// Restauration de la quantitï¿½ de médicament normale
    	usedMedicineStat = maxMedicine;
    }
    
    public void manageFamine(){
        if (usedFoodStat == maxFood) {
            float faminePossibility = beings.random.nextFloat();
            if (faminePossibility <= Constants.FAMINE_PROBABILITY) {
                famine();
            }
        } else {
            famineDuration--;
            if (famineDuration == 0){
                restoreFood();
            }
        }
    }
    
    public void manageShortage(){
        if (usedMedicineStat == maxMedicine) {
            float shortagePossibility = beings.random.nextFloat();
            if (shortagePossibility <= Constants.SHORTAGE_PROBABILITY) {
                medicineShortage();
            }
        } else {
            shortageDuration--;
            if (shortageDuration == 0){
                restoreMedicine();
            }
        }
    }
    
    public void manageVirus(){
        if (virusGap==Constants.VIRUS_GAP) {
            float virusPossibility = beings.random.nextFloat();
            if (virusPossibility <= Constants.VIRUS_PROBABILITY) {
                beings.addAgentsVirus();
                virusGap--;
            }
        } else {
        	virusGap--;
            if (virusGap == 0){
            	virusGap = Constants.VIRUS_GAP;
            }
        }
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
