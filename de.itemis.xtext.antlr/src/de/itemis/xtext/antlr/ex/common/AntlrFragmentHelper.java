/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.itemis.xtext.antlr.ex.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.Alternatives;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.EnumRule;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.Group;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.TerminalRule;

/**
 * The fragment helper will be passed to the extended Antlr grammar template and allows to
 * override certain aspects. This is an attempt to circumvent the limitations of static java
 * extensions. 
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class AntlrFragmentHelper {

	public String getAntlrTokenFileProviderClassName(Grammar grammar) {
		return GrammarUtil.getNamespace(grammar) + ".parser.antlr." + GrammarUtil.getName(grammar)	+ "AntlrTokenFileProvider";
	}
	
	public String getLexerClassName(Grammar g) {
		return getLexerGrammarFileName(g) + "Lexer";
	}

	public String getParserClassName(Grammar g) {
		return GrammarUtil.getNamespace(g) + ".parser.antlr." + GrammarUtil.getName(g) + "Parser";
	}

	public String getInternalParserClassName(Grammar g) {
		return getParserGrammarFileName(g) + "Parser";
	}

	public String getLexerGrammarFileName(Grammar g) {
		return GrammarUtil.getNamespace(g) + ".parser.antlr.lexer.Internal" + GrammarUtil.getName(g);
	}
	
	public String getParserGrammarFileName(Grammar g) {
		return GrammarUtil.getNamespace(g) + ".parser.antlr.internal.Internal" + GrammarUtil.getName(g);
	}

	public String getContentAssistParserClassName(Grammar g) {
		return GrammarUtil.getNamespace(g) + ".contentassist.antlr." + GrammarUtil.getName(g) + "Parser";
	}

	public String getInternalContentAssistLexerClassName(Grammar g) {
		return getContentAssistLexerGrammarFileName(g) + "Lexer";
	}
	
	public String getLexerSuperClass(Grammar g) {
		return "org.eclipse.xtext.parser.antlr.Lexer";
	}
	
	public String getContentAssistLexerSuperClass(Grammar g) {
		return "org.eclipse.xtext.ui.common.editor.contentassist.antlr.internal.Lexer";
	}

	public String getInternalContentAssistParserClassName(Grammar g) {
		return getContentAssistParserGrammarFileName(g) + "Parser";
	}

	public String getContentAssistLexerGrammarFileName(Grammar g) {
		return GrammarUtil.getNamespace(g) + ".contentassist.antlr.lexer.Internal" + GrammarUtil.getName(g);
	}
	
	public String getContentAssistParserGrammarFileName(Grammar g) {
		return GrammarUtil.getNamespace(g) + ".contentassist.antlr.internal.Internal" + GrammarUtil.getName(g);
	}

	public Collection<? extends AbstractElement> getAllAlternatives(Grammar g) {
		return getAllElementsByType(g, Alternatives.class);
	}

	public Collection<? extends AbstractElement> getAllGroups(Grammar g) {
		return getAllElementsByType(g, Group.class);
	}
	
	public Collection<? extends AbstractElement> getAllAssignments(Grammar g) {
		return getAllElementsByType(g, Assignment.class);
	}
	
	/**
	 * Synthetic terminal rules are rules which will not lead to a real terminal 
	 * rule in the generated lexer grammar but only provide the respective token types 
	 * instead.
	 * @return <code>true</code> if this rule should not get an own lexer body.
	 */
	public boolean isSyntheticTerminalRule(TerminalRule rule) {
		return false;
	}

	protected <T extends AbstractElement> Collection<T> getAllElementsByType(Grammar g, Class<T> type) {
		Collection<ParserRule> allParserRules = GrammarUtil.allParserRules(g);
		List<T> result = new ArrayList<T>(30);
		for (ParserRule rule : allParserRules) {
			result.addAll(EcoreUtil2.getAllContentsOfType(rule, type));
		}
		Collection<EnumRule> allEnumRules = GrammarUtil.allEnumRules(g);
		for (EnumRule rule : allEnumRules) {
			result.addAll(EcoreUtil2.getAllContentsOfType(rule, type));
		}
		return result;
	}
	
}
