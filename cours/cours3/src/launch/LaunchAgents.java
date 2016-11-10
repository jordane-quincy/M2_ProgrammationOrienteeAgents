package launch;

public class LaunchAgents {

	// public static Logger logger = Logger.getLogger("simu");

	LaunchAgents() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		new LaunchAgents();

		// ******************JADE******************
		String[] jadeArgs = new String[2];
		jadeArgs[0] = "-gui";

		String agents = "";
		// Creation des agences
		for (int i = 0; i < 4; i++) {
			agents += "agence" + i + ":agents.AgenceAgent;";
		}
		// Creation des travellers
		// for (int i = 0; i < 2; i++) {
		// agents += "traveller" + i + ":agents.TravellerAgent;";
		// }

		jadeArgs[1] = agents;

		jade.Boot.main(jadeArgs);
	}

}
