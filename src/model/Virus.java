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
	private int timeBeforeActivation; // durée avant d'être ressenti par
										// l'humain
	private int gravity; // gravité de la maladie (impact sur la santé lorsque
							// le virus est actif)

	private int x, y;

	// Stoppable.
	private Stoppable stoppable;

	private Beings beings;

	// Cases contenant des humains et leur distance par rapport au virus.
	private ArrayList<Case> humanCases;

	public Virus(int _gravity, int _moveRange, int _infectingArea, int _propagationDuration, int _nbHumanToInfect,
			int _timeBeforeActivation) {
		moveRange = _moveRange;
		infectingArea = _infectingArea;
		propagationDuration = _propagationDuration;
		nbHumanToInfect = _nbHumanToInfect;
		timeBeforeActivation = _timeBeforeActivation;
	}

	@Override
	public void step(SimState state) {
		beings = (Beings) state;
		if (isAlive())
			move();
	}

	private boolean isAlive() {
		if (propagationDuration == 0) {

			// Suppression dans la grille.
			beings.yard.set(x, y, null);
			// Suppression de l'agent dans le scheduling
			stoppable.stop();

			return false;
		}
		return true;
	}

	private void move() {
		// Un d�placement coute un point de dur�e.
		propagationDuration--;
		
		int oldx = x;
		int oldy = y;

		// D�placement al�atoire.

        x = beings.yard.stx(x + ThreadLocalRandom.current().nextInt(1, moveRange + 1));
        y = beings.yard.sty(y + ThreadLocalRandom.current().nextInt(1, moveRange + 1));

		beings.yard.set(x, y, this);
		beings.yard.set(oldx, oldy, null);

		detectInfectableHumans();
		infect();
	}

	private void detectInfectableHumans() {
		humanCases = new ArrayList<Case>();

		int x_depart = x - infectingArea;
		int y_depart = y - infectingArea;

		int x_fin = x + infectingArea;
		int y_fin = y + infectingArea;

		// Parcours de toutes les cases
		for (int indexX = x_depart; indexX <= x_fin; ++indexX) {
			for (int indexY = y_depart; indexY <= y_fin; ++indexY) {
							
				// Pour ne pas sortir de la grille
                int realX = beings.yard.stx(indexX);
                int realY = beings.yard.sty(indexY);
				
				// Objet aux coordonn�es
				Object object = beings.yard.get(realX, realY);
				if (object != null) {
					// Si la case contient un objet Human
					if (object instanceof Human) {
						// Ajout de la case
						humanCases.add(new Case(realX, realY));
					}
				}
			}
		}
	}

	private void infect() {
		// Une infection coute 5 point de dur�e.
		//propagationDuration -= 5;

		int nbInfectedHuman = 0;

		// D�tection des humains dans la zone de contamination
		// Les humains infect�s al�atoirement.

		// S'il y a des humains atteignables
		if (!humanCases.isEmpty()) {

			// Tant que le nombre max d'humain � infecter n'a pas �t�
			// atteint.
			while (nbInfectedHuman <= nbHumanToInfect && humanCases.size() > 0) {

				// On r�cup�re une case al�atoire dans la liste.
				int indexhuman = ThreadLocalRandom.current().nextInt(0, humanCases.size());
				Case hcase = humanCases.get(indexhuman);

				Object object = beings.yard.get(hcase.getX(), hcase.getY());

				// Si la case contient bien un humain sain.
				if (object instanceof Human && ((Human)object).getCondition() == Human.Condition.FINE ) {
					Human h = (Human) object;

					// L'humain est infect�.
					System.out.println("Humain infect�");
					h.setCondition(Condition.SICK);
					h.setTimeBeforeSuffering(timeBeforeActivation);
					h.setInfectionGravity(gravity);
					nbInfectedHuman++;
				}
				humanCases.remove(indexhuman);
			}
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

	public void setStoppable(Stoppable stoppable) {
		this.stoppable = stoppable;
	}

}
