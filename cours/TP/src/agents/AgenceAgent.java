
package agents;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import com.opencsv.CSVReader;

import data.Journey;
import data.JourneysList;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import launch.LaunchSimu;

/**
 * Journey Seller
 * 
 * @author Emmanuel ADAM
 */
@SuppressWarnings("serial")
public class AgenceAgent extends GuiAgent {
	/** code shared with the gui to add a journey */
	public static final int ADD_TRAVEL = 1;
	/** code shared with the gui to quit the agent */
	public static final int QUIT = 0;

	/** catalog of the proposed journeys */
	private JourneysList catalog;
	/** graphical user interface linked to the seller agent */
	private gui.AgenceGui window;

	// Initialisation de l'agent
	@Override
	protected void setup() {
		final Object[] args = getArguments(); // Recuperation des arguments
		catalog = new JourneysList();
		window = new gui.AgenceGui(this);
		window.display();

		 if (args != null && args.length > 0) {
			 fromCSV2Catalog("./resources/csv/"+ (String) args[0]);
		 }
		
//		fromCSV2Catalog("./resources/csv/catalog1.csv");
//		buildSampleCatalog();
		
		window.println("here is my catalog : ");
		window.println(catalog.toString());

		AgentToolsEA.register(this, "travel agency", "seller");

		// attendre une demande de catalogue
		waitAsk4Catalog();

	}

	/**
	 * Ask a behaviour that wait for a call for catalog
	 */
	private void waitAsk4Catalog() {
		MessageTemplate mt = MessageTemplate.MatchConversationId("CATALOG_ASK");
		addBehaviour(new AchieveREResponder(this, mt) {
			@Override
			protected ACLMessage handleRequest(ACLMessage request) {
				ACLMessage result = request.createReply();
				result.setPerformative(ACLMessage.AGREE);
				try {
					result.setContentObject(catalog);
				} catch (IOException e) {
					e.printStackTrace();
				}
				window.println("j'envoie mon catalogue à l'agent " + request.getSender().getLocalName());
				return result;
			}
		});
	}

	// Fermeture de l'agent
	@Override
	protected void takeDown() {
		// S'effacer du service pages jaunes
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			LaunchSimu.logger.log(Level.SEVERE, fe.getMessage());
		}
		LaunchSimu.logger.log(Level.INFO, "Agent Agence : " + getAID().getName() + " quitte la plateforme.");
		window.dispose();
	}

	/**
	 * methode invoquee par la gui
	 */
	@Override
	protected void onGuiEvent(GuiEvent guiEvent) {
		if (guiEvent.getType() == AgenceAgent.QUIT) {
			doDelete();
		}
	}

	/** build a sample cataog to test the agent */
	private void buildSampleCatalog() {
		catalog.addJourney(new Journey("dep", "pt1", "car", 1100, 10));
		catalog.addJourney(new Journey("dep", "pt2", "car", 1120, 10));
		catalog.addJourney(new Journey("pt1", "arr", "train", 1200, 15));
	}

	/**
	 * initialize the catalog from a cvs file<br>
	 * csv line = origine, destination,means,departureTime,duration,financial
	 * cost, co2, confort, nbRepetitions(optional),frequence(optional)
	 * 
	 * @param file
	 *            name of the cvs file
	 */
	void fromCSV2Catalog(final String file) {
		CSVReader reader;
		try {
			URL urlFile = getClass().getClassLoader().getResource(file);			
			FileReader fr = new FileReader(urlFile.getFile());
			
			reader = new CSVReader(fr);
			String [] line;
			if((line = reader.readNext()) != null){
				//maintenant qu'on a deja lu la premiere ligne (en tete)
				while ((line = reader.readNext()) != null) {
					String from = line[0].trim().toUpperCase();
					String to = line[1].trim().toUpperCase();
					String means = line[2].trim().toUpperCase();
					int heureDep = Integer.parseInt(line[3].trim());
					int duree = Integer.parseInt(line[4].trim());
					catalog.addJourney(new Journey(from, to, means, heureDep, duree));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * repeat a journey on a sequence of dates into a catalog
	 * 
	 * @param departureDate
	 *            date of the first journey
	 * @param nbRepetitions
	 *            nb of journeys to add
	 * @param frequence
	 *            frequency of the journeys in minutes
	 * @param journey
	 *            the first journey to clone
	 */
	private void repeatJourney(final int departureDate, final int nbRepetitions, final int frequence,
			final Journey journey) {
	}

	///// GETTERS AND SETTERS
	public gui.AgenceGui getWindow() {
		return window;
	}

	/**
	 * @return the catalogue
	 */
	public JourneysList getCatalog() {
		return catalog;
	}

}
