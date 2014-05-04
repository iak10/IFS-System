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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * Allows user to load in existing graphical images
 * which can then be modified by transferring in images 
 * from the IFSPlotter. Users can save the result
 */
public class ArtWindow extends IFSFrame {

	private static int H = 700; // Height of window
	private static int W = 700; // Width of window
	private int leftMargin = 20;
	private int rightMargin = 35;
	private int topMargin = 50;
	private int bottomMargin = 50;
	private BufferedImage theAI;
	private Graphics2D theAG;
	private JMenuBar menubar = new JMenuBar();
	private JMenu file;
	private JMenuItem save, saveAs, load,  exit;
	int prevXOrigin;
	int prevYOrigin;
	String filePath;
	boolean awaitImage = false;
	ArrayList<PlotData> plotArchive;
	private int noOfPlots;
	private IFSGUI copyOfGui;
	MyArtCanvas canvas = new MyArtCanvas();

	/**
	 * Initialises the GUI and sets up the actionlisteners and mouselisteners
	 * required for user interactivity. Menu bar functionality is implemented here
	 * including save and load options. Also takes in an instance of IFSGUI
	 * and makes it visible to entire class by assigning to a global variable.
	 * @param ifsgui - is assigned to global variable to make visible elsewhere in class
	 */
	public ArtWindow(IFSGUI ifsgui){

		copyOfGui = ifsgui;
		// set up the menu bar and drop-down menu
		menubar = new JMenuBar();
		setJMenuBar(menubar);
		file = new JMenu("File");  
		menubar.add(file);

		save = new JMenuItem("Save Image");
		saveAs = new JMenuItem("Save as");
		load = new JMenuItem("Load");
		exit = new JMenuItem("Exit");	

		file.add(save);
		file.add(saveAs);
		file.add(load);
		file.add(exit);

		/**
		 * Allows the user to specify a filename and path to save to by using
		 * the JFileChooser. If a file of the same path & name exists the user 
		 * is offered the choice of overwriting or not.
		 */
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {	 			

				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"bmp files", "bmp");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showSaveDialog(getParent());
				if(returnVal == JFileChooser.APPROVE_OPTION) //if open button clicked on chooser 
				{
					filePath = chooser.getSelectedFile().getPath();	//sets path to the one chosen by user		
					if(chooser.getSelectedFile().exists()) //if trying to save to already existing file
					{
						final JOptionPane optionPane = new JOptionPane();
						int reply = JOptionPane.showConfirmDialog(optionPane, "Overwrite existing file?"); //ask user if they want to overwrite
						if(reply == 0) //if no selected
						{
							try
							{
								ImageIO.write(theAI, "bmp", new File(chooser.getSelectedFile().getPath() + ".bmp"));
							}
							catch(IOException e)
							{
								System.out.println(e);
							}
						}
						else if(reply == 1)//if yes selected
						{
							save.doClick();		
						}
					}
					else //file does not already exist
					{
						try
						{
							ImageIO.write(theAI, "bmp", new File(chooser.getSelectedFile().getPath() + ".bmp")); //create and save new file
						}
						catch(IOException e)
						{
							System.out.println(e);
						}
					}		 	 	
				}	
				repaint();
			}
		});
		/**
		 *  Writes back to an existing filename and path if there is one,
		 *  otherwise behaves as if the user had selected saveAS
		 */
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {	
				if(filePath == null) // If no filepath user is asked to provide one
				{	
					saveAs.doClick(); // by simulating a click on saveAs
				}
				try
				{
					ImageIO.write(theAI, "bmp", new File(filePath + ".bmp")); // write to existing file name and path
				}
				catch(IOException e)
				{
					System.out.println(e);
				}
				repaint();
			}
		});
		/**
		 * Loads an image into theAI (a BufferedImage instance) and re-sizes the 
		 * JFrame and JPanel so theAI will fit on it. The user is invited to use  
		 * the JFileChooser to select the image to load.
		 * @param e - MouseEvent
		 */

		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"BMP files", "bmp"); // file chooser lets user pick a file
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(getParent());
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: " +
							chooser.getSelectedFile().getName());
					filePath = chooser.getSelectedFile().getPath();
					theAI = null;
					try {
						theAI = ImageIO.read(new File(filePath)); // load image into the alternate image
					} catch (IOException e) {
						System.out.println(e);
					} 		
					H = theAI.getHeight() +  topMargin + bottomMargin; // re-size JFrame to fit loaded image
					W = theAI.getWidth() + leftMargin + rightMargin;
					canvas.setBounds(leftMargin, topMargin - 20, W - leftMargin - rightMargin, H - topMargin - bottomMargin);
					setSize(W, H);
				}
				repaint(); // invoke repaint to draw image on JFrame
			}
		});

		setLocation(820,500);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener (new WindowAdapter() 
		{
			/**
			 * Alerts IFSGUI to the closing to allow for GUI to update
			 * @param evt - WindowEvent
			 */
			public void windowClosing (WindowEvent evt) 
			{
				copyOfGui.artWindowClose();
				dispose();
			}
		});
		// set properties of JFrame 
		setTitle("Art Pad");
		setFocusable(true);
		setSize(W + leftMargin + rightMargin, H + topMargin + bottomMargin);
		Container cp = getContentPane();
		cp.setLayout(null);
		canvas.setBounds(leftMargin, topMargin - 20, W, H);
		cp.add(canvas);
		setVisible(true);		
		clearScreen();//displays white canvas on startup
	} // end of constructor

	/**
	 * transferImage receives as parameters the data required to recreate a series of 
	 * plots and makes them available to the rest of the class by assigning them to 
	 * global variables. The data to create each plot is held in an instance  
	 * of PlotData. An image is usually composed of several plots, and an 
	 * arrayList of PlotData is used to hold the plots'data to draw an image.
	 * @param archive - Holds the data to recreate a number of plots as an 
	 * arrayList of PlotData
	 * @param xOldOrigin - X coordinate of origin of the plot when first created
	 * @param yOldOrigin - Y coordinate of origin when first created
	 * @param plots - Number of plots used to make the image
	 */
	public void transferImage(ArrayList<PlotData> archive, int xOldOrigin, int yOldOrigin, int plotsUsed)
	{
		awaitImage = true;
		plotArchive = archive;
		prevXOrigin = xOldOrigin;
		prevYOrigin = yOldOrigin;
		noOfPlots = plotsUsed;
	}
	/**
	 * Resets the entire "canvas" (the alternate image)
	 * which is pasted onto the JFrame to white 
	 */
	public void clearScreen()
	{
		theAI = (BufferedImage) createImage(W, H);
		theAG = theAI.createGraphics();				
		theAG.setPaint(Color.WHITE);
		theAG.fillRect(0, 0, W, H);		
		repaint();
	}
	
	/**
	 * The MyCanvas class is an extension of JPanel, and provides
	 * a panel on the JFrame onto which graphics can be placed
	 * by drawing from the BufferedImage instance, and plotted
	 * from the data sent by the IFSPlooter instance.
	 */
	
	public class MyArtCanvas extends JPanel
    {
		private int xPrev, yPrev;
		int xOrigin = 200, yOrigin = 200;
		public MyArtCanvas()
		{
			addMouseListener(new MouseAdapter() 			
			{
				/**
				 * Allows image to be created at point where mouse is released
				 * @param e - MouseEvent
				 */
				public void mouseReleased (MouseEvent e)
				{
					if(awaitImage)// if an image is ready to be transferred
					{
						xOrigin = e.getX(); //set origin relative to canvas margins
						yOrigin = H - e.getY();
						theAG = theAI.createGraphics();
						plotArchive(plotArchive, H, theAG, xOrigin - prevXOrigin, yOrigin - prevYOrigin, noOfPlots); //plot entire archive to recreate image
						awaitImage = false; //no longer transferring image
						repaint();

					}
				} 		    	    	
			});

			addMouseMotionListener(new MouseMotionAdapter() 
			{
				/**
				 * Provides the user with feedback as to where their image will be displayed
				 * by plotting the position of the origin as a small cross.
				 * @param e - MouseEvent
				 */
				public void mouseDragged (MouseEvent e)
				{ 
					if(awaitImage)// if an image is ready to be transferred
					{
						int xDrag = e.getX();
						int yDrag = e.getY();
						if (mouseInRange(xDrag, yDrag))//if mouse is on the canvas
						{
							int xRepaintRange = Math.abs(xDrag - xPrev) + 15;//work out area that requires repainting
							int yRepaintRange = Math.abs(yDrag - yPrev) + 15;
							xOrigin = xDrag; //work out origin in relation to margins
							yOrigin = H - yDrag;
							repaint((Math.min(xPrev, xDrag) - 7), Math.min(yPrev, yDrag) - 7, xRepaintRange, yRepaintRange);
							xPrev = xDrag;
							yPrev = yDrag;        	    
						}            
					}            	         
				}
			});
		}
		/**
		 * This copies the image from theAI (an instance of BufferedImage)
		 * onto the panel. If an image is awaited from the sketch pad it 
		 * plots the origin to assist the user in pacing the image which will
		 * be plotted when the user releases the mouse button.
		 * @Param g Graphics object used to draw image
		 */
	
		@Override	
		public void paintComponent(Graphics g) 
		{ 							 
			super.paintComponent(g);	
			g.drawImage( theAI,  0,  0,  this);	
			if(awaitImage) plotOrigin((Graphics2D) g, xOrigin, H - yOrigin);
		}	
		/**
		 * Draws a small cross to represent the point that is the origin
		 * @param g - the graphics object drawn on
		 * @param x - The x coordinate of the origin
		 * @param y - the y coordinate of the origin
		 */
		public void plotOrigin(Graphics2D g, int x, int y)
		{
			g.setPaint(new Color(225, 50, 0));
			g.drawLine(x-7, y, x + 7, y);
			g.drawLine(x, y - 7, x, y + 7);		
		}

		/**
		 * Returns true if the mouse position is within the bounds of the 
		 * canvas and otherwise returns false
		 * @param mouseXp - x coordinate of mouse position
		 * @param mouseYp - y coordinate of mouse position
		 */
		public boolean mouseInRange( int mouseXp, int mouseYp)
		{
			return (mouseXp <= W && mouseYp <= H  && mouseXp >= 0 && mouseYp >= 0);	
		}
    }
}
