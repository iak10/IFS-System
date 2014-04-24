package Project.IFS_System;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JFrame;


public class IFSFrame extends JFrame {

	public void plot(Graphics2D theAG, double[][] matrixData, int noOfTransforms, Color penColour, int H)
	{
		double xO = matrixData[noOfTransforms][3];
		double yO = matrixData[noOfTransforms][4];
		Integer tilt = (int) matrixData[noOfTransforms][5];
		Integer scale = (int) matrixData[noOfTransforms][6];
		Integer dense = (int) matrixData[noOfTransforms + 1][1];
		int repeats = (int) Math.round(scale*scale/20000) + 1;
		for (int ix = 0; ix < repeats; ix++)
		{	
			int[] prob = new int[noOfTransforms];
			double[] currentTransform = null;
			for(int i = 0; i < noOfTransforms; i++)
			{
				prob[i] = (int) (100*matrixData[i][6]);
			}
			LoadedDice dice = new LoadedDice(prob);
			int b = (2000*dense) + 300;
			int[] rolls = dice.rollMany(b);

			theAG.setPaint(penColour);
			System.out.println("repeats = " + repeats);

			double x = 0.5;
			double y = 0.5;
			for(int i = 0; i < rolls.length; i++)  
			{
				int a = rolls[i];
				currentTransform = matrixData[a];
				double xNew = (x*currentTransform[0]) + (y*currentTransform[1]) + currentTransform[4];
				double yNew = (x*currentTransform[2]) + (y*currentTransform[3]) + currentTransform[5];
				x = xNew;
				y = yNew;
				double xPlot = x * Math.cos(Math.toRadians(tilt)) + (y * Math.sin(Math.toRadians(tilt)));
				double yPlot = -x * Math.sin(Math.toRadians(tilt)) + (y * Math.cos(Math.toRadians(tilt)));
				if (i>300) 
				{
					theAG.draw(new Line2D.Double(xPlot*scale + xO, H - yO - yPlot*scale, xPlot*scale + xO, H - yO - yPlot*scale ));
				}		
			}	
		}
		repaint();
	}

	public void plot(Graphics2D theAG, double[][] matrixData, int noOfTransforms, Color penColour, int xOrigin, int yOrigin,  int H)
	{
		Integer tilt = (int) matrixData[noOfTransforms][5];
		Integer scale = (int) matrixData[noOfTransforms][6];
		Integer dense = (int) matrixData[noOfTransforms + 1][1];
		int repeats = (int) Math.round(scale*scale/20000) + 1;
		for (int ix = 0; ix < repeats; ix++)
		{	
			int[] prob = new int[noOfTransforms];
			double[] currentTransform = null;
			for(int i = 0; i < noOfTransforms; i++)
			{
				prob[i] = (int) (100*matrixData[i][6]);
			}
			LoadedDice dice = new LoadedDice(prob);
			int b = (2000*dense) + 300;
			int[] rolls = dice.rollMany(b);

			theAG.setPaint(penColour);
			System.out.println("repeats = " + repeats);

			double x = 0.5;
			double y = 0.5;
			for(int i = 0; i < rolls.length; i++)  
			{
				int a = rolls[i];
				currentTransform = matrixData[a];
				double xNew = (x*currentTransform[0]) + (y*currentTransform[1]) + currentTransform[4];
				double yNew = (x*currentTransform[2]) + (y*currentTransform[3]) + currentTransform[5];
				x = xNew;
				y = yNew;
				double xPlot = x * Math.cos(Math.toRadians(tilt)) + (y * Math.sin(Math.toRadians(tilt)));
				double yPlot = -x * Math.sin(Math.toRadians(tilt)) + (y * Math.cos(Math.toRadians(tilt)));
				if (i>300) 
				{
					theAG.draw(new Line2D.Double(xPlot*scale + xOrigin, H - yOrigin - yPlot*scale, xPlot*scale + xOrigin, H - yOrigin - yPlot*scale ));
				}		
			}	
		}
		repaint();
	}

	public void plotArchive(ArrayList<double[][]> archive, int H, Graphics2D theAG, int plots)
	{
		for(int i = 0; i < plots; i++)
		{
			double[][] archiveMatrix = archive.get(i);
			//	System.out.println("Length returned from length " + archiveMatrix.length);
			Color penColour = new Color((int)archiveMatrix[archiveMatrix.length - 2][0], (int) archiveMatrix[archiveMatrix.length - 2][1], (int) archiveMatrix[archiveMatrix.length - 2][2]);
			int xOrigin = (int) archiveMatrix[archiveMatrix.length - 2][3];
			//	System.out.println("Xorigin is " + xOrigin);
			int yOrigin = (int) archiveMatrix[archiveMatrix.length - 2][4];
			Integer tiltAngle = (int) archiveMatrix[archiveMatrix.length - 2][5];
			//	System.out.println("Yorigin is " + yOrigin);
			Integer scaleFactor = (int) archiveMatrix[archiveMatrix.length - 2][6];
			int noOfTransforms = (int) archiveMatrix[archiveMatrix.length - 1][0];
			Integer densityFactor = (int) archiveMatrix[archiveMatrix.length - 1][1];
			//	System.out.println("number of transforms is " + noOfTransforms);
			System.out.println("Plotting from archive with plots = " + plots);
			plot(theAG,  archiveMatrix, noOfTransforms, penColour, H);
		}

	}

	public void plotArchive(ArrayList<double[][]> archive, int H, Graphics2D theAG, int xOriginOffset, int yOriginOffset, int plots)
	{
		for(int i = 0; i < plots; i++)
		{
			double[][] archiveMatrix = archive.get(i);
			//	System.out.println("Length returned from length " + archiveMatrix.length);
			Color penColour = new Color((int)archiveMatrix[archiveMatrix.length - 2][0], (int) archiveMatrix[archiveMatrix.length - 2][1], (int) archiveMatrix[archiveMatrix.length - 2][2]);
			int xOrigin = (int) archiveMatrix[archiveMatrix.length - 2][3];
			//	System.out.println("Xorigin is " + xOrigin);
			int yOrigin = (int) archiveMatrix[archiveMatrix.length - 2][4];
			Integer tiltAngle = (int) archiveMatrix[archiveMatrix.length - 2][5];
			//	System.out.println("Yorigin is " + yOrigin);
			Integer scaleFactor = (int) archiveMatrix[archiveMatrix.length - 2][6];
			Integer densityFactor = (int) archiveMatrix[archiveMatrix.length - 1][1];
			int noOfTransforms = (int) archiveMatrix[archiveMatrix.length - 1][0];
			//	System.out.println("number of transforms is " + noOfTransforms);
			plot(theAG,  archiveMatrix, noOfTransforms, penColour, xOrigin + xOriginOffset, yOrigin + yOriginOffset, H);
		}

	}

}
