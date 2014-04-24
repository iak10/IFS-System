package Project.IFS_System;

public class App {

	public static void main(String args[])
	{
		IFSGUI gui = new IFSGUI();
//		ArtWindow artWindow = new ArtWindow(gui);
		IFSPlotter plotter = new IFSPlotter(gui, null);
		
	}
}
