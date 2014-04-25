package Project.IFS_System;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Allows user to load in existing graphical images
 * which can then be modified by transfering in images 
 * from the IFSPlotter. Users can save the result
 */
public class ArtWindow extends IFSFrame {

	private static int H = 700; // Height of window
	private static int W = 700; // Width of window
	private int leftMargin = 20;
	private int rightMargin = 20;
	private int topMargin = 100;
	private int bottomMargin = 20;
	private BufferedImage theAI;
	private Graphics2D theAG;
	private JMenuBar menubar = new JMenuBar();
	private JMenu file;
	private JMenuItem save, saveAs, load,  exit;
	int xOrigin = 200;
	int yOrigin = 200;
	int prevXOrigin;
	int prevYOrigin;
	String filePath;
	boolean awaitImage = false;
	ArrayList<PlotData> plotArchive;
	private int xPrev, yPrev;
	private int noOfPlots;
	private IFSGUI copyOfGui;

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

		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {	 			

				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"bmp files", "bmp");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showSaveDialog(getParent());
				if(returnVal == JFileChooser.APPROVE_OPTION) //open button clicked on chooser 
				{
					System.out.println("You chose to Save to file: " +
							chooser.getSelectedFile().getName());
					filePath = chooser.getSelectedFile().getPath();	//sets path to the one chosen by user		
					if(chooser.getSelectedFile().exists()) //if trying to save to already existing file
					{
						final JOptionPane optionPane = new JOptionPane();
						int a = JOptionPane.showConfirmDialog(optionPane, "Overwrite existing file?"); //ask user if they want to overwrite
						if(a == 0) //if no selected
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
						else if(a == 1)//if yes selected
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
					setSize(W, H);
				}
				repaint(); // invoke repaint to draw image on JFrame
			}
		});

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
					xOrigin = e.getX() - leftMargin; //set origin relative to canvas margins
					yOrigin = topMargin + H - e.getY();
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
						xOrigin = xDrag - leftMargin; //work out origin in relation to margins
						yOrigin = topMargin + H - yDrag;
						repaint((Math.min(xPrev, xDrag) - 7), Math.min(yPrev, yDrag) - 7, xRepaintRange, yRepaintRange);
						xPrev = xDrag;
						yPrev = yDrag;        	    
					}            
				}            	         
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
		setVisible(true);
		
		clearScreen();//displays white canvas on startup
	}

	/**
	 * Will repaint window when first shown or damaged and also provide visual
	 * feedback to user by showing a cross representing the origin when if
	 * an image is currently in the process of being transfered.
	 * @param g - the Graphics object that is used to draw
	 */
	public void paint(Graphics g) 
	{ 							 
		super.paint(g);	
		g.drawImage( theAI,  0 + leftMargin,  topMargin,  this);	
		if(awaitImage) plotOrigin((Graphics2D) g, leftMargin + xOrigin, topMargin + H - yOrigin);
	}

	/**
	 * takes as parameters what is required to recreate a series of plots
	 * and makes them available to the rest of the class by assigning them to global variables.
	 * The data to create each plot is held in a 2D array of double, and consists of the 
	 * mathematical codes for several affine transformations, and several
	 * numbers specifying the scale factor, the tilt and the colour of 
	 * the plot. An image is usually composed of several plots, and an 
	 * arrayList of 2D arrays of double is used to hold the data to draw an image.
	 * @param archive - Holds the data to recreate each plot 
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
	 * Returns true if the mouse position is within the bounds of the canvas
	 * @param mouseXp - x coordinate of mouse position
	 * @param mouseYp - y coordinate of mouse position
	 */
	public boolean mouseInRange( int mouseXp, int mouseYp)
	{
		return (mouseXp <= W + leftMargin && mouseYp <= H + topMargin && mouseXp >= leftMargin && mouseYp >= topMargin);	
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

}
