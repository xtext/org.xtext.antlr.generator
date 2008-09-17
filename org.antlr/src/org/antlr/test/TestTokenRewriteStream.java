/*
 [The "BSD licence"]
 Copyright (c) 2005-2006 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.antlr.test;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.TokenRewriteStream;
import org.antlr.tool.Grammar;
import org.antlr.tool.Interpreter;

public class TestTokenRewriteStream extends BaseTest {

    /** Public default constructor used by TestRig */
    public TestTokenRewriteStream() {
    }

	public void testInsertBeforeIndex0() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.insertBefore(0, "0");
		String result = tokens.toString();
		String expecting = "0abc";
		assertEquals(result, expecting);
	}

	public void testInsertAfterLastIndex() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.insertAfter(2, "x");
		String result = tokens.toString();
		String expecting = "abcx";
		assertEquals(result, expecting);
	}

	public void test2InsertBeforeAfterMiddleIndex() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.insertBefore(1, "x");
		tokens.insertAfter(1, "x");
		String result = tokens.toString();
		String expecting = "axbxc";
		assertEquals(result, expecting);
	}

	public void testReplaceIndex0() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(0, "x");
		String result = tokens.toString();
		String expecting = "xbc";
		assertEquals(result, expecting);
	}

	public void testReplaceLastIndex() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(2, "x");
		String result = tokens.toString();
		String expecting = "abx";
		assertEquals(result, expecting);
	}

	public void testReplaceMiddleIndex() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(1, "x");
		String result = tokens.toString();
		String expecting = "axc";
		assertEquals(result, expecting);
	}

	public void test2ReplaceMiddleIndex() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(1, "x");
		tokens.replace(1, "y");
		String result = tokens.toString();
		String expecting = "ayc";
		assertEquals(result, expecting);
	}

	public void testReplaceThenDeleteMiddleIndex() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(1, "x");
		tokens.delete(1);
		String result = tokens.toString();
		String expecting = "ac";
		assertEquals(result, expecting);
	}

	public void testReplaceThenInsertSameIndex() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(0, "x");
		tokens.insertBefore(0, "0");
		String result = tokens.toString();
		String expecting = "0xbc";
		assertEquals(result, expecting);
	}

	public void testReplaceThen2InsertSameIndex() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(0, "x");
		tokens.insertBefore(0, "y");
		tokens.insertBefore(0, "z");
		String result = tokens.toString();
		String expecting = "zyxbc";
		assertEquals(result, expecting);
	}

	public void testInsertThenReplaceSameIndex() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.insertBefore(0, "0");
		tokens.replace(0, "x");
		String result = tokens.toString();
		String expecting = "0xbc";
		assertEquals(result, expecting);
	}

	public void test2InsertMiddleIndex() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.insertBefore(1, "x");
		tokens.insertBefore(1, "y");
		String result = tokens.toString();
		String expecting = "ayxbc";
		assertEquals(result, expecting);
	}

	public void test2InsertThenReplaceIndex0() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.insertBefore(0, "x");
		tokens.insertBefore(0, "y");
		tokens.replace(0, "z");
		String result = tokens.toString();
		String expecting = "yxzbc";
		assertEquals(result, expecting);
	}

	public void testReplaceThenInsertBeforeLastIndex() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(2, "x");
		tokens.insertBefore(2, "y");
		String result = tokens.toString();
		String expecting = "abyx";
		assertEquals(result, expecting);
	}

	public void testInsertThenReplaceLastIndex() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.insertBefore(2, "y");
		tokens.replace(2, "x");
		String result = tokens.toString();
		String expecting = "abyx";
		assertEquals(result, expecting);
	}

	public void testReplaceThenInsertAfterLastIndex() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abc");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(2, "x");
		tokens.insertAfter(2, "y");
		String result = tokens.toString();
		String expecting = "abxy";
		assertEquals(result, expecting);
	}

	public void testReplaceRangeThenInsertInMiddle() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abcccba");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(2, 4, "x");
		tokens.insertBefore(3, "y"); // no effect; can't insert in middle of replaced region
		String result = tokens.toString();
		String expecting = "abxba";
		assertEquals(result, expecting);
	}

	public void testReplaceRangeThenInsertAtLeftEdge() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abcccba");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(2, 4, "x");
		tokens.insertBefore(2, "y");
		String result = tokens.toString();
		String expecting = "abyxba";
		assertEquals(result, expecting);
	}

	public void testReplaceRangeThenInsertAtRightEdge() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abcccba");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(2, 4, "x");
		tokens.insertBefore(4, "y"); // no effect; within range of a replace
		String result = tokens.toString();
		String expecting = "abxba";
		assertEquals(result, expecting);
	}

	public void testReplaceRangeThenInsertAfterRightEdge() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abcccba");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(2, 4, "x");
		tokens.insertAfter(4, "y");
		String result = tokens.toString();
		String expecting = "abxyba";
		assertEquals(result, expecting);
	}

	public void testReplaceAll() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abcccba");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(0, 6, "x");
		String result = tokens.toString();
		String expecting = "x";
		assertEquals(result, expecting);
	}

	public void testReplaceSubsetThenFetch() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abcccba");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(2, 4, "xyz");
		String result = tokens.toString(0,6);
		String expecting = "abxyzba";
		assertEquals(result, expecting);
	}

	public void testReplaceThenReplaceSuperset() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abcccba");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(2, 4, "xyz");
		tokens.replace(2, 5, "foo"); // kills previous replace
		String result = tokens.toString();
		String expecting = "abfooa";
		assertEquals(result, expecting);
	}

	public void testReplaceThenReplaceLowerIndexedSuperset() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abcccba");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(2, 4, "xyz");
		tokens.replace(1, 3, "foo"); // executes first since 1<2; then ignores replace@2 as it skips over 1..3
		String result = tokens.toString();
		String expecting = "afoocba";
		assertEquals(result, expecting);
	}

	public void testReplaceSingleMiddleThenOverlappingSuperset() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		CharStream input = new ANTLRStringStream("abcba");
		Interpreter lexEngine = new Interpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.LT(1); // fill buffer
		tokens.replace(2, 2, "xyz");
		tokens.replace(0, 3, "foo");
		String result = tokens.toString();
		String expecting = "fooa";
		assertEquals(result, expecting);
	}

}
