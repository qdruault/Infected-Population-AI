package model;

import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Created by Louis on 20/05/2017.
 */
public class Human implements Steppable {
	
	private static final long serialVersionUID = 1L;

	public enum Gender {
        MALE,
        FEMALE
    };
    
    public enum Condition {
        SICK,
        FINE
    };

    // Age
    private int age;
    // PV
    private int health;
    // Capacité de résistance au virus.
    private int immunity;
    // Fertilité.
    private int fertility;
    // Niveau de statiete
    private int gratification;
    // Homme ou Femme    
    private Gender gender;
    // Malade ou pas
    private Condition condition;
    // Coordonnées.
    private int x;
    private int y;

    /**
     * Constructeur vide.
     */
    public Human(){

    }
    
    /**
     * Constructeur
     * @param immunity
     * @param fertility
     * @param gender
     * @param condition
     */
    public Human(int immunity, int fertility, Gender gender, Condition condition) {
    	health = Constants.MAX_HEALTH;
        gratification = Constants.MAX_GRATIFICATION;
        age = 0;
		this.immunity = immunity;
		this.fertility = fertility;
		this.gender = gender;
		this.condition = condition;
	}

    // Getters and setters.
    public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
	public void addHealth(int health) {
		this.health += health;
	}

	public int getImmunity() {
		return immunity;
	}

	public void setImmunity(int immunity) {
		this.immunity = immunity;
	}
	
	public void addImmunity(int immunity) {
		this.immunity += immunity;
	}

	public int getFertility() {
		return fertility;
	}

	public void setFertility(int fertility) {
		this.fertility = fertility;
	}

	public int getGratification() {
		return gratification;
	}

	public void setGratification(int gratification) {
		this.gratification = gratification;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	private Boolean mustDie(){
        return health == 0;
    }
    
    @Override
    public void step(SimState state) {
        Beings beings = (Beings) state;

        if (mustDie()){
            beings.yard.remove(this);
        }
    }
}
