/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package de.itemis.xtext.antlr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import de.itemis.xtext.antlr.AntlrLexerSplitter.ExtractedMethod;

/**
 *
 * @author Heiko Behrens - Initial contribution and API
 */
public class AntlrLexerSplitterTest extends AbstractAntlrSplitterTest {

	public void testMethodSignaturePattern() throws Exception {
		Pattern p = AntlrLexerSplitter.METHOD_SIGNATURE_PATTERN;
		assertFalse(p.matcher("    public final void mRULE_ANY_OTHER() throws RecognitionException {").find());
		assertTrue(p.matcher("    public void mTokens() throws RecognitionException {").find());
	}
	
	public void testAssignmentPattern() throws Exception {
		Pattern p = AntlrLexerSplitter.ASSIGNMENT_PATTERN;
		assertFalse(p.matcher("    string foo").find());
		Matcher m = p.matcher("        int alt12=16;");
		assertTrue(m.find());
		assertEquals("alt12", m.group(1));
	}
	
	public void testIndentLevelPattern() throws Exception {
		Pattern p = AntlrLexerSplitter.INDENT_LEVEL_PATTERN;
		assertFalse(p.matcher("").matches());
		assertFalse(p.matcher("a                  b").matches());
		assertFalse(p.matcher("        if ( (LA12_0=='i') ) {").find());
		assertTrue(p.matcher("            int LA12_1 = input.LA(2);").find());
		assertTrue(p.matcher("            if ( (LA12_1=='m') ) {").find());
		
	}
	
	public void testTransform() throws Exception {
		String content = readFileIntoString("de/itemis/xtext/antlr/InternalLexerExample.java.txt"); 
		AntlrLexerSplitter splitter = new AntlrLexerSplitter(content);
		String actual = splitter.transform();
		assertNotNull(actual);
		assertTrue(!actual.equals(content));
	}
	
	public void testDFAPattern() throws Exception {
		Pattern p = AntlrLexerSplitter.DFA_ASSIGNMENT_PATTERN;
		assertTrue(p.matcher("        alt12 = dfa12.predict(input);").find());
		assertFalse(p.matcher("        int LA12_0 = input.LA(1);").find());
	}
	
	public void testNoDFATransform() throws Exception {
		String content = readFileIntoString("de/itemis/xtext/antlr/InternalLexerExample2.java.txt"); 
		AntlrLexerSplitter splitter = new AntlrLexerSplitter(content);
		String actual = splitter.transform();
		assertNotNull(actual);
		assertTrue(actual.equals(content));
	}

	public void testExtractedLine() throws Exception {
		ExtractedMethod m = new ExtractedMethod("myVar", 1);
		assertEquals("mTokensHelper001", m.getName());
		assertEquals("  foo();", m.getAsExtractedLine("  foo();"));
		assertEquals("  myVar();", m.getAsExtractedLine("  myVar();"));
		assertEquals("  return 123;", m.getAsExtractedLine("  myVar=123;"));
		assertEquals("  return 123;}", m.getAsExtractedLine("  myVar=123;}"));
	}
	
	public void testExtectedMethodSimple() throws Exception {
		ExtractedMethod m = new ExtractedMethod("myVar", 1);
		m.addLine("            if ( someComplexExpression ) {");
		m.addLine("                myVar=1;");
		m.addLine("            }");
		m.addLine("            else {");
		m.addLine("                myVar=2;}");

		StringBuilder sb = new StringBuilder();
		m.writeTo(sb);
		
		String expected = "    private int mTokensHelper001() throws RecognitionException {\n" +
			"        if ( someComplexExpression ) {\n" +
			"            return 1;\n" +
			"        }\n" +
			"        else {\n" +
			"            return 2;}\n" +
			"    }\n";
		assertEquals(expected, sb.toString());
	}
	
	public void testExtectedMethodRemoveBreaks() throws Exception {
		ExtractedMethod m = new ExtractedMethod("alt12", 1);
		m.addLine("            switch ( input.LA(2) ) {");
		m.addLine("            case '*':");
		m.addLine("                {");
		m.addLine("                alt12=13;");
		m.addLine("                }");
		m.addLine("                break;");
		m.addLine("            case '/':");
		m.addLine("                {");
		m.addLine("                alt12=14;");
		m.addLine("                }");
		m.addLine("                break;");
		m.addLine("            default:");
		m.addLine("                alt12=16;}");
		
		StringBuilder sb = new StringBuilder();
		m.writeTo(sb);
		
		String expected = "    private int mTokensHelper001() throws RecognitionException {\n" +
		"        switch ( input.LA(2) ) {\n" +
		"        case '*':\n" +
		"            {\n" +
		"            return 13;\n" +
		"            }\n" +
		"        case '/':\n" +
		"            {\n" +
		"            return 14;\n" +
		"            }\n" +
		"        default:\n" +
		"            return 16;}\n" +
		"    }\n";
		assertEquals(expected, sb.toString());
	}

}
