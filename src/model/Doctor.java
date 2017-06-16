package model;

import res.values.Constants;
import sim.engine.SimState;
import sim.util.Int2D;

import java.util.ArrayList;
import java.util.List;

import model.Human.Condition;
import model.Human.Gender;

/**
 * Created by Louis on 22/05/2017.
 */


// TODO add the methods basicNeed: Heal myself, Cure myself, vaccinate myself, help someone who called, help someone who doesn't call
public class Doctor extends Human {

	protected static final long serialVersionUID = 1L;
	// Stock de m�dicaments.
	protected int drugStock;
	// Facilit� � soigner les gens (comp�tence du medecin)
	protected float skill; //between 0 and 1
	protected List<Human> humansToHelp;

	public Doctor() {
		super();
	}

	/**
	 * 
	 * @param immunity
	 * @param fertility
	 * @param gender
	 * @param condition
	 * @param vision
	 * @param skill
	 * @param beings
	 */
	public Doctor(int immunity, int fertility, Gender gender, Condition condition, int vision, float skill, Beings beings) {
		super(immunity, fertility, gender, condition, vision, beings);
		this.skill = skill;
		this.drugStock = Constants.MAX_DRUG_STOCK;
		this.humansToHelp= new ArrayList<>();//initialize the list to empty
		
		// MAJ des stats.
		this.beings.increaseNbDoctor();
	}

	/**
	 * To create doctors at the  beginning of the simulation
	 * @param immunity
	 * @param fertility
	 * @param gender
	 * @param vision
	 * @param age
	 * @param skill
	 * @param beings
	 */
	public Doctor(int immunity, int fertility, Gender gender, int vision, int age, float skill, Beings beings){
		super(immunity, fertility, gender, vision, age, beings);
		this.skill=skill;
		this.drugStock = Constants.MAX_DRUG_STOCK;
		this.humansToHelp= new ArrayList<>();//initialize the list to empty
		
		// MAJ des stats.
		this.beings.increaseNbDoctor();
	}
	
	@Override
	public void step(SimState state) {
		beings = (Beings)state;
		setHasRecentlyProcreated(false);
		setAge(getAge() + 1);

		if (mustDie()) {
			beings.yard.set(x, y, null);
			getStoppable().stop();
			beings.decreaseNbHuman();
			beings.decreaseNbDoctor();
			if (gender == Gender.MALE) {
				beings.decreaseNbMen();
			} else {
				beings.decreaseNbWomen();
			}
			if (condition == Condition.SICK) {
				beings.decreaseNbInfectedHuman();
			}
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

			if (humansToHelp.isEmpty() == false) {
				// On vire ceux qui sont morts
				List<Human> deadPatients = new ArrayList<Human>();
				for (Human patient : humansToHelp) {
					if (patient.mustDie()) {
						deadPatients.add(patient);
					}
				}

				humansToHelp.removeAll(deadPatients);
				System.out.println(humansToHelp.size() + " client(s) restant(s)");
			}


			// S'il y a un malade juste � cot� on le soigne.
			if (!basicNeedHelpHumanAdjacent()) {

				if (needEatingStrong()){
					basicNeedEat();
				} else if (needDrugs()) {
					// TODO add the basicNeedLookForFood method here
				} else if (needHealing()){
					basicNeedHealth();
				} else if (needCuration()){
					System.out.println("J'essaye de me soigner");
					basicNeedCuration();
				} else if (needVaccination()){
					basicNeedVaccination();
				} else if (!humansToHelp.isEmpty()) {
					basicNeedHelpHumanFar();
				} else if (needEatingMedium()){
					basicNeedEat();
				} else {
					moveRandom();
				}
			}
		}
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
	 * Soigne un malade pr�s de lui.
	 * @return true si r�ussi.
	 */
	protected boolean basicNeedHelpHumanAdjacent(){
		if (humansToHelp.isEmpty()) {
			return false;
		}
		
		Human patient = humansToHelp.get(0);
		if (objectIsAdjacent(patient)) {
			// SI je suis a cote de lui je le soigne.
			System.out.println("Je te soigne.");
			handlePatient(patient);
			humansToHelp.remove(0);
			return true;
		}
		
		return false;
	}

	/**
	 * Part soigner qqun.
	 */
	protected void basicNeedHelpHumanFar(){
		//System.out.println("Humains a soigner");
		// Si il y a des gens a soigner.
		Human patient = humansToHelp.get(0);
		System.out.println("Je pars soigner un humain !");
		moveTowardsPatient();
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
		} else if (patient.getImmunity() < Constants.LOW_IMMUNITY){
			vaccinate(patient);
		}
		// On retire l'appel.
		patient.setDoctorCalled(null);
	}

	/**
	 * Vacine un humain, augmente son immunit�.
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
		//System.out.println("J'ai rep�r� un patient.");
		humansToHelp.add(human);
	}

	public void pickUpMedicine(Medicine medicine){
		int quantityPickedUp = medicine.consume(Constants.MAX_MEDICINE_QUANTITY - drugStock);
		drugStock = Math.min(drugStock + quantityPickedUp * medicine.getQuantity(), Constants.MAX_MEDICINE_QUANTITY);
	}

	public boolean needDrugs(){
		return drugStock > Constants.HEAL_CONSUMMATION;
	}
}
