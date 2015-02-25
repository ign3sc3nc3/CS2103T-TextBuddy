/**
 * Program: TextBuddy CE2
 * Author: Jonathan Lim Siu Chi
 * Matric Number: A0110839H
 * Group: W15-3J
 * 
 * TextBuddy takes in a file name as an argument on execution. It checks for the existence of a file
 * of that name. If it exists, the user can choose to overwrite the file by giving Y (for yes) or N (for no)
 * as input. If it does not exist, a new file with that name will be automatically created.
 * 
 * Functions of TextBuddy are as follows: 
 *  1. add <string> : Will add string input into file.
 *  2. delete <integer> : Will delete the nth line as specified by integer input in the file. 
 *  	Throws error if no integer is given. 
 *  3. display : Prints out the content of the file.
 *  4. clear : Deletes all contents of the file.
 *  5. exit: Exits the program.
 *  
 *  Every add, delete and clear operation will save the file.
 *	Improper commands given will result in an error message.
 *	Deleting non-existent lines will result in an error message.
 */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class TextBuddy {

	private static final String MESSAGE_COMMAND_NOT_RECOGNISED = "Command is not recognised. Please try again.";
	private static final String MESSAGE_LINE_NOT_FOUND = "Line %1$s cannot be found in %2$s.";
	private static final String MESSAGE_CLEAR_FILE = "All content deleted from %1$s";
	private static final String MESSAGE_DELETE_LINE = "Deleted from %1$s: \"%2$s\" ";
	private static final String MESSAGE_ADD_LINE = "Added to %1$s: \"%2$s\" ";
	private static final String MESSAGE_DISPLAY_LINE = "%1$s. %2$s";
	private static final String MESSAGE_EMPTY_FILE = "%1$s is empty.";
	private static final String MESSAGE_WELCOME = "Welcome to Textbuddy.";
	private static final String MESSAGE_READY = "%1$s is ready for use.";
	private static final String MESSAGE_COMMAND = "Command: ";
	private static final String MESSAGE_FILE_ALREADY_EXISTS = "%1$s already exists. Overwrite existing file? (Y/N) ";
	private static final String MESSAGE_ENTER_NEW_FILE = "Enter new file name: ";
	private static final String MESSAGE_OVERWRITE_FILE = "%1$s will be overwritten.";

	private static final String MESSAGE_IO_ERROR = "Error reading or writing file.";
	private static final String MESSAGE_CREATE_TEMP_FILE_ERROR = "Error in creating temporary file.";

	private static final String STRING_PREFIX = "temp";
	private static final String STRING_POSTFIX = "txt";

	public static File outputFile;
	private static File temporaryFile;
	private static PrintWriter writer;
	private static Scanner reader;

	enum ACTION_TYPE {
		ADD_LINE, DELETE_LINE, CLEAR_FILE, DISPLAY_FILE, INVALID_COMMAND, EXIT_PROGRAM, SORT;

	}

	public static void main(String[] args) {
		String outputFileName = args[0];
		Scanner commandLineInput = new Scanner(System.in);

		printWelcomeMessage();

		outputFileName = overwriteExistingFile(outputFileName, commandLineInput);

		outputFile = new File(outputFileName);
		createFileIfDoesNotExist();

		printReadyMessage();

		commandExecuter(commandLineInput);

	}

	private static void commandExecuter(Scanner commandLineInput) {
		while (true) {
			System.out.print(MESSAGE_COMMAND);

			String command = commandLineInput.nextLine();
			String actionString = getFirstWord(command);
			String sentence = getLineToAddOrDelete(command);
			ACTION_TYPE actionType = actionInterpreter(actionString);

			switch (actionType) {
				case ADD_LINE:
					addLineToFile(sentence);
					printAddMessage(sentence);
					break;

				case DELETE_LINE:

					if (isNumberString(sentence)) {
						int lineToDelete = getLineToDelete(sentence);
						deleteLineFromFile(lineToDelete);
					}
					break;

				case CLEAR_FILE:
					clearFileData();
					printClearMessage();
					break;

				case DISPLAY_FILE:
					printFile();
					break;
					
				case SORT:
					sortFileContents();
					break;

				case EXIT_PROGRAM:
					exitProgram();
					break;

				default:
					messagePrinter(MESSAGE_COMMAND_NOT_RECOGNISED);
					break;
			}

		}
	}
	
	private static int getLineToDelete(String sentence){
		return Integer.parseInt(sentence);
	}
	
	private static Boolean isNumberString(String sentence) {
		try {
			Integer.parseInt(sentence);
			return true;
		} catch (NumberFormatException e) {
			messagePrinter(MESSAGE_COMMAND_NOT_RECOGNISED);
			return false;
		}

	}

	private static void createFileIfDoesNotExist() {
		if (!outputFile.exists()) {
			try {
				outputFile.createNewFile();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String overwriteExistingFile(String outputFileName, Scanner commandLineInput) {
		while (new File(outputFileName).exists()) {
			String chooseToOverwrite = null;
			String message = String.format(MESSAGE_FILE_ALREADY_EXISTS, outputFileName);
			System.out.print(message);

			chooseToOverwrite = commandLineInput.nextLine().toLowerCase().trim();

			if (chooseToOverwrite.equals("n")) {
				System.out.print(MESSAGE_ENTER_NEW_FILE);
				outputFileName = commandLineInput.nextLine();
			} else {
				message = String.format(MESSAGE_OVERWRITE_FILE, outputFileName);
				messagePrinter(message);
				break;
			}
		}
		return outputFileName;
	}

	private static void addLineToFile(String sentence) {
		setUpWriter(outputFile, true); // true: append data to file
		writer.println(sentence);
		closeWriter();
	}

	private static void deleteLineFromFile(int lineToDelete) {

		createTemporaryFile();

		setUpReader(outputFile);
		setUpWriter(temporaryFile, false); 
		
		int lineNumber = 0;
		String sentenceDeleted = null;
		Boolean sentenceIsFound = false;
		
		//transfer undeleted files to temporary file
		while (reader.hasNextLine()) {
			String lineFromFile = reader.nextLine();
			lineNumber++;

			if (lineNumber == lineToDelete) {
				sentenceIsFound = true;
				sentenceDeleted = lineFromFile;
			} else { 
				writer.println(lineFromFile);
			}
		}

		closeReader();
		closeWriter();

		rewriteActualFileWithTempFile();

		if (sentenceIsFound) {
			printDeleteMessage(sentenceDeleted);
		} else {
			printNotFoundMessage(lineToDelete);
		}

	}

	private static void createTemporaryFile() {
		try {
			temporaryFile = File.createTempFile(STRING_PREFIX, STRING_POSTFIX);
		} catch (IOException e) {
			messagePrinter(MESSAGE_CREATE_TEMP_FILE_ERROR);
			e.printStackTrace();
		}
	}

	private static void rewriteActualFileWithTempFile() {

		setUpReader(temporaryFile);
		setUpWriter(outputFile, false);

		while (reader.hasNextLine()) {
			writer.println(reader.nextLine());
		}

		closeReader();
		closeWriter();
		deleteTemporaryFile();
	}
	
	private static void sortFileContents(){
		if(isEmptyFile()){
			printEmptyFileMessage();
			return;
		}

		ArrayList<String> list = generateSortedList();		
		transferSortedListToFile(list);
	}

	private static void transferSortedListToFile(ArrayList<String> list) {
		Iterator<String> iterator = list.iterator();
		
		//false: clears output file of all contents
		setUpWriter(outputFile, false);
		
		while(iterator.hasNext()){
			writer.println(iterator.next());
		}
		
		closeWriter();
	}

	private static ArrayList<String> generateSortedList() {
		ArrayList<String> list = new ArrayList<String>();
		
		setUpReader(outputFile);
		
		while(reader.hasNextLine()){
			list.add(reader.nextLine());
		}
		
		closeReader();
		
		Collections.sort(list);
		
		return list;
	}

	private static void setUpWriter(File fileToWrite, Boolean isAppend) {
		try {
			// if isAppend is true, input stream appends to file being written
			writer = new PrintWriter(new FileWriter(fileToWrite, isAppend));
		} catch (IOException e) {
			messagePrinter(MESSAGE_IO_ERROR);
			e.printStackTrace();
		}
	}

	private static void closeWriter() {
		writer.close();
	}

	private static void setUpReader(File fileToRead) {
		try {
			reader = new Scanner(fileToRead);
		} catch (IOException e) {
			messagePrinter(MESSAGE_IO_ERROR);
			e.printStackTrace();
		}
	}

	private static void closeReader() {
		reader.close();
	}

	public static String getFirstWord(String sentence) {
		String[] splitSentence = sentence.trim().split("\\s");
		String firstWord = splitSentence[0];

		return firstWord;
	}

	public static String getLineToAddOrDelete(String sentence) {
		String sentenceWithoutFirstWord = sentence.replace(getFirstWord(sentence), "").trim();

		return sentenceWithoutFirstWord;
	}

	private static void printFile() {
		int lineCount = 0;

		setUpReader(outputFile);
		
		while (reader.hasNextLine()) {
			lineCount++;
			String message = String.format(MESSAGE_DISPLAY_LINE, lineCount, reader.nextLine());
			messagePrinter(message);
		}

		if (isEmptyFile()) {
			printEmptyFileMessage();
		}
	}

	private static Boolean isEmptyFile() {
		Boolean isFileEmpty = false;
		setUpReader(outputFile);

		if (reader.hasNextLine() == false) {
			isFileEmpty = true;
		}

		return isFileEmpty;

	}

	private static void clearFileData() {
		setUpWriter(outputFile, false);
		closeWriter();
	}

	private static void deleteTemporaryFile() {
		temporaryFile.delete();
	}

	private static ACTION_TYPE actionInterpreter(String action) {
		String standardisedAction = action.toLowerCase();

		switch (standardisedAction) {
			case "add":
				return ACTION_TYPE.ADD_LINE;

			case "delete":
				return ACTION_TYPE.DELETE_LINE;

			case "clear":
				return ACTION_TYPE.CLEAR_FILE;

			case "display":
				return ACTION_TYPE.DISPLAY_FILE;

			case "exit":
				return ACTION_TYPE.EXIT_PROGRAM;
				
			case "sort":
				return ACTION_TYPE.SORT;

			default:
				return ACTION_TYPE.INVALID_COMMAND;

		}

	}

	private static void printWelcomeMessage() {
		messagePrinter(MESSAGE_WELCOME);
	}

	private static void printAddMessage(String sentence) {
		String fileName = outputFile.getName();
		String message = String.format(MESSAGE_ADD_LINE, fileName, sentence);
		messagePrinter(message);
	}

	private static void printDeleteMessage(String sentence) {
		String fileName = outputFile.getName();
		String message = String.format(MESSAGE_DELETE_LINE, fileName, sentence);
		messagePrinter(message);
	}

	private static void printClearMessage() {
		String fileName = outputFile.getName();
		String message = String.format(MESSAGE_CLEAR_FILE, fileName);
		messagePrinter(message);

	}

	private static void printNotFoundMessage(int lineNumber) {
		String fileName = outputFile.getName();
		String message = String.format(MESSAGE_LINE_NOT_FOUND, lineNumber, fileName);
		messagePrinter(message);
	}

	private static void printReadyMessage() {
		String fileName = outputFile.getName();
		String message = String.format(MESSAGE_READY, fileName);
		messagePrinter(message);
	}
	
	private static void printEmptyFileMessage() {
		String fileName = outputFile.getName();
		String message = String.format(MESSAGE_EMPTY_FILE, fileName);
		messagePrinter(message);
	}


	private static void messagePrinter(String message) {
		System.out.println(message);
	}

	private static void exitProgram() {
		System.exit(0);
	}
}
