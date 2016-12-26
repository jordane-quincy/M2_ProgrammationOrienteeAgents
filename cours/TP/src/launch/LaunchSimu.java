package launch;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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

	private static final List<String> fichiersAgence = Arrays.asList("car.csv","bus.csv","train.csv");
	
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
		int nbAgence = 1;
		for(String fichierAgence : fichiersAgence){
			sbAgents.append("vendeur"+nbAgence+":agents.AgenceAgent("+fichierAgence+")").append(";");
			nbAgence++;
		}
		//Création de l'Agent d'Alertes
		int nb_alert_agent = 1;
		for(int i = 0;i < nb_alert_agent;i++){
			sbAgents.append("alert"+i+":agents.AlertAgent").append(";");
		}

		jadeArgs[0] = "-gui -services jade.core.event.NotificationService;jade.core.messaging.TopicManagementService";
		jadeArgs[1] = sbAgents.toString();

		jade.Boot.main(jadeArgs);
	}

}
