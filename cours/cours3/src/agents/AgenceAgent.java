package agents;

import data.JourneysList;
import gui.SimpleGui4Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
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

	/** little gui to display debug messages */
	public SimpleGui4Agent window;

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
		window = new SimpleGui4Agent(this);
		window.println("Hello! ");
		AgentToolsEA.register(this, "cordialite", "accueil");

		addBehaviour(new CyclicBehaviour(this) {
			@Override
			public void action() {
				ACLMessage msg = myAgent.receive();
				if (msg != null) {
					window.println("j'ai recu un message de " + msg.getSender(), true);
					window.println("voici le contenu : " + msg.getContent(), true);
				}
			}
		});

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
		case SimpleGui4Agent.SENDCODE:
			sendHello();
			break;
		case SimpleGui4Agent.QUITCODE:
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
