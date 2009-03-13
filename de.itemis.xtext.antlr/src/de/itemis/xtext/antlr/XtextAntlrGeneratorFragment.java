/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.itemis.xtext.antlr;

import java.util.Map;

import org.eclipse.xpand2.XpandExecutionContext;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.generator.AbstractGeneratorFragment;
import org.eclipse.xtext.generator.BindFactory;
import org.eclipse.xtext.generator.BindKey;
import org.eclipse.xtext.generator.BindValue;
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
	
	@Override
	public Map<BindKey, BindValue> getGuiceBindingsRt(Grammar grammar) {
		return new BindFactory()
			.addTypeToType(IAntlrParser.class.getName(),getParserClassName(grammar))
			.addTypeToType(ITokenToStringConverter.class.getName(),AntlrTokenToStringConverter.class.getName())
			.addTypeToType(IAntlrTokenFileProvider.class.getName(),getAntlrTokenFileProviderClassName(grammar))
			.addTypeToType(Lexer.class.getName(),getLexerClassName(grammar))
			.addTypeToType(ITokenDefProvider.class.getName(),AntlrTokenDefProvider.class.getName())
			.getBindings();
	}

	@Override
	public Map<BindKey, BindValue> getGuiceBindingsUi(Grammar grammar) {
		return new BindFactory()
			.addTypeToType("org.eclipse.xtext.ui.common.editor.syntaxcoloring.antlr.ITokenColorer","org.eclipse.xtext.ui.common.editor.syntaxcoloring.antlr.CommonAntlrTokenColorer")
			.addTypeToType("org.eclipse.jface.text.rules.ITokenScanner","org.eclipse.xtext.ui.common.editor.syntaxcoloring.antlr.AntlrTokenScanner")
			.addTypeToType("org.eclipse.xtext.ui.common.editor.syntaxcoloring.ITokenStyleProvider", "org.eclipse.xtext.ui.common.editor.syntaxcoloring.antlr.AntlrTokenStyleProvider")
			.getBindings();
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
