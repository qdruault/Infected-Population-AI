package model;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import res.values.Constants;
import sim.engine.SimState;
import model.Human;

/**
 * Created by Louis on 22/05/2017.
 */
public class Doctor extends Human {

	private static final long serialVersionUID = 1L;
	// Stock de m�dicaments.
	private int drugStock;
    // Facilit� � soigner les gens (comp�tence du medecin)
	private int skill;
	private List<Human> humansToHelp;
	private Beings beings;

	public Doctor() {
		super();
	}

	public Doctor(int immunity, int fertility, Gender gender, Condition condition, int vision int skill) {
		super(immunity, fertility, gender, condition, vision);
		this.skill = skill;
		this.drugStock = Constants.MAX_DRUG_STOCK;
		this.humansToHelp=new ArrayList<Human>(); //initialize the list to empty
	}

	@Override
    public void step(SimState state) {
	    //TODO exclusive strategy of a doctor which supports his humans needs
        //if sick --> heal himself
        //if weak immunity --> vaccinate himself
        //if humansToHelp --> help human
        //else --> move to human
        //++ consider his human needs
//        super.step(state);
        beings = (Beings)state;
    }

    /**
     * Decide to heal himself
     * @param human
     * @return true if successed, false otherwise
     */
    public boolean vaccinateMyself(){
        if (canVaccinate()){
            return tryVaccinate(this);
        }
    }

    /**
     * Decide to heal himself
     * @param human
     * @return true if successed, false otherwise
     */
    public boolean healMyself(){
        if (canHeal()) {
            feelBetter=tryHeal(this);
        }
        if(!hasHelped){
            if (canCure())
                feelBetter = tryCure(this);
        }
        return feelBetter;
    }


    /**
     * Decide which one to cure
     * @param human
     * @return true if somebody got cured; false otherwise
     */
    public boolean helpHumans(){
        int minHealth= Constants.MAX_HEALTH;
        Human sickestHuman, fineHuman;
        int nbToVaccinate = 0;
        boolean hasHelped;
        for (Human human : humansToHelp){
            //look for the sickest human
            if (human.getCondition == Condition.SICK){
                if (human.getHealth()<minHealth) {
                    sickestHuman=human;
                    minHealth=human.getHealth();
                }
            }
            else {
                nbToVaccinate++;
                fineHuman=human;
            }
        }
        if (minHealth<=10 || nbToVaccinate==0){
            //heal or cure the sickest human
            if (canHeal()) {
                hasHelped=tryHeal(sickestHuman);
            }
            if(!hasHelped){
                if (canCure())
                    hasHelped = tryCure(sickestHuman);
            }
        }
        if((minHealth>10 || !hasHelped) && nbToVaccinate!=0){
            //vaccinate a fine human
            if (canVaccinate()){
                hasHelped=tryVaccinate(fineHuman);
            }
        }
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
        human.addImmunity(Constants.IMPROVE_IMMUNITY_HEALED)
        if (human!=this){
            //if the doctor is not healing himself, get more immunity
            this.immunity += Constants.IMPROVE_IMMUNITY_DOCTOR_1;
        }
        this.drugStock -= Constants.HEAL_CONSUMMATION;
        //get more immunity when in contact with sick people
    }
    
    /**
     * Soigne un Humain.
     * @param human
     */
    public void cureDisease(Human human) {
    	human.setCondition(Condition.FINE);
        human.addImmunity(Constants.IMPROVE_IMMUNITY_CURED)
        if (human!=this){
            //if the doctor is not healing himself, get more immunity
            this.immunity += Constants.IMPROVE_IMMUNITY_DOCTOR_2;
        }
    }
    
    /**
     * Vacine un humain, augmente son immunit�.
     * @param beings
     * @param human
     */
    public void vaccinate( Human human){
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
    public Boolean tryHeal(Human human){
    	if (tryOperation(beings, Constants.HEAL_DIFFICULTY)){
    	    heal(human);
        }
    }
    public Boolean tryCure(Human human) {
    	if (tryOperation(beings, Constants.CURE_DIFFICULTY)){
    	    cureDisease(human);
        }
    }
    public Boolean tryVaccinate(Human human){
    	if(tryOperation(beings, Constants.VACCINATE_DIFFICULTY)){
    	    vaccinate(human);
        }
    }
    // The resulting number of the calculation must be superior to the SUCCESS_DIFFICULTY for the operation to succeed
    // The resulting number varies between 0.5f and 1f
    // The resulting number depends on the level of skill of the doctor, the difficulty of the operation and a random number between 0f (open) and 1f (open)
    public Boolean tryOperation(int operationSuccessLevel){
    	// TODO simplifier formule
    	return (0.5f + (Math.pow(skill * 0.5f, operationSuccessLevel) * beings.random.nextFloat(false, false)) > Constants.SUCCESS_DIFFICULTY); 
    }


}
