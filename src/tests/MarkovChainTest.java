package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import model.MarkovChain;

public class MarkovChainTest {

	@Test
	public void testGetWord() {
		MarkovChain mc = new MarkovChain("test");
		assertEquals(mc.getWord(), "test");
	}

	@Test
	public void testGetAndAddOccurrences() {
		MarkovChain mc = new MarkovChain("foo");
		assertEquals(mc.getOccurrences(), 1);
		
		mc.addOccurrence();
		assertEquals(mc.getOccurrences(), 2);
	}
	
	@Test
	public void testGetAndAddPossibleState() {
		// No possible states yet
		MarkovChain mc = new MarkovChain("foo");
		assertEquals(mc.getPossibleState(), "");
		
		// Only one possible state
		mc.addState("bar");
		assertEquals(mc.getPossibleState(), "bar");
		
		// Two possible states
		mc.addState("foobar");
		assertTrue(mc.getPossibleState() == "bar" || mc.getPossibleState() == "foobar");
	}
	
	@Test
	public void testIsCapitalAndSetIsCapital() {
		MarkovChain mc = new MarkovChain("TEST");
		assertFalse(mc.isCapital());
		
		mc.setIsCapital(true);
		assertTrue(mc.isCapital());
		
		mc.setIsCapital(false);
		assertFalse(mc.isCapital());
	}
	
	@Test
	public void testIsProperNounAndSetter() {
		MarkovChain mc = new MarkovChain("Bob");
		assertFalse(mc.isProperNoun());
		
		mc.setIsProperNoun(true);
		assertTrue(mc.isProperNoun());
		
		mc.setIsProperNoun(false);
		assertFalse(mc.isProperNoun());
	}
}
