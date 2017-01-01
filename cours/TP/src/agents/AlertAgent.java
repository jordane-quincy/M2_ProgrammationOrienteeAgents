package agents;

import java.awt.Color;
import java.util.Arrays;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVWriter;

import gui.AlertAgentGui;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.messaging.TopicManagementHelper;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

/**
 * Journey searcher
 * 
 * @author Emmanuel ADAM
 */
@SuppressWarnings("serial")
public class AlertAgent extends GuiAgent {
	/** code to quit the agent from gui */
	public static final int QUIT = 0;
	/** code to send traffic news from gui */
	public static final int SEND_NEWS = 42;
	
	/** traffic topic */
	public static final String TOPIC_TRAFFIC = "TRAFFIC_NEWS";

	/** gui */
	private AlertAgentGui window;
	
	/** topic d'alerte info traffic */
	private AID topic;

	/** Initialisation de l'agent */
	@Override
	protected void setup() {
		this.window = new AlertAgentGui(this);
		window.setColor(Color.orange);
		window.println("Hello! AgentAcheteurCN " + this.getLocalName() + " est pret. ");
		window.display();
				
		// création du topic d'info traffic
		createTopicTrafficNews();
	}

	private void createTopicTrafficNews() {
	    // Définition du topic
	    TopicManagementHelper topicHelper = null;
	    try {
	      topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
	      topic = topicHelper.createTopic(AlertAgent.TOPIC_TRAFFIC);
	      topicHelper.register(topic);
	      } catch (ServiceException e) {e.printStackTrace(); }
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
	public AlertAgentGui getWindow() {
		return window;
	}

	/**
	 * Send traffic news.
	 * 
	 * @param from
	 *            origin
	 * @param to
	 *            arrival
	 * @param mean
	 *            transport mode impacted
	 */
	private void sendTrafficNews(final String from, final String to, final String mean) {
//		l'alerte doit être de type
//		rail,depart,destination
//		ou
//		route,depart,destination

		String trafficNewsMessage = mean +","+ from +","+ to;
		trafficNewsMessage = trafficNewsMessage.toUpperCase();
		window.println("sendTrafficNews : "+ trafficNewsMessage);
		
		//création du msg
	    ACLMessage msg = new ACLMessage(ACLMessage.INFORM); 
	    msg.setConversationId(AlertAgent.TOPIC_TRAFFIC);
	    msg.setContent(trafficNewsMessage);
	    
	    //ajout des destinataires
  		AID[] lstAgentTraveller = AgentToolsEA.searchAgents(this, "traveller agent", null);
  		window.println("lstAgentTraveller : "+ Arrays.asList(lstAgentTraveller));
  		for(AID travel : lstAgentTraveller){
  			msg.addReceiver(travel);
  		}
		
  		//envoi
	    send(msg);
	}
	
	/** get event from the GUI */
	@Override
	protected void onGuiEvent(final GuiEvent eventFromGui) {
		if (eventFromGui.getType() == AlertAgent.QUIT) {
			doDelete();
		}
		if (eventFromGui.getType() == AlertAgent.SEND_NEWS) {
			sendTrafficNews((String) eventFromGui.getParameter(0), (String) eventFromGui.getParameter(1),
					(String) eventFromGui.getParameter(2));
		}
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
}