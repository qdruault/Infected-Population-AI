package model;

import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Stoppable;
import sim.field.grid.ObjectGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

import java.util.Random;

public class Beings extends SimState {


	public SparseGrid2D yard = new SparseGrid2D(Constants.GRID_SIZE,Constants.GRID_SIZE);

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

  // Add human Agents on random free cells
  public void addAgentsHuman(){
	  for(int  i  =  0;  i  <  Constants.NUM_HUMANS;  i++) {
		  Human  a  =  new Human(random.nextInt(Constants.MAX_IMMUNITY), random.nextInt(31) + 20, random.nextInt(Constants.MAX_FERTILITY), random.nextInt(2));
		  Int2D location = getFreeLocation();
		  yard.setObjectLocation(a, location.x, location.y);
		  a.x = location.x;
		  a.y = location.y;
		  schedule.scheduleRepeating(a);
	  }
  }


  // Add food Agents on random free cells
  public void addAgentsFood(){
	  for(int  i  =  0;  i  <  Constants.NUM_FOODS;  i++) {
		  Food  a  =  new Food(random.nextInt(Constants.MAX_QUANTITY), random.nextInt(Constants.MAX_NUTRIOTINAL_PROVISION));
		  Int2D location = getFreeLocation();
		  yard.setObjectLocation(a, location.x, location.y);
		  a.x = location.x;
		  a.y = location.y;
		  schedule.scheduleRepeating(a);
	  }
  }

  public boolean free(int x,int y) {
	 int xx = yard.stx(x);
	 int yy = yard.sty(y);
	 return yard.getObjectsAtLocation(xx,yy) == null;
  }
  private Int2D getFreeLocation() {
	  Int2D location = new Int2D(random.nextInt(yard.getWidth()),
	           random.nextInt(yard.getHeight()) );
	  Object ag;
	  while ((ag = yard.getObjectsAtLocation(location.x,location.y)) != null) {
	   	  location = new Int2D(random.nextInt(yard.getWidth()),
	   	           random.nextInt(yard.getHeight()) );
	  }
	  return location;
  }
}
