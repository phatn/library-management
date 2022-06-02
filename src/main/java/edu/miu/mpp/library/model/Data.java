package edu.miu.mpp.library.model;
import java.util.*;

/**
 * This class stores "dummy data" for the Gui. 
 */
public class Data {
	
	
	public static final Data INSTANCE = new Data();
	private Data() {
		//private constructor; must be accessed with getInstance method
	}
	
	
	/////////////names
	public final static String MESSIAH_OF_DUNE = "Messiah Of Dune";	
	public final static String GONE_WITH_THE_WIND = "Gone With The Wind";
	public final static String GARDEN_OF_RAMA = "Garden of Rama";	
	
    
    public static List<String> bookTitles = new ArrayList<>() {
    	
    	{
           add(MESSIAH_OF_DUNE);
           add(GONE_WITH_THE_WIND);
           add(GARDEN_OF_RAMA);
    	}
    };
    
    public static void addBookTitle(String title) {
    	bookTitles.add(title);
    }
    
    public static Role currentAuth = null;
    
    public static List<User> logins = new ArrayList<>() {
    	
    	{
           add(new User("Joe", "111".toCharArray(), Role.SELLER));
           add(new User("Ann", "101".toCharArray(), Role.MEMBER));
           add(new User("Dave", "102".toCharArray(), Role.BOTH));
    	}
    };
    
    
    
    
           

    
    
}