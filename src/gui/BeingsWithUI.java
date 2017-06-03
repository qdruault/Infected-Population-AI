package gui;

import java.awt.Color;

import javax.swing.JFrame;

import model.Food;
import model.Human;
import model.Virus;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.grid.ObjectGridPortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import model.Beings;


public class BeingsWithUI extends GUIState {
	public static int FRAME_SIZE = 600;
	public Display2D display;
	public JFrame displayFrame;
	ObjectGridPortrayal2D yardPortrayal = new ObjectGridPortrayal2D();
	
	public BeingsWithUI(SimState state) {
		super(state);
	}
	public static String getName() {
		return "Simulation de propagation de maladie dans une population humaine";
	}

	public void start() {
		super.start();
		setupPortrayals();
	}

	public void load(SimState state) {
		super.load(state);
		setupPortrayals();
	}
	public void setupPortrayals() {
		Beings beings = (Beings) state;
		yardPortrayal.setField(beings.yard );
		setupPortrayalForHumans(beings);
		yardPortrayal.setPortrayalForClass(Food.class, getFoodPortrayal());
		yardPortrayal.setPortrayalForClass(Virus.class, getVirusPortrayal());
		display.reset();
		display.setBackdrop(Color.LIGHT_GRAY);
		display.repaint();
	}

	private void setupPortrayalForHumans(Beings beings){
		for(Object o: beings.yard.elements()){
			if (o instanceof Human){
				if (((Human) o).getGender() == Human.Gender.MALE)
					yardPortrayal.setPortrayalForObject(o, getMaleHumanPortrayal());
				else
					yardPortrayal.setPortrayalForObject(o, getFemaleHumanPortrayal());
			}
		}
	}

	private OvalPortrayal2D getHumanPortrayal() {
		OvalPortrayal2D r = new OvalPortrayal2D();
		r.paint = Color.BLUE;
		r.filled = true;
		return r;
	}

	private OvalPortrayal2D getMaleHumanPortrayal() {
		OvalPortrayal2D r = new OvalPortrayal2D();
		r.paint = Color.BLUE;
		r.filled = true;
		return r;
	}

	private OvalPortrayal2D getFemaleHumanPortrayal() {
		OvalPortrayal2D r = new OvalPortrayal2D();
		r.paint = Color.PINK;
		r.filled = true;
		return r;
	}

	private OvalPortrayal2D getFoodPortrayal() {
		OvalPortrayal2D r = new OvalPortrayal2D();
		r.paint = Color.GREEN;
		r.filled = true;
		return r;
	}
	
	private OvalPortrayal2D getVirusPortrayal() {
		OvalPortrayal2D r = new OvalPortrayal2D();
		r.paint = Color.RED;
		r.filled = true;
		return r;
	}

	public void init(Controller c) {
		super.init(c);
		display = new Display2D(FRAME_SIZE,FRAME_SIZE,this);
		display.setClipping(false);
		displayFrame = display.createFrame();
		displayFrame.setTitle("Beings");
		c.registerFrame(displayFrame); // so the frame appears in the "Display" list
		displayFrame.setVisible(true);
		display.attach( yardPortrayal, "Yard" );
	}

	public  Object  getSimulationInspectedObject()  {  return  state;  }
	public  Inspector  getInspector() {
	Inspector  i  =  super.getInspector();
		i.setVolatile(true);
		return  i;
	}
}
