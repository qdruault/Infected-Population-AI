package res.values;

/**
 * Created by Louis on 20/05/2017.
 */
public class Constants {

    public static int GRID_SIZE = 35;

    // BEGINNING parameters
    public static int NUM_HUMANS = 75;
    public static int NUM_DOCTORS = 25;
    public static int NUM_FOODS = 20;

    // HUMAN parameters
    public static int NB_DIRECTIONS = 8;
    public static int MAX_HEALTH = 100;
    public static int PASSIVE_HEALTH_GAIN = 5;
    public static int MID_HEALTH = 60;
    public static int LOW_HEALTH = 30;
    public static int MAX_SURVIVAL = 100;
    public static int MAX_GRATIFICATION = 100;
    public static int GRATIFICATION_LOSS = 3;  // HERE
    public static int MAX_IMMUNITY = 100;
    public static int LOW_IMMUNITY = 30;
    public static int MAX_FERTILITY = 100;
    public static int MAX_AGE = 100;
    public static int MAX_AGE_START = 25;
    public static int MAX_VISION = 4;
    public static int STARVATION_LOSS = 5;    // HERE
    public static float TRANSMISSION_PROBABILITY_0 = 0.1f;
    public static float TRANSMISSION_PROBABILITY_1 = 0.5f;
    public static float TRANSMISSION_PROBABILITY_2 = 0.75f;
    public static float DOCTOR_PROBABILITY_2 = 0.75f;
    public static float DOCTOR_PROBABILITY_1 = 0.5f;
    public static float DOCTOR_PROBABILITY_0 = 0.25f;
    public static int IMPROVE_IMMUNITY_HEALED=4;
    public static int IMPROVE_IMMUNITY_CURED=2;
    public static final int MIN_AGE_PROCREATE = 15;
    public static final int MAX_AGE_PROCREATE = 80;
    public static final int TIME_BETWEEN_PROCREATION = 10; // HERE


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
    public static int QUANTITY_PER_MEDICINE = 3;
    public static int NUM_MEDICINE = 5;
    public static int MAX_MEDICINE_QUANTITY = 100;
    public static int MAX_EXPIRATION_DATE = 10;

    // FOOD parameters
    public static int MAX_NUTRITIONAL_PROVISION = 10;
    public static int MAX_FOOD_QUANTITY = 5;
    public static int ROTTING_DURATION = 10;
    public static final int MAX_FOOD = 15;
    public static final int MIN_FOOD = 10;
    
    // VIRUS parameters
    public static final int MAX_MOVE_RANGE = 3;
    public static final int MAX_INFECTING_ZONE = 5;
    public static final int MAX_PROPAGATION_DURATION = 15;
    public static final int MAX_NB_HUMAN_TO_CONTAMINATE = 2;
    public static final int MAX_GRAVITY = 5;
    public static final int MAX_TIME_BEFORE_ACTIVATION=3;

    // ENVIRONMENT parameters
    public static int BASE_FOOD = 2;
    public static int BASE_MEDICINE = 1;
    public static final int FAMINE_DURATION = 15;
    public static final float FAMINE_PROBABILITY = 0.5f;
    public static final int SHORTAGE_DURATION = 10;
    public static final float SHORTAGE_PROBABILITY = 0.05f;
    public static final int SHORTAGE_REDUCTION = 4;
    public static final int VIRUS_NB_TOUR = 10;
    public static final int FOOD_NB_TOUR = 5;
    public static final int MEDICINE_NB_TOUR = 15;
    public static final int STARVATION_GAP = 200;
    public static final int MAX_NB_HUMAN = 200;
}
