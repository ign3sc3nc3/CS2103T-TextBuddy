/**
 * Program: TextBuddy CE2 Test Driver
 * Author: Jonathan Lim Siu Chi
 * Matric Number: A0110839H
 * Group: W15-3J
 */

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class TextBuddyTest {

	String sortCommand = "sort";
	String displayCommand = "display";
	String clearCommand = "clear";

	@Before
	public void testSetup() {
		TextBuddy.outputFile = new File("test.txt");
	}

	@After
	public void testTearDown() {
		TextBuddy.deleteFile();
	}

	@Test
	public void testSearch() {
		String s1 = "add hello how are you?";
		String s2 = "add a quick brown fox jumped over the lazy dog. how";
		String s3 = "search how";
		String s4 = "add how now brown cow";
		String s5 = "search people";
		String s6 = "search ";

		// test for searching key word that appears in 1 line
		assertEquals("Added to test.txt: \"hello how are you?\"", TextBuddy.commandExecuter(s1));
		assertEquals("1. hello how are you?\n", TextBuddy.commandExecuter(displayCommand));
		assertEquals("1. hello how are you?\n", TextBuddy.commandExecuter(s3));

		// test for searching key word that appears in 2 lines
		assertEquals("Added to test.txt: \"a quick brown fox jumped over the lazy dog. how\"",
				TextBuddy.commandExecuter(s2));
		assertEquals("1. hello how are you?\n2. a quick brown fox jumped over the lazy dog. how\n",
				TextBuddy.commandExecuter(s3));

		// test for searching key word that appears in 3 lines
		assertEquals("Added to test.txt: \"how now brown cow\"", TextBuddy.commandExecuter(s4));
		assertEquals(
				"1. hello how are you?\n2. a quick brown fox jumped over the lazy dog. how\n3. how now brown cow\n",
				TextBuddy.commandExecuter(s3));

		// test for searching for non-existent key word
		assertEquals("", TextBuddy.commandExecuter(s5));

		// test for searching with empty keyword
		assertEquals("", TextBuddy.commandExecuter(s6));

		// test for empty file
		assertEquals("All content deleted from test.txt", TextBuddy.commandExecuter(clearCommand));
		assertEquals("", TextBuddy.commandExecuter(s3));
	}

	@Test
	public void testSort() {
		String s1 = "add apple of my eye";
		String s2 = "add busy bees in the beehive";
		String s3 = "add cranberries";
		String s4 = "add xylophone swinging";
		String s5 = "add xenophobes ringing";

		// test sort function for 1 line
		assertEquals("Added to test.txt: \"xenophobes ringing\"", TextBuddy.commandExecuter(s5));
		TextBuddy.commandExecuter(sortCommand);
		assertEquals("1. xenophobes ringing\n", TextBuddy.commandExecuter(displayCommand));

		// test sort function for 2 lines
		assertEquals("Added to test.txt: \"busy bees in the beehive\"",
				TextBuddy.commandExecuter(s2));
		TextBuddy.commandExecuter(sortCommand);
		assertEquals("1. busy bees in the beehive\n2. xenophobes ringing\n",
				TextBuddy.commandExecuter(displayCommand));

		// test sort function for 3 lines and a single-worded line
		assertEquals("Added to test.txt: \"cranberries\"", TextBuddy.commandExecuter(s3));
		TextBuddy.commandExecuter(sortCommand);
		assertEquals("1. busy bees in the beehive\n2. cranberries\n3. xenophobes ringing\n",
				TextBuddy.commandExecuter(displayCommand));

		// test sort function for 4 lines and two lines with same starting
		// letters
		assertEquals("Added to test.txt: \"xylophone swinging\"", TextBuddy.commandExecuter(s4));
		TextBuddy.commandExecuter(sortCommand);
		assertEquals(
				"1. busy bees in the beehive\n2. cranberries\n3. xenophobes ringing\n4. xylophone swinging\n",
				TextBuddy.commandExecuter(displayCommand));

		// test sort function for 5 lines
		assertEquals("Added to test.txt: \"apple of my eye\"", TextBuddy.commandExecuter(s1));
		TextBuddy.commandExecuter(sortCommand);
		assertEquals(
				"1. apple of my eye\n2. busy bees in the beehive\n3. cranberries\n4. xenophobes ringing\n5. xylophone swinging\n",
				TextBuddy.commandExecuter(displayCommand));

		// test sort function for empty file
		assertEquals("All content deleted from test.txt", TextBuddy.commandExecuter(clearCommand));
		assertEquals("test.txt is empty.", TextBuddy.commandExecuter(sortCommand));
	}

	@Test
	public void testGetFirstWord() {
		// trailing white spaces
		String s1 = "add hello how are you?             ";

		// normal delete usagae
		String s2 = "delete 30";

		// symbol and merged words
		String s3 = "add.badtypist skills";

		// trailing white spaces before and after the first word
		String s4 = "       delete even worse typing skills     ";

		// normal add usage
		String s5 = "add love makes the world go round";

		assertEquals("add", TextBuddy.getFirstWord(s1));
		assertEquals("delete", TextBuddy.getFirstWord(s2));
		assertEquals("add.badtypist", TextBuddy.getFirstWord(s3));
		assertEquals("delete", TextBuddy.getFirstWord(s4));
		assertEquals("add", TextBuddy.getFirstWord(s5));
	}

	@Test
	public void testDeleteFirstWord() {
		// trailing white spaces
		String s1 = "add hello how are you?             ";

		// normal delete usagae
		String s2 = "delete 30";

		// symbol and merged words
		String s3 = "add.badtypist skills";

		// trailing white spaces before and after the first word
		String s4 = "       delete even worse typing skills     ";

		// normal add usage
		String s5 = "add love makes the world go round";

		assertEquals("hello how are you?", TextBuddy.getTextLine(s1));
		assertEquals("30", TextBuddy.getTextLine(s2));
		assertEquals("skills", TextBuddy.getTextLine(s3));
		assertEquals("even worse typing skills", TextBuddy.getTextLine(s4));
		assertEquals("love makes the world go round", TextBuddy.getTextLine(s5));

	}

}
