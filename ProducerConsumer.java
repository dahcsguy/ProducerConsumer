/**
*  @author     Wayne Zhang
*  Class:      4002-219
*  Assignment: Producer/Consumer
*  Purpose:    To get an GUI that sets up reader and writer cooperation between two threads using a shared resource to 
*              demonstrate a producer/consumer problem.
*/

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.text.DefaultCaret;


/**
*  @author     Wayne Zhang
*  Class:      4002-219
*  Assignment: Producer/Consumer
*  Purpose:    To a GUI that sets up reader and writer cooperation between two threads using a shared resource to 
*              demonstrate a producer/consumer problem.
*/

public class ProducerConsumer extends JFrame implements ActionListener
{
	//JTextFields
	private JTextField jtfMax, jtfTar, jtfBuffer;
	//TheArea
	private TheArea tArea;
	//JLabels
	private JLabel jlMax, jlNum, jlBuffer;
	//JButtons
	private JButton jbStart, jbReset, jbExit;
	//JPanels
	private JPanel jpTop, jpCenter, jpBottom;
	//random int you will generate
	private int randomInt = 0;
	//max number you enter
	private int maxNum = 0;
	//target number you enter
	private int targetNum = 0;
	//global booleans to alternate between reading and writing
	public static boolean keepGoing;
	public static boolean go;
	public static boolean ran;


	/**
*  The GUI of the program
*/
	public ProducerConsumer()
	{
		//sets the title to Number Finder
		setTitle("Number Finder");
		//sets the size to 700, 700
		setSize( 700, 700 );
		//sets the location to 150, 150
		setLocation(150,150);
		
		//JPanels
		jpTop = new JPanel();
		jpCenter = new JPanel();
		jpBottom = new JPanel();
		
		
		//JTextAreas
		jtfMax = new JTextField(4);
		jtfTar = new JTextField(4);
		jtfBuffer = new JTextField(4);
		//make jtfBuffer uneditable
		jtfBuffer.setEditable(false);


		//instantized tArea
		tArea = new TheArea();
		
		//make the text in tArea to go to the next line if its too long
		tArea.setLineWrap(true);
		tArea.setWrapStyleWord(true);
		//add create a scroll bar to tArea
		add( new JScrollPane( tArea ), BorderLayout.CENTER );   
		tArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret) tArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		//JLabels
		jlMax = new JLabel("Enter Max #");
		jlNum = new JLabel("Target #");
		jlBuffer = new JLabel("Buffer");
		
		//JButtons and their mnemonics
		jbStart = new JButton("Start");
		jbStart.setMnemonic('S');
		jbStart.setEnabled(false);
		jbReset = new JButton("Reset");
		jbReset.setMnemonic('R');
		jbExit = new JButton("Exit");
		jbExit.setMnemonic('x');

		//add the JTextFields and the JLabels to jpTop
		jpTop.add(jlMax);
		jpTop.add(jtfMax);
		jpTop.add(jlNum);
		jpTop.add(jtfTar);
		jpTop.add(jlBuffer);
		jpTop.add(jtfBuffer);
		
		//add jpTop to the north side of the JFrame
		add(jpTop, "North");
		
		//add the JButtons to jpBottom
		jpBottom.add(jbStart);
		jpBottom.add(jbReset);
		jpBottom.add(jbExit);
		
		//add jpBottom to the south side of the JFrame
		add(jpBottom, "South");
		DocumentListener listener = new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				enableButton(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				enableButton(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				enableButton(e);
			}

			private void enableButton(DocumentEvent e) {
				if(jtfMax.getText().trim().equals("")||jtfTar.getText().trim().equals(""))
					jbStart.setEnabled(false);
				else
					jbStart.setEnabled(true);
			}
		};
		jtfMax.getDocument().addDocumentListener(listener);
		jtfTar.getDocument().addDocumentListener(listener);
		//anonymous inner class 
		jbStart.addActionListener(
		new ActionListener()
		{
			/**
			*  Invoked when jbStart is clicked
			*  @param ae The button that was click
			*/   
			public void actionPerformed(ActionEvent e)
			{
				//start time of when you click start
				final long startTime = System.currentTimeMillis();
				keepGoing = true;
				ran = true;
				go = true;
				//create new threads
				Thread t1 = new Thread( new Reader(jtfMax, jtfTar, tArea) );
				Thread t2 = new Thread(new Writer(jtfBuffer, tArea));
				
				//start threads
				
				t1.start();
				t2.start();
				
				
				//thrad to keep track of time
				Thread starter = 
				new Thread(){
					//run starter
					public void run(){
						while(keepGoing&&ran)
						{
							//disable running when running
							jbStart.setEnabled(false);
							jbReset.setEnabled(false);
						}
						//enable buttons when finish
						jbStart.setEnabled(true);
						jbReset.setEnabled(true);
						//end time of when program finds the target number
						final long endTime = System.currentTimeMillis();
						//prints time to the tArea
						if(ran){
							tArea.append("\n"+"It took "+(endTime-startTime)+" milliseconds.\n");
						}
						
					}
				};
				//start the thread
				
				starter.start();
				
			}
		});

		//addActionListener to jbReset and jbExit
		jbReset.addActionListener(this);
		jbExit.addActionListener(this);
		
		//exit program
		setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
		
		//makes the JFrame visible
		setVisible( true );
	}
	/**
*  @author    Wayne Zhang
*  Purpose:   To read the value that was generated in the Writer class
*/   
	class Reader implements Runnable
	{
		//JTextFields
		private JTextField jtf1, jtf2;
		//TheArea
		private TheArea ta;
		/**
	*  Reads the values from jtf1 and jtf2 to determine if you can start generating random numbers
	*  Gets the value from the Writer class
	*  @param _jtf1 the JTextField where you put the max number
	*  @param _jtf2 the JTextField where you put the target number
	*  @param _ta   the TheArea is where you get access to the getValue() method
	*/   
		public Reader(JTextField _jtf1 , JTextField _jtf2, TheArea _ta)
		{
			//jtf1 is equal to _jtf1
			jtf1 = _jtf1;
			//jtf2 is equal to _jtf2
			jtf2 = _jtf2;
			//ta is equal to _ta
			ta = _ta;
		}
		/**
	*  Runs the Reader class
	*/   
		public synchronized void run()
		{
			ProducerConsumer.go = true;
			
			//checks if you can parseInt jtf1
			try
			{
				//maxNum is equal to the int value of jtf1
				maxNum = Integer.parseInt(jtf1.getText());
			}
			//jtf1 contains characters other than numbers
			catch(NumberFormatException nfe)
			{
				//display message
				JOptionPane.showMessageDialog(null, "You entered: "+jtfMax.getText()+"\nPlease enter a postive number");
				//the while loop wont run
				go=false;
				keepGoing = false;
				ran = false;
			}
			//checks if you can parseInt jtf2
			if(go){
				try
				{
					//targetNum is equal to the int value of jtf2
					targetNum = Integer.parseInt(jtf2.getText());
				}
				//jtf2 contains characters other than numbers
				catch(NumberFormatException nfe)
				{
					//display message
					JOptionPane.showMessageDialog(null, "You entered: "+jtfTar.getText()+"\nPlease enter a postive number");
					//the while loop wont run
					go = false;
					keepGoing = false;
					ran = false;
					
				}
			}
			
			while(go)
			{
				//if the targetNum is greater than the maxNum
				if(targetNum > maxNum)
				{
					//display message
					JOptionPane.showMessageDialog(null, "Please enter a Target # that is < Max #");
					//break out of the loops
					go=false;
					keepGoing = false;
				}
				//if either the targetNum or the maxNum is less than 0
				else if(targetNum < 0 || maxNum < 0)
				{
					//display message
					JOptionPane.showMessageDialog(null, "Please enter #'s that is positive");
					//break out of the loops
					go=false;
					keepGoing = false;
				}
				//the targetNum is equal to or lower than the maxNum
				else
				{ 
					//read the random int
					ta.getValue();
					
				}
			}
			
			
		}
	}
	/**
*  @author    Wayne Zhang
*  Purpose:   To write the random value to jtfbuffer and the JTextArea
*/   
	class Writer implements Runnable
	{
		//JTextField
		private JTextField jtf;
		//TheArea
		private TheArea ta;
		/**
	*  Writes random value to jtf and ta
	*  @param _jtf the JTextField where it shows the number that was randomly generated
	*  @param _ta   the TheArea is where you get access to the setValue() method
	*/   
		public Writer(JTextField _jtf, TheArea _ta)
		{
			jtf = _jtf;
			ta = _ta;
		}
		/**
	*  Run the Writer class
	*/   
		public synchronized void run()
		{
			
			while(keepGoing)
			{
				//creates a Random object
				Random ran = new Random();
				try{
					//creates a random integer lower than or equal to the max number
					randomInt = ran.nextInt(maxNum+1);
				}
				//catch IllegalArgumentException
				catch(IllegalArgumentException iae)
				{
					
				}
				//sets the value to the randomInt
				ta.setValue(randomInt+" ");
			}
		}
	}
	/**
*  @author    Wayne Zhang
*  Purpose:   Give the Writer and Reader class the setValue() and getValue() methods
*/   
	class TheArea extends JTextArea   
	{
		//boolean to alternate between writing and reading
		boolean alternate = true;
		/**
	*  Writes the random number to jtfBuffer and to the JTextArea
	*  @param theValue
	*/      
		public synchronized void setValue(String theValue)
		{
			//alternate between Writer and Reader
			if(alternate)
			{
				//if you have to keep generating numbers
				if(keepGoing)
				{
					//set the random to jtfBuffer
					jtfBuffer.setText(randomInt+"");
					//add the number to the JTextArea
					this.append(theValue+" ");
					//makes it possible to alternate between Writer and Reader
					alternate = false;
				}
			}
		}
		/**
	*  Checks if the target number is equal to the number generated
	*/      
		public synchronized void getValue()
		{
			//alternate between Writer and Reader
			if(!alternate)
			{
				//gets the text of the JTextArea
				this.getText();
				//if jtfBuffer is equal to jtfTar
				if(jtfBuffer.getText().equals(jtfTar.getText()))
				{
					//end the loops
					go=false;
					keepGoing = false;
				}
				//makes it possible to alternate between Writer and Reader
				alternate = true;
			}
		}
	}
	/**
*  Invoked when an action occurs
*  @param ae The button that was click
*/      
	public void actionPerformed( ActionEvent ae )
	{
		//if the button that was click is equal to Reset
		if(ae.getActionCommand().equals("Reset"))
		{
			//reset everything in the JFrame
			jtfMax.setText("");
			jtfTar.setText("");
			jtfBuffer.setText("");
			tArea.setText("");
			//cursor is focus on jtfMax
			jtfMax.requestFocusInWindow();
		}
		//if the button that was click is equal to Exit
		else if(ae.getActionCommand().equals("Exit"))
		{
			//exit the program
			System.exit(1);
		}  
	}
	/**
	ProducerConsumer is executed here
*/
	public static void main(String [] args)
	{
		new ProducerConsumer();
	}
}