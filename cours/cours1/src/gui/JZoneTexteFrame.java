package gui;

import jade.core.Agent;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import agents.HelloAgent;


/** a simple window with a text area to display informations*/
@SuppressWarnings("serial")
public class JZoneTexteFrame extends JFrame implements ActionListener {
	/** Main Text area */
	JTextArea mainTextArea;
	
	/** Low Text area */
	JTextArea lowTextArea;

	/**monAgent linked to this frame */
	GuiAgent monAgent;
	
	/**nb of windows created*/
	static int nb=0;
	
	/**no of the window */
	int no;
	
	/**button to pause and restart agent*/
	JButton jbPauseRestart;
	
	/**cde associated to the Quit button*/
	final String QUITCMD = "QUIT";

	/**cde associated to the Stop button*/
	final String STOPCMD = "STOP";


	public JZoneTexteFrame() {
		int preferedWidth = 500; 
		int preferedHeight = 300; 
		no = nb++;

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;
		
		int dX = preferedWidth;
		
		int x = (no * dX ) % screenWidth;
		int y = (((no * dX ) / screenWidth) * preferedHeight) % screenHeight;
		
		
		setBounds(x, y, 500, 300);
		buildGui();
		setVisible(true);
	}

	public JZoneTexteFrame(Agent _agent) {
		this();
		monAgent = (HelloAgent)_agent;
		setTitle(monAgent.getLocalName());
	}

	public JZoneTexteFrame(String _titre) {
		this();
		setTitle(_titre);
	}

	public JZoneTexteFrame(String _titre, Agent _agent) {
		this(_titre);
		monAgent =  (HelloAgent)_agent;
	}


	/** build the gui : a text area in the center of the window, with scroll bars*/
	private void buildGui()
	{
		getContentPane().setLayout(new BorderLayout());
		mainTextArea = new JTextArea();
		mainTextArea.setRows(5);
		JScrollPane jScrollPane  = new JScrollPane(mainTextArea);        
		getContentPane().add(BorderLayout.CENTER, jScrollPane);
		lowTextArea = new JTextArea();
		lowTextArea.setRows(5);
		 jScrollPane  = new JScrollPane(lowTextArea);        
		getContentPane().add(BorderLayout.SOUTH, jScrollPane);
		
		JPanel jpanel = new JPanel();
		jpanel.setLayout(new GridLayout(0, 2));
		
		jbPauseRestart = new JButton("--- STOP ---");
		jbPauseRestart.addActionListener(this);
		jbPauseRestart.setActionCommand(STOPCMD);
		jpanel.add(jbPauseRestart);
		jbPauseRestart = new JButton("--- QUIT ---");
		jbPauseRestart.addActionListener(this);
		jbPauseRestart.setActionCommand(QUITCMD);
		jpanel.add( jbPauseRestart);

		getContentPane().add(BorderLayout.NORTH,jpanel);
	}


	/** add a string to the low text area */
	public void println(String chaine) {
		String texte = lowTextArea.getText();
		texte = texte +  chaine + "\n";
		lowTextArea.setText(texte);
		lowTextArea.setCaretPosition(texte.length());
	}

	/** add a string to the low text area */
	public void println(String chaine, boolean main) {
		if(main)
		{
//			String texte = mainTextArea.getText();
//			texte = texte +  chaine + "\n";
			mainTextArea.setText(chaine);
			mainTextArea.setCaretPosition(chaine.length());
		}
		else
		{
			String texte = lowTextArea.getText();
			texte = texte +  chaine + "\n";
			lowTextArea.setText(texte);
			lowTextArea.setCaretPosition(texte.length());
		}
	}
	

	public void actionPerformed(ActionEvent evt) {
		String source = evt.getActionCommand();
		if(source.equals(STOPCMD)){
			GuiEvent ev = new GuiEvent((Object)this,0);
			monAgent.postGuiEvent(ev);
		}
		if(source.equals(QUITCMD)){
			GuiEvent ev = new GuiEvent((Object)this,-1);
			monAgent.postGuiEvent(ev);
		}
			
				
		}
	}


