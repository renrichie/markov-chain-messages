package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import model.MessageGenerator;

public class MessageGeneratorTest {

	@Test
	public void testGenerateTextNoInput() {
		MessageGenerator mg = new MessageGenerator();
		assertEquals(mg.generateText(), "");
	}
	
	@Test
	public void testAddInputAndGenerateText() {
		MessageGenerator mg = new MessageGenerator();
		mg.addInput("foo", "bar");
		
		String message = mg.generateText();
		String[] tokenized = message.split("\\s+");
		
		String firstOne = tokenized[0];
		
		assertTrue(Character.isUpperCase(firstOne.charAt(0)));
		assertFalse(Character.isUpperCase(firstOne.charAt(1)));
		
		assertTrue(message.charAt(message.length() - 1) == '.');
		
		assertTrue(tokenized.length >= 10 && tokenized.length <= 30);
		
		assertTrue(message.contains("foo"));
		assertTrue(message.contains("bar"));
	}
	
	@Test
	public void testClearInput() {
		MessageGenerator mg = new MessageGenerator();
		mg.addInput("foo", "bar");
		
		assertTrue(mg.generateText() != "");
		
		mg.clearInput();
		assertTrue(mg.generateText() == "");
	}
}
