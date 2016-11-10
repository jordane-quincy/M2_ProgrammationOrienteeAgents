package agents;

import data.JourneysList;
import gui.AgenceGui;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

/**
 * an agent that say hello * @author eadam
 */
@SuppressWarnings("serial")
public class AgenceAgent extends GuiAgent {

	/** code to quit */
	public static final int EXIT = -11;

	/** little gui to display debug messages */
	public AgenceGui window;

	/** address (aid) of the other agents */
	AID[] neighbourgs;

	/** msg to send */
	String helloMsg;

	/** */
	private JourneysList catalog;

	/**
	 * initialize the agent <br>
	 */
	@Override
	protected void setup() {
		String[] args = (String[]) this.getArguments();
		helloMsg = args != null && args.length > 0 ? args[0] : "Hello";
		window = new AgenceGui(this);
		window.println("Agence UP");
		AgentToolsEA.register(this, "agence", null);

		// addBehaviour(new CyclicBehaviour(this) {
		// @Override
		// public void action() {
		// ACLMessage msg = myAgent.receive();
		// if (msg != null) {
		// window.println("j'ai recu un message de " + msg.getSender(), true);
		// window.println("voici le contenu : " + msg.getContent(), true);
		// }
		// }
		// });

		setupCatalog();
	}

	/*
	 * Initialize journey
	 */
	private void setupCatalog() {
		catalog = new JourneysList();
		catalog.addJourney("Valenciennes", "Lille", "car", 1440, 30);
		catalog.addJourney("Valenciennes", "Lille", "train", 1440, 40);
		catalog.addJourney("Valenciennes", "Lille", "train", 1440, 40);
	}

	private void sendHello() {
		neighbourgs = AgentToolsEA.searchAgents(this, "cordialite", null);
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		for (AID other : neighbourgs) {
			msg.addReceiver(other);
		}
		msg.setContent("vous avez le bonjour de " + this.getLocalName());
		send(msg);
	}

	@Override
	protected void onGuiEvent(GuiEvent ev) {
		switch (ev.getType()) {
		case AgenceAgent.EXIT:
			window.dispose();
			doDelete();
			break;
		}
	}

	@Override
	protected void takeDown() {
		// S'effacer du service pages jaunes
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.err.println("Agent : " + getAID().getName() + " quitte la plateforme.");
		window.dispose();
	}

	public JourneysList getCatalog() {
		return catalog;
	}

	public void setCatalog(JourneysList catalog) {
		this.catalog = catalog;
	}
}
