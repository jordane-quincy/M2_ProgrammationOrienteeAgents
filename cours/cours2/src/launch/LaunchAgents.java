package launch;


public class LaunchAgents {

//	public static Logger logger = Logger.getLogger("simu");

	LaunchAgents() {	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		new LaunchAgents();


		// ******************JADE******************
		String[] jadeArgs = new String[2];
		jadeArgs[0] = "-gui";

		String agents = "hello:agents.HelloAgent;";
		agents += "bonjour:agents.HelloAgent;";
		agents += "salut:agents.HelloAgent;";
		
		jadeArgs[1] = agents;
		

		jade.Boot.main(jadeArgs);
	}

}
