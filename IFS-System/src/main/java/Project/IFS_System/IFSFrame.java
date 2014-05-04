package Project.IFS_System;
import java.util.Random;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 * Defines methods to plot images from IFS codes
 * These methods are inherited by its sub classes
 * Allows for a single plot or many plots from an archive
 * Includes facilities for recreating images in a different 
 * position by redefining the origin
 */
abstract class IFSFrame extends JFrame {
	
	Random generator = new Random();	
	
	/**
	 * This version of plotArchive iterates through the instances of PlotData
	 * for each plot in the archive, and invokes the plot method for each one.
	 * It will draw several plots to recreate an image in its original place on screen
	 * @param archive - contains the transforms and other assorted data to pertaining to the plots
	 * @Param H - Height of drawing area
	 * @Param theAG - Graphics2D object used to draw on
	 * @Param plots - number of plots used to make the image
	 */
	public void plotArchive(ArrayList<PlotData> archive, int H, Graphics2D theAG, int plots)
	{
		for(int i = 0; i < plots; i++)
		{
			PlotData archivePlot = archive.get(i);
			plot(theAG,  archivePlot, H);
		}
	}
	
	/**
	 * This version of plotArchive Will draw several plots to recreate an image at a specified 
	 * origin. The origin may be specified to draw the image in a different position. Each 
	 * image in the plot is re-created with the offset required to place it at the new
	 * origin (x=0, y=0). This version of plotArchive iterates through the instances of
	 * PlotData, modifies the coordinates of the origin, invokes the plot method, then
	 * re-sets the origin as before to avoid a cumulative effect on future images.
	 * @param archive - contains the transforms and other assorted data to pertaining to the plots
	 * @Param H - Height of drawing area
	 * @Param theAG - Graphics2D object used to draw on
	 * @Param xOriginOffset - The x coordinate of the new origin
	 * @Param yOriginOffset - The y coordinate of the new origin
	 * @Param plots - number of plots used to make the image
	 */
	public void plotArchive(ArrayList<PlotData> archive, int H, Graphics2D theAG, int xOriginOffset, int yOriginOffset, int plots)
	{
		for(int i = 0; i < plots; i++)
		{
			PlotData archivePlot = archive.get(i);
			archivePlot.setOrigin(archivePlot.getOrigin()[0] + xOriginOffset, archivePlot.getOrigin()[1] + yOriginOffset);
			plot(theAG, archivePlot, H);
			archivePlot.setOrigin(archivePlot.getOrigin()[0] - xOriginOffset, archivePlot.getOrigin()[1] - yOriginOffset);
		}
	}
	
	
	/**
	 * Will draw a single plot with a specified origin
	 * Information required for the size, colour, and orientation
	 * of the plot is extracted from the instance of PlotData before 
	 * plotting. A random seed is generated before each execution of 
	 * the inner for-loop, and 300 iterations are allowed to ensure
	 * the points are within the invariant set before plotting starts.
	 * The out loop allows for several repeats of the process for a
	 * large plot that would otherwise not get adequate coverage of points.
	 * @param theAG - The Graphics object used to draw
	 * @Param matrixData - A two dimensional array of double
	 * holding the required transformations and other data 
	 * (e.g. colour) for the image to be drawn
	 * @Param xOrigin - The x coordinate of the new origin
	 * @Param yOrigin - The y coordinate of the new origin
	 * @param H - Height of drawing area
	 */
	
	public void plot(Graphics2D theAG, PlotData aPlotData,  int H)
	{
		double[] transform;
		double[][] matrixData =  aPlotData.getMatrixData();
		theAG.setPaint(aPlotData.getColour());	
		int tilt = aPlotData.getTilt();  
		int scale = aPlotData.getScale();
		int[] origin = aPlotData.getOrigin();
		int repeats = Math.round(scale*scale/20000) + 1; // allow several repeats for a large plot
		for (int ix = 0; ix < repeats; ix++)
		{	
			int[] rolls = rollDice(matrixData, aPlotData.getDensity()); 
			double[] coords = new double[]{ generator.nextDouble(), generator.nextDouble() };
			for(int i = 0; i < rolls.length; i++)  
			{
				transform = matrixData[rolls[i]];
				coords = iterate(coords, transform);
				if (i>300) 
				{
					double xPlot = coords[0] * Math.cos(Math.toRadians(tilt))*scale + (coords[1] * Math.sin(Math.toRadians(tilt)))*scale;
				    double yPlot = -coords[0] * Math.sin(Math.toRadians(tilt))*scale + (coords[1] * Math.cos(Math.toRadians(tilt)))*scale;
					theAG.draw(new Line2D.Double(xPlot + origin[0], H -  origin[1] - yPlot, xPlot +  origin[0], H -  origin[1] - yPlot));
	        	}   
			}			
		}
		repaint();
	}
	
	/**
	 * Returns an array of int holding the results of randomly selecting
	 * a transform for each point to be plotted using the probability weightings.
	 * The number of points to be plotted is related to the density of the plot.
	 * @Param matrixData - contains the probability weightings required
	 * @Param dense - used to calculate the amount of dice rolls required
	 */
	public int[] rollDice( double[][] matrixData, int dense)
	{ 
		int noOfTransforms = matrixData.length;
		int[] prob = new int[noOfTransforms];
		for(int i = 0; i < noOfTransforms; i++)
		{
			prob[i] = (int) (100*matrixData[i][6]);
		}
		LoadedDice dice = new LoadedDice(prob);
		int noOfRolls = (2000*dense) + 300; // the result of the first 300 rolls will not be plotted
		int[] rolls = dice.rollMany(noOfRolls); // no of points plotted is proportional to density
		return rolls;
	}
	
	/**
	 * Iterates the coordinates to the coordinates of the next  mathematical 
	 * point to be plotted returns an array of double to represent the point
	 */
	public double[] iterate(double[] oldCoords, double[] currentTransform)
	{
		double xNew = (oldCoords[0]*currentTransform[0]) + (oldCoords[1]*currentTransform[1]) + currentTransform[4];
		double yNew = (oldCoords[0]*currentTransform[2]) + (oldCoords[1]*currentTransform[3]) + currentTransform[5];
		return new double[] {xNew, yNew};
	}
}
