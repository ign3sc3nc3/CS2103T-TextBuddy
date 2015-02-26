import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class TextBuddyTest {
	//trailing white spaces
	String s1 = "add hello how are you?             ";
	
	//normal delete usagae
	String s2 = "delete 30";
	
	//symbol and merged words
	String s3 = "add.badtypist skills";
	
	//trailing white spaces before and after the first word
	String s4 = "       delete even worse typing skills     ";
	
	//normal add usage
	String s5 = "add love makes the world go round";
	
	@Before
	public void testSetup(){
		TextBuddy.outputFile = new File("textbuddytest.txt");
	} 
	
	@Test
	public void testGetFirstWord() {
		//String with trailing white spaces
				
		assertEquals("add", TextBuddy.getFirstWord(s1));
		assertEquals("delete", TextBuddy.getFirstWord(s2));
		assertEquals("add.badtypist", TextBuddy.getFirstWord(s3));
		assertEquals("delete", TextBuddy.getFirstWord(s4));
		assertEquals("add", TextBuddy.getFirstWord(s5));
	}	
	
	@Test
	public void testDeleteFirstWord(){
		assertEquals("hello how are you?", TextBuddy.getTextLine(s1));
		assertEquals("30", TextBuddy.getTextLine(s2));
		assertEquals("skills", TextBuddy.getTextLine(s3));
		assertEquals("even worse typing skills", TextBuddy.getTextLine(s4));
		assertEquals("love makes the world go round", TextBuddy.getTextLine(s5));
		
	}
	
	@Test
	public void testSearch(){
		
	}
	
}
