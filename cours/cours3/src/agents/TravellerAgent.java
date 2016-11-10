package agents;

import data.JourneysList;
import gui.TravellerGui;
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
public class TravellerAgent extends GuiAgent {

	/** code to buy travel */
	public static final int BUY_TRAVEL = 42;
	/** code to quit */
	public static final int EXIT = -10;

	/** little gui to display debug messages */
	public TravellerGui window;

	/** address (aid) of the other agents */
	AID[] agences;

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
		window = new TravellerGui(this);
		window.println("Hello! ");
		// il ne s'enregisre pas pour le moment :
		// AgentToolsEA.register(this,"cordialite", "accueil");

		// Pas de message reçu pour le moment :
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

	}

	private void buyCmd() {
		agences = AgentToolsEA.searchAgents(this, "agence", null);
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		for (AID other : agences) {
			msg.addReceiver(other);
		}
		msg.setContent("vous avez le bonjour de " + this.getLocalName());
		send(msg);
	}

	@Override
	protected void onGuiEvent(GuiEvent ev) {
		switch (ev.getType()) {
		case TravellerAgent.BUY_TRAVEL:
			buyCmd();
			break;
		case TravellerAgent.EXIT:
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
}
