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
    
    int starvationGap = Constants.STARVATION_GAP / 2;
    protected int cpt = 0;
    
    /**
     * Constructeur
     * @param beings
     */
    public Environment(Beings beings){
    	this.beings = beings;
    }
    
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
     * Gestion de la famine.
     */
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
			// Si c'est la famine, on diminue le temps restant.
			famineDuration--;
			
            if (famineDuration == 0) {
				System.out.println("Fin famine.");
			}
        }
    	
    }
    
    /**
     * Gestion penurie de medicaments.
     */
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
    
    /**
     * Gestion virus.
     */
    public void manageVirus(){
    	if (cpt % Constants.VIRUS_NB_TOUR == 0) {
    		float ratioHuman = beings.getNbHuman() / ((float)Constants.MAX_NB_HUMAN * 4);
        	
        	// Plus il y a d'humain, plus la proba augmente.
            float virusPossibility = beings.random.nextFloat();
            if (virusPossibility < ratioHuman) {
                beings.addAgentsVirus();
            }
    	}
    }

    /**
     * Generate a random food quantity on the yard
     */
    public void generateFood() {
        if (cpt % Constants.FOOD_NB_TOUR == 0) {
        	int result;
            
            // Si famine
            if (famineDuration != 0) {
            	result = Constants.MIN_FOOD;
    		} else {
    			result = beings.getNbHuman() / 2;
    			if (result > Constants.MAX_FOOD) {
					result = Constants.MAX_FOOD;
				}
    		}
            Stoppable stoppable;
//          System.out.println("Result food "+result);
            
            for (int i = 0; i < result; i++){
                //Int2D pos = beings.getFreeFoodLocation();
            	Int2D pos = beings.freeLocation();
                if (pos != null) {
                	Food food = new Food(beings.random.nextInt(Constants.MAX_NUTRITIONAL_PROVISION), beings.random.nextInt(Constants.MAX_FOOD_QUANTITY), beings);
                    beings.yard.set(pos.x, pos.y, food);
                    food.setX(pos.x);
                    food.setY(pos.y);
                    stoppable = beings.schedule.scheduleRepeating(food);
                    food.setStoppable(stoppable);
				}
            }
		}
    }


    /**
     * Generate a random medicine quantity on the yard
     * @param max
     */
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
    
    /**
     * On lance la famine.
     */
    protected void famine(){
    	// R�duction de la nourriture.
    	//usedFoodStat = maxFood / Constants.FAMINE_REDUCTION;
    	System.out.println("Debut famine");
    	// Plus il y a d'humain, plus elle est longue.
        famineDuration = beings.getNbHuman() / 10;
    }
    
    /**
     * On lance la penurie.
     */
    public void medicineShortage(){
    	// R�duction des medicaments.
    	usedMedicineStat = maxMedicine / Constants.SHORTAGE_REDUCTION;
        shortageDuration = Constants.SHORTAGE_DURATION;
    }
    
    /**
     * On remet le niveau normal de medicaments.
     */
    public void restoreMedicine(){
    	// Restauration de la quantit� de m�dicament normale
    	usedMedicineStat = maxMedicine;
    }
   
	public int getMaxMedicine() {
		return maxMedicine;
	}

	public void setMaxMedicine(int maxMedicine) {
		this.maxMedicine = maxMedicine;
	}

}
