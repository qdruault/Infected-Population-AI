package model;

import gui.BeingsWithUI;
import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Stoppable;
import sim.field.grid.ObjectGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

import model.Human.Gender;

public class Beings extends SimState {

	private BeingsWithUI beingsWithUI;
	public ObjectGrid2D yard = new ObjectGrid2D(Constants.GRID_SIZE, Constants.GRID_SIZE);
	protected int nbHuman = 0;
	protected int nbMen = 0;
	protected int nbWomen = 0;
	protected int nbDoctor = 0;
	protected int nbFood = 0;
	protected int nbInfectedHuman = 0;
	protected int nbMedicine = 0;
	protected Map map;

	public Beings(long seed) {
		super(seed);
	}
	public void start() {
		//System.out.println("Simulation started");
		super.start();
		yard.clear();
		
		// RAZ des stats.
		nbHuman = 0;
		nbMen = 0;
		nbWomen = 0;
		nbDoctor = 0;
		nbFood = 0;
		nbInfectedHuman = 0;
		nbMedicine = 0;

		addObstacles();
		
		addAgentsHuman();
		addAgentsFood();
		addEnvironment();
		addAgentsDoctor();
		addAgentsVirus();
	}

	/**
	 * Gï¿½nï¿½re des Humains sur la carte.
	 */
	public void addAgentsHuman(){
		Stoppable stoppable;
		for(int  i  =  0;  i  <  Constants.NUM_HUMANS;  i++) {
			Gender gender;
			if (i % 2 != 0) {
				gender = Gender.FEMALE;
			} else {
				gender = Gender.MALE;
			}
			int immunity = random.nextInt(Constants.MAX_IMMUNITY);
			int fertility = random.nextInt(Constants.MAX_FERTILITY);
			int age = random.nextInt(Constants.MAX_AGE);
			int vision = 10;
			Human a = new Human(immunity, fertility, gender, vision, age, this);
			Int2D location = freeLocation();
			yard.set(location.x, location.y, a);
			a.setX(location.x);
			a.setY(location.y);
			stoppable = schedule.scheduleRepeating(a);
			a.setStoppable(stoppable);
		}
	}
	/**
	 * Gï¿½nï¿½re des docteurs sur la carte.
	 */
	public void addAgentsDoctor(){
		Stoppable stoppable;
		for(int  i  =  0;  i  <  Constants.NUM_DOCTORS;  i++) {
			Gender gender;
			if (i % 2 != 0) {
				gender = Gender.FEMALE;
			} else {
				gender = Gender.MALE;
			}
			int immunity = random.nextInt(Constants.MAX_IMMUNITY);
			int fertility = random.nextInt(Constants.MAX_FERTILITY);
			int age = random.nextInt(Constants.MAX_AGE);
			int vision = 10;
			float skill = random.nextFloat();
			Human a = new Doctor(immunity, fertility, gender, vision, age, skill, this);
			Int2D location = freeLocation();
			yard.set(location.x, location.y, a);
			a.setX(location.x);
			a.setY(location.y);
			stoppable = schedule.scheduleRepeating(a);
			a.setStoppable(stoppable);
		}
	}

	/**
	 * Gï¿½nï¿½re de la nourriture sur la carte.
	 */
	public void addAgentsFood(){
		Stoppable stoppable;
		for(int  i  =  0;  i  <  Constants.NUM_FOODS;  i++) {
			Food  a  =  new Food(random.nextInt(Constants.MAX_FOOD_QUANTITY), random.nextInt(Constants.MAX_NUTRITIONAL_PROVISION), this);
			Int2D location = freeLocation();
			yard.set(location.x, location.y, a);
			a.setX(location.x);
			a.setY(location.y);
			stoppable = schedule.scheduleRepeating(a);
			a.setStoppable(stoppable);
		}
	}

	/**
	 * Gï¿½nï¿½re un virus sur la carte.
	 */
	public void addAgentsVirus(){
		Stoppable stoppable;
		System.out.println("NOUVEAU VIRUS");

			// Gravite entre 8 et 12
			int gravity= random.nextInt(Constants.MAX_GRAVITY) + 8;
			int moveRange = Constants.MAX_MOVE_RANGE;
			int infectingArea = Constants.MAX_INFECTING_ZONE;
			int propagationDuration= Constants.MAX_PROPAGATION_DURATION;
			int nbHumanToInfect = Constants.MAX_NB_HUMAN_TO_CONTAMINATE;
			Virus  a  =  new Virus(gravity, moveRange, infectingArea, propagationDuration, nbHumanToInfect);
			Int2D location = freeLocation();

			yard.set(location.x, location.y, a);
			a.setX(location.x);
			a.setY(location.y);
			stoppable = schedule.scheduleRepeating(a);
			a.setStoppable(stoppable);
	}

	/**
	 * Gï¿½nï¿½re un environnement.
	 */
	public void addEnvironment(){
		Environment  a  =  new Environment(this);
		schedule.scheduleRepeating(a);
	}
	
	/**
	 * Génère les obstacles.
	 */
	public void addObstacles(){
		map = new Map();

		for(int  i  =  0;  i  <  map.getObstacles().size();  i++) {
			yard.set(map.getObstacles().get(i).getX(), map.getObstacles().get(i).getY(), map.getObstacles().get(i));
		}
	}
	
	/** Remet un obstacle sur la carte.
	 * 
	 * @param x
	 * @param y
	 */
	public void putBackObstacle(int x, int y){
		for(int  i  =  0;  i  <  map.getObstacles().size();  i++) {
			if (map.getObstacles().get(i).getX() == x && map.getObstacles().get(i).getY() == y)
				yard.set(x, y, map.getObstacles().get(i));
		}
	}
	
	/**
	 * Indique si une case est libre.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean free(int x,int y) {
		int xx = yard.stx(x);
		int yy = yard.sty(y);
		return yard.get(xx,yy) == null;
	}

	/**
	 * Trouve un endroit libre sur la carte.
	 * @return
	 */
	public Int2D freeLocation() {
		Int2D location = new Int2D(random.nextInt(yard.getWidth()),
				random.nextInt(yard.getHeight()) );
		Object ag;
		while ((ag = yard.get(location.x,location.y)) != null || !map.isLocationFree(location.x,location.y)) {
			location = new Int2D(random.nextInt(yard.getWidth()),
					random.nextInt(yard.getHeight()) );
		}
		return location;
	}

	// Return a free adjacent cell if there is one, null otherwise
	public Case getFreeAdjacentCell(int x, int y){
		// RIGHT
		if( yard.get(yard.stx(x + 1), y) == null){
			return new Case(yard.stx(x + 1), y);
		}
		// LEFT
		else if( yard.get(yard.stx(x - 1), y) == null){
			return new Case(yard.stx(x -1), y);
		}
		// DOWN
		else if ( yard.get(x, yard.sty(y + 1)) == null){
			return new Case(x, yard.sty(y + 1));
		}
		// UP
		else if (yard.get(x, yard.sty(y - 1)) == null){
			return new Case(x, yard.sty(y - 1));
		}
		// TOP RIGHT
		else if (yard.get(yard.stx(x + 1), yard.sty(y - 1)) == null ){
			return new Case(yard.stx(x + 1), yard.sty(y - 1));
		}
		// TOP LEFT
		else if (yard.get(yard.stx(x - 1), yard.sty(y - 1)) == null ){
			return new Case(yard.stx(x - 1), yard.sty(y - 1));
		}
		// BOTTOM RIGHT
		else if (yard.get(yard.stx(x + 1), yard.sty(y + 1)) == null ){
			return new Case(yard.stx(x + 1), yard.sty(y + 1));
		}
		// BOTTOM LEFT
		else if (yard.get(yard.stx(x - 1), yard.sty(y + 1)) == null ){
			return new Case(yard.stx(x - 1), yard.sty(y + 1));
		}
		return null;
	}

	// TODO remove the old instructions when the new instructions have been tested
	// Return a list of adjacent cells to the position (x,y)
	public Bag getAdjacentCells(int x, int y){
		Bag objects = new Bag();
		// RIGHT
//		if (x + 1 > Constants.GRID_SIZE-1)
//			objects.add( yard.get(0, y));
//		else
//			objects.add( yard.get(x + 1, y));

		objects.add(yard.get(yard.stx(x + 1), y));

		// LEFT
//		if (x - 1 < 0)
//			objects.add( yard.get(Constants.GRID_SIZE-1, y));
//		else
//			objects.add( yard.get(x - 1, y));

		objects.add(yard.get(yard.stx(x - 1), y));

		// DOWN
//		if (y + 1 > Constants.GRID_SIZE-1)
//			objects.add( yard.get(x, 0));
//		else
//			objects.add( yard.get(x, y + 1));

		objects.add(yard.get(x, yard.sty(y + 1)));

		// UP
//		if (y - 1 < 0)
//			objects.add( yard.get(x, Constants.GRID_SIZE-1));
//		else
//			objects.add( yard.get(x, y - 1));

		objects.add(yard.get(x, yard.sty(y - 1)));

		// TOP RIGHT
		objects.add(yard.get(yard.stx(x + 1), yard.sty(y - 1)));

		// TOP LEFT
		objects.add(yard.get(yard.stx(x - 1), yard.sty(y - 1)));

		// BOTTOM LEFT
		objects.add(yard.get(yard.stx(x - 1), yard.sty(y + 1)));

		// BOTTOM RIGHT
		objects.add(yard.get(yard.stx(x + 1), yard.sty(y - 1)));

		return objects;
	}
	

	public int getNbHuman() {
		return nbHuman;
	}	
	public int getNbMen() {
		return nbMen;
	}
	public int getNbWomen() {
		return nbWomen;
	}
	public void setBeingsWithUI(BeingsWithUI beingsWithUI){ this.beingsWithUI = beingsWithUI; }
	public int getNbDoctor() {
		return nbDoctor;
	}
	public int getNbFood() {
		return nbFood;
	}
	public int getNbInfectedHuman() {
		return nbInfectedHuman;
	}
	public BeingsWithUI getBeingsWithUI() { return beingsWithUI; }
	public void increaseNbHuman() {
		this.nbHuman++;
	}
	public void decreaseNbHuman() {
		this.nbHuman--;
	}
	public void increaseNbWomen() {
		this.nbWomen++;
	}
	public void decreaseNbWomen() {
		this.nbWomen--;
	}
	public void increaseNbMen() {
		this.nbMen++;
	}
	public void decreaseNbMen() {
		this.nbMen--;
	}
	public void increaseNbDoctor() {
		this.nbDoctor++;
	}
	public void decreaseNbDoctor() {
		this.nbDoctor--;
	}
	public void increaseNbFood(int p_quantity) {
		this.nbFood += p_quantity;
	}
	public void decreaseNbFood(int p_quantity) {
		this.nbFood -= p_quantity;
	}
	public void increaseNbInfectedHuman() {
		this.nbInfectedHuman++;
	}
	public void decreaseNbInfectedHuman() {
		this.nbInfectedHuman--;
	}
	public void decreaseNbMedicine(int p_quantity) { this.nbMedicine -= p_quantity; }
	public void increaseNbMedicine(int p_quantity) { this.nbMedicine += p_quantity; }
}
