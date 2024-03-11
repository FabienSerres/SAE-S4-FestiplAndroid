package fr.iut.festiplandroid.utils;

/**
 * Utility class containing methods for FestiPlAndroid application.
 */
public class Utils {

    private final static String LOGIN_DEV = "identifiant";
    private final static String PASSWORD_DEV = "password123";
    
    public static String[] INFO_API_ID = new String[2];


    /**
     * Check if the param given by the user are valid (for the moment they need to be equals to
     * the LOGIN and PASSWORD _DEV) and if they are, connect him.
     *
     * @param id the id given by the user
     * @param password the password given by the user
     * @return true if the information of connection are true, false otherwise
     */
    public static boolean connect(String id, String password) {
        return id.equals(LOGIN_DEV) && password.equals(PASSWORD_DEV);
    }

}
