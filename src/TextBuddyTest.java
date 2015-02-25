import static org.junit.Assert.*;

import org.junit.Test;


public class TextBuddyTest {

	@Test
	public void testGetFirstWord() {
		TextBuddy tb = new TextBuddy();
		assertEquals("First word of \"add wash clothes\" is \"add\"", "add", tb.getFirstWord("add wash clothes"));
		assertEquals("First word of \"add change documents\" is \"add\"", "add", tb.getFirstWord("add change documents"));
		assertEquals("First word of \"delete wash clothes\" is \"delete\"", "delete", tb.getFirstWord("delete wash clothes"));
		assertEquals("First word of \"delete change documents\" is \"delete\"", "delete", tb.getFirstWord("delete wash clothes"));
	}	
	public void testDeleteFirstWord(){
		TextBuddy tb = new TextBuddy();
		assertEquals("Remaining text after deleting command word", "wash clothes", tb.getLineToAddOrDelete("add wash clothes"));
		
	}
}
