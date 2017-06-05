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
	private Beings beings;
    private Bag neighbors;

    // Age
    private int age;
    // PV
    private int health;
    //temps avant l'activation du virus
    private int timeBeforeSuffering;
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
    // A MAX_SURVIVAL à la naissance puis diminue avec les maladies et autres 
    // ± probabilite de mourir
    public int survival;
    public enum Gender {
        MALE,
        FEMALE
    };
    public enum Condition {
        SICK,
        FINE
    };


    @Override
    public void step(SimState state) {
        beings = (Beings) state;
        IntBag    xPos       = new IntBag();
        IntBag    yPos       = new IntBag();
        Bag       neighbours = toPerceive(xPos, yPos);

        // TODO remove the agent from the scheduling
        // remove if needed
        if (mustDie()){
            beings.yard.set(x, y, null);
        }
        
        //Partie à intégrer dans autre chose 
        //le cas de la procréation
        int     index            = 0;
        boolean procreationDone  = false;
        boolean eatDone  = false;
        
        for (Object object : neighbours) {
            int x = xPos.get(index);
            int y = yPos.get(index);
	    	int posX=getX();
	    	int posY=getY();
	    	if(object instanceof Human){	//If there is another human nearby 
                int minX  = Math.min(this.x, x);
                int maxX  = Math.max(this.x, x);
                int diffX = Math.min((maxX - minX), Constants.GRID_SIZE - (maxX - minX));
                int minY  = Math.min(this.y, y);
                int maxY  = Math.max(this.y, y);
                int diffY = Math.min((maxY - minY), Constants.GRID_SIZE - (maxY - minY));

                if (diffX <= 1 && diffY <= 1) {
                    System.out.println("Let's procreate");
                    if (tryToProcreate((Human) object)) {
                        toProcreate((Human) object);
                        procreationDone = true;
                    }
                    break;
                }	    		
	    	}
	    	
	    	if(object instanceof Food){	//If there is some food nearby 
                int minX  = Math.min(this.x, x);
                int maxX  = Math.max(this.x, x);
                int diffX = Math.min((maxX - minX), Constants.GRID_SIZE - (maxX - minX));
                int minY  = Math.min(this.y, y);
                int maxY  = Math.max(this.y, y);
                int diffY = Math.min((maxY - minY), Constants.GRID_SIZE - (maxY - minY));

                if (diffX <= 1 && diffY <= 1) {
                    System.out.println("Let's eat");
                    int quantity;
                    if (getGratification()<=50) quantity = 5;
                    else quantity = 2;
                    toEat((Food) object, quantity);
                    eatDone = true;
                    break;
                }	    		
	    	}
	    	
	    	index ++;
        }
        //decrease health level depending on his condition
        if (this.condition ==Condition.SICK){
            if(timeBeforeSuffering==0)
                health--;
            else timeBeforeSuffering --;
        }
    }
    

    /**
     * Constructeur vide.
     */
    public Human(){}
    
    /**
     * Constructeur
     * @param immunity
     * @param fertility
     * @param gender
     * @param condition
     * @param vision
     */
    public Human(int immunity, int fertility, Gender gender, Condition condition, int vision) {
    	health = Constants.MAX_HEALTH;
        gratification = Constants.MAX_GRATIFICATION;
        age = 0;
		this.immunity = immunity;
		this.fertility = fertility;
		this.gender = gender;
		this.condition = condition;
		this.vision = vision;
		this.survival=Constants.MAX_SURVIVAL;
	}

	// To create humans at the  beginning of the simulation
    public Human(int immunity, int fertility, Gender gender, int vision, int age){
        health = Constants.MAX_HEALTH;
        gratification = Constants.MAX_GRATIFICATION;
        this.immunity = immunity;
        this.fertility = fertility;
        this.gender = gender;
        this.condition = Condition.FINE;
        this.vision = vision;
        this.survival=Constants.MAX_SURVIVAL;
        this.age = age;
    }

    // Generate a boolean based on the fertility of both humans
    public boolean tryToProcreate(Human h){
        float fertilityProbability1 = (float) fertility  / (float) Constants.MAX_FERTILITY;
        float fertilityProbability2 = (float) h.getFertility() / (float) Constants.MAX_FERTILITY;

        float successProbability = fertilityProbability1 * fertilityProbability2;
        float f = 1f - successProbability;
        successProbability = 1f - ( f / (float) Constants.PROCREATION_MULTIPLIER);

        return  beings.random.nextFloat() < successProbability;
    }


    // TODO add a pregnancy mecanism
    public void toProcreate(Human h){
	    if(this.getGender()!=h.getGender() && this.getAge()>15 && this.getAge()<60 && h.getAge()>15 && h.getAge()<60){
	        if ((gender == Gender.FEMALE && beings.getFreeAdjacentCell(x, y) != null) || beings.getFreeAdjacentCell(h.getX(), h.getY()) != null)
            {

                int immunity = beings.random.nextInt(Constants.MAX_IMMUNITY);
                int fertility = beings.random.nextInt(Constants.MAX_FERTILITY);
                Gender gender = (beings.random.nextInt(2) == 0) ? Gender.MALE : Gender.FEMALE;
                int vision = beings.random.nextInt(Constants.MAX_VISION);

                Condition condition = Condition.FINE;
                float conditionResult = beings.random.nextFloat();
                if (getCondition() == Condition.SICK && h.getCondition() == Condition.SICK) {
                    if (conditionResult < Constants.TRANSMISSION_PROBABILITY_2)
                        condition = Condition.SICK;
                } else if (getCondition() == Condition.SICK || getCondition() == Condition.SICK) {
                    if (conditionResult < Constants.TRANSMISSION_PROBABILITY_1)
                        condition = Condition.SICK;
                }
                float doctorProbability =  beings.random.nextFloat();
                if (doctorProbability> Constants.DOCTOR_PROBABILITY){
                    float skill = beings.random.nextFloat();
                    Doctor child = new Doctor(immunity, fertility, gender, condition, vision, skill);
                }
                else {
                    Human child = new Human(immunity, fertility, gender, condition, vision);
                }

                beings.yard.set(this.getX(), this.getY(), child);
                child.x = this.getX();
                child.y = this.getY();
                beings.schedule.scheduleRepeating(child);
            }
	    }
    }
    
    public void toEat(Food f, int quantity){
    	f.consume(quantity);
        gratification = Math.min(gratification + quantity * f.getNutritionalProvision(), Constants.MAX_GRATIFICATION);
    }
    
    //Perceive the cells around, record location, is called at the beginning of each step
    private Bag toPerceive(IntBag xPos, IntBag yPos) {
        Bag result = new Bag();
        beings.yard.getMooreNeighborsAndLocations(x, y, vision, Grid2D.TOROIDAL, false, result, xPos, yPos);
        return result;
    }

    // TODO remove the death if gratification == 0 and replace by a loss of health
	private Boolean mustDie(){
		if (health == 0 || gratification == 0 || survival <= 10)
            return true;
        else return false;	
    }
    
    //Perceive the cells around, should be called at the beginning of each step
    public void perceiveCells(Beings beings){
        neighbors = beings.yard.getRadialNeighbors(x, y, vision ,Grid2D.TOROIDAL, false, new Bag(), new IntBag(), new IntBag());
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
            beings.yard.set(beings.yard.stx(resultX), beings.yard.sty(resultY),this);
        }
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

	public void setTimeBeforeSuffering(int time){
        this.timeBeforeSuffering=time;
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

    public int getVision() {
		return vision;
	}

	public void setVision(int vision) {
		this.vision = vision;
	}
}
