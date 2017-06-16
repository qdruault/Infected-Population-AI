package res.values;

/**
 * Created by Louis on 20/05/2017.
 */
public class Constants {

    public static int GRID_SIZE = 25;

    // BEGINNING parameters
    public static int NUM_HUMANS = 2;
    public static int NUM_DOCTORS = 0;
    public static int NUM_FOODS = 0;

    // HUMAN parameters
    public static int NB_DIRECTIONS = 8;
    public static int MAX_HEALTH = 100;
    public static int PASSIVE_HEALTH_GAIN = 2;
    public static int MID_HEALTH = 60;
    public static int LOW_HEALTH = 30;
    public static int MAX_SURVIVAL = 100;
    public static int MAX_GRATIFICATION = 100;
    public static int GRATIFICATION_LOSS = 5;
    public static int MAX_IMMUNITY = 100;
    public static int LOW_IMMUNITY = 30;
    public static int MAX_FERTILITY = 100;
    public static int MAX_AGE = 100;
    public static int MAX_VISION = 4;
    public static int STARVATION_LOSS = 10;
    public static int PROCREATION_MULTIPLIER = 100;
    public static float TRANSMISSION_PROBABILITY_1 = 0.5f;
    public static float TRANSMISSION_PROBABILITY_2 = 0.25f;
    public static float DOCTOR_PROBABILITY = 0.7f;
    public static int IMPROVE_IMMUNITY_HEALED=4;
    public static int IMPROVE_IMMUNITY_CURED=2;


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
    public static int IMPROVE_IMMUNITY_DOCTOR_1=5;
    public static int IMPROVE_IMMUNITY_DOCTOR_2=10;

    // MEDICINE parameters
    public static int MAX_MEDICINE_QUANTITY = 100;

    // FOOD parameters
    public static int MAX_NUTRITIONAL_PROVISION = 10;
    public static int MAX_FOOD_QUANTITY = 5;
    public static int ROTTING_DURATION = 100;

    // VIRUS parameters
    public static final int MAX_MOVE_RANGE = 3;
    public static final int MAX_INFECTING_ZONE = 2;
    public static final int MAX_PROPAGATION_DURATION = 10;
    public static final int MAX_NB_HUMAN_TO_CONTAMINATE = 2;
    public static final int MAX_GRAVITY = 5;
    public static final int MAX_TIME_BEFORE_ACTIVATION = 500;

    // ENVIRONMENT parameters
    public static int BASE_FOOD = 5;
    public static int BASE_MEDICINE = 5;
    public static final int FAMINE_DURATION = 10;
    public static final float FAMINE_PROBABILITY = 0.05f;
    public static final int FAMINE_REDUCTION = 4;
}
