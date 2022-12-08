package JunitTestingCh9;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.io.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.sql.*;


/**This class is used to create a class which handle which words are in the file and their frequency
 * 
 * @author Adamb
 * @version 1.9
 * @since 11-4-2022
 *
 */
class Word {
	
	String word;
	int frequency;
	
	/**This overloaded constructor is used to create a word with the inserted string. A new word always has the frequency 1 because it is the first time showing up.
	 * 
	 * @param word Takes in a string to create the name for the instantiated Word object.
	 */
	Word(String word) {
		this.word = word;
		frequency = 1;
	}
	
	/**This method is used to get the frequency of a Word object
	 * 
	 * @return This returns a int, the frequency of a given Word object.
	 */
	public int getFrequency() {
		return this.frequency;
	}
}

/**This is a class to contain and store a list of words objects
 * 
 * @author Adamb
 * @version 1.9
 * @since 11-4-2022
 *
 */
class WordList {
	
	/**Sorted WordList is a arraylist to contain Words
	 * 
	 */
	static List<Word> SortedWordList = new ArrayList<Word>();
	
	/**This method is used to set the Wordlist (an arraylist of words) to the instantiated WordList class 
	 * 
	 * @param SortedWordList Takes a list of Words and sets it to the instantiated WordList class
	 */
	public void SetWordList (List<Word> SortedWordList) {
		this.SortedWordList = SortedWordList;
	}
	
	/**This method gets the size of the WordList object
	 * 
	 * @return This returns and int, the size of the WordList object
	 */
	public int GetWordListSize () {
		return SortedWordList.size();
	}
}

	/**This is the main class of the program. This class holds void main method to run the program
	 * 
	 * @author Adamb
	 * @version 1.9
	 * @since 11-4-2022
	 */
public class TextAnalyzer {
	static DatabaseConnection db = new DatabaseConnection();
	
	/**This method  is used in constructing the GUI and setting up a new frame
	 * 
	 */
	private static void constructGUI()
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		MyFrame frame = new MyFrame(db, StringList);
		frame.setVisible(true);
	}
	
	/**These are the two variables used to Create the lists
	 * 
	 */
	static List<Word> WordList = new ArrayList<Word>();
	static List<String> StringList = new ArrayList<String>();

	
	/**This method creates a list of every word in the file. It takes in a string txt file.
	 * It has a try and catch statement to when add the file word by word to the string list.
	 * 
	 * @param file Takes in a string txt file that the user wants to be read.
	 * @throws Catches all exceptions in the file uploading process, mostly IO exceptions when inputing the document
	 * @return Returns a string list with the imported words in the file.
	 */
	public static List<String> ReadFile(String file) {
		
		
		/**Try to read all lines in the file
		 * 
		 */
		try {
		    Scanner scnr = new Scanner(new File(file));
		    while (scnr.hasNextLine()) {
		    String data = scnr.next();
		    StringList.add(data);
		      }
		    scnr.close();
		}
		/**Catches exceptions if found
		 * 
		 */
		catch (Exception e) {
			System.out.println("You have an error.");
			e.printStackTrace();
		}
		
		return StringList;
	}
	
	/**This method checks the entire file and then checks to see if word is in unique word array and adds frequency if it is.
	 * If not it creates a new instantiated version of the Word
	 * 
	 * @param word Takes in a string. The word that should be checked against the WordList
	 */
	
	public static void DatabaseAddWord (List<Word> wl) {
		
		for (Word input: wl) {
			db.addData("INSERT INTO word VALUES ('" + input.word + "'," + input.getFrequency() + ")" ) ;
		}
		
	}
	
	
	public synchronized static void CheckWord(String word, DatabaseConnection db) {
		boolean wordFound = false;
			
		
		if(WordList.size() == 0) {
			WordList.add(new Word(word));
		} else {
			boolean isMatch = false;
			
			for (Word input: WordList) {
				
			if(word.equalsIgnoreCase(input.word)) {
				input.frequency += 1;
				isMatch = true;
			}				
		  }
			if (isMatch == false) {
				WordList.add(new Word(word));
			}
		}
	 }
	
	/**This method is used to sort the words by frequency
	 * 
	 * @param wordList Takes a list of words(WordList) and sorts them by their frequency
	 * @return Returns the sorted WordList by highest frequency
	 */
	public static List<Word> SortWord (List<Word> wordList) {
		
		Comparator<Word> byFrequency = Comparator.comparing(Word::getFrequency);
		wordList.sort(byFrequency.reversed());
		
		return wordList;
	}
	
	
	/**This method is the main method for the program. It reads the file, checks the words, outprints, sorts the list and creates the GUI
	 * 
	 * @param args Unused
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/**Reading the file, in this case the Raven poem
		 * 
		 */
		ReadFile("TheRavenPoem.txt");	
		
		db.connect();
		
		/**checks words
		 * A for loop to check each word in the file
		 */

		for (String input: StringList)
			{
			CheckWord(input, db);
			}
		
		/**sorting the words
		 * 
		 */
		WordList = SortWord(WordList);
		
		WordList SortedWordList = new WordList();
		
		SortedWordList.SetWordList(WordList);
		
		/**outputs the words
		 * 
		 */
		for (Word input: WordList)
		{
		System.out.print("Word: " + input.word + "\n " + "Frequency: " + input.frequency + "\n\n");;
		}
		
		System.out.print(SortedWordList.GetWordListSize() + " ");
		
		DatabaseAddWord(WordList);
		/**Creates the GUI on the Screen
		 * 
		 */
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run() {
			constructGUI();
			}
		});
	}
	
	
}


/**This class is used for Creating the action listener and implements the ActionListener class. 
 * 
 * @author Adamb
 * @version 1.9	
 * @since 11-4-2022
 *
 */
class MyButtonListener implements ActionListener {
	MyFrame fr;
	DatabaseConnection db;
	List<String> StringList;
	/**This method takes in the frame that the user wants to use for the ActionListener and sets it to the Fr variable
	 * 
	 * @param frame Takes in a MyFrame object and sets it to the MyFrame that the ActionListener will use
	 */
	public MyButtonListener(MyFrame frame, DatabaseConnection db, List<String> sl)
	{
		fr = frame;
		this.db = db;
		StringList = sl;
	}

	/**This method is used by the ActionListener to see if an action was performed and if the search was pressed it searchs the WordList to see if there is a match
	 * If there is, it displays the frequency. If there is no match it displays that the word does not appear in the poem.
	 * 
	 * @param e Takes and ActionEvent to tie to the button
	 */
	public void actionPerformed(ActionEvent e)
	{
		JButton btn = (JButton) e.getSource();
		ResultSet rs =  db.selectFrequency(fr.WordField.getText());
				
		if (fr.WordField.getText().equalsIgnoreCase("exit")) {
		 	for (String input: StringList)
			{
			db.clearTable(input);
			}
			db.disconnect();		 
		 } else
			try {
				if(rs.next()) {
					String test = rs.getString(1);
				if (fr.WordField.getText().equalsIgnoreCase(rs.getString(1))) {
				 fr.result.setText("Results: The word " + fr.WordField.getText() + " appears in the poem " + rs.getInt(2)  + " times");
				 } else {
				 fr.result.setText("Results: The word " + fr.WordField.getText() + " does not appear in the poem");
				 }
				}
				else {
					fr.result.setText("An error has occured");
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	
	}
}

/**This class creates the class for the Jframe for the GUI by extending the JFrame class
 * 
 * @author Adamb
 * @version 1.9
 * @since 11-4-2022
 */
class MyFrame extends JFrame {
	public JLabel result;
	public JTextField WordField;
	DatabaseConnection db;
	List<String> StringList;
	
	public MyFrame(DatabaseConnection db, List<String> sl) {
		super();
		this.db = db;
		StringList = sl;
		init();
	}
	
	/**This method is used to initialize the MyFrame class
	 * 
	 */
private void init() {
	this.setTitle("Text Analyzer for the Raven"); /**Setting title */
	JLabel wordSearchLabel = new JLabel("Word to be searched"); /**Label for the first number*/
	JButton search = new JButton("Search"); /**Creates the search button*/
	search.addActionListener(new MyButtonListener(this, db, StringList)); /**Actionlistener for the search button*/
	WordField = new JTextField(); /**Text for first number*/
	result = new JLabel("Please enter a word that you would like to search, and press \"search\" to receive results"); /**Jlabel for results*/
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setLayout(new GridLayout(3, 2)); /**Using the gridlayout*/
	/**Adding everything to the GUI*/
	this.add(wordSearchLabel);
	this.add(WordField);
	this.add(new JLabel());
	this.add(search);
	this.add(result);
	this.pack();
	this.setVisible(true);
	}
}


class DatabaseConnection
{
	//Variables for connection, resultset and statement
	Connection connection =null;
	ResultSet results = null;
	Statement st = null;
	int rowsAffected = 0;
	
	//Connects to database
	public void connect()
	{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			//ADD CODE HERE
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/wordoccurrences",
				"root","dolphins");
	//TODO: Run a select statement and print out the ResultSet
	} catch (SQLException e) {
			e.printStackTrace();
	}
}
	
	//Disconnects from database
	public void disconnect()
	{
		try {
			st.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet addData(String query) {

			try {
				st = connection.createStatement();
				rowsAffected = st.executeUpdate(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			//System.out.print(query + " results: " + results);
			
			return results;
	}
	
	public ResultSet updateFrequency(String query) {

		try {
			st = connection.createStatement();
			rowsAffected = st.executeUpdate(query);
		} catch (SQLException e) {
		e.printStackTrace();
		}
		System.out.print(query + " results: " + results);
	
		return results;
	}
	
	public ResultSet selectFrequency(String word) {
		
		try {
			st = connection.createStatement();
			results = st.executeQuery("SELECT * FROM word WHERE word_name = '" + word + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.print(word + " results: " + results);	

		return results;
	}
	
	public ResultSet clearTable(String word) {
		
		try {
			st = connection.createStatement();
			rowsAffected = st.executeUpdate("DELETE from word WHERE word_name = '" + word + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.print(word + " results: " + results);	

		return results;
	}
	
}