package launch;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * launch the simulation of travelers and travel agencies
 * 
 * @author emmanueladam
 */
public class LaunchSimu {

	public static final Logger logger = Logger.getLogger("simu");

	/**
	 * @param args
	 */
	public static void main(String... args) {

		logger.setLevel(Level.ALL);
		Handler fh;
		try {
			fh = new FileHandler("./simuAgences.xml", false);
			logger.addHandler(fh);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

		// ******************JADE******************
		String[] jadeArgs = new String[2];
		StringBuffer sbAgents = new StringBuffer();

		//Création des Traveller
		for(int nbTraveller = 1; nbTraveller <= 2; nbTraveller++){
			sbAgents.append("client"+nbTraveller+":agents.TravellerAgent").append(";");
		}
		
		//Création des Agences
		for(int nbAgence = 1; nbAgence <= 4; nbAgence++){
			int numFichier = (nbAgence % 2 == 0 ? 2 : 1);
			sbAgents.append("vendeur"+nbAgence+":agents.AgenceAgent(catalog"+ numFichier +".csv)").append(";");
		}

		jadeArgs[0] = "-gui";
		jadeArgs[1] = sbAgents.toString();

		jade.Boot.main(jadeArgs);
	}

}
