package Project.IFS_System;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;


public class IFSGUI extends JFrame {
	private static int H = 650; // Height of window
	private static final int W = 700; // Width of window
	private int xMargin = 70;
	private int yMargin = 60;
	private int topOffset = 150;
	private int rightMargin = 30;
	private int maxTransformations = 8;
	private int currentTransformations = 8;
	private int dataOnTransformations = 7;
	private int gap = 90;
	private Color backgroundColour= new Color(250,250,220);
	private JFormattedTextField[][] matrixData = new JFormattedTextField[maxTransformations][dataOnTransformations];
	private JLabel[] labels1 = new JLabel[maxTransformations];
	private JLabel[] labels2 = new JLabel[maxTransformations];
	private JLabel[] labels3 = new JLabel[maxTransformations];
	private JLabel[] labels4 = new JLabel[maxTransformations];
	private JLabel[] equals = new JLabel[maxTransformations];
	private JLabel[] plus = new JLabel[maxTransformations];
	private JLabel[] brackets1 = new JLabel[maxTransformations];
	private JLabel[] brackets2 = new JLabel[maxTransformations];
	private JLabel[] brackets3 = new JLabel[maxTransformations];
	private JLabel[] brackets4 = new JLabel[maxTransformations];
	private JLabel[] brackets5 = new JLabel[maxTransformations];
	private JLabel[] brackets6 = new JLabel[maxTransformations];
	private JLabel[] brackets7 = new JLabel[maxTransformations];
	private JLabel[] brackets8 = new JLabel[maxTransformations];
	private JMenuBar menubar = new JMenuBar();
	private JMenu file;
	private String filePath = null;
	private NumberFormat numFormat1; 
	private NumberFormat numFormat2; 
	int[] xCoordinates = new int[]{ 170, 225, 170, 225, 410, 410, 550 };
	int[] yOffset = new int[]{ 100, 100, 125, 125, 100, 125, 110 };
	boolean artWindow = false;
	IFSGUI selfReference = null;
	final JCheckBox artWindowBox = new JCheckBox("Art Window");
	ArtWindow artWindowFrame;


	public IFSGUI(){
		setSize(W + xMargin + rightMargin, H + yMargin + topOffset ); // Size of drawing area
		getContentPane().setBackground( backgroundColour );
		setDefaultCloseOperation(EXIT_ON_CLOSE);    
		setResizable(false); 
		Container cp = getContentPane();
		cp.setLayout(null);
		selfReference = this;

		numFormat1 = NumberFormat.getNumberInstance();

		for(int i = 0; i < maxTransformations; i++)
		{
			for(int j = 0; j < dataOnTransformations; j++ )
			{
				matrixData[i][j] = new JFormattedTextField(numFormat1);
				matrixData[i][j].setBounds(xCoordinates[j], yOffset[j] + gap*i, 45, 20);
				cp.add(matrixData[i][j]);
				matrixData[i][j].setValue(0);
				if (j == 6) matrixData[i][j].setValue(0.1);
			}

			labels1[i] = new JLabel("<html>x<sub>n+1</sub></html>");
			labels2[i] = new JLabel("<html>y<sub>n+1</sub></html>");
			labels3[i] = new JLabel("<html>x<sub>n</sub></html>");
			labels4[i] = new JLabel("<html>y<sub>n</sub></html>");

			brackets1[i] = new JLabel("[");
			brackets3[i] = new JLabel("[");
			brackets5[i] = new JLabel("[");
			brackets7[i] = new JLabel("[");

			brackets2[i] = new JLabel("]");
			brackets4[i] = new JLabel("]");
			brackets6[i] = new JLabel("]");
			brackets8[i] = new JLabel("]");

			equals[i] = new JLabel("=");
			plus[i] = new JLabel("+");

			labels1[i].setFont(new Font("Serif", Font.PLAIN, 18));
			labels2[i].setFont(new Font("Serif", Font.PLAIN, 18));
			labels3[i].setFont(new Font("Serif", Font.PLAIN, 18));
			labels4[i].setFont(new Font("Serif", Font.PLAIN, 18));

			brackets1[i].setFont(new Font("Serif", Font.PLAIN, 48));
			brackets2[i].setFont(new Font("Serif", Font.PLAIN, 48));
			brackets3[i].setFont(new Font("Serif", Font.PLAIN, 54));
			brackets4[i].setFont(new Font("Serif", Font.PLAIN, 54));
			brackets5[i].setFont(new Font("Serif", Font.PLAIN, 54));
			brackets6[i].setFont(new Font("Serif", Font.PLAIN, 54));
			brackets7[i].setFont(new Font("Serif", Font.PLAIN, 54));
			brackets8[i].setFont(new Font("Serif", Font.PLAIN, 54));

			equals[i].setFont(new Font("Serif", Font.BOLD, 30));
			plus[i].setFont(new Font("Serif", Font.BOLD, 30));

			labels1[i].setBounds(50, gap * i + 97, 55, 25);
			labels2[i].setBounds(50, gap * i + 122, 55, 25);
			labels3[i].setBounds(310, gap * i + 97, 55, 25);
			labels4[i].setBounds(310, gap * i + 122, 55, 25);

			brackets1[i].setBounds(32, gap*i + 63, 25, 100);
			brackets2[i].setBounds(85, gap*i + 63, 25, 100);
			brackets3[i].setBounds(152, gap*i + 65, 35, 100);
			brackets4[i].setBounds(267, gap*i + 65, 35, 100);
			brackets5[i].setBounds(292, gap*i + 65, 25, 100);
			brackets6[i].setBounds(325, gap*i + 65, 25, 100);
			brackets7[i].setBounds(394, gap*i + 65, 25, 100);
			brackets8[i].setBounds(450, gap*i + 65, 25, 100);

			equals[i].setBounds(120, gap*i + 103, 30, 30);
			plus[i].setBounds(360, gap*i + 103, 30, 30);
			
			artWindowBox.setBounds(700, 100, 20, 20);
			cp.add(artWindowBox);
			cp.add(labels1[i]);
			cp.add(labels2[i]);
			cp.add(labels3[i]);
			cp.add(labels4[i]);	
			cp.add(brackets1[i]);
			cp.add(brackets2[i]);
			cp.add(brackets3[i]);
			cp.add(brackets4[i]);
			cp.add(brackets5[i]);
			cp.add(brackets6[i]);
			cp.add(brackets7[i]);
			cp.add(brackets8[i]);
			cp.add(equals[i]);
			cp.add(plus[i]);
		} // end of for loop to position widget for eight transformations
		
		artWindowBox.setMnemonic(KeyEvent.VK_C); 
		artWindowBox.setSelected(false);
		artWindowBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(artWindowBox.isSelected()) System.out.println("checkbox checked");
				if(artWindow == false) System.out.println("artwindow set to false");
				if(artWindowBox.isSelected() && artWindow == false)
				{
					System.out.println("entered if statement");
					artWindowFrame = new ArtWindow(selfReference);
					artWindow = true;
				}
				else if (!artWindowBox.isSelected() && artWindow == true)
				{
					artWindowFrame.dispose();
					artWindowClose();
				}
			}
		} );

		JLabel transformationsLabel = new JLabel("Select the number of transformations you wish to use:");
		JLabel artBoxLabel = new JLabel("Open Art Window");
		artBoxLabel.setFont(new Font("Serif", Font.PLAIN, 15));
		transformationsLabel.setFont(new Font("Serif", Font.PLAIN, 17));
		transformationsLabel.setBounds(35, 25, 450, 25);
		artBoxLabel.setBounds(655, 65, 450, 25);
		cp.add(transformationsLabel);
		cp.add(artBoxLabel);

		JLabel probability = new JLabel("<html><body>Probablity<br>Weightings</body></html>");
		probability.setFont(new Font("Serif", Font.PLAIN, 15));
		probability.setBounds(15, 25, 100, 65);
		cp.add(probability);

		menubar = new JMenuBar();
		setJMenuBar(menubar);

		file = new JMenu("File");  
		menubar.add(file);

		JMenuItem save = new JMenuItem("Save");
		final JMenuItem saveAs = new JMenuItem("Save as");
		JMenuItem load = new JMenuItem("Load");
		JMenuItem exit = new JMenuItem("Exit");	

		file.add(save);
		file.add(saveAs);
		file.add(load);
		file.add(exit);

		JPanel probabilities = new JPanel(null);
		probabilities.setBackground(new Color(200, 200, 255));
		cp.add(probabilities);
		probabilities.add(probability);
		probabilities.setBounds(535, 0, 100, H + topOffset + yMargin);

		String[] transList = { "1", "2", "3", "4", "5", "6", "7", "8"};


		final JComboBox transformList = new JComboBox(transList);
		transformList.setSelectedIndex(7);
		transformList.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){

						JComboBox combo = (JComboBox)e.getSource();
						String t = (String)combo.getSelectedItem();
						currentTransformations = Integer.parseInt(t);
						H = (currentTransformations - 1) * gap;
						setSize(W + xMargin + rightMargin, H + yMargin + topOffset );

					}
				} 
				);

		transformList.setBounds(415, 25, 45, 30);
		cp.add(transformList);

		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);  // closes the program
			}
		});

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if(filePath == null)
				{

					saveAs.doClick();
				}
				else
				{
					try
					{
						FileOutputStream fos = new FileOutputStream(filePath);
						DataOutputStream dos = new DataOutputStream(fos);

						dos.writeDouble(currentTransformations);
						for(int i = 0; i < currentTransformations; i++)
						{
							for(int j = 0; j < dataOnTransformations; j++ )
							{
								dos.writeDouble(Double.parseDouble(matrixData[i][j].getText()));

							}
						}
					}
					catch(IOException e)
					{
						System.out.println(e);
					}
				}
			}
		});

		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"IFS files", "ifs");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(getParent());
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: " +
							chooser.getSelectedFile().getName());
					filePath = chooser.getSelectedFile().getPath();

					try
					{
						FileInputStream fis = new FileInputStream(filePath);
						DataInputStream dis = new DataInputStream(fis);

						double b = dis.readDouble();
						System.out.println(b);
						int c = (int) Math.round(b);
						transformList.setSelectedItem(String.valueOf(c));
						System.out.println("no of trans = " + currentTransformations);
						for(int i = 0; i < currentTransformations; i++)
						{
							for(int j = 0; j < dataOnTransformations; j++ )
							{
								String a = String.valueOf(dis.readDouble());
								matrixData[i][j].setText(a);

							}
						}	 			

					}
					catch(IOException e)
					{
						System.out.println(e);
					}

				}
			}
		});

		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {


				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"IFS files", "ifs");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showSaveDialog(getParent());
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to Save to file: " +
							chooser.getSelectedFile().getName() + ".ifs");
					filePath = chooser.getSelectedFile().getPath() + ".ifs";


					if(chooser.getSelectedFile().exists())
					{
						final JOptionPane optionPane = new JOptionPane();
						int a = JOptionPane.showConfirmDialog(optionPane, "Overwrite existing file?");
						if(a == 0)
						{
							try
							{
								FileOutputStream fos = new FileOutputStream(filePath);
								DataOutputStream dos = new DataOutputStream(fos);

								dos.writeDouble(currentTransformations);
								for(int i = 0; i < currentTransformations; i++)
								{
									for(int j = 0; j < dataOnTransformations; j++ )
									{
										dos.writeDouble(Double.parseDouble(matrixData[i][j].getText()));

									}
								}
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
								saveAs.doClick();
							}
						}
					}
					else
					{
						try
						{
							FileOutputStream fos = new FileOutputStream(filePath);
							DataOutputStream dos = new DataOutputStream(fos);

							dos.writeDouble(currentTransformations);
							for(int i = 0; i < currentTransformations; i++)
							{
								for(int j = 0; j < dataOnTransformations; j++ )
								{
									dos.writeDouble(Double.parseDouble(matrixData[i][j].getText()));

								}
							}
						}
						catch(IOException e)
						{
							System.out.println(e);
						}
					}
				}

			}
		});
		setTitle("Transformations for IFS image generation");
		setVisible(true);
		repaint();
	} // end of constructor

	public double[][] getData()
	{
		double[][] matrix = new double[currentTransformations][dataOnTransformations];
		for(int i = 0; i < currentTransformations; i++)
		{
			for(int j = 0; j < dataOnTransformations; j++)
			{
				matrix[i][j] = Double.parseDouble(matrixData[i][j].getText());
			}
		}
		return matrix;

	}
	
	public void artWindowClose()
	{
		System.out.println("art window close invoked");
		artWindow = false;
		artWindowBox.setSelected(false);
	}
	
	public ArtWindow getArtWindow()
	{
		if(artWindow)
		{
			return artWindowFrame;
		}
		else
		{
			return null;
		}
	}

}
