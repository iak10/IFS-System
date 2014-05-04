package Project.IFS_System;
import java.awt.Color;

public class PlotData {
	Color plotColour;
	int[] origin;
	int tilt;
	int density;
	int scale;
	double[][] matrixData;
	
	public PlotData(Color colour, int[] aOrigin, int aTilt, int aDensity, int aScale, double[][] matricies)
	{
		plotColour = colour;
		origin = aOrigin;
		tilt = aTilt;
		density = aDensity;
		scale = aScale;
		matrixData = matricies;
	}
	
	public void setColour(Color aColour)
	{
		plotColour = aColour;
	}
	
	public Color getColour()
	{
		return plotColour;
	}
	
	public void setOrigin(int x, int y)
	{
		origin[0] = x;
		origin[1] = y;
	}
	
	public int[] getOrigin()
	{
		return origin;
	}
	
	public void setTilt(int aTilt)
	{
		tilt = aTilt;
	}
	
	public int getTilt()
	{
		return tilt;
	}
	public void setDensity(int aDensity)
	{
		density = aDensity;
	}
	
	public int getDensity()
	{
		return density;
	}
	
	public void setScale(int aScale)
	{
		scale = aScale;
	}
	
	public int getScale()
	{
		return scale;
	}
	
	public void setMatrixData(double[][] aMatrixData)
	{
		matrixData = aMatrixData;
	}
	
	public double[][] getMatrixData()
	{
		return matrixData;
	}
}
