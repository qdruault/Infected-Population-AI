package model;

import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.Grid2D;
import sim.util.Bag;
import sim.util.Int2D;
import sim.util.IntBag;

/**
 * Created by Louis on 20/05/2017.
 */
public class Human implements Steppable {
	
	private static final long serialVersionUID = 1L;

    // Age
    private int age;
    // PV
    private int health;
    // Capacit� de r�sistance au virus.
    private int immunity;
    // Fertilit�.
    private int fertility;
    // Niveau de statiete
    private int gratification;
    // Champs de vision
    public int vision;
    // Nombre de cellules de mouvement par tour
    public int move;
    // Homme ou Femme
    private Gender gender;
    // Malade ou pas
    private Condition condition;
    // Coordonn�es.
    private int x;
    private int y;

    public enum Gender {
        MALE,
        FEMALE
    };

    public enum Condition {
        SICK,
        FINE
    };

    private Bag neighbors;

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
            beings.yard.set(x, y, null);
        }
    }

    //Perceive the cells around, should be called at the beginning of each step
    public void perceiveCells(Beings beings){
        neighbors = beings.yard.getRadialNeighbors(x, y, vision, Grid2D.BOUNDED, false, new Bag(), new IntBag(), new IntBag());
    }

    //Check if there are any food available around
    public Int2D lookForFood(){
        Object currentNeighbor = neighbors.pop();
        while (currentNeighbor != null){
            if (currentNeighbor instanceof Food){
                Food food = (Food)currentNeighbor;
                return new Int2D(food.getX(), food.getY());
            } else {
                currentNeighbor = neighbors.pop();
            }
        }
        return new Int2D();
    }

    // Move toward the given cell until it's reached or the human can't move anymore
    public void moveTowardCell(Int2D position, Beings beings){
        int diffX = position.x - x;
        int diffY = position.y - y;
        int movesLeft = move;

        int resultX = x;
        int resultY = y;

        while (movesLeft > 0 && (diffX != 0 || diffY != 0)){
            if (diffX != 0){
                int increment = (diffX > 0) ? -1 : 1;
                resultX -= increment;
                diffX += increment;
                movesLeft --;
            }
            if (diffY != 0) {
                int increment = (diffY > 0) ? -1 : 1;
                resultY -= increment;
                diffY += increment;
                movesLeft --;
            }
        }
        if(resultX != x || resultY != y){
            beings.yard.set(beings.yard.stx(resultX), beings.yard.sty(resultY), this);
        }
    }
}
