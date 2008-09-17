package org.antlr.test;

/** Test the set stuff in lexer and parser */
public class TestSets extends BaseTest {
	protected boolean debug = false;

	/** Public default constructor used by TestRig */
	public TestSets() {
	}

	public void testSeqDoesNotBecomeSet() throws Exception {
		// this must return A not I to the parser; calling a nonfragment rule
		// from a nonfragment rule does not set the overall token.
		String grammar =
			"grammar P;\n" +
			"a : C {System.out.println(input);} ;\n" +
			"fragment A : '1' | '2';\n" +
			"fragment B : '3' '4';\n" +
			"C : A | B;\n";
		String found = execParser("P.g", grammar, "PParser", "PLexer",
								  "a", "34", debug);
		assertEquals("34\n", found);
	}

	public void testParserSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : t=('x'|'y') {System.out.println($t.text);} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "x", debug);
		assertEquals("x\n", found);
	}

	public void testParserNotSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : t=~('x'|'y') 'z' {System.out.println($t.text);} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "zz", debug);
		assertEquals("z\n", found);
	}

	public void testParserNotToken() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ~'x' 'z' {System.out.println(input);} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "zz", debug);
		assertEquals("zz\n", found);
	}

	public void testParserNotTokenWithLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : t=~'x' 'z' {System.out.println($t.text);} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "zz", debug);
		assertEquals("z\n", found);
	}

	public void testRuleAsSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a @after {System.out.println(input);} : 'a' | 'b' |'c' ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "b", debug);
		assertEquals("b\n", found);
	}

	public void testRuleAsSetAST() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : 'a' | 'b' |'c' ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "b", debug);
		assertEquals("b\n", found);
	}

	public void testNotChar() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : ~'b' ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "x", debug);
		assertEquals("x\n", found);
	}

	public void testOptionalSingleElement() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A? 'c' {System.out.println(input);} ;\n" +
			"A : 'b' ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "bc", debug);
		assertEquals("bc\n", found);
	}

	public void testOptionalLexerSingleElement() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println(input);} ;\n" +
			"A : 'b'? 'c' ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "bc", debug);
		assertEquals("bc\n", found);
	}

	public void testStarLexerSingleElement() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println(input);} ;\n" +
			"A : 'b'* 'c' ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "bbbbc", debug);
		assertEquals("bbbbc\n", found);
		found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "c", debug);
		assertEquals("c\n", found);
	}

	public void testPlusLexerSingleElement() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println(input);} ;\n" +
			"A : 'b'+ 'c' ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "bbbbc", debug);
		assertEquals("bbbbc\n", found);
	}

	public void testOptionalSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ('a'|'b')? 'c' {System.out.println(input);} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "ac", debug);
		assertEquals("ac\n", found);
	}

	public void testStarSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ('a'|'b')* 'c' {System.out.println(input);} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abaac", debug);
		assertEquals("abaac\n", found);
	}

	public void testPlusSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ('a'|'b')+ 'c' {System.out.println(input);} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abaac", debug);
		assertEquals("abaac\n", found);
	}

	public void testLexerOptionalSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println(input);} ;\n" +
			"A : ('a'|'b')? 'c' ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "ac", debug);
		assertEquals("ac\n", found);
	}

	public void testLexerStarSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println(input);} ;\n" +
			"A : ('a'|'b')* 'c' ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abaac", debug);
		assertEquals("abaac\n", found);
	}

	public void testLexerPlusSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println(input);} ;\n" +
			"A : ('a'|'b')+ 'c' ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "abaac", debug);
		assertEquals("abaac\n", found);
	}

	public void testNotCharSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : ~('b'|'c') ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "x", debug);
		assertEquals("x\n", found);
	}

	public void testNotCharSetWithLabel() throws Exception {
		// This doesn't work in lexer yet.
		// Generates: h=input.LA(1); but h is defined as a Token
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : h=~('b'|'c') ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "x", debug);
		assertEquals("x\n", found);
	}

	public void testNotCharSetWithRuleRef() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : ~('a'|B) ;\n" +
			"B : 'b' ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "x", debug);
		assertEquals("x\n", found);
	}

	public void testNotCharSetWithRuleRef2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : ~('a'|B) ;\n" +
			"B : 'b'|'c' ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "x", debug);
		assertEquals("x\n", found);
	}

	public void testNotCharSetWithRuleRef3() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : ('a'|B) ;\n" +
			"fragment\n" +
			"B : ~('a'|'c') ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "x", debug);
		assertEquals("x\n", found);
	}

	public void testNotCharSetWithRuleRef4() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : ('a'|B) ;\n" +
			"fragment\n" +
			"B : ~('a'|C) ;\n" +
			"fragment\n" +
			"C : 'c'|'d' ;\n ";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "a", "x", debug);
		assertEquals("x\n", found);
	}

}
