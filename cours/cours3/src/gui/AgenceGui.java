package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import agents.AgenceAgent;
import data.JourneysList;
import jade.gui.GuiEvent;

/**
 * Agence Gui, communication with AgenceAgent throw GuiEvent
 *
 * @author Emmanuel Adam - LAMIH
 */
@SuppressWarnings("serial")
public class AgenceGui extends JFrame {

	private static int nbVendeurGuiCN = 0;
	private int noVendeurGuiCN;

	/** Text area */
	JTextArea jTextArea;

	/** window color */
	Color color;

	/** nb of windows created */
	static int nb = 0;
	/** no of the window */
	int no;

	private AgenceAgent myAgent;

	public AgenceGui(AgenceAgent a) {
		super(a.getName());
		noVendeurGuiCN = ++nbVendeurGuiCN;

		myAgent = a;

		jTextArea = new JTextArea();
		jTextArea.setBackground(new Color(255, 255, 240));
		jTextArea.setEditable(false);
		jTextArea.setColumns(40);
		jTextArea.setRows(5);
		JScrollPane jScrollPane = new JScrollPane(jTextArea);
		getContentPane().add(BorderLayout.CENTER, jScrollPane);

		// Make the agent terminate when the user closes
		// the GUI using the button on the upper right corner
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// SEND AN GUI EVENT TO THE AGENT !!!
				GuiEvent guiEv = new GuiEvent(this, AgenceAgent.EXIT);
				myAgent.postGuiEvent(guiEv);
				// END SEND AN GUI EVENT TO THE AGENT !!!
			}
		});

		setResizable(true);

		final int preferedWidth = 500;
		final int preferedHeight = 300;
		no = nb++;

		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;
		int dX = preferedWidth;
		int x = no * dX % screenWidth;
		int y = no * dX / screenWidth * preferedHeight % screenHeight;

		setBounds(x, y, preferedWidth, preferedHeight);
		setVisible(true);
	}

	public void display() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int width = this.getWidth();
		int xx = noVendeurGuiCN * width % screenWidth;
		int yy = noVendeurGuiCN * width / screenWidth * getHeight();
		setLocation(xx, yy);
		setTitle(myAgent.getLocalName());
		setVisible(true);
	}

	/** add a string to the text area */
	public void println(String chaine) {
		String texte = jTextArea.getText();
		texte = texte + chaine + "\n";
		jTextArea.setText(texte);
		jTextArea.setCaretPosition(texte.length());
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		jTextArea.setBackground(color);
	}

	public void displayCatalogue() {
		JourneysList catalog = myAgent.getCatalog();
		println(catalog.toString());
	}

}
