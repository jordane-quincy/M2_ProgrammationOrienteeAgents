package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;

import agents.AlertAgent;
import agents.TravellerAgent;
import jade.gui.GuiEvent;

/**
 * Journey resarch Gui, communication with TravellerAgent throw GuiEvent
 * 
 * @author modif. Emmanuel Adam - LAMIH
 */
@SuppressWarnings("serial")
public class AlertAgentGui extends JFrame {

	private static int nbTravellerGui = 0;
	private int noTravellerGui;

	/** Text area */
	JTextArea jTextArea;

	/** window color */
	Color color;

	private AlertAgent myAgent;
	//private JLabel lblPrice;
	private JComboBox<String> jListFrom;
	private JComboBox<String> jListTo;
	private JComboBox<String> jListMean;
//	private JComboBox<String> jListCriteria;
	//private JSlider sliderTime;

	private String departure;
	private String arrival;
	private int nbElements;
	private String mean;

	public AlertAgentGui(AlertAgent agentAlert) {
		this.setBounds(0, 300, 600, 400);
		noTravellerGui = ++nbTravellerGui;

		myAgent = agentAlert;
		if (agentAlert != null)
			setTitle(myAgent.getLocalName());

		jTextArea = new JTextArea();
		jTextArea.setBackground(new Color(255, 255, 240));
		jTextArea.setEditable(false);
		jTextArea.setColumns(10);
		jTextArea.setRows(5);
		JScrollPane jScrollPane = new JScrollPane(jTextArea);
		getContentPane().add(BorderLayout.CENTER, jScrollPane);

		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0, 4, 0, 0));
		p.add(new JLabel("From:"));
		p.add(new JLabel("To:"));

		//lblPrice = new JLabel("Departure: 9:30");
		//p.add(lblPrice);

		p.add(new JLabel("Criteria"));

		getContentPane().add(p, BorderLayout.SOUTH);

		JButton addButton = new JButton("Send");
		addButton.addActionListener(event -> {
			try {
				departure = (String) jListFrom.getSelectedItem();
				arrival = (String) jListTo.getSelectedItem();
				mean = (String) jListMean.getSelectedItem();
				nbElements = 1;
				// SEND AN GUI EVENT TO THE AGENT !!!
				GuiEvent guiEv = new GuiEvent(this, AlertAgent.SEND_NEWS);
				guiEv.addParameter(departure);
				guiEv.addParameter(arrival);
//				guiEv.addParameter(jListCriteria.getSelectedItem());
				guiEv.addParameter(mean);
				myAgent.postGuiEvent(guiEv);
				// END SEND AN GUI EVENT TO THE AGENT !!!
			} catch (Exception e) {
				JOptionPane.showMessageDialog(AlertAgentGui.this, "Invalid values. " + e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		jListFrom = new JComboBox<>(new String[] { "-", "a", "b", "c", "d", "e", "f" });
		jListFrom.setSelectedIndex(1);
		p.add(jListFrom);

		jListTo = new JComboBox<>(new String[] { "-", "a", "b", "c", "d", "e", "f" });
		jListTo.setSelectedIndex(6);
		p.add(jListTo);

		/*sliderTime = new JSlider();
		sliderTime.setPreferredSize(new Dimension(100, 10));
		sliderTime.setMinimum(200);
		sliderTime.setMaximum(2300);
		sliderTime.setValue(950);
		sliderTime.setMajorTickSpacing(100);
		sliderTime.setMinorTickSpacing(25);
		sliderTime.setSnapToTicks(true);
		sliderTime.setPaintTicks(true);
		sliderTime.addChangeListener(event -> {
			int hh = sliderTime.getValue() / 100;
			int mm = (int) (sliderTime.getValue() % 100 / 100d * 60d);
			String smm = (mm < 10) ? ("0" + mm) : String.valueOf(mm);
			lblPrice.setText("Departure: " + hh + ":" + smm);
			lblPrice.repaint();
		});
		p.add(sliderTime);*/

//		jListCriteria = new JComboBox<>(new String[] { "blocage" });
//		jListCriteria.setSelectedIndex(0);
//		p.add(jListCriteria);

		jListMean = new JComboBox<>(new String[] { "rail", "route" });
		jListMean.setSelectedIndex(0);
		p.add(jListMean);

		p.add(addButton);

		// Make the agent terminate when the user closes
		// the GUI using the button on the upper right corner
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// SEND AN GUI EVENT TO THE AGENT !!!
				GuiEvent guiEv = new GuiEvent(this, TravellerAgent.QUIT);
				myAgent.postGuiEvent(guiEv);
				// END SEND AN GUI EVENT TO THE AGENT !!!
			}
		});

		setResizable(true);
	}

	public void display() {System.out.println("ALERT AGENT");
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int width = this.getWidth();
		System.out.println(noTravellerGui);
		int xx = (noTravellerGui * width - this.getWidth()) % screenWidth;
		int yy = ((noTravellerGui * width) / screenWidth) * getHeight();
		setLocation(xx, yy + 2*this.getHeight());
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

	/**
	 * @return the bookName
	 */
	public String getBookName() {
		return arrival;
	}

	/**
	 * @return the nbElements
	 */
	public int getNbElements() {
		return nbElements;
	}

	public static void main(String[] args) {
		AlertAgentGui test = new AlertAgentGui(null);
		test.setVisible(true);
	}

}
