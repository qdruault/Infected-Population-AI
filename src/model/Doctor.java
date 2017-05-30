package model;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import res.values.Constants;
import sim.engine.SimState;

/**
 * Created by Louis on 22/05/2017.
 */
public class Doctor extends Human {

	private static final long serialVersionUID = 1L;
	// Stock de médicaments.
	private int drugStock;
    // Facilité à soigner les gens (compétence du medecin)
	private int skill;

	public Doctor() {
		super();
	}

	public Doctor(int immunity, int fertility, Gender gender, Condition condition, int skill) {
		super(immunity, fertility, gender, condition);
		this.skill = skill;
		this.drugStock = Constants.MAX_DRUG_STOCK;
	}

	@Override
    public void step(SimState state) {
        super.step(state);
        Beings beings = (Beings)state;
    }

    /**
     * Soigne un peu
     * @param human
     */
    public void heal(Human human ){
        if (human.getHealth() + Constants.HEAL_VALUE < Constants.MAX_HEALTH){
            human.addHealth(Constants.HEAL_VALUE);
        } else {
            human.setHealth(Constants.MAX_HEALTH);
        }
        this.drugStock -= Constants.HEAL_CONSUMMATION;
    }
    
    /**
     * Soigne un Humain.
     * @param human
     */
    public void cureDisease(Human human) {
    	human.setCondition(Condition.FINE);
    }
    
    /**
     * Vacine un humain, augmente son immunité.
     * @param beings
     * @param human
     */
    public void vaccinate(Beings beings, Human human){ 
    	//human.addImmunity(Constants.VACCINATE_EFFICIENCY * ); 
    }

    // Check if enough drugs are available for the operation requested
    public Boolean canHeal(){
        return drugStock > Constants.HEAL_CONSUMMATION;
    }
    public Boolean canCure(){
        return drugStock > Constants.CURE_CONSUMMATION;
    }
    public Boolean canVaccinate(){
        return drugStock > Constants.VACCINATE_CONSUMMATION;
    }

    // Check the success of an operation based on the skill level of the doctor
    public Boolean tryHeal(Beings beings){ 
    	return tryOperation(beings, Constants.HEAL_DIFFICULTY); 
    }
    public Boolean tryCure(Beings beings) {
    	return tryOperation(beings, Constants.CURE_DIFFICULTY); 
    }
    public Boolean tryVaccinate(Beings beings){ 
    	return tryOperation(beings, Constants.VACCINATE_DIFFICULTY); 
    }
    // The resulting number of the calculation must be superior to the SUCCESS_DIFFICULTY for the operation to succeed
    // The resulting number varies between 0.5f and 1f
    // The resulting number depends on the level of skill of the doctor, the difficulty of the operation and a random number between 0f (open) and 1f (open)
    public Boolean tryOperation(Beings beings, int operationSuccessLevel){
    	// A refaire avec des int.
    	return (0.5f + (Math.pow(skill * 0.5f, operationSuccessLevel) * beings.random.nextFloat(false, false)) > Constants.SUCCESS_DIFFICULTY); 
    }


}
