package Project.IFS_System;

import java.awt.Color;
import java.util.Arrays;

import junit.framework.Assert;
import org.jmock.Mockery;
import org.jmock.Expectations;
import org.junit.Test;

public class MockTest
{
	IFSPlotter plotter;
	
	
	@Test
	public void testJmock1()
	{
		Mockery TestInterfaceMock= new Mockery();
		final IFSGUIInterface testInterface=TestInterfaceMock.mock(IFSGUIInterface.class);
		TestInterfaceMock.checking(new Expectations()
		{{
			oneOf(testInterface).getData(1);
			will(returnValue(new double[][]{
					{0, 0, 0, 0.16, 0, 0, 0.01},
					{0.85, 0.04, -0.04, 0.85, 0, 0.16, 0.6},
					{ 0.2, -0.26, 0.23, 0.22, 0, 0.16, 0.07},
					{-0.15, 0.28, 0.26, 0.24, 0, 0.044, 0.07}
			}	));
			
		}});
		double[][] j=testInterface.getData(1);
		Assert.assertTrue(Arrays.deepEquals(j, new double[][]{
				{0.0, 0.0, 0.0, 0.16, 0.0, 0.0, 0.01},
				{0.85, 0.04, -0.04, 0.85, 0.0, 0.16, 0.6},
				{ 0.2, -0.26, 0.23, 0.22, 0.0, 0.16, 0.07},
				{-0.15, 0.28, 0.26, 0.24, 0.0, 0.044, 0.07}
		})	);  
		PlotData plotData = new PlotData(new Color(0, 200, 0), new int[]{150, 150}, 20, 15, 100, j);
		Assert.assertEquals(plotData.getColour(), new Color(0, 200, 0));
		for(int i = 0; i < plotData.getOrigin().length; i ++)
		{
			Assert.assertEquals(plotData.getOrigin()[i], 150);
		}
		Assert.assertEquals(plotData.getTilt(), 20);
		Assert.assertEquals(plotData.getDensity(), 15);
		Assert.assertEquals(plotData.getScale(), 100);
		Assert.assertEquals(plotData.getMatrixData(), j);
		IFSTestFrame myIFSTestFrame = new IFSTestFrame(plotData);
		myIFSTestFrame.saveImage(1);
	}
	
	
	@Test
	public void testJmock2()
	{
		Mockery TestInterfaceMock= new Mockery();
		final IFSGUIInterface testInterface=TestInterfaceMock.mock(IFSGUIInterface.class);
		TestInterfaceMock.checking(new Expectations()
		{{
			oneOf(testInterface).getData(2);
			will(returnValue(new double[][]{
					{0.5, 0, 0, 0.5, 0, 0, 1},
					{0.5, 0, 0, 0.5, 0.5, 0, 1},
					{0.5, 0, 0, 0.5, 0.25, 0.5, 1}	
			}	));
			
		}});
		double[][] j=testInterface.getData(2);
		Assert.assertTrue(Arrays.deepEquals(j, new double[][]{
				{0.5, 0, 0, 0.5, 0, 0, 1},
				{0.5, 0, 0, 0.5, 0.5, 0, 1},
				{0.5, 0, 0, 0.5, 0.25, 0.5, 1}	
		})	);  
		PlotData plotData = new PlotData(new Color(10, 10, 200), new int[]{100, 100}, 0, 25, 180, j);
		Assert.assertEquals(plotData.getColour(), new Color(10, 10, 200));
		for(int i = 0; i < plotData.getOrigin().length; i ++)
		{
			Assert.assertEquals(plotData.getOrigin()[i], 100);
		}
		Assert.assertEquals(plotData.getTilt(), 0);
		Assert.assertEquals(plotData.getDensity(), 25);
		Assert.assertEquals(plotData.getScale(), 180);
		Assert.assertEquals(plotData.getMatrixData(), j);
		IFSTestFrame myIFSTestFrame = new IFSTestFrame(plotData);
		myIFSTestFrame.saveImage(2);
	}
	
}
