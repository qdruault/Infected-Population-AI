package res.values;

/**
 * Created by Louis on 20/05/2017.
 */
public class Constants {

    public static int GRID_SIZE = 20;
    public static int NUM_HUMANS = 30;
    public static int NUM_FOODS = 10;

    // HUMAN parameters
    public static int NB_DIRECTIONS = 8;
    public static int MAX_HEALTH = 100;
    public static int MAX_GRATIFICATION = 100;
    public static int MAX_IMMUNITY = 100;
    public static int MAX_FERTILITY = 100;

    // DOCTOR parameters
    public static int MAX_DRUG_STOCK = 100;
    public static int HEAL_CONSUMMATION = 10;
    public static int HEAL_VALUE = 50;
    public static int HEAL_DIFFICULTY = 2;
    public static int CURE_CONSUMMATION = 30;
    public static int CURE_DIFFICULTY = 3;
    public static int VACCINATE_CONSUMMATION = 50;
    public static int VACCINATE_DIFFICULTY = 1;
    public static int VACCINATE_EFFICIENCY = 100;
    public static float SUCCESS_DIFFICULTY = 0.8f;

    // FOOD parameters
    public static int MAX_NUTRIOTINAL_PROVISION = 100;
    public static int MAX_QUANTITY = 100;
    public static int ROTTING_DURATION = 100;
    
    // VIRUS parameters
    public static final int MAX_MOVE_RANGE = 100;
    public static final int MAX_INFECTING_ZONE = 20;
    public static final int MAX_PROPAGATION_DURATION = 100;
    public static final int MAX_NB_HUMAN_TO_CONTAMINATE = 5;

}
