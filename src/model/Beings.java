package model;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Stoppable;
import sim.field.grid.ObjectGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

import model.Human.Gender;

import java.util.ArrayList;
import java.util.List;

public class Beings extends SimState {


	public ObjectGrid2D yard = new ObjectGrid2D(Constants.GRID_SIZE, Constants.GRID_SIZE);


	public Beings(long seed) {
		super(seed);
	}
	public void start() {
		//System.out.println("Simulation started");
		super.start();
		yard.clear();
		addAgentsHuman();
		addAgentsFood();
		addEnvironment();
		addAgentsDoctor();
		addAgentsVirus();
	}

	/**
	 * G�n�re des Humains sur la carte.
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
			Human a = new Human(immunity, fertility, gender, vision, age);
			Int2D location = getFreeLocation();
			yard.set(location.x, location.y, a);
			a.setX(location.x);
			a.setY(location.y);
			stoppable = schedule.scheduleRepeating(a);
			a.setStoppable(stoppable);
		}
	}
	/**
	 * G�n�re des docteurs sur la carte.
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
			Human a = new Doctor(immunity, fertility, gender, vision, age, skill);
			Int2D location = getFreeLocation();
			yard.set(location.x, location.y, a);
			a.setX(location.x);
			a.setY(location.y);
			stoppable = schedule.scheduleRepeating(a);
			a.setStoppable(stoppable);
		}
	}
	
	


	/**
	 * G�n�re de la nourriture sur la carte.
	 */
	public void addAgentsFood(){
		Stoppable stoppable;
		for(int  i  =  0;  i  <  Constants.NUM_FOODS;  i++) {
			Food  a  =  new Food(random.nextInt(Constants.MAX_FOOD_QUANTITY), random.nextInt(Constants.MAX_NUTRITIONAL_PROVISION));
			Int2D location = getFreeLocation();
			yard.set(location.x, location.y, a);
			a.setX(location.x);
			a.setY(location.y);
			stoppable = schedule.scheduleRepeating(a);
			a.setStoppable(stoppable);
		}
	}

	/**
	 * G�n�re un virus sur la carte.
	 */
	public void addAgentsVirus(){
		Stoppable stoppable;

			int gravity= random.nextInt(Constants.MAX_GRAVITY);
			int moveRange = Constants.MAX_MOVE_RANGE;
			int infectingArea = Constants.MAX_INFECTING_ZONE;
			int propagationDuration= Constants.MAX_PROPAGATION_DURATION;
			int nbHumanToInfect = Constants.MAX_NB_HUMAN_TO_CONTAMINATE;
			int timeBeforeActivation = random.nextInt(Constants.MAX_TIME_BEFORE_ACTIVATION);
			Virus  a  =  new Virus(gravity, moveRange, infectingArea, propagationDuration, nbHumanToInfect, timeBeforeActivation);
			Int2D location = getFreeLocation();
			yard.set(location.x, location.y, a);
			a.setX(location.x);
			a.setY(location.y);
			stoppable = schedule.scheduleRepeating(a);
			a.setStoppable(stoppable);
	}

	/**
	 * G�n�re un environnement.
	 */
	public void addEnvironment(){
		Environment  a  =  new Environment();
		schedule.scheduleRepeating(a);
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
	public Int2D getFreeLocation() {
		Int2D location = new Int2D(random.nextInt(yard.getWidth()),
				random.nextInt(yard.getHeight()) );
		Object ag;
		while ((ag = yard.get(location.x,location.y)) != null) {
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
}
