package model;

import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Stoppable;
import sim.field.grid.ObjectGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

import java.util.Random;

import model.Human.Condition;
import model.Human.Gender;

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
			//Remplacer par le bon constructeur 
			Human  a = new Human();
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
			Food  a  =  new Food(random.nextInt(Constants.MAX_QUANTITY), random.nextInt(Constants.MAX_NUTRITIONAL_PROVISION));
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

	// Génère une quantité aléatoire de nourriture sur la grille
	// Pour une valeur de maxInt = 100 :
	// Le nombre de cellules de nourriture générées va de 0 à 4.
	public void generateFood(int maxInt) {
		int result = random.nextInt(maxInt)/25;

		for (int i = 0; i < result; i++){
			Int2D pos = getFreeLocation();
			Food food = new Food(random.nextInt(Constants.MAX_NUTRITIONAL_PROVISION), random.nextInt(Constants.MAX_QUANTITY));
			yard.set(pos.x, pos.y, food);
			food.setX(pos.x);
			food.setY(pos.y);
		}
	}
}
