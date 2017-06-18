package gui;

import java.awt.Color;

import javax.swing.*;

import model.*;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.grid.ObjectGridPortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.*;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;

import java.awt.*;

import static java.awt.SystemColor.info;
import static jdk.nashorn.internal.runtime.regexp.joni.Syntax.Java;


public class BeingsWithUI extends GUIState {
	public static int FRAME_SIZE = 600;
	public Display2D display;
	public JFrame displayFrame;
	private ObjectGridPortrayal2D yardPortrayal = new ObjectGridPortrayal2D();
	
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
		yardPortrayal.setPortrayalForClass(Medicine.class, getMedicinePortrayal());
		yardPortrayal.setPortrayalForClass(Obstacle.class, getObstaclePortrayal());
		display.reset();
		display.setBackdrop(Color.LIGHT_GRAY);
		display.repaint();
	}

	private void setupPortrayalForHumans(Beings beings){
		for(Object o: beings.yard.elements()){
			if (o instanceof Human){
				if (o instanceof Doctor){
					yardPortrayal.setPortrayalForObject(o, getDoctorPortrayal());
				}
				else {
					yardPortrayal.setPortrayalForObject(o, getHumanPortrayal());
				}
			}
		}
	}


//	private OvalPortrayal2D getMaleHumanPortrayal() {
//		OvalPortrayal2D r = new OvalPortrayal2D();
//		r.paint = Color.BLUE;
//		r.filled = true;
//		return r;
//	}

//	private OvalPortrayal2D getFemaleHumanPortrayal() {
//		OvalPortrayal2D r = new OvalPortrayal2D();
//		r.paint = Color.PINK;
//		r.filled = true;
//		return r;
//	}

	public OvalPortrayal2D getHumanPortrayal() {
		OvalPortrayal2D hPortrayal = new OvalPortrayal2D()
		{
			public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
			{
				Human h = (Human)object;
				//make orange if sick
				Human.Condition c = h.getCondition();
				int age = h.getAge();
				if (c== Human.Condition.SICK)
					paint=new Color(255, 128, 0);
				else if (age<15)
					paint=new Color(153, 51, 255);
				else if (c== Human.Condition.FINE)
					paint=new Color(0, 128,255);
				scale=1;
				super.draw(object, graphics, info);  // it'll use the new paint and scale values
			}
		};
		return hPortrayal;
	}

	public RectanglePortrayal2D getDoctorPortrayal() {
		RectanglePortrayal2D dPortrayal = new RectanglePortrayal2D()
		{
			public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
			{
				Doctor d = (Doctor)object;
				//make orange if sick
				Human.Condition c = d.getCondition();
				int age = d.getAge();
				if (c== Human.Condition.SICK)
//					paint = new Color(255, 255, 0);
					paint=new Color(255, 128, 0);
				else if (age<15)
//					paint = new Color(153, 51, 255);
					paint=new Color(153, 51, 255);
				else if (c== Human.Condition.FINE)
//					paint=new Color(0,153,153);
					paint=new Color(0, 128,255);
				scale=1;
				super.draw(object, graphics, info);  // it'll use the new paint and scale values
			}
		};
		return dPortrayal;
	}

//	private OvalPortrayal2D getFemaleHumanPortrayal() {
//		OvalPortrayal2D malePortrayal = new OvalPortrayal2D()
//		{
//			public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
//			{
//				Human female = (Human)object;
//				//make orange if sick
//				Human.Condition c = female.getCondition();
//				int age = female.getAge();
//				if (c== Human.Condition.SICK)
//					paint=new Color(255, 255, 0);
//				else if (age<15)
//					paint=new Color(153, 51, 255);
//				else if (c== Human.Condition.FINE)
//					paint=new Color(204,0,204);
//				scale=1;
//				super.draw(object, graphics, info);  // it'll use the new paint and scale values
//			}
//		};
//		return malePortrayal;
//	}

	public HexagonalPortrayal2D getVirusPortrayal() {
		HexagonalPortrayal2D r = new HexagonalPortrayal2D();
		r.paint = Color.RED;
		r.filled = true;
		return r;
	}

	private HexagonalPortrayal2D getMedicinePortrayal() {
		HexagonalPortrayal2D r = new HexagonalPortrayal2D();
		r.paint=new Color(255, 0,255);
		r.filled = true;
		return r;
	}
	private OvalPortrayal2D getFoodPortrayal() {
		OvalPortrayal2D r = new OvalPortrayal2D();
		//r.paint = Color.GREEN;
		r.paint = new Color(54, 178, 42);
		//54 178 42
		r.filled = true;
		return r;
	}
	
	private RectanglePortrayal2D getObstaclePortrayal() {
		RectanglePortrayal2D r = new RectanglePortrayal2D();
		r.paint = new Color(24, 40, 79);
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

	public ObjectGridPortrayal2D getYardPortrayal(){ return yardPortrayal; }
	public void setYardPortrayal(){ this.yardPortrayal = yardPortrayal; }
}
