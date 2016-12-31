package agents;

import java.util.Arrays;
import java.util.Vector;

import data.JourneysList;
import gui.TravellerGui;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;

/**
 * use the AchieveRE protocol to ask to the sellers their catalogs
 * 
 * @author emmanueladam
 */
@SuppressWarnings("serial")
public class Ask4Catalog extends AchieveREInitiator {

	/** traveller agent linked to this behaviour */
	private TravellerAgent agent;

	/**
	 * constructor, initialize the message to send to the sellers
	 * 
	 * @param agent
	 *            the traveller agent linked to this behaviour
	 * @param msg
	 *            the msg to send (initially an empty INFORM message)
	 */
	public Ask4Catalog(final Agent traveller, final ACLMessage msg) {
		super(traveller, msg);
		agent = (TravellerAgent) traveller;
		final AID[] vendeurs = agent.getVendeurs();
		agent.println("Ask4Catalog vendeurs = " + Arrays.asList(vendeurs));
		for (AID v : vendeurs) {
			msg.addReceiver(v);
		}
		msg.setConversationId("CATALOG_ASK");
		msg.setContent("could I have your catalog please ?");
		this.reset(msg);
	}

	/**
	 * method lauched when all the seller have sent their catalog, or after a
	 * delay
	 * 
	 * @param responses
	 *            a vector of ACLMessage sent by the sellers
	 */
	@Override
	@SuppressWarnings("rawtypes")
	protected void handleAllResponses(final Vector responses) {
//		System.out.println("Agent catalogue avant ask : "+ agent.getCatalogs());
		final int nbAnswers = responses.size();
//		System.out.println("nbAnswers : "+ nbAnswers);
		final JourneysList catalogs = (agent.getCatalogs() != null ? agent.getCatalogs() : new JourneysList());
		agent.println("catalogue avant ask : "+ catalogs.getInfos());
		for (int i = 0; i < nbAnswers; i++) {
			final ACLMessage ans = (ACLMessage) responses.get(i);
			if (ans.getPerformative() == ACLMessage.AGREE) {
				JourneysList receivedCatalog = null;
				try {
					receivedCatalog = (JourneysList) ans.getContentObject();
					agent.println("receivedCatalog de "+ ans.getSender().getLocalName() +" : "+ receivedCatalog.getInfos());
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				if (receivedCatalog != null)
					catalogs.addJourneys(receivedCatalog);
			}
		}
		//sort cataglos depending on preference
		//On récupère le sortMode from the traveller agent
//		String sortMode = agent.getSortMode();
//		TravellerGui window = agent.getWindow();
//		window.println("pref : " + agent.getSortMode());
		
		
		agent.setCatalogs(catalogs);
		agent.println("catalogue apres ask : "+ agent.getCatalogs().getInfos());
	}

}
