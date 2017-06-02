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
	private Beings model;

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
    private Bag neighbors;


    @Override
    public void step(SimState state) {
        Beings beings = (Beings) state;
        IntBag    xPos       = new IntBag();
        IntBag    yPos       = new IntBag();
        Bag       neighbours = toPerceive(xPos, yPos);

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
                    toProcreate((Human) object);
                    procreationDone = true;
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
                    if (getGratification()>=50) quantity = 5;
                    else quantity = 2;
                    toEat((Food) object, quantity);
                    eatDone = true;
                    break;
                }	    		
	    	}
	    	
	    	index ++;
        }
    }
    

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

    public void toProcreate(Human h){
	    if(this.getGender()!=h.getGender() && this.getAge()>15 && this.getAge()<60 && h.getAge()>15 && h.getAge()<60){
			  //Human  child  =  new Human(random.nextInt(Constants.MAX_IMMUNITY), random.nextInt(31) + 20, random.nextInt(Constants.MAX_FERTILITY), random.nextInt(2), random.nextFloat());
			  // Refaire avec les bons param�tres et des entiers. + ajouter vision + beings
			  Human  child = new Human();
//			  model.yard.setObjectLocation(child, this.getX(), this.getY());
			  model.yard.set(this.getX(),this.getY(),child);
				child.x = this.getX();
				child.y = this.getY();
				model.schedule.scheduleRepeating(child);
	    }
    }
    
    public void toEat(Food f, int quantity){
    	f.consume(quantity);
        gratification = Math.max(gratification - quantity, 0);
    }
    
    //Perceive the cells around, record location, is called at the beginning of each step
    private Bag toPerceive(IntBag xPos, IntBag yPos) {
        Bag result = new Bag();
        model.yard.getMooreNeighborsAndLocations(x, y, vision, Grid2D.TOROIDAL, false, result, xPos, yPos);
        return result;
    }
    
	private Boolean mustDie(){
		if (health == 0 || gratification == 100 || survival <= 10)
            return true;
        else return false;	
    }
    
    //Perceive the cells around, should be called at the beginning of each step
    public void perceiveCells(Beings beings){
        neighbors = beings.yard.getMooreNeighbors(x, y, vision , Grid2D.TOROIDAL, false, new Bag(), new IntBag(), new IntBag());
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

    /* Ne marche pas dans ObjectGrid (getObjectATLocation*/
    /*
    // get the Food object if there is one on the current cell
    public Food food(Beings beings){
        Food food = null;
        // Get the location
        Int2D flocation = new Int2D(beings.yard.stx(x), beings.yard.sty(y));
        // Get the list of objects at the current location
        Bag localObjects = beings.yard.getObjectsAtLocation(flocation.x, flocation.y);
        Object currentObject = localObjects.pop();
        while(currentObject != null){
            if (currentObject instanceof Food){
                food = (Food) currentObject;
                currentObject = null;
            } else {
                currentObject = localObjects.pop();
            }
        }
        return food;
    }
*/
    
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
