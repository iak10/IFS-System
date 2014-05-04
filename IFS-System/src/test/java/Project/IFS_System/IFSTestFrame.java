package Project.IFS_System;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class IFSTestFrame extends IFSFrame {
	
	private BufferedImage theAI;
	private Graphics2D theAG;
	private PlotData aPlotData;
	
	public IFSTestFrame(PlotData plotData)
	{
		aPlotData = plotData;
		setSize(400,400);
		Container cp = getContentPane();
		cp.setLayout(null);
		setVisible(true);
	} // end of contrcuctor
	
	public void saveImage(int no)
	{
		theAI = (BufferedImage) createImage(300, 300); // set drawing area to user's chosen colour
		theAG = theAI.createGraphics();	
		theAG.setPaint(Color.white);
		theAG.fillRect(0, 0, 300, 300);
		plot(theAG, aPlotData,  300);
		try {
			ImageIO.write(theAI, "png", new File("testIFSImage" + no + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
