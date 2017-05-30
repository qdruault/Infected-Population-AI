package model;

import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Created by Louis on 20/05/2017.
 */
public class Food implements Steppable {


	private static final long serialVersionUID = 1L;
	// Coordonn�es.
	private int x;
    private int y;

    // Dur�e avent que �a pourisse.
    private int rottingIn = Constants.ROTTING_DURATION;
    // Pourri.
    private boolean rotten;
    // Nutriments apport�s en la mangeant.
    private int nutritionalProvision;
    // Nombre de portions.
    private int quantity;
    
    /**
     * Constructeur
     * @param nutritionalProvision
     * @param quantity
     */
    public Food(int nutritionalProvision, int quantity) {
		this.nutritionalProvision = nutritionalProvision;
		this.quantity = quantity;
		rotten = false;
	}
    
    // Getters and setters.
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

	public int getRottingIn() {
		return rottingIn;
	}

	public void setRottingIn(int rottingIn) {
		this.rottingIn = rottingIn;
	}

	public boolean isRotten() {
		return rotten;
	}

	public void setRotten(boolean rotten) {
		this.rotten = rotten;
	}

	public int getNutritionalProvision() {
		return nutritionalProvision;
	}

	public void setNutritionalProvision(int nutritionalProvision) {
		this.nutritionalProvision = nutritionalProvision;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}


	@Override
    public void step(SimState state) {
        Beings beings = (Beings) state;

        // remove if needed
        if (mustDisappear()){
            beings.yard.remove(this);
        }
        // Rotting
        if (!rotten){
        	// Il se p�rime.
            rottingIn -= 5;
            if (rottingIn <= 0) {
            	// Compl�tement pourri.
                rotten = true;
                // On diminue sa qualit� nutritionnelle.
                nutritionalProvision *= 0.25;
            }
        }
    }

    
	/**
     * Consommation de la nourriture.
     * @param q : quantit� voulue.
     * @return : quantit� consomm�e.
     */
    public int consume(int q){
        if(quantity > q){
            quantity -= q;
            return q;
        } else {
            q -= quantity;
            quantity = 0;
            return q;
        }
    }

    /**
     * Quand il n'y a plus de nourriture.
     * @return
     */
    public Boolean mustDisappear(){
        return quantity == 0;
    }
}
