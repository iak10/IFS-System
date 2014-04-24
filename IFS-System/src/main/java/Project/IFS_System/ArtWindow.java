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
	ArrayList<double[][]> plotArchive;
	private int xM, yM, xPrev, yPrev;
	private int noOfPlots = 0;
	private IFSGUI copyOfGui;

	public ArtWindow(IFSGUI ifsgui){
	
		copyOfGui = ifsgui;

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

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {	
				if(filePath == null)
				{	
					saveAs.doClick();
				}
				try
				{
					ImageIO.write(theAI, "bmp", new File(filePath + ".bmp"));
				}
				catch(IOException e)
				{
					System.out.println(e);
				}
			}
		});

		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"BMP files", "bmp");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(getParent());
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: " +
							chooser.getSelectedFile().getName());
					filePath = chooser.getSelectedFile().getPath();
					theAI = null;
					try {
						theAI = ImageIO.read(new File(filePath));
					} catch (IOException e) {
						System.out.println(e);
					} 		
					H = theAI.getHeight() +  topMargin + bottomMargin;
					W = theAI.getWidth() + leftMargin + rightMargin;
					setSize(W, H);
				}
			}
		});

		addMouseListener(new MouseAdapter() 			
		{
			public void mouseReleased (MouseEvent e)
			{
				if(awaitImage)
				{
					xOrigin = e.getX() - leftMargin;
					yOrigin = topMargin + H - e.getY();
					theAG = theAI.createGraphics();
					plotArchive(plotArchive, H, theAG, xOrigin - prevXOrigin, yOrigin - prevYOrigin, noOfPlots);
					awaitImage = false;
					repaint();

				}
			} 		    	    	
		});

		addMouseMotionListener(new MouseMotionAdapter() 
		{
			public void mouseDragged (MouseEvent e)
			{ 
				if(awaitImage)
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
		setLocation(820,500);
		
	    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	    addWindowListener (new WindowAdapter() 
	    {
	    	public void windowClosing (WindowEvent evt) 
	    	{
	    		copyOfGui.artWindowClose();
	    		dispose();
	    	}
	    });
	    
		setTitle("Art Pad");
		setFocusable(true);
		setSize(W + leftMargin + rightMargin, H + topMargin + bottomMargin);
		setVisible(true);
		clearScreen();

	}

	public void paint(Graphics g) // When 'Window' is first
	{ // shown or damaged
		super.paint(g);	
		g.drawImage( theAI,  0 + leftMargin,  topMargin,  this);	
		if(awaitImage) plotOrigin((Graphics2D) g, leftMargin + xOrigin, topMargin + H - yOrigin);
	}

	public void transferImage(ArrayList<double[][]> archive, int xOldOrigin, int yOldOrigin, int plots)
	{
		awaitImage = true;
		plotArchive = archive;
		prevXOrigin = xOldOrigin;
		prevYOrigin = yOldOrigin;
		noOfPlots = plots;
	}

	public boolean mouseInRange( int mouseXp, int mouseYp)
	{
		return (mouseXp <= W + leftMargin && mouseYp <= H + topMargin && mouseXp >= leftMargin && mouseYp >= topMargin);	
	}

	public void plotOrigin(Graphics2D g, int x, int y)
	{
		g.setPaint(new Color(225, 50, 0));
		g.drawLine(x-7, y, x + 7, y);
		g.drawLine(x, y - 7, x, y + 7);		
	}

	public void clearScreen()
	{
		theAI = (BufferedImage) createImage(W, H);
		theAG = theAI.createGraphics();				
		theAG.setPaint(Color.WHITE);
		theAG.fillRect(0, 0, W, H);		
		repaint();
	}

}
