/**
 * Program: TextBuddy
 * Done by: Jonathan Lim Siu Chi
 * Matric Number: A0110839H
 * Group: W15-3J
 * 
 * TextBuddy takes in a file name as an argument on execution. It checks for the existence of a file
 * of that name. If it exists, the user can choose to overwrite the file by giving Y (for yes) or N (for no)
 * as input. If it does not exist, a new file with that name will be automatically created.
 * 
 * Functions of TextBuddy are as follows: 
 *  1. add <string> : Will add string input into file.
 *  2. delete <integer> : Will delete the nth line as specified by integer input in the file. Integer input is assumed. 
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

	private static File outputFile = null;
	private static File temporaryFile = null;

	enum ACTION_TYPE {
		ADD_LINE, DELETE_LINE, CLEAR_FILE, DISPLAY_FILE, INVALID_COMMAND, EXIT_PROGRAM;

	}

	public static void main(String[] args) {
		String outputFileName = args[0];
		Scanner commandLineInput = new Scanner(System.in);

		printWelcomeMessage();

		outputFileName = overwriteExistingFile(outputFileName, commandLineInput);

		outputFile = new File(outputFileName);
		createFileIfDoesNotExist();

		printReadyMessage();

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
					int lineToDelete = Integer.parseInt(sentence);
					deleteLineFromFile(lineToDelete);
					break;

				case CLEAR_FILE:
					clearFileData();
					printClearMessage();
					break;

				case DISPLAY_FILE:
					printFile();
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
			String message = String.format(MESSAGE_FILE_ALREADY_EXISTS,
					outputFileName);
			System.out.print(message);

			chooseToOverwrite = commandLineInput.nextLine().toLowerCase()
					.trim();

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

		PrintWriter writerToFile = null;

		try {

			writerToFile = new PrintWriter(new FileWriter(outputFile, true)); // true: append data to file
			writerToFile.println(sentence);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writerToFile.close();
		}

	}

	private static void deleteLineFromFile(int lineToDelete) {
		Scanner fileReader = null;
		PrintWriter writerToFile = null;

		temporaryFile = new File("temp.txt");
		String sentenceDeleted = null;
		Boolean sentenceIsFound = false;

		try {
			fileReader = new Scanner(outputFile);
			writerToFile = new PrintWriter(new FileWriter(temporaryFile)); // transfer undeleted
																		   //lines to temp file
			int lineNumber = 0;
			while (fileReader.hasNextLine()) {
				String lineFromFile = fileReader.nextLine();
				lineNumber++;

				if (lineNumber == lineToDelete) {
					sentenceIsFound = true;
					sentenceDeleted = lineFromFile;
				} else { // write undeleted lines to temporary file
					writerToFile.println(lineFromFile);
				}
			}

		} catch (IOException e) {
			e.getStackTrace();
		} finally {
			fileReader.close();
			writerToFile.close();
		}

		rewriteActualFileWithTempFile(temporaryFile);

		if (sentenceIsFound) {
			printDeleteMessage(sentenceDeleted);
		} else {
			printNotFoundMessage(lineToDelete);
		}
	}

	private static void rewriteActualFileWithTempFile(File temporaryFile) {
		Scanner fileReader = null;
		PrintWriter writerToFile = null;
		try {

			fileReader = new Scanner(temporaryFile);
			writerToFile = new PrintWriter(new FileWriter(outputFile));

			while (fileReader.hasNextLine()) {
				writerToFile.println(fileReader.nextLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fileReader.close();
			writerToFile.close();
		}

		deleteTemporaryFile();
	}

	public static String getFirstWord(String sentence) {
		String[] splitSentence = sentence.trim().split("\\s");
		String firstWord = splitSentence[0];

		return firstWord;
	}

	public static String getLineToAddOrDelete(String sentence) {
		String sentenceWithoutFirstWord = sentence.replace(
				getFirstWord(sentence), "").trim();

		return sentenceWithoutFirstWord;
	}

	private static void printFile() {
		Scanner fileReader = null;
		int lineCount = 0;
		try {

			fileReader = new Scanner(outputFile);

			while (fileReader.hasNextLine()) {
				lineCount++;
				String message = String.format(MESSAGE_DISPLAY_LINE, lineCount,
						fileReader.nextLine());
				messagePrinter(message);
			}

		} catch (IOException e) {

		} finally {
			if (fileReader != null) {
				fileReader.close();
			}
		}

		if (isEmptyFile()) {
			String fileName = outputFile.getName();
			String message = String.format(MESSAGE_EMPTY_FILE, fileName);
			messagePrinter(message);
		}

	}

	private static Boolean isEmptyFile() {
		Scanner fileReader = null;
		Boolean isFileEmpty = false;
		try {
			fileReader = new Scanner(outputFile);
			if (fileReader.hasNextLine() == false)
				isFileEmpty = true;

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fileReader.close();
		}

		return isFileEmpty;

	}

	private static void clearFileData() {
		PrintWriter writerToFile = null;
		try {
			writerToFile = new PrintWriter(new FileWriter(outputFile));

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writerToFile.close();
		}

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
		String message = String.format(MESSAGE_LINE_NOT_FOUND, lineNumber,
				fileName);
		messagePrinter(message);
	}

	private static void printReadyMessage() {
		String fileName = outputFile.getName();
		String message = String.format(MESSAGE_READY, fileName);
		messagePrinter(message);
	}

	private static void messagePrinter(String message) {
		System.out.println(message);
	}

	private static void exitProgram() {
		System.exit(0);
	}
}
