package model;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import model.Human.Condition;
import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Bag;

public class Virus implements Steppable {

	private int moveRange; // distance de propagation du virus
	private int infectingArea; // zone de contamination
	private int propagationDuration; // temps avant la disparition du virus
	private int nbHumanToInfect; // nombre d'humain � infecter
	private int timeBeforeActivation; // durée avant d'être ressenti par l'humain

	private int x, y;


	// Stoppable.
	private Stoppable stoppable;

	// Cases contenant des humains et leur distance par rapport au virus.
	private ArrayList<Case> humanCases;


	//TODO homogenize, add parameters and move the random generation into the Beings class
	public Virus(int _timeBeforeActivation) {
		moveRange = ThreadLocalRandom.current().nextInt(1, Constants.MAX_MOVE_RANGE + 1);
		infectingArea = ThreadLocalRandom.current().nextInt(1, Constants.MAX_INFECTING_ZONE + 1);
		propagationDuration = ThreadLocalRandom.current().nextInt(1, Constants.MAX_PROPAGATION_DURATION + 1);
		nbHumanToInfect = ThreadLocalRandom.current().nextInt(1, Constants.MAX_NB_HUMAN_TO_CONTAMINATE + 1);
		timeBeforeActivation=_timeBeforeActivation;
	}

	@Override
	public void step(SimState state) {
		Beings beings = (Beings) state;
		if (isAlive(beings))
			move(beings);
	}

	private boolean isAlive(Beings beings) {
		if (propagationDuration == 0) {

			// Suppression dans la grille.
			beings.yard.set(x, y, null);
			// Suppression de l'agent dans le scheduling
			stoppable.stop();

			return false;
		}
		return true;
	}

	private void move(Beings beings) {
		// Un d�placement coute un point de dur�e.
		propagationDuration--;

		// D�placement al�atoire.
		x += ThreadLocalRandom.current().nextInt(-moveRange, moveRange + 1);
		// Modulo la taille de la grille.
		x %= Constants.GRID_SIZE;
		
		if(x<0)
			x = Constants.GRID_SIZE + x;

		y += ThreadLocalRandom.current().nextInt(-moveRange, moveRange + 1);
		y %= Constants.GRID_SIZE;;
		if(y<0)
			y = Constants.GRID_SIZE + y;
		
		beings.yard.set(x, y, this);
		
		detectInfectableHumans(beings);
		infect(beings);
	}

	private void detectInfectableHumans(Beings beings) {
		humanCases = new ArrayList<Case>();

		int x_depart = x - infectingArea;
		int y_depart = y - infectingArea;

		int x_fin = x + infectingArea;
		int y_fin = y + infectingArea;

		// Parcours de toutes les cases
		for (int indexX = x_depart; indexX <= x_fin; ++indexX) {
			for (int indexY = y_depart; indexY <= y_fin; ++indexY) {
				// Objet aux coordonn�es
				Object object = beings.yard.get(indexX, indexY);
				if (object != null) {
					// Si la case contient un objet Human
					if (object instanceof Human) {
						// Ajout de la case
						humanCases.add(new Case(indexX, indexY));
					}
				}
			}
		}
	}

	private void infect(Beings beings) {
		// Une infection coute 5 point de dur�e.
		propagationDuration -= 5;

		int nbInfectedHuman = 0;

		// D�tection des humains dans la zone de contamination
		// Les humains infect�s al�atoirement.

		// Tant que le nombre max d'humain � infecter n'a pas �t� atteint.
		while (nbInfectedHuman <= nbHumanToInfect) {
			
			// On r�cup�re une case al�atoire dans la liste.
			int indexhuman = ThreadLocalRandom.current().nextInt(1, humanCases.size() + 1);
			Case hcase = humanCases.get(indexhuman);

			Object object = beings.yard.get(hcase.getX(), hcase.getY());

			// Si la case contient bien un humain.
			if(object instanceof Human){
				Human h = (Human) object;

				// L'humain est infect�.
				h.setCondition(Condition.SICK);

				//TODO change the health level of the human, depending on the type of the virus
				h.setTimeBeforeSuffering(timeBeforeActivation);
				nbInfectedHuman++;
			}
			humanCases.remove(indexhuman);
		}
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

}
