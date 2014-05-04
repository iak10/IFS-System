package Project.IFS_System;


public class IFSGUIStub implements IFSGUIInterface{

	public double[][] getData(int dataNo) {
		if (dataNo == 1)
		{
	    	return new double[][]{
				{0, 0, 0, 0.16, 0, 0, 0.01},
				{0.85, 0.04, -0.04, 0.85, 0, 0.16, 0.6},
				{ 0.2, -0.26, 0.23, 0.22, 0, 0.16, 0.07},
				{-0.15, 0.28, 0.26, 0.24, 0, 0.044, 0.07}
		    };	
		}
	else 
	{ 
		return new double[][]{
				{0.5, 0, 0, 0.5, 0, 0, 1},
				{0.5, 0, 0, 0.5, 0, 0, 1},
				{0.5, 0, 0, 0.5, 0, 0, 1}	
		};	
	}	
  }
}

