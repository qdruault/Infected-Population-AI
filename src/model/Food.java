package model;

import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Int2D;

/**
 * Created by Louis on 20/05/2017.
 */
public class Food implements Steppable {

	private Beings beings;
	private Stoppable stoppable;

	private static final long serialVersionUID = 1L;
	// Coordonnees.
	private int x;
    private int y;

    // Duree avent que la nourriture pourisse.
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
    public Food(int quantity, int nutritionalProvision, Beings beings) {
		this.nutritionalProvision = nutritionalProvision;
		this.quantity = quantity;
		rotten = false;
		this.beings = beings;
		
		// MAJ des stats.
		beings.increaseNbFood(quantity);
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

	public void setStoppable(Stoppable stoppable){ this.stoppable = stoppable; }


	@Override
    public void step(SimState state) {
        beings = (Beings) state;

        // remove if needed
        if (mustDisappear()){
            beings.yard.set(getX(), getY(), null);
			stoppable.stop();
//            addFood();      
			beings.decreaseNbFood(quantity);
        } else {
			// Rotting
			if (!rotten) {
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
    }

	/**
     * Consommation de la nourriture.
     * @param q : quantit� voulue.
     * @return : quantit� consomm�e.
     */
    public int consume(int q){
        if(quantity > q){
            quantity -= q;
            beings.decreaseNbFood(q);
            return q;
        } else {
            q = quantity;
            quantity = 0;
            beings.decreaseNbFood(q);
            return q;
        }
    }

    /**
     * Quand il n'y a plus de nourriture.
     * Ou qu'elle est trop pourrie
     * @return
     */
    //TODO : changer le rotten pour avoir une condition de disparition quand la nourriture est trop périmée
    public Boolean mustDisappear(){
        if (quantity == 0)
            return true;
        else return false;
    }
       
    private void addFood(){
    	System.out.println(" Food added ");
    	int quantity = beings.random.nextInt(Constants.MAX_FOOD_QUANTITY);
        int nutritionalProvision = beings.random.nextInt(Constants.MAX_NUTRITIONAL_PROVISION);
        Food  a  =  new Food(nutritionalProvision,quantity, beings);
    	System.out.println(" Food added 2");
		Int2D location = beings.freeLocation();
    	System.out.println(" Food added  3");
		beings.yard.set(location.x, location.y, a);
		a.setX(location.x);
		a.setY(location.y);
		beings.schedule.scheduleRepeating(a);
    }
}
