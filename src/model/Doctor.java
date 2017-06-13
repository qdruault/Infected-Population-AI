package model;

import res.values.Constants;
import sim.engine.SimState;
import sim.util.Int2D;

import java.util.ArrayList;
import java.util.List;

import model.Human.Condition;

/**
 * Created by Louis on 22/05/2017.
 */


// TODO add the methods basicNeed: Heal myself, Cure myself, vaccinate myself, help someone who called, help someone who doesn't call
public class Doctor extends Human {

	protected static final long serialVersionUID = 1L;
	// Stock de mï¿½dicaments.
	protected int drugStock;
    // Facilitï¿½ ï¿½ soigner les gens (compï¿½tence du medecin)
	protected float skill; //between 0 and 1
	protected List<Human> humansToHelp;

	public Doctor() {
		super();
	}

	public Doctor(int immunity, int fertility, Gender gender, Condition condition, int vision, float skill) {
		super(immunity, fertility, gender, condition, vision);
		this.skill = skill;
		this.drugStock = Constants.MAX_DRUG_STOCK;
		this.humansToHelp= new ArrayList<>();//initialize the list to empty
	}

    // To create doctors at the  beginning of the simulation
    public Doctor(int immunity, int fertility, Gender gender, int vision, int age, float skill){
	    super(immunity, fertility, gender, vision, age);
	    this.skill=skill;
        this.drugStock = Constants.MAX_DRUG_STOCK;
        this.humansToHelp= new ArrayList<>();//initialize the list to empty
    }
    @Override
    public void step(SimState state) {
    	beings = (Beings)state;
        setHasRecentlyProcreated(false);
        setAge(getAge() + 1);

        if (mustDie()) {
            beings.yard.set(x, y, null);
            getStoppable().stop();
        } else {
            //decrease health level depending on his condition the activation and gravity of the virus
            if (getCondition() == Condition.SICK) {
                if (timeBeforeSuffering == 0)
                    health -= infection_gravity;
                else timeBeforeSuffering--;
            } else if (getCondition() == Condition.FINE && getGratification() > 0 && getHealth() < Constants.MID_HEALTH) {
                // Increase the health level if the human is fine
                setHealth(getHealth() + Constants.PASSIVE_HEALTH_GAIN);
            }

            if (getGratification() <= 0) {
                health--; // the Human is Starving
            } else {
                setGratification(getGratification() - Constants.GRATIFICATION_LOSS);
            }

            if (needEatingStrong()){
                basicNeedEat();
            } else if (needHealing()){
                basicNeedHealth();
            } else if (needCuration()){
                basicNeedCuration();
            } else if (needVaccination()){
                basicNeedVaccination();
            } else if (humansToHelp.isEmpty() == false) {
            	System.out.println("Humains à soigner");
            	// Si il y a des gens à soigner.
            	Human patient = humansToHelp.get(0);
            	if (objectIsAdjacent(patient)) {
					// SI je suis a cote de lui je le soigne.
            		System.out.println("Je te soigne.");
            		handlePatient(patient);
            		humansToHelp.remove(0);
            		
				} else {
					System.out.println("Poussez vous je suis medecin !");
					// SInon je m'approche de lui.
					moveTowardsPatient();
				}
            } else if (needEatingMedium()){
                basicNeedEat();
            }


        }


        //TODO exclusive strategy of a doctor which supports his humans needs
        // 1. Eat if gratification is very low => needEatingStrong


        //if sick --> heal himself
        //if weak immunity --> vaccinate himself
        //if humansToHelp --> help human
        //else --> move to human
        //++ consider his human needs
//        super.step(state);
        
    }


    @Override
    protected void basicNeedHealth(){
        healMyself();
    }

    protected void basicNeedCuration(){
        cureMyself();
    }

    protected void basicNeedVaccination(){
        vaccinateMyself();
    }

    /**
     * Decide to vaccinate himself
     * @return true if successed, false otherwise
     */
    public boolean vaccinateMyself(){
        if (canVaccinate()){
            return tryVaccinate(this);
        }
        return false;
    }

    /**
     * Decide to heal himself
     * @return true if successed, false otherwise
     */
    public boolean healMyself(){
        boolean feelBetter= false;
        if (canHeal()) {
            feelBetter=tryHeal(this);
        }
        if(!feelBetter){
            if (canCure())
                feelBetter = tryCure(this);
        }
        return feelBetter;
    }

    private boolean cureMyself(){

        boolean feelBetter = false;

        if(canCure()){
            feelBetter = tryCure(this);
        }
        return feelBetter;
    }

    /**
     * Va vers le premier patient.
     */
    protected void moveTowardsPatient() {
    	Int2D positionFirstPatient = new Int2D(humansToHelp.get(0).getX(), humansToHelp.get(0).getY());
    	moveTowardsCell(positionFirstPatient);	
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
        human.addImmunity(Constants.IMPROVE_IMMUNITY_HEALED);
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
        human.addImmunity(Constants.IMPROVE_IMMUNITY_CURED);
        if (human!=this){
            //if the doctor is not healing himself, get more immunity
            this.immunity += Constants.IMPROVE_IMMUNITY_DOCTOR_2;
        }
    }
    
    /**
     * S'occupe d'un de ses patients.
     * @param patient
     */
    protected void handlePatient(Human patient) {
    	if (patient.getHealth() < Constants.LOW_HEALTH) {
    		heal(patient);
    	} else if (patient.getCondition() == Condition.SICK) {
    		cureDisease(patient);
    	}
    }
    
    /**
     * Vacine un humain, augmente son immunitï¿½.
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
    	if (tryOperation(Constants.HEAL_DIFFICULTY)){
    	    heal(human);
            return true;
        }
        return false;
    }
    public Boolean tryCure(Human human) {
    	if (tryOperation(Constants.CURE_DIFFICULTY)){
    	    cureDisease(human);
    	    return true;
        }
        return false;
    }
    public Boolean tryVaccinate(Human human){
    	if(tryOperation(Constants.VACCINATE_DIFFICULTY)){
    	    vaccinate(human);
    	    return true;
        }
        return false;
    }
    // The resulting number of the calculation must be superior to the SUCCESS_DIFFICULTY for the operation to succeed
    // The resulting number varies between 0.5f and 1f
    // The resulting number depends on the level of skill of the doctor, the difficulty of the operation and a random number between 0f (open) and 1f (open)
    public Boolean tryOperation(int operationSuccessLevel){
    	return (0.5f + (Math.pow(skill, operationSuccessLevel)/0.5f * beings.random.nextFloat(false, false)) > Constants.SUCCESS_DIFFICULTY);
    }

    /**
     * Add a patient to the list of humans to help
     * @param human who is calling for help
     */
    public void processRequest(Human human){
        humansToHelp.add(human);
    }

}
