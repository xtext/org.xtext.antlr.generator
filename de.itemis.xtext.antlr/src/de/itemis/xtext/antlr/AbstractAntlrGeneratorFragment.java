/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.itemis.xtext.antlr;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.xpand2.XpandExecutionContext;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.generator.AbstractGeneratorFragment;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public abstract class AbstractAntlrGeneratorFragment extends AbstractGeneratorFragment {

	private AntlrOptions options = new AntlrOptions();

	public void setOptions(AntlrOptions options) {
		this.options = options;
	}
	
	public AntlrOptions getOptions() {
		return options;
	}

	@Override
	protected List<Object> getParameters(Grammar grammar) {
		return Collections.singletonList((Object)options);
	}
	
	@Override
	public void generate(Grammar grammar, XpandExecutionContext ctx) {
		AbstractRule firstRule = grammar.getRules().get(0);
		if (!(firstRule instanceof ParserRule) || GrammarUtil.isDatatypeRule((ParserRule) firstRule))
			throw new IllegalArgumentException("You may not generate an ANTLR parser for a grammar without production rules.");
		super.generate(grammar, ctx);
	}
	
	protected String readFileIntoString(String filename) throws IOException {
		FileInputStream inputStream = new FileInputStream(filename);
		try {
			byte[] buffer = new byte[2048];
			int bytesRead = 0;
			StringBuffer b = new StringBuffer();
			do {
				bytesRead = inputStream.read(buffer);
				if (bytesRead != -1)
					b.append(new String(buffer, 0, bytesRead));
			} while (bytesRead != -1);
			return b.toString();
		} finally {
			inputStream.close();
		}
	}
	
	protected void writeStringIntoFile(String filename, String content) throws IOException {
		FileWriter writer = new FileWriter(filename);
		try {
			writer.append(content);
		} finally {
			writer.close();
		}
	}
	
	protected void splitLexerClassFile(String filename) throws IOException {
		String content = readFileIntoString(filename);
		AntlrLexerSplitter splitter = new AntlrLexerSplitter(content);
		writeStringIntoFile(filename, splitter.transform());
	}

	protected void splitParserClassFile(String filename) throws IOException {
		String content = readFileIntoString(filename);
		AntlrParserSplitter splitter = new AntlrParserSplitter(content);
		writeStringIntoFile(filename, splitter.transform());
	}

	protected void splitParserAndLexerIfEnabled(String absoluteLexerGrammarFileName,
			String absoluteParserGrammarFileName) {
		
		if(getOptions().isClassSplitting()) {
			try {
				splitLexerClassFile(absoluteLexerGrammarFileName.replaceAll("\\.g$", "Lexer.java"));
				splitParserClassFile(absoluteParserGrammarFileName.replaceAll("\\.g$", "Parser.java"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	protected void splitParserAndLexerIfEnabled(String absoluteGrammarFileName) {
		splitParserAndLexerIfEnabled(absoluteGrammarFileName, absoluteGrammarFileName);
	}

}