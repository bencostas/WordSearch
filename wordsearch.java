/*
Ben Costas

Word Search 2D GUI

*/

import javax.swing.*;
import java.awt.*; // Import GUI
import java.awt.event.*;//Adding the event since we will now be using an action listener
import java.util.*;
import java.io.*;

public class wordsearch extends JFrame implements ActionListener{
    //Create Panels (6):
    JPanel instructionPanel = new JPanel();
    JPanel[] rows = new JPanel[6];
    JPanel processAndPrintPanel = new JPanel();
    JPanel wordExistsPanel = new JPanel();
    
    //Initialize Random
    Random randGen = new Random();

    //Initialize file and file reader
    File file = new File("wordlist.txt");
    Scanner filePrint = new Scanner(file);

    //Initialize alphabet
    String[] consonants = {"B", "C", "D", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "V", "W", "X", "Y", "Z"};
    String[] vowels = {"A", "E", "I", "O", "U"};

    //Create alphabetGrid 
    String[][] alphabetGrid = new String[6][6];

    //Initialize arraylist to store all words of the text file
    ArrayList<String> wordList = new ArrayList<String>();

    //Components
    JLabel[][] alphabetDisplay = new JLabel[6][6];

    JButton okButton = new JButton("OK");
    JTextField enterWord = new JTextField(6);
    JLabel wordExists = new JLabel("", JLabel.RIGHT);
    JLabel instructions = new JLabel("Please enter a word in the box and press 'OK'", JLabel.CENTER);

    public wordsearch() throws FileNotFoundException{

        //Set all items in the file to an arraylist
        while (filePrint.hasNext()) {
            wordList.add(filePrint.nextLine());
        }

        //GUI
        setTitle("Word Search");
        setSize(450,300);
        setResizable(false);//Cannot adjust the size of window

        //Initialize Layouts
        BoxLayout boxLayout = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
        GridLayout gridLayout = new GridLayout();
        FlowLayout flowLayout = new FlowLayout();

        setLayout(boxLayout); // Set layout of the frame to box

        //Set panels to grid layout
        instructionPanel.setLayout(flowLayout);

        for (int i = 0; i < rows.length; i++) {
            rows[i] = new JPanel();
            rows[i].setLayout(gridLayout);
        }
        processAndPrintPanel.setLayout(flowLayout);
        wordExistsPanel.setLayout(flowLayout);


        okButton.addActionListener(this); // create action listener for button

        //Add components 

        instructionPanel.add(instructions);

        int vowelCounter = 0; //Count the # of vowels

        for (int i = 0; i < alphabetGrid.length; i++) {
            System.out.print(" | "); // Console output
            int vowelRow = 0; // Count the # of vowels per row
            for (int k = 0; k < alphabetGrid[0].length; k++) {

                int determineVowelorConsonant = randGen.nextInt(2); // generate a number from 0-2; (33% chance for a vowel)

                if (determineVowelorConsonant == 0  && vowelCounter < 12 && vowelRow < 2) { // vowel occurrence (2 per row and # of vowels has to be less than 12)
                    alphabetGrid[i][k] = vowels[randGen.nextInt(4)];; // Set vowel to the alphabet grid
                    System.out.print(alphabetGrid[i][k] + " | "); // Console output
                    alphabetDisplay[i][k] = new JLabel((alphabetGrid[i][k]) , JLabel.CENTER); // Print out the random letter (must correspond with alphabet grid)
                    vowelCounter++;
                    vowelRow++;
                }

                else { // consonant occurrence
                    alphabetGrid[i][k] = consonants[randGen.nextInt(20)]; // Set consonant to the alphabet grid
                    System.out.print(alphabetGrid[i][k] + " | "); // Console output
                    alphabetDisplay[i][k] = new JLabel((alphabetGrid[i][k]) , JLabel.CENTER); // Print out the random letter (must correspond with alphabet grid)
                }
                rows[i].add(alphabetDisplay[i][k]); // Add it to the column
            }
            System.out.println();
        }
        System.out.println("Please enter a word in the GUI window: "); // Console output

        processAndPrintPanel.setLayout(flowLayout); // Set processing panel to flow

        processAndPrintPanel.add(enterWord); // Add components to panel
        processAndPrintPanel.add(okButton);

        wordExistsPanel.add(wordExists);

        // Add the panels to the display
        add(instructionPanel);

        for (int i = 0; i < rows.length; i++) { 
            add(rows[i]);
        }
        add(processAndPrintPanel);
        add(wordExistsPanel);

        setVisible(true); // Make the components visible on frame
    }

    public void actionPerformed (ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("OK")) { // If the user clicks OK button
            wordExists.setText("");
            String wordEntered = enterWord.getText(); //Get the textfield string and make it a variable
            System.out.println("-\nWord entered: " + wordEntered); // output to console
            
            String checkWord = search(wordEntered, alphabetGrid); // Check if the word exists using the search method

            if (checkWord.equalsIgnoreCase(wordEntered)) { // If word exists on grid 
                binarySearchWordList(wordList, wordEntered, wordExists); // Determine if the word exists in the grid or not
                System.out.println();
            }
            if (!checkWord.equalsIgnoreCase(wordEntered)) { // If the word does not exist on the grid
                System.out.println("The word does not exist on the grid");
                wordExists.setText("The word does not exist on the grid");
            }
        }
    }

    public static String search(String wordEntered, String[][] alphabetGrid) {
        // Initialize x and y components - use these to determine if the word entered is in the matrix
        // 0 - vertical dir., 1 horizontal dir., 2 - diagonal (R&U)/(L&D), 3 - diagonal (R&D)/(L&U)
        int[] xComponent = {0, 1, 1, 1};
        int[] yComponent = {1, 0, 1, -1}; 

        //Split the wordEntered array
        String[] wordEnteredArray = wordEntered.split("");

        //Initialize a false word if the word does not exist
        String falseWord = "x";

        //Find where the first letter of the word is in the matrix:
        for (int i = 0; i < 6; i++) {
            for (int k = 0; k < 6; k++) {
                if (alphabetGrid[i][k].equalsIgnoreCase(wordEntered)) { // If the word entered is the letter at index alphabetGrid[i][k]
                    return alphabetGrid[i][k];
                }
                if (alphabetGrid[i][k].equalsIgnoreCase(wordEnteredArray[0])) { // If the first letter of the word matches with the letter at the specific index
                    
                    //Determine directions around it to fill the word (search all 4 directions)
                    for (int direction = 0; direction < 8; direction++) {// Check every direction around the letter
                        String verification = alphabetGrid[i][k]; // Create variable to concat alphabetGrid letters
                        //Determine the starting component

                        int startX;
                        int startY;

                        if (direction > 3) {//Reverse Direction
                            startX = i - xComponent[direction-4]; //Subtract 4 (go back to the starting index)
                            startY = k - yComponent[direction-4]; // Subtract the components (negative direction)
                        }

                        else { // Fwds directions
                            startX = i + xComponent[direction]; // Add the component
                            startY = k + yComponent[direction];
                        }
                        
                        //Match the rest of the characters (start at the next index)
                        //Search surrounding letters
                        for (int r = 1; r < wordEntered.length(); r++) { 
                            if (startX < 0 || startX > 5 || startY < 0 || startY > 5) { //If the components go out of bounds
                                break; // break out of this loop (go to next dir.)
                            }
                            if (!alphabetGrid[startX][startY].equalsIgnoreCase(wordEnteredArray[r])) { // If the letter does not match the specified letter of the wordEntered
                                break; // break out of this loop (go to next dir.)
                            }
                            verification += alphabetGrid[startX][startY]; // Add letters to verification variable

                            if (direction > 3) { //Reverse direction
                                startX -= xComponent[direction-4]; //continue in the specified direction (x)
                                startY -= yComponent[direction-4]; //continue in the specified direction (y)
                            }
                            
                            else {
                                startX += xComponent[direction]; //continue in the specified direction (x)
                                startY += yComponent[direction]; //continue in the specified direction (y)
                            }
                            if (verification.equalsIgnoreCase(wordEntered)) {//If the word is found
                                return verification;//return to main (correct word)
                            }
                        }
                    }
                }
            }
        }
    return falseWord; // if it goes through all the comparisons and the word doesn't exist, return ("x")
}
    public static void binarySearchWordList (ArrayList<String> wordList, String wordEntered, JLabel wordExists) { //Binary Search algorithm (use non recursive as it holds less memory (makes program faster))
        int low = 0;
        int high = wordList.size() - 1;

        while (low <= high) {
            int middle = (low+high)/2;

            if (wordEntered.equalsIgnoreCase(wordList.get(middle))) { // If the word exists
                System.out.print("The word exists on the grid and exists in the word list"); // output
                wordExists.setText("The word exists on the grid and exists in the word list");
                break;
            }

            if (wordEntered.compareToIgnoreCase(wordList.get(middle)) > 0) { // If the word entered is greater than the "middle"
                low = middle + 1; // Set low to middle + 1
            }

            else { // If the word entered is less than the "middle"
                high = middle - 1; // Set high to middle - 1
            }
        }
        if (high < low) { // If the word does not exist in the list
        System.out.print("The word exists on the grid and does not exist in the word list"); // output
        wordExists.setText("The word exists on the grid and does not exist in the word list");
    }
}
    //Main Method
    public static void main(String[] args) throws FileNotFoundException {
        wordsearch wordSearchGUI = new wordsearch();
    }
}