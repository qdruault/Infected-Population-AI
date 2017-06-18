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

    int maxMedicine = Constants.BASE_MEDICINE;
    int usedMedicineStat = maxMedicine;

    Beings beings;
    
    int famineDuration = 0;
    int shortageDuration = Constants.SHORTAGE_DURATION;
    
    int virusGap = Constants.VIRUS_GAP - 1;
    int starvationGap = Constants.STARVATION_GAP / 2;
    protected int cpt = 0;
    
    @Override
    public void step(SimState state) {
        beings = (Beings) state;
        
        manageFamine();
        manageShortage();
        manageVirus();

        
        generateFood();
        //generateMedicine(usedMedicineStat);
        
        cpt++;
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
    public void generateFood() {
    	// Tous les 5 tours.
        if (cpt % 5 == 0) {
        	int result;
            
            // Si famine
            if (famineDuration != 0) {
            	result = beings.getNbHuman() / 15;
    		} else {
    			if (beings.getNbHuman() > Constants.MAX_NB_HUMAN / 2) {
    				result = beings.getNbHuman() / 4;
				} else {
					result = beings.getNbHuman() / 2;
				}    			
    		}
            Stoppable stoppable;
//          System.out.println("Result food "+result);
            
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
    }


    // Generate a random medicine quantity on the yard
    public void generateMedicine(int max){
        Stoppable stoppable;
        int result = beings.random.nextInt(max + 1);
//        System.out.println("Result medecine "+result);

        for (int i = 0; i < result; i++){
            Int2D pos = beings.freeLocation();
            Medicine medicine = new Medicine(beings.random.nextInt(Constants.MAX_MEDICINE_QUANTITY), beings);
            beings.yard.set(pos.x, pos.y, medicine);
            medicine.setX(pos.x);
            medicine.setY(pos.y);
            stoppable = beings.schedule.scheduleRepeating(medicine);
            medicine.setStoppable(stoppable);
        }
    }
    
    public void famine(){
    	// Rï¿½duction de la nourriture.
    	//usedFoodStat = maxFood / Constants.FAMINE_REDUCTION;
    	System.out.println("Début famine");
        famineDuration = beings.getNbHuman() / 10;
    }
    
    public void medicineShortage(){
    	// Rï¿½duction des medicaments.
    	usedMedicineStat = maxMedicine / Constants.SHORTAGE_REDUCTION;
        shortageDuration = Constants.SHORTAGE_DURATION;
    }
    
    public void restoreMedicine(){
    	// Restauration de la quantitï¿½ de mï¿½dicament normale
    	usedMedicineStat = maxMedicine;
    }
    
    public void manageFamine(){
    	// Si c'est pas la famine
        if (famineDuration == 0) {
        	// On peut la lancer tous les 150 tours
        	if (starvationGap == Constants.STARVATION_GAP ) {
                float faminePossibility = beings.random.nextFloat();
                if (faminePossibility <= Constants.FAMINE_PROBABILITY) {
                    famine();
                }
            } 
        	starvationGap--;
        	if (starvationGap == 0) {
        		starvationGap = Constants.STARVATION_GAP;
			}
		} else {
			// Si c'est la famine, on diminue le temps restant sauf en cas de surpopulation.
			if (beings.getNbHuman() < Constants.MAX_NB_HUMAN) {
				famineDuration--;
			}
            
            if (famineDuration == 0) {
				System.out.println("Fin famine.");
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
    	
        if (virusGap == Constants.VIRUS_GAP || beings.getNbHuman() >= Constants.MAX_NB_HUMAN) {
            float virusPossibility = beings.random.nextFloat();
            if (virusPossibility <= Constants.VIRUS_PROBABILITY) {
                beings.addAgentsVirus();
                virusGap--;
            }
        } else {
        	if (beings.getNbHuman() < Constants.MAX_NB_HUMAN) {
        		virusGap--;
			}
        	
            if (virusGap == 0){
            	virusGap = Constants.VIRUS_GAP;
            }
        }
    }
   
	public int getMaxMedicine() {
		return maxMedicine;
	}

	public void setMaxMedicine(int maxMedicine) {
		this.maxMedicine = maxMedicine;
	}

}
