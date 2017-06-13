package model;

import res.values.Constants;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.grid.Grid2D;
import sim.util.Bag;

import sim.util.Int2D;
import sim.util.IntBag;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Created by Louis on 20/05/2017.
 */
public class Human implements Steppable {
	
	protected static final long serialVersionUID = 1L;
	protected Beings beings;
	protected Stoppable stoppable;
    protected Bag neighbors = new Bag();
    protected IntBag neighborsPosX = new IntBag();
    protected IntBag neighborsPosY = new IntBag();

    // Age
    protected int age;
    // PV
    protected int health;
    //temps avant l'activation du virus
    protected int timeBeforeSuffering;
    // Capacitï¿½ de rï¿½sistance au virus.
    protected int immunity;
    // Fertilitï¿½.
    protected int fertility;
    // Niveau de statiete
    protected int gratification;
    // Champs de vision
    public int vision;
    // Nombre de cellules de mouvement par tour
    public int move;
    // Homme ou Femme
    protected Gender gender;
    // Malade ou pas
    protected Condition condition;
    // Coordonnï¿½es.
    protected int x;
    protected int y;
    protected int infection_gravity;
    // A MAX_SURVIVAL Ã  la naissance puis diminue avec les maladies et autres 
    // Â± probabilite de mourir
    public int survival;
    public enum Gender {
        MALE,
        FEMALE
    };
    public enum Condition {
        SICK,
        FINE
    };
    protected boolean hasRecentlyProcreated = false;
    

    protected Doctor doctorCalled = null;

    @Override
    public void step(SimState state) {
        beings = (Beings) state;

        hasRecentlyProcreated = false;
        setAge(getAge() + 1);

        // TODO remove the agent from the scheduling
        // remove if needed
        if (mustDie()) {
            beings.yard.set(x, y, null);
            stoppable.stop();
        } else {
            //decrease health level depending on his condition the activation and gravity of the virus
            if (this.condition == Condition.SICK) {
                if (timeBeforeSuffering == 0)
                    health -= infection_gravity;
                else timeBeforeSuffering--;
            } else if (getCondition() == Condition.FINE && getGratification() > 0 && getHealth() < Constants.MID_HEALTH) {
                // Increase the health level if the human is fine
                setHealth(getHealth() + Constants.PASSIVE_HEALTH_GAIN);
            }

            if (getGratification() <= 0) {
                health--; // the Human is Starving
            } else {
                setGratification(getGratification() - Constants.GRATIFICATION_LOSS);
            }

            // Perceive the cells around himself
            perceiveCells(neighborsPosX, neighborsPosY);

            // Eat: Rule about eating, a human always try to eat so that gratification = 100, meaning that he's full
            // Gratification: if the gratification level is only half empty: the stomach is empty and the human is hungry. If it is below this level, the hunger starts to be dangerous
            // TODO add a parameter to define the level of gratification below which each human start looking for food
            // TODO change the behaviour when looking for food, if no food is available on an adjacent cell, the human must look for a cell with food

            // NEED DOCTOR
            if (doctorCalled != null) {
				// On se dirige vers le docteur.
            	moveTowardsDoctor();
			} else if (needEatingStrong()) {
				// NEED TO EAT
                //System.out.println("I strongly need to eat");
                basicNeedEat();
            } else {
                // NEED TO LOOK FOR A DOCTOR
                if (needDoctor()) {
                    //System.out.println("I need a doctor");
                    basicNeedHealth();
                } else {
                    // NEED TO EAT
                    if (needEatingMedium()) {
                        //System.out.println("I need to eat");
                        basicNeedEat();
                    } else {
                        // NEED TO PROCREATE
                        //System.out.println("I need to procreate");
                        basicNeedProcreate();
                    }
                }
            }
        }
    }

    /**
     * Constructeur vide.
     */
    public Human(){}
    
    /**
     * Constructeur
     * @param immunity
     * @param fertility
     * @param gender
     * @param condition
     * @param vision
     */
    public Human(int immunity, int fertility, Gender gender, Condition condition, int vision) {
    	health = Constants.MAX_HEALTH;
        gratification = Constants.MAX_GRATIFICATION;
        age = 0;
		this.immunity = immunity;
		this.fertility = fertility;
		this.gender = gender;
		//TODO the condition is always initialized with FINE
		this.condition = condition;
		this.vision = vision;
		this.survival=Constants.MAX_SURVIVAL;
		this.move = 1;
		this.infection_gravity=0;
	}

	// To create humans at the  beginning of the simulation
    public Human(int immunity, int fertility, Gender gender, int vision, int age){
        health = Constants.MAX_HEALTH;
//        gratification = Constants.MAX_GRATIFICATION;
        gratification = 60;
        this.immunity = immunity;
        this.fertility = fertility;
        this.gender = gender;
        this.condition = Condition.FINE;
        this.vision = vision;
        this.survival=Constants.MAX_SURVIVAL;
        this.age = age;
		this.move = 1;
    }

    //
    // START OF THE BASIC NEED SECTION
    //

    protected void basicNeedEat(){
        System.out.println("Basic Need Eat");
        // Look for food
        Food food = leastRottenFood(lookForAdjacentFood());
        // Eat adjacent food
        if (food != null){
            int quantity;
            while (getGratification() < 100 && food.getQuantity() > 0 ){
                toEat(food, 1);
            }
        } else {
            if (canMove()) {
                // Move to find food
                Int2D foodCase = lookForFoodLocation();

                if (foodCase != null) {
                    moveTowardsCell(foodCase);
                } else {
                    // Move in a random direction and hope to find food
                    moveRandom();
                }
            } else {
//            	Make sure it does not create an infinite loop
            	basicNeedProcreate();	// Instead of waiting depending on what there is on the adjacent cells decide to procreate
            }
        }
    }

    protected void basicNeedHealth(){
        // Call a doctor if not done
        if (doctorCalled == null) {
            Int2D doctorLocation = lookForDoctorLocation();
            if (doctorLocation != null){
                doctorCalled = (Doctor)beings.yard.get(doctorLocation.getX(), doctorLocation.getY());
                callDoctor(doctorCalled);
            }
        }

        // If no doctor available around
        if (doctorCalled == null){
            // Random move
            if (canMove()){
                moveRandom();
            }
        } else {
            // Move to reach the calledDoctor if can move and not already adjacent
            if (!objectIsAdjacent(doctorCalled) && canMove()){
                moveTowardsCell(new Int2D(doctorCalled.getX(), doctorCalled.getY()));
            }
        }
    }


    protected void basicNeedProcreate(){
        System.out.println("I would like to procreate");
        Human human = getHumanOfOppositeGender(lookForAdjacentHumans());
        if (human != null && canProcreateWith(human)) {
            tryToProcreate(human);
        } else {
            if (canMove()) {
                Int2D humanLocation = lookForOppositeGenderHumanLocation();
                if (humanLocation != null) {
                    moveTowardsCell(humanLocation);
                } else {
                    // Move in a random Direction and hope to find a human to procreate with
                    moveRandom();
                	
                }
            } else {
            	basicNeedEat();	          //Instead of waiting depending on what there is on the adjacent cells decide to go and eat
            }
        }
    }

    protected Boolean mustDie(){
        if (health == 0 || survival <= 10 || getAge() >= Constants.MAX_AGE){
            System.out.println("I'm dead, health = "+getHealth()+" age ="+getAge()+" gratification = "+getGratification());
            return true;
        }
        else return false;
    }

    //
    // END OF THE BASIC NEED SECTION
    //




    // TODO try to factorize the three loofForLocation methods
    //
    // START OF THE OBJECTS SEARCH SECTION
    //


    // Check if there are any food available around
    public Int2D lookForFoodLocation(){

        HashMap<Case, Integer> foodCases = new HashMap<Case, Integer>();


        int x_depart = x - vision;
        int y_depart = y - vision;

        int x_fin = x + vision;
        int y_fin = y + vision;

        // Parcours de toutes les cases
        for (int indexX = x_depart; indexX <= x_fin; ++indexX) {
            for (int indexY = y_depart; indexY <= y_fin; ++indexY) {
                // Pour pas sortir de la grille.
                int realX = indexX % Constants.GRID_SIZE;
                if (realX < 0) {
                    realX = Constants.GRID_SIZE + realX;
                }

                int realY = indexY % Constants.GRID_SIZE;
                if (realY < 0) {
                    realY = Constants.GRID_SIZE + realY;
                }

                // Objet aux coordonnï¿½es
                Object object = beings.yard.get(realX, realY);
                if (object != null) {
                    // Si la case contient un objet Human
                    if (object instanceof Food) {
                        // Ajout de la case avec sa distance.
                        Integer distance = Math.max(Math.abs(indexX - x), Math.abs(indexY - y));
                        foodCases.put(new Case(indexX, indexY), distance);
                    }
                }
            }
        }

        // On cherche la plus proche.
        Int2D res = null;
        Integer minD = Constants.GRID_SIZE;

        Iterator<Entry<Case, Integer>> it = foodCases.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            Integer value = (Integer)pair.getValue();
            Case key = (Case)pair.getKey();
            if(value < minD) {
                minD = value;
                res = new Int2D(key.getX(), key.getY());
            }
        }
        return res;
    }

    // Check if there are any food available around
    public Int2D lookForDoctorLocation(){

        HashMap<Case, Integer> doctorCases = new HashMap<Case, Integer>();


        int x_depart = x - vision;
        int y_depart = y - vision;

        int x_fin = x + vision;
        int y_fin = y + vision;

        // Parcours de toutes les cases
        for (int indexX = x_depart; indexX <= x_fin; ++indexX) {
            for (int indexY = y_depart; indexY <= y_fin; ++indexY) {
                // Pour pas sortir de la grille.
                int realX = indexX % Constants.GRID_SIZE;
                if (realX < 0) {
                    realX = Constants.GRID_SIZE + realX;
                }

                int realY = indexY % Constants.GRID_SIZE;
                if (realY < 0) {
                    realY = Constants.GRID_SIZE + realY;
                }

                // Objet aux coordonnï¿½es
                Object object = beings.yard.get(realX, realY);
                if (object != null) {
                    // Si la case contient un objet Docteur
                    if (object instanceof Doctor) {
                        // Ajout de la case avec sa distance.
                        Integer distance = Math.max(Math.abs(indexX - x), Math.abs(indexY - y));
                        doctorCases.put(new Case(indexX, indexY), distance);
                    }
                }
            }
        }

        // On cherche la plus proche.
        Int2D res = null;
        Integer minD = Constants.GRID_SIZE;

        Iterator<Entry<Case, Integer>> it = doctorCases.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            Integer value = (Integer)pair.getValue();
            Case key = (Case)pair.getKey();
            if(value < minD) {
                minD = value;
                res = new Int2D(key.getX(), key.getY());
            }
        }
        return res;
    }

    public Int2D lookForOppositeGenderHumanLocation(){

        HashMap<Case, Integer> humanCases = new HashMap<Case, Integer>();


        int x_depart = x - vision;
        int y_depart = y - vision;

        int x_fin = x + vision;
        int y_fin = y + vision;

        // Parcours de toutes les cases
        for (int indexX = x_depart; indexX <= x_fin; ++indexX) {
            for (int indexY = y_depart; indexY <= y_fin; ++indexY) {
                // Pour pas sortir de la grille.
                int realX = indexX % Constants.GRID_SIZE;
                if (realX < 0) {
                    realX = Constants.GRID_SIZE + realX;
                }

                int realY = indexY % Constants.GRID_SIZE;
                if (realY < 0) {
                    realY = Constants.GRID_SIZE + realY;
                }

                // Objet aux coordonnï¿½es
                Object object = beings.yard.get(realX, realY);
                if (object != null) {
                    // Si la case contient un objet Humain
                    if (object instanceof Human && ((Human) object).getGender() != getGender()) {
                        // Ajout de la case avec sa distance.
                        Integer distance = Math.max(Math.abs(indexX - x), Math.abs(indexY - y));
                        humanCases.put(new Case(indexX, indexY), distance);
                    }
                }
            }
        }

        // On cherche la plus proche.
        Int2D res = null;
        Integer minD = Constants.GRID_SIZE;

        Iterator<Entry<Case, Integer>> it = humanCases.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            Integer value = (Integer)pair.getValue();
            Case key = (Case)pair.getKey();
            if(value < minD) {
                minD = value;
                res = new Int2D(key.getX(), key.getY());
            }
        }

        //System.out.println("Cible x : " + res.x);
        //System.out.println("Cible y : " + res.y);

        return res;
    }

    // TODO find a clean way to factorize these two methods, Java Generics aren't very advisable here
    // Return the adjacent humans
    protected Bag lookForAdjacentHumans(){
        Bag humans = new Bag();
        Bag neighbors = beings.getAdjacentCells(getX(), getY());

        Object currentNeighbor = neighbors.pop();

        for (int i = 0; i < 8; i++){
            if (currentNeighbor instanceof  Human) {
                System.out.println("FOUND FOOD");
                humans.add(currentNeighbor);
            }
            currentNeighbor = neighbors.pop();
        }
        return humans;
    }

    // Return the adjacent food
    protected Bag lookForAdjacentFood(){
        Bag foods = new Bag();
        Bag neighbors = beings.getAdjacentCells(getX(), getY());

        System.out.println(neighbors.size());
        Object currentNeighbor = neighbors.pop();

        for (int i = 0; i < 8; i++){
            if (currentNeighbor instanceof  Food) {
                System.out.println("FOUND FOOD");
                foods.add(currentNeighbor);
            }
            currentNeighbor = neighbors.pop();
        }
        return foods;
    }

    //Perceive the cells around, should be called at the beginning of each step
    public void perceiveCells(IntBag xPos, IntBag yPos){
        neighbors = beings.yard.getRadialNeighbors(x, y, vision ,Grid2D.TOROIDAL, false, new Bag(), xPos, yPos);
    }

    // Return a human of requested gender
    protected Human getHumanOfOppositeGender(Bag humans){
        Human human;
        Bag availableHumans = new Bag();
        while((human = (Human) humans.pop()) != null){
            if (human.getGender() != getGender()){
                availableHumans.add(human);
            }
        }
        // if there are several possibilities, return a random
        if (!availableHumans.isEmpty())
            return (Human)availableHumans.get(beings.random.nextInt(availableHumans.size()));
        return null;
    }

    // True if the given object is adjacent to the human
    protected boolean objectIsAdjacent(Object o){
        return beings.getAdjacentCells(getX(), getY()).contains(o);
    }

    //
    // END OF THE OBJECTS SEARCH SECTION
    //




    //
    // START OF THE MOVEMENT SECTION
    //

    // Move one cell in a random direction
    protected void moveRandom(){
        int direction = beings.random.nextInt(7);
        int xRes = getX();
        int yRes = getY();

        switch (direction){
            case 0:
                // UP
                yRes = beings.yard.sty(--yRes);
                break;
            case 1:
                // RIGHT
                xRes = beings.yard.stx(++xRes);
                break;
            case 2:
                // DOWN
                yRes = beings.yard.sty(++yRes);
                break;
            case 3:
                // LEFT
                xRes = beings.yard.stx(--xRes);
                break;
            case 4:
                // TOP RIGHT
                xRes = beings.yard.stx(++xRes);
                yRes = beings.yard.sty(--yRes);
            case 5:
                // TOP LEFT
                xRes = beings.yard.stx(--xRes);
                yRes = beings.yard.sty(--yRes);
            case 6:
                // BOTTOM RIGHT
                xRes = beings.yard.stx(++xRes);
                yRes = beings.yard.sty(++yRes);
            case 7:
                // BOTTOM LEFT
                xRes = beings.yard.stx(--xRes);
                yRes = beings.yard.sty(++yRes);
            default:
                break;
        }
        if (canMoveOn(xRes, yRes)) {
            beings.yard.set(getX(), getY(), null);
            beings.yard.set(xRes, yRes, this);
            setX(xRes);
            setY(yRes);
        } else moveRandom();
    }

    public void move(Int2D position){
        if(position==null){

            beings.yard.set(beings.yard.stx(x), beings.yard.sty(y),null);
            x++;
            y++;
            x %= Constants.GRID_SIZE;
            y %= Constants.GRID_SIZE;

            beings.yard.set(beings.yard.stx(x), beings.yard.sty(y),this);

        }else
            moveTowardsCell(position);
    }

    // TODO prevent the human from going on an occupied cell.
    // Move toward the given cell until it's reached or the human can't move anymore
    public void moveTowardsCell(Int2D position){
        int diffX = position.x - x;
        int diffY = position.y - y;
        int movesLeft = move;

        int resultX = x;
        int resultY = y;

        // Move until there's no more move possible or the target is reached
        while (movesLeft > 0 && (diffX != 0 || diffY != 0)){
            if (diffX != 0){
                int increment = (diffX > 0) ? 1 : -1;
                resultX += increment;
                diffX += -increment;
                movesLeft --;
            }
            if (diffY != 0) {
                int increment = (diffY > 0) ? 1 : -1;
                resultY += increment;
                diffY += -increment;
                movesLeft --;
            }
        }

        if(resultX != x || resultY != y){

            if (resultX > Constants.GRID_SIZE-1)
                resultX=0;

            if (resultX < 0)
                resultX=Constants.GRID_SIZE-1;

            if (resultY > Constants.GRID_SIZE-1)
                resultY=0;

            if (resultY < 0)
                resultY = Constants.GRID_SIZE-1;


            if (canMoveOn(resultX, resultY)) {
                beings.yard.set(beings.yard.stx(resultX), beings.yard.sty(resultY), this);

                beings.yard.set(beings.yard.stx(getX()), beings.yard.sty(getY()), null);

                setX(resultX);
                setY(resultY);
            } else {
                // TODO create a new strategy to move in an intelligent way
                moveRandom();
            }
        }
    }

    protected boolean canMoveOn(int x, int y){
        return beings.yard.get(x, y) == null;
    }

    protected boolean canMove(){
        return beings.getFreeAdjacentCell(getX(), getY()) != null;
    }

    //
    // END OF THE MOVEMENT SECTION
    //




    //
    // START OF THE EATING SECTION
    //

    public void toEat(Food f, int quantity){
        int quantityEaten = f.consume(quantity);
        gratification = Math.min(gratification + quantityEaten * f.getNutritionalProvision(), Constants.MAX_GRATIFICATION);
    }

    // Return non rotten food in priority
    protected Food leastRottenFood(Bag foods){
        Object food = foods.pop();
        Food leastRottenFood = null;
        while(food != null) {
            if (leastRottenFood == null || (food != null && food instanceof Food && !((Food) food).isRotten() && leastRottenFood.isRotten())) {
                leastRottenFood = (Food) food;
            }
            food = foods.pop();
        }
        System.out.println(leastRottenFood);
        return leastRottenFood;
    }

    protected boolean needEatingStrong(){
        return (getGratification() < 0.2f * Constants.MAX_GRATIFICATION);
    }
    protected boolean needEatingMedium(){
        return (getGratification() < 0.6f * Constants.MAX_GRATIFICATION);
    }

    //
    // END OF THE EATING SECTION
    //




    //
    // START OF THE PROCREATION SECTION
    //

    // TODO add a pregnancy mecanism
    public void toProcreate(Human h){
        System.out.println("I want to procreate, age 1 ="+this.getAge()+" age 2 ="+h.getAge()+" gender 1 ="+this.getGender()+" gender 2 ="+h.getGender());

        if(this.getGender()!=h.getGender() && this.getAge()>15 && this.getAge()<80 && h.getAge()>15 && h.getAge()<80){
            if ((gender == Gender.FEMALE && beings.getFreeAdjacentCell(x, y) != null) || beings.getFreeAdjacentCell(h.getX(), h.getY()) != null)
            {
                System.out.println("I have a child!");

                int immunity = beings.random.nextInt(Constants.MAX_IMMUNITY);
                int fertility = beings.random.nextInt(Constants.MAX_FERTILITY);
                Gender gender = (beings.random.nextInt(2) == 0) ? Gender.MALE : Gender.FEMALE;
                int vision = beings.random.nextInt(Constants.MAX_VISION);

                Condition condition = Condition.FINE;
                float conditionResult = beings.random.nextFloat();
                if (getCondition() == Condition.SICK && h.getCondition() == Condition.SICK) {
                    if (conditionResult < Constants.TRANSMISSION_PROBABILITY_2)
                        condition = Condition.SICK;
                } else if (getCondition() == Condition.SICK || h.getCondition() == Condition.SICK) {
                    if (conditionResult < Constants.TRANSMISSION_PROBABILITY_1)
                        condition = Condition.SICK;
                }

                float doctorProbability =  beings.random.nextFloat();
                if (doctorProbability> Constants.DOCTOR_PROBABILITY){
                    float skill = beings.random.nextFloat();
                    Doctor child = new Doctor(immunity, fertility, gender, condition, vision, skill);
                    Case pos = beings.getFreeAdjacentCell(getX(), getY());
                    beings.yard.set(pos.getX(), pos.getY(), child);
                    child.setX(pos.getX());
                    child.setY(pos.getY());
                    beings.schedule.scheduleRepeating(child);
                }
                else {
                    Human child = new Human(immunity, fertility, gender, condition, vision);
                    Case pos = beings.getFreeAdjacentCell(getX(), getY());
                    beings.yard.set(pos.getX(), pos.getY(), child);
                    child.setX(pos.getX());
                    child.setY(pos.getY());
                    beings.schedule.scheduleRepeating(child);
                }
            }
        }
    }

    // Try to procreate based on both fertilities
    public void tryToProcreate(Human h){
        float fertilityProbability1 = (float) fertility  / (float) Constants.MAX_FERTILITY;
        float fertilityProbability2 = (float) h.getFertility() / (float) Constants.MAX_FERTILITY;

        float successProbability = fertilityProbability1 * fertilityProbability2;
        float f = 1f - successProbability;
        successProbability = 1f - ( f / (float) Constants.PROCREATION_MULTIPLIER);

        if (beings.random.nextFloat() < successProbability){
            toProcreate(h);
        }
        hasRecentlyProcreated = true;
    }

    public boolean canProcreateWith(Human h){
        System.out.println("Data about both humans: " + this.getGender() + " " + h.getGender() + " " + this.getAge() + " " + h.getAge());
        return (this.getGender()!=h.getGender() && this.getAge()>15 && this.getAge()<80 && h.getAge()>15 && h.getAge()<80 && !h.getHasRecentlyProcreated());
    }

    //
    // END OF THE PROCREATION SECTION
    //




    //
    // START OF THE DOCTOR SECTION
    //

    /**
     * Ask to be curated by a doctor in the perception zone
     * @return true if a doctor was called
     */
    // TODO call the nearest doctor around
    public boolean callDoctor() {
        boolean success = false;
        for (Object object: neighbors){
            if (object instanceof Doctor){
                ((Doctor) object).processRequest(this);
                success =true;
            }
            //call only one doctor per step
            if (success) break;
        }
        return success;
    }

    // Call the doctor passed as a parameter
    public void callDoctor(Doctor doctor){
    	System.out.println("j'appelle un docteur.");
        doctor.processRequest(this);
    }

    protected boolean needDoctor(){
    	System.out.println("J'ai besoin d'un docteur.");
        return (health < Constants.LOW_HEALTH || getCondition() == Condition.SICK);
    }
    
    /**
     * Se déplace vers le docteur appelé.
     */
    protected void moveTowardsDoctor() {
    	System.out.println("Je m'approche d'un docteur.");
    	Int2D positionDoctor = new Int2D(doctorCalled.getX(), doctorCalled.getY());
    	moveTowardsCell(positionDoctor);
    }

    protected boolean needHealing() { return health < Constants.LOW_HEALTH; }

    protected boolean needCuration() { return getCondition() == Condition.SICK; }

    protected boolean needVaccination() { return immunity < Constants.LOW_IMMUNITY; }

    //
    // END OF THE DOCTOR SECTION
    //




	// Getters and setters.
    public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
	public void addHealth(int health) {
		this.health += health;
	}

	public void setTimeBeforeSuffering(int time){
        this.timeBeforeSuffering=time;
    }
	public int getImmunity() {
		return immunity;
	}

	public void setImmunity(int immunity) {
		this.immunity = immunity;
	}
	
	public void addImmunity(int immunity) {
		this.immunity += immunity;
	}

	public int getFertility() {
		return fertility;
	}

	public void setFertility(int fertility) {
		this.fertility = fertility;
	}

	public int getGratification() {
		return gratification;
	}

	public void setGratification(int gratification) {
		this.gratification = gratification;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
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

	public int getInfectionGravity(){
	    return infection_gravity;
    }
    public void setInfectionGravity (int _infection_gravity){
	    this.infection_gravity =_infection_gravity;
    }
    public int getVision() {
		return vision;
	}

	public void setVision(int vision) {
		this.vision = vision;
	}

	public void setStoppable(Stoppable stoppable){ this.stoppable = stoppable; }

	public Stoppable getStoppable(){ return this.stoppable; }

	public boolean getHasRecentlyProcreated(){ return this.hasRecentlyProcreated; }

	public void setHasRecentlyProcreated(boolean b){ this.hasRecentlyProcreated = b; }
}