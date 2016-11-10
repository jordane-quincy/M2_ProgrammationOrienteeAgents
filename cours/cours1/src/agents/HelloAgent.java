package agents;

import gui.JZoneTexteFrame;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

/**
 * an agent that say hello * @author eadam
 */
@SuppressWarnings("serial")
public class HelloAgent extends GuiAgent {

	/** variable to stop the agent */
	public static final int STOP = 0;
	/** variable to quit the agents */
	public static final int QUIT = -1;

	/** little gui to display debug messages */
	public gui.JZoneTexteFrame window;

	/** boolean to stop the agent */
	boolean stop;

	/** address (aid) of the other queens */
	AID[] neighbourgs;

	/**
	 * initialize the agent <br>
	 */
	@Override
	protected void setup() {
		// String [] args = (String[]) this.getArguments();
		this.stop = false;
		this.window = new JZoneTexteFrame("Agent " + this.getName(), this);
		this.window.println("Hello! ");

		// Enregistrer le service associe a l'agent aux pages jaunes
		// décrire une description sur soi
		final DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(this.getAID());
		// décrire un service
		final ServiceDescription sd = new ServiceDescription();
		// le type de service
		sd.setType("Vente");
		// le nom (sous-type) du service
		sd.setName("vendeur");
		dfd.addServices(sd);
		// tenter l'enrefisgrement dans les pages jaunes
		try {
			DFService.register(this, dfd);
		} catch (final FIPAException fe) {
			fe.printStackTrace();
		}

		this.addBehaviour(new WakerBehaviour(this, 200) {
			@Override
			protected void onWake() {

				HelloAgent.this.neighbourgs = HelloAgent.this.updateVoisins("Vente", null);

				HelloAgent.this.window.println("Mes voisins :");
				for (final AID aid : HelloAgent.this.neighbourgs) {
					HelloAgent.this.window.println(aid.getLocalName());
				}
			}
		});

		this.addBehaviour(new WakerBehaviour(this, 500) {
			@Override
			protected void onWake() {
				final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

				msg.setContent("premiere communication de la part de " + this.myAgent.getLocalName());

				for (final AID aid : HelloAgent.this.neighbourgs) {
					msg.addReceiver(aid);
				}

				HelloAgent.this.window.println(this.myAgent.getLocalName() + " envoi un message a ses voisins");
				this.myAgent.send(msg);
			}
		});

		// this.addBehaviour(new TickerBehaviour(this, 200) {
		// @Override
		// public void onTick() {
		// final ACLMessage msgRecu = this.myAgent.receive();
		//
		// if (msgRecu != null) {
		// HelloAgent.this.window.println(this.myAgent.getLocalName() + " a recu
		// le message de : "
		// + msgRecu.getSender().getLocalName());
		// HelloAgent.this.window.println(" dont le contenu est : " +
		// msgRecu.getContent());
		// }
		// }
		// });

		// CyclicBehaviour == TickerBehaviour mais à chaque top d'horloge du cpu
		this.addBehaviour(new CyclicBehaviour(this) {
			@Override
			public void action() {
				final ACLMessage msgRecu = this.myAgent.receive();

				if (msgRecu != null) {
					HelloAgent.this.window.println(this.myAgent.getLocalName() + " a recu le message de : "
							+ msgRecu.getSender().getLocalName());
					HelloAgent.this.window.println(" dont le contenu est : " + msgRecu.getContent());
				} else {
					// utilité ?
					this.block();
				}
			}
		});

	}

	@Override
	protected void onGuiEvent(final GuiEvent ev) {
		switch (ev.getType()) {
		case STOP:
			this.stop = true;
			break;
		case QUIT:
			System.exit(0);
			break;
		}
	}

	/**
	 * @return the stop
	 */
	public boolean isStop() {
		return this.stop;
	}

	/**
	 * @param stop
	 *            the stop to set
	 */
	public void setStop(final boolean stop) {
		this.stop = stop;
	}

	/***
	 * met à jour la liste des voisins
	 */
	public AID[] updateVoisins(final String _typeService, final String _nameService) {
		AID[] result = null;
		;
		final DFAgentDescription modele = new DFAgentDescription();
		final ServiceDescription sd = new ServiceDescription();
		sd.setType(_typeService);
		sd.setName(_nameService);
		modele.addServices(sd);
		int j = 0;
		AID[] agentsAID = null;
		try {
			final DFAgentDescription[] agentsDescription = DFService.search(this, modele);
			agentsAID = new AID[agentsDescription.length];
			for (int i = 0; i < agentsDescription.length; ++i) {
				if (!agentsDescription[i].getName().equals(this.getName())) {
					agentsAID[j] = agentsDescription[i].getName();
					j++;
				}
			}
		} catch (final FIPAException fe) {
			fe.printStackTrace();
		}
		result = new AID[j];
		for (int i = 0; i < j; i++) {
			result[i] = agentsAID[i];
		}
		return result;
	}

}
