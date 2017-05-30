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
	private int nbHumanToInfect; // nombre d'humain à infecter

	private int x, y;

	// Stoppable.
	private Stoppable stoppable;

	// Cases contenant des humains et leur distance par rapport au virus.
	private ArrayList<Case> humanCases;

	public Virus() {
		moveRange = ThreadLocalRandom.current().nextInt(1, Constants.MAX_MOVE_RANGE + 1);
		infectingArea = ThreadLocalRandom.current().nextInt(1, Constants.MAX_INFECTING_ZONE + 1);
		propagationDuration = ThreadLocalRandom.current().nextInt(1, Constants.MAX_PROPAGATION_DURATION + 1);
		nbHumanToInfect = ThreadLocalRandom.current().nextInt(1, Constants.MAX_NB_HUMAN_TO_CONTAMINATE + 1);

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
			beings.yard.removeObjectsAtLocation(x, y);
			// Suppression de l'agent dans le scheduling
			stoppable.stop();

			return false;
		}
		return true;
	}

	private void move(Beings beings) {
		// Un déplacement coute un point de durée.
		propagationDuration--;

		// Déplacement aléatoire.
		x += ThreadLocalRandom.current().nextInt(-moveRange, moveRange + 1);
		// Modulo la taille de la grille.
		x %= Constants.GRID_SIZE;
		
		if(x<0)
			x = -x;

		y += ThreadLocalRandom.current().nextInt(-moveRange, moveRange + 1);
		y %= Constants.GRID_SIZE;;
		if(y<0)
			y = -y;
		
		beings.yard.setObjectLocation(this, x, y);
		
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
				// Objets aux coordonnées
				Bag bag = beings.yard.getObjectsAtLocation(indexX, indexY);
				if (bag != null) {
					for (int i = 0; i < bag.size(); i++) {
						// Si la case contient un objet Human
						if (bag.get(i).getClass().equals(Human.class)) {
							// Ajout de la case
							humanCases.add(new Case(indexX, indexY));
						}

					}
				}

			}
		}

	}

	private void infect(Beings beings) {
		// Une infection coute 5 point de durée.
		propagationDuration -= 5;

		int nbInfectedHuman = 0;

		// Détection des humains dans la zone de contamination
		// Les humains infectés aléatoirement.

		// Tant que le nombre max d'humain à infecter n'a pas été atteint.
		while (nbInfectedHuman <= nbHumanToInfect) {
			
			// On récupère une case aléatoire dans la liste.
			int indexhuman = ThreadLocalRandom.current().nextInt(1, humanCases.size() + 1);
			Case hcase = humanCases.get(indexhuman);

			Bag bag = beings.yard.getObjectsAtLocation(hcase.getX(), hcase.getY());
			
			// Si la case contient bien un humain.
			if (bag.get(0).getClass().equals(Human.class)) {
				Human h = (Human) bag.get(0);

				// L'humain est infecté.
				h.setCondition(Condition.SICK);

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
