package model;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import res.values.Constants;
import sim.engine.SimState;
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
		System.out.println("Simulation started");
		super.start();
		yard.clear();
		addAgentsHuman();
		addAgentsFood();
	}

	/**
	 * G�n�re des Humains sur la carte.
	 */
	public void addAgentsHuman(){
		for(int  i  =  0;  i  <  Constants.NUM_HUMANS;  i++) {
			Gender gender;
			if (i % 2 == 0) {
				gender = Gender.FEMALE;
			} else {
				gender = Gender.MALE;
			}
			int immunity = random.nextInt(Constants.MAX_IMMUNITY);
			int fertility = random.nextInt(Constants.MAX_FERTILITY);
			int age = random.nextInt(Constants.MAX_AGE);
			int vision = random.nextInt(Constants.MAX_VISION);
			Human a = new Human(immunity, fertility, gender, vision, age);
			Int2D location = getFreeLocation();
			yard.set(location.x, location.y, a);
			a.setX(location.x);
			a.setY(location.y);
			schedule.scheduleRepeating(a);
		}
	}


	/**
	 * G�n�re de la nourriture sur la carte.
	 */
	public void addAgentsFood(){
		for(int  i  =  0;  i  <  Constants.NUM_FOODS;  i++) {
			Food  a  =  new Food(random.nextInt(Constants.MAX_FOOD_QUANTITY), random.nextInt(Constants.MAX_NUTRITIONAL_PROVISION));
			Int2D location = getFreeLocation();
			yard.set(location.x, location.y, a);
			a.setX(location.x);
			a.setY(location.y);
			schedule.scheduleRepeating(a);
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
		if( yard.get(x + 1, y) == null){
			return new Case(x + 1, y);
		} else if( yard.get(x - 1, y) == null){
			return new Case(x -1, y);
		} else if ( yard.get(x, y + 1) == null){
			return new Case(x, y + 1);
		} else if (yard.get(x, y - 1) == null){
			return new Case(x, y - 1);
		}
		return null;
	}

	// Return a list of adjacent cells
	public Bag getAdjacentCells(int x, int y){
		Bag objects = new Bag();
		if (x + 1 > Constants.GRID_SIZE)
			objects.add( yard.get(0, y));
		else
			objects.add( yard.get(x + 1, y));

		if (x - 1 > 0)
			objects.add( yard.get(Constants.GRID_SIZE, y));
		else
			objects.add( yard.get(x - 1, y));

		if (y + 1 > Constants.GRID_SIZE)
			objects.add( yard.get(x, 0));
		else
			objects.add( yard.get(x, y + 1));

		if (y - 1 < 0)
			objects.add( yard.get(x, Constants.GRID_SIZE));
		else
			objects.add( yard.get(x, y - 1));

		return objects;
	}
}
