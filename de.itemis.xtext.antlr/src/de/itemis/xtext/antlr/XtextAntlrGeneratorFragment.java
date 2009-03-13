/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.itemis.xtext.antlr;

import static org.eclipse.xtext.util.Tuples.*;

import java.util.Map;

import org.eclipse.xpand2.XpandExecutionContext;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.generator.AbstractGeneratorFragment;
import org.eclipse.xtext.generator.Generator;
import org.eclipse.xtext.parser.ITokenToStringConverter;
import org.eclipse.xtext.parser.antlr.AntlrTokenDefProvider;
import org.eclipse.xtext.parser.antlr.AntlrTokenToStringConverter;
import org.eclipse.xtext.parser.antlr.IAntlrParser;
import org.eclipse.xtext.parser.antlr.IAntlrTokenFileProvider;
import org.eclipse.xtext.parser.antlr.ITokenDefProvider;
import org.eclipse.xtext.parser.antlr.Lexer;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 * @author Sven Efftinge
 */
public class XtextAntlrGeneratorFragment extends AbstractGeneratorFragment {

	@Override
	public void generate(Grammar grammar, XpandExecutionContext ctx) {
		super.generate(grammar, ctx);
		String srcGenPath = ctx.getOutput().getOutlet(Generator.SRC_GEN).getPath();
		de.itemis.xtext.antlr.AntlrToolRunner.run(srcGenPath+"/"+getGrammarFileName(grammar).replace('.', '/')+".g");
	}

	@Override
	public String[] getExportedPackagesRt(Grammar grammar) {
		return new String[]{
				GrammarUtil.getNamespace(grammar) + ".parser.antlr"
		};
	}

	@Override
	public String[] getRequiredBundlesRt(Grammar grammar) {
		return new String[]{
				"org.antlr.runtime"
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getGuiceBindingsRt(Grammar grammar) {
		return toMap(pair(IAntlrParser.class.getName(),getParserClassName(grammar)),
					pair(ITokenToStringConverter.class.getName(),AntlrTokenToStringConverter.class.getName()),
					pair(IAntlrTokenFileProvider.class.getName(),getAntlrTokenFileProviderClassName(grammar)),
					pair(Lexer.class.getName(),getLexerClassName(grammar)),
					pair(ITokenDefProvider.class.getName(),AntlrTokenDefProvider.class.getName()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getGuiceBindingsUi(Grammar grammar) {
		return toMap(
				pair("org.eclipse.xtext.ui.common.editor.syntaxcoloring.antlr.ITokenColorer", "org.eclipse.xtext.ui.common.editor.syntaxcoloring.antlr.CommonAntlrTokenColorer"),
				pair("org.eclipse.jface.text.rules.ITokenScanner", "org.eclipse.xtext.ui.common.editor.syntaxcoloring.antlr.AntlrTokenScanner"),
				pair("org.eclipse.xtext.ui.common.editor.syntaxcoloring.ITokenStyleProvider", "org.eclipse.xtext.ui.common.editor.syntaxcoloring.antlr.AntlrTokenStyleProvider"));
	}

	public static String getAntlrTokenFileProviderClassName(Grammar grammar) {
		return GrammarUtil.getNamespace(grammar) + ".parser.antlr" +"." + GrammarUtil.getName(grammar)	+ "AntlrTokenFileProvider";
	}

	public static String getLexerClassName(Grammar g) {
		return GrammarUtil.getNamespace(g) + ".parser.antlr.internal.Internal" + GrammarUtil.getName(g)	+ "Lexer";
	}

	public static String getParserClassName(Grammar g) {
		return GrammarUtil.getNamespace(g) + ".parser.antlr." + GrammarUtil.getName(g) + "Parser";
	}

	public static String getInternalParserClassName(Grammar g) {
		return GrammarUtil.getNamespace(g) + ".parser.antlr.internal.Internal" + GrammarUtil.getName(g) + "Parser";
	}

	public static String getGrammarFileName(Grammar g) {
		return GrammarUtil.getNamespace(g) + ".parser.antlr.internal.Internal" + GrammarUtil.getName(g);
	}

}
