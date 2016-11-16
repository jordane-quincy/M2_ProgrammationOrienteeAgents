package agents;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.ComposedJourney;
import data.Journey;
import data.JourneysList;
import gui.TravellerGui;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

/**
 * Journey searcher
 * 
 * @author Emmanuel ADAM
 */
@SuppressWarnings("serial")
public class TravellerAgent extends GuiAgent {
	/** code to quit the agent from gui */
	public static final int QUIT = 0;
	/** code to search a travel from gui */
	public static final int SEARCH_TRAVEL = 1;

	/** liste des vendeurs */
	protected AID[] vendeurs;

	/**
	 * preference between journeys -, cost, co2, duration or confort ("-" = cost
	 * by defaul)}
	 */
	private String sortMode;

	/** catalog received by the sellers */
	protected JourneysList catalogs;

	/** gui */
	private TravellerGui window;

	/** Initialisation de l'agent */
	@Override
	protected void setup() {
		this.window = new TravellerGui(this);
		window.setColor(Color.cyan);
		window.println("Hello! AgentAcheteurCN " + this.getLocalName() + " est pret. ");
		window.setVisible(true);
	}

	// 'Nettoyage' de l'agent
	@Override
	protected void takeDown() {
		window.println("Je quitte la plateforme. ");
	}

	///// SETTERS AND GETTERS
	/**
	 * @return agent gui
	 */
	public TravellerGui getWindow() {
		return window;
	}

	/**
	 * try to find a journey : create a sequential behaviour with 3
	 * subbehaviours : search sellers, ask for catalogs, find if the journey is
	 * possible
	 * 
	 * @param from
	 *            origin
	 * @param to
	 *            arrival
	 * @param departure
	 *            date of departure
	 * @param preference
	 *            choose the best (in cost, co2, confort, ...)
	 */
	private void buyJourney(final String from, final String to, final int departure, final String preference) {

		sortMode = preference;
		window.println("recherche de voyage de " + from + " vers " + to + " Ã  partir de " + departure);

		final SequentialBehaviour seqB = new SequentialBehaviour(this);
		seqB.addSubBehaviour(new OneShotBehaviour(this) {
			/** ask the DFAgent for agents that are in the travel agency */
			@Override
			public void action() {
				vendeurs = AgentToolsEA.searchAgents(myAgent, "travel agency", null);
			}
		});
		seqB.addSubBehaviour(new OneShotBehaviour(this) {
			/** add a behaviour to ask a catalog of journeys to the sellers */
			@Override
			public void action() {
				myAgent.addBehaviour(new Ask4Catalog(myAgent, new ACLMessage(ACLMessage.INFORM)));
			}
		});
		seqB.addSubBehaviour(new WakerBehaviour(this, 100) {
			/**
			 * display the merged catalog and try to find the best journey that
			 * corresponds to the data sent by the gui
			 */
			@Override
			protected void onWake() {

				if (catalogs != null) {
					println("here is my catalog : ");
					println(" -> " + catalogs);
					computeComposedJourney(from, to, departure, preference);

				}
				if (catalogs == null) {
					println("I have no catalog !!! ");
				}
			}
		});

		addBehaviour(seqB);

	}

	/** compute a journey composed of several journey to meet the needs */
	private void computeComposedJourney(final String from, final String to, final int departure,
			final String preference) {
		boolean found = false;
		ArrayList<Journey> currentJourney = new ArrayList<Journey>();
		List<String> via = new ArrayList<String>();
		ArrayList<ComposedJourney> results = new ArrayList<ComposedJourney>();
		println("On recherche un chemin de : " + from);
		println("Pour aller vers : " + to);
		println("A une heure : " + departure);
		println("catalog : " + catalogs);
		found = catalogs.findIndirectJourney(from, to, departure, currentJourney, via, results);
		if(found) {
			/*ComposedJourney composed_journey = null;
			for(int i = 0;i < results.size();i++){
				composed_journey = results.get(i);
				
			}*/
			switch(preference){
				case "cost": Collections.sort(results, (j1, j2)->(int)(j1.getCost() - j2.getCost()));
				break;
				case "confort": Collections.sort(results, (j1, j2)->(int)(j2.getCost() - j1.getCost()));
				break;
				case "co2": Collections.sort(results, (j1, j2)->(int)(j1.getCost() - j2.getCost()));
				break;
				case "duration": Collections.sort(results, (j1, j2)->(int)(j1.getCost() - j2.getCost()));
				break;
			}
			ComposedJourney best = results.get(0);
			println("best way : " + best);
		}
		else {
			println("Pas de chemin trouvé");
		}
	}

	/** get event from the GUI */
	@Override
	protected void onGuiEvent(final GuiEvent eventFromGui) {
		if (eventFromGui.getType() == TravellerAgent.QUIT) {
			doDelete();
		}
		if (eventFromGui.getType() == TravellerAgent.SEARCH_TRAVEL) {
			buyJourney((String) eventFromGui.getParameter(0), (String) eventFromGui.getParameter(1),
					(Integer) eventFromGui.getParameter(2), (String) eventFromGui.getParameter(3));
		}
	}

	/**
	 * @return the vendeurs
	 */
	public AID[] getVendeurs() {
		return vendeurs.clone();
	}

	/**
	 * @param vendeurs
	 *            the vendeurs to set
	 */
	public void setVendeurs(final AID... vendeurs) {
		this.vendeurs = vendeurs;
	}

	/** -, cost, co2, duration or confort */
	public String getSortMode() {
		return sortMode;
	}

	/**
	 * print a message on the window lined to the agent
	 * 
	 * @param msg
	 *            text to display in th window
	 */
	public void println(final String msg) {
		window.println(msg);
	}

	/** @return the list of journeys */
	public JourneysList getCatalogs() {
		return catalogs;
	}

	/** set the list of journeys */
	public void setCatalogs(final JourneysList catalogs) {
		this.catalogs = catalogs;
	}

}
