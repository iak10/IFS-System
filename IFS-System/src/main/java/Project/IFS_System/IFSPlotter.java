package Project.IFS_System;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class IFSPlotter extends IFSFrame  {
	private JButton plot, clear;;	
	private static final int H = 500; // Height of window
	private static final int W = 500; // Width of window
	private int leftMargin = 20;
	private int rightMargin = 20;
	private int topMargin = 115;
	private int bottomMargin = 20;
	int noOfTransforms;
	private double[][] matrixData;
	private ArrayList<PlotData> archive = new ArrayList<PlotData>();
	private BufferedImage theAI;
	private Graphics2D theAG;
	private Transaction theAL = new Transaction();
	private Color penColour = new Color(0,0,0);
	private Color backgroundColour = new Color(255,255,255);
	IFSGUI matricies;
	ArtWindow artWindow;
	private JMenuBar menubar = new JMenuBar();
	private JMenu file, edit, colour, origin, transfer;
	private JMenuItem save, exit, plotColour, background, show, hide, drag, undo, redo, move;
	private int defaultScale = 150;
	SpinnerModel model = new SpinnerNumberModel(defaultScale, defaultScale - 100, defaultScale + 200, 5 );
	JSpinner scaler = new JSpinner(model);
	SpinnerModel dense = new SpinnerNumberModel(8, 1, 10, 1 );
	JSpinner density = new JSpinner(dense);
	SpinnerModel tilts = new SpinnerNumberModel(0, -180, 180, 3 );
	JSpinner tilt = new JSpinner(tilts);
	JLabel scaleLabel = new JLabel("Scale");
	JLabel tiltLabel = new JLabel("Tilt");
	JLabel denseLabel = new JLabel("Density");
	int xOrigin = 200;
	int yOrigin = 200;
	private boolean showOrigin = false;
	private boolean dragOrigin = false;
	private boolean originPickedUp = false;
	private int xM, yM, xPrev, yPrev;
	private int plotsUsed = 0;

	public IFSPlotter(JFrame gui, JFrame art)  
	{
		matricies = (IFSGUI) gui;
		artWindow = (ArtWindow) art;
		setSize(W + leftMargin + rightMargin, H + topMargin + bottomMargin ); // Size of drawing area
		setResizable(false);
		setLocation(820,0);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Container cp = getContentPane();
		cp.setLayout(null);

		plot = new JButton("Plot");
		plot.setBounds(5, 10 , 90, 30);
		plot.addActionListener(theAL);
		cp.add(plot);

		clear = new JButton("Clear");
		clear.setBounds(100, 10 , 90, 30);
		clear.addActionListener(theAL);
		cp.add(clear);

		scaler.setBounds(250, 10 , 40, 28);		
		cp.add(scaler);

		scaleLabel.setBounds(210, 10, 70, 20);
		cp.add(scaleLabel);

		tilt.setBounds(335, 10 , 50, 28);		
		cp.add(tilt);

		tiltLabel.setBounds(305, 10, 70, 20);
		cp.add(tiltLabel);

		density.setBounds(450, 10 , 50, 28);		
		cp.add(density);

		denseLabel.setBounds(400, 10, 70, 20);
		cp.add(denseLabel);

		setVisible(true);

		menubar = new JMenuBar();
		setJMenuBar(menubar);

		file = new JMenu("File");  
		edit = new JMenu("Edit");  
		colour = new JMenu("Colour");
		origin = new JMenu("Origin");
		transfer = new JMenu("Transfer");

		menubar.add(file);
		menubar.add(edit);
		menubar.add(colour);
		menubar.add(origin);
		menubar.add(transfer);

		save = new JMenuItem("Save Image");
		exit = new JMenuItem("Exit");

		undo = new JMenuItem("Undo");
		redo = new JMenuItem("Redo");

		plotColour = new JMenuItem("Plot Colour");
		background = new JMenuItem("Background Colour");

		move = new JMenuItem("Send to art window");

		show = new JMenuItem("Show Origin");
		hide = new JMenuItem("Hide Origin");
		drag = new JMenuItem("Drag Origin");

		file.add(save);
		file.add(exit);

		edit.add(undo);
		edit.add(redo);    

		colour.add(plotColour);
		colour.add(background);

		origin.add(show);
		origin.add(hide);
		origin.add(drag);

		transfer.add(move);

		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);  // closes the program
			}
		});

		plotColour.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) {
				pickColour();     // asks user to choose colour of plot
			}
		});

		background.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				pickBackground();   // invites user to pick a colour

				theAI = (BufferedImage) createImage(W, H); // set drawing area to user's chosen colour
				theAG = theAI.createGraphics();	
				theAG.setPaint(backgroundColour);
				theAG.fillRect(0, 0, W, H);
				plotArchive(archive, H, theAG,plotsUsed);
				repaint();
			}
		});

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String filePath;
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"png files", "png");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showSaveDialog(getParent());
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					System.out.println("You chose to Save to file: " +
							chooser.getSelectedFile().getName());
					filePath = chooser.getSelectedFile().getPath();	 		
					if(chooser.getSelectedFile().exists())
					{
						final JOptionPane optionPane = new JOptionPane();
						int a = JOptionPane.showConfirmDialog(optionPane, "Overwrite existing file?");
						if(a == 0)
						{
							try
							{
								ImageIO.write(theAI, "png", new File(chooser.getSelectedFile().getPath() + ".png"));
							}
							catch(IOException e)
							{
								System.out.println(e);
							}
						}
						else 
						{
							if(a == 1)
							{
								save.doClick();
							}
						}
					}
					else
					{
						try
						{
							ImageIO.write(theAI, "png", new File(chooser.getSelectedFile().getPath() + ".png"));
						}
						catch(IOException e)
						{
							System.out.println(e);
						}

					}		 	 	
				}	 		
			}
		});

		show.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				showOrigin = true;				
				repaint();
			}
		});

		hide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showOrigin = false;				
				repaint();
			}
		});

		drag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dragOrigin = true;				
			}
		});

		move.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		
				artWindow = matricies.getArtWindow();
				if(artWindow != null)
				{
					artWindow.transferImage(archive, xOrigin, yOrigin,plotsUsed);		
				}
				else
				{
					JOptionPane frame = new JOptionPane();
					JOptionPane.showMessageDialog(frame, "No art window running");
				}
			}
		});

		undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Plots currently used at this undo " + plotsUsed);
				System.out.println("Plots currently in archive at this undo " + archive.size());
				clearScreen();
				if(plotsUsed > 0){
					plotsUsed--;
					plotArchive(archive, H, theAG,plotsUsed);
					System.out.println("Plots currently used at this undo " + plotsUsed);
					System.out.println("Plots currently in archive at this undo " + archive.size());
				}
			}
		});

		redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		 		
				if(plotsUsed !=  archive.size())
				{
					clearScreen();
					plotsUsed++;
					plotArchive(archive, H, theAG,plotsUsed);
					System.out.println("Plots currently used at this redo " + plotsUsed);
					System.out.println("Plots currently in archive at this redo " + archive.size());
				}
			}
		});

		addMouseListener(new MouseAdapter() 			
		{
			public void mousePressed (MouseEvent e)
			{	
				xM = e.getX();
				yM = e.getY();
				if(mouseInRange(xM, yM))
				{		        	
					xPrev = xM;
					yPrev = yM;
					if(dragOrigin && xM - (xOrigin + leftMargin) < 20 && xM - (xOrigin + leftMargin) > - 20 && yM - (topMargin + H - yOrigin) < 20 && yM - (topMargin + H - yOrigin) > -20)
					{
						originPickedUp = true;
						xOrigin = xM - leftMargin;
						yOrigin = topMargin + H - yM;
						repaint();
					}
				}
			};


			public void mouseReleased (MouseEvent e)
			{
				if(dragOrigin && originPickedUp)
				{
					xOrigin = e.getX() - leftMargin;
					yOrigin = topMargin + H - e.getY();
					repaint();
					dragOrigin = false;
					originPickedUp = false;
				}
			} 		    	    	
		});

		addMouseMotionListener(new MouseMotionAdapter() 
		{
			public void mouseDragged (MouseEvent e)
			{
				if(dragOrigin && originPickedUp)
				{
					int xDrag = e.getX();
					int yDrag = e.getY();
					if (mouseInRange(xDrag, yDrag))
					{
						int xRepaintRange = Math.abs(xDrag - xPrev) + 15;
						int yRepaintRange = Math.abs(yDrag - yPrev) + 15;
						xOrigin = xDrag - leftMargin;
						yOrigin = topMargin + H - yDrag;
						repaint((Math.min(xPrev, xDrag) - 7), Math.min(yPrev, yDrag) - 7, xRepaintRange, yRepaintRange);
						xPrev = xDrag;
						yPrev = yDrag;        	    
					}            
				}            	         
			}
		});

		theAI = (BufferedImage) createImage(W, H); // set plot area to white initially
		theAG = theAI.createGraphics();	
		theAG.setPaint(Color.WHITE);
		theAG.fillRect(0, 0, W, H);
		setTitle("Sketch Pad");
		setVisible(true);
		clearScreen();
	} // end of constructor


	public void paint(Graphics g) // When 'Window' is first
	{ // shown or damaged
		super.paint(g);	
		g.drawImage( theAI,  0 + leftMargin,  topMargin,  this);	
		if(showOrigin == true) plotOrigin((Graphics2D) g, leftMargin + xOrigin, topMargin + H - yOrigin);
	}

	class Transaction implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			String actionIs = ae.getActionCommand();
			
			if (actionIs.equals("Pick Colour"))
			{ 
				pickColour();
			} 	
			
			if (actionIs.equals("Plot"))
			{ 	    	
				if(archive.size() > plotsUsed)
				{
					for(int i = archive.size() - 1; i > plotsUsed - 1; i--)
					{
						archive.remove(i);
					}

				}
				matrixData = matricies.getData();  
				int[] origin = {xOrigin, yOrigin};
				PlotData data = new PlotData(penColour, origin, (Integer) tilt.getValue(), 
						(Integer) density.getValue(), (Integer) scaler.getValue(), matrixData);
				archive.add(data);
				plot(theAG, data, H);  
				plotsUsed++;
			} 
			
			if (actionIs.equals("Clear"))
			{ 
				archive = new ArrayList<PlotData>();
				plotsUsed = 0;
				clearScreen();
				repaint();
			} 
		}
	}

	public void clearScreen()
	{
		theAI = (BufferedImage) createImage(W, H);
		theAG = theAI.createGraphics();				
		theAG.setPaint(backgroundColour);
		theAG.fillRect(0, 0, W, H);		
		repaint();
	}

	public void pickColour()
	{
		penColour = JColorChooser.showDialog(this, "Pick colour", Color.WHITE );
	}

	public void pickBackground()
	{
		backgroundColour = JColorChooser.showDialog(this, "Pick colour", Color.WHITE );
	}

	public void plotOrigin(Graphics2D g, int x, int y)
	{
		g.setPaint(new Color(255, 0, 0));
		g.drawLine(x-7, y, x + 7, y);
		g.drawLine(x, y - 7, x, y + 7);		
	}

	public boolean mouseInRange( int mouseXp, int mouseYp)
	{
		return (mouseXp <= W + leftMargin && mouseYp <= H + topMargin && mouseXp >= leftMargin && mouseYp >= topMargin);	
	}
}
