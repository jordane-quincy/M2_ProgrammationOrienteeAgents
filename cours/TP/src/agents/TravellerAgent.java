package agents;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Stream;

import data.ComposedJourney;
import data.Journey;
import data.JourneysList;
import gui.TravellerGui;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

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
	
	/** le topic des alertes traffic */
	private AID topic;

	/** Initialisation de l'agent */
	@Override
	protected void setup() {
		this.window = new TravellerGui(this);
		window.setColor(Color.cyan);
		println("Hello! AgentAcheteurCN " + this.getLocalName() + " est pret. ");
		window.display();
		
		AgentToolsEA.register(this, "traveller agent", "traveller");

		// attendre une info traffic
		followTrafficNews();
		
	}

	// 'Nettoyage' de l'agent
	@Override
	protected void takeDown() {
		println("Je quitte la plateforme. ");
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
		println("recherche de voyage de " + from + " vers " + to + " à partir de " + departure);

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
					println(preference);
					println("*****************************************************");
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
		//120 = durée qu'on veut bien attendre (ou décallage)		
		found = catalogs.findIndirectJourney(from.trim().toUpperCase(), to.trim().toUpperCase(), departure, 120, currentJourney, via, results);
		if(found) {
			Stream<ComposedJourney> strCJ1 = results.stream();
			Stream<ComposedJourney> strCJ2 = results.stream();
			switch(preference){
				case "cost": Collections.sort(results, (j1, j2)->(int)(j1.getCost() - j2.getCost()));
				break;
				case "confort": Collections.sort(results, (j1, j2)->(int)(j2.getConfort() - j1.getConfort()));
				break;
				case "co2": Collections.sort(results, (j1, j2)->(int)(j1.getCo2() - j2.getCo2()));
				break;
				case "duration": Collections.sort(results, (j1, j2)->(int)(j1.getDuration() - j2.getDuration()));
				break;
				case "cost + duration": //Collections.sort(results, (j1, j2)->(int)(j1.getCost() - j2.getCost()));
					//création d'un flux d'entiers à partir des durées des voyages composés et calcul de moyenne
					OptionalDouble moyCost = strCJ1.mapToInt(cj->(int)cj.getCost()).average();
					OptionalDouble moyDuration = strCJ2.mapToInt(cj->cj.getDuration()).average();
					double avgCost = moyCost.getAsDouble();
					double avgDuration = moyDuration.getAsDouble();
					results.forEach(cj -> cj.setNormDuration( (cj.getDuration() - avgDuration)/(Math.sqrt( Math.pow(avgDuration * avgDuration - avgDuration, 2) ))));
					results.forEach(cj -> cj.setNormCost( (cj.getCost() - avgCost)/(Math.sqrt( Math.pow(avgCost * avgCost - avgCost, 2) ))));
					Collections.sort(results, (j1, j2)->(int)((j1.getNormDuration()*0.5 + j1.getNormCost()*0.5) - (j2.getNormDuration()*0.5 + j2.getNormCost()*0.5)));
				break;
				case "duration + confort":
					OptionalDouble moyConfort = strCJ1.mapToInt(cj->(int)cj.getConfort()).average();
					OptionalDouble moyDuration2 = strCJ2.mapToInt(cj->cj.getDuration()).average();
					double avgConfort = moyConfort.getAsDouble();
					double avgDuration2 = moyDuration2.getAsDouble();
					results.forEach(cj -> cj.setNormDuration( (cj.getDuration() - avgDuration2)/(Math.sqrt( Math.pow(avgDuration2 * avgDuration2 - avgDuration2, 2) ))));
					results.forEach(cj -> cj.setNormConfort( (cj.getConfort() - avgConfort)/(Math.sqrt( Math.pow(avgConfort * avgConfort - avgConfort, 2) ))));
					Collections.sort(results, (j1, j2)->(int)((j1.getNormDuration()*0.5 + j1.getNormConfort()*0.5) - (j2.getNormDuration()*0.5 + j2.getNormConfort()*0.5)));
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
	
	private void createTopicTrafficNews() {
	    // Définition du topic
	    TopicManagementHelper topicHelper = null;
	    try {
	      topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
	      topic = topicHelper.createTopic(AlertAgent.TOPIC_TRAFFIC);
	      topicHelper.register(topic);
	    } catch (ServiceException e) {e.printStackTrace(); }
	    println("Création du topic traffic ok");
	}
	
	/**
	 * Ask a behaviour that wait for a traffic news.
	 */
	private void followTrafficNews() {
		// creation du topic
		createTopicTrafficNews();

		// ajout du comportement pour traiter les messages
		MessageTemplate mt = MessageTemplate.MatchConversationId(AlertAgent.TOPIC_TRAFFIC);
		addBehaviour(new AchieveREResponder(this, mt) {
			@Override
			protected ACLMessage handleRequest(ACLMessage request) {
				println("Réception d'un message de "+ request.getSender().getLocalName() +" : "+ request.getContent());
				ACLMessage result = request.createReply();
				result.setPerformative(ACLMessage.AGREE);
				
				String receivedNews = request.getContent();
				if(receivedNews != null && receivedNews.contains(" ")){
					// Les champs de Mr Adam sont séparés par des espaces. Exemple : "blocage train pointA pointB"
					String[] newsPart = receivedNews.split(" ");
					String type = newsPart[0];
					String means = newsPart[1];
					String from = newsPart[2];
					String to = newsPart[3];
	
					//FIXME: ici retirer les trajets ayant ces caracteristiques
					if(catalogs != null){
						Hashtable<String,ArrayList<Journey>> hashTable = catalogs.getCatalog();
						//tout les journey au départ de from
						ArrayList<Journey> lstJourney = hashTable.get(from);
						if(lstJourney != null){
							for(Journey j : lstJourney){
								//si la destination et le mode de transport sont ceux recherchés
								if(j.getStop().equalsIgnoreCase(to) &&
									j.getMeans().equalsIgnoreCase(means)
								){
									//FIXME: ici faire le remove du journey dans le catalogue + recalculer un nouveau trajet
								}
							}
						}else{
							println("Le catalogue n'a pas été mis à jour car il ne contient pas de journey à partir de : "+ from);
						}							
					}else{
						println("Le catalogue n'a pas été mis à jour car il n'existe pas.");
					}
					

				}else{
					println("Le message d'info traffic reçu n'est pas valide.");
				}
				
				return result;
			}
		});
	}

}