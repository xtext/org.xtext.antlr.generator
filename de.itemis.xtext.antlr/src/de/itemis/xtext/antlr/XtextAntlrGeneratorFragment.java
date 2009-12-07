/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.itemis.xtext.antlr;

import java.util.Set;

import org.eclipse.emf.mwe.core.issues.Issues;
import org.eclipse.xpand2.XpandExecutionContext;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.generator.BindFactory;
import org.eclipse.xtext.generator.Binding;
import org.eclipse.xtext.generator.Generator;
import org.eclipse.xtext.parser.ITokenToStringConverter;
import org.eclipse.xtext.parser.antlr.AntlrTokenDefProvider;
import org.eclipse.xtext.parser.antlr.AntlrTokenToStringConverter;
import org.eclipse.xtext.parser.antlr.IAntlrParser;
import org.eclipse.xtext.parser.antlr.IAntlrTokenFileProvider;
import org.eclipse.xtext.parser.antlr.ITokenDefProvider;
import org.eclipse.xtext.parser.antlr.Lexer;

/**
 * Converts the Xtext grammar to an AntLR grammar runs the AntLR generator. 
 * Additionally generates some parser/lexer related services
 *  
 * @author Sebastian Zarnekow - Initial contribution and API
 * @author Sven Efftinge
 */
public class XtextAntlrGeneratorFragment extends AbstractAntlrGeneratorFragment {
	
	@Override
	public void generate(Grammar grammar, XpandExecutionContext ctx) {
		super.generate(grammar, ctx);
		String srcGenPath = ctx.getOutput().getOutlet(Generator.SRC_GEN).getPath();
		String absoluteGrammarFileName = srcGenPath+"/"+getGrammarFileName(grammar).replace('.', '/')+".g";
		AntlrToolRunner.run(absoluteGrammarFileName);
		splitParserAndLexerIfEnabled(absoluteGrammarFileName);
	}
	
	@Override
	public void checkConfiguration(Issues issues) {
		super.checkConfiguration(issues);
		if (getOptions().isBacktrackLexer()) {
			issues.addError("This fragment does not support the option 'backtracking' for the lexer. Use 'de.itemis.xtext.antlr.ex.rt.AntlrGeneratorFragment' instead");
		}
		if (getOptions().isIgnoreCase()) {
			issues.addError("This fragment does not support the option 'ignorecase'. Use 'de.itemis.xtext.antlr.ex.rt.AntlrGeneratorFragment' instead");
		}
	}
	
	@Override
	public String[] getExportedPackagesRt(Grammar grammar) {
		return new String[]{
				GrammarUtil.getNamespace(grammar) + ".parser.antlr",
				GrammarUtil.getNamespace(grammar) + ".parser.antlr.internal"
		};
	}
	
	@Override
	public String[] getRequiredBundlesRt(Grammar grammar) {
		return new String[]{
				"org.antlr.runtime"
		};
	}
	
	@Override
	public Set<Binding> getGuiceBindingsRt(Grammar grammar) {
		return new BindFactory()
			.addTypeToType(IAntlrParser.class.getName(),getParserClassName(grammar))
			.addTypeToType(ITokenToStringConverter.class.getName(),AntlrTokenToStringConverter.class.getName())
			.addTypeToType(IAntlrTokenFileProvider.class.getName(),getAntlrTokenFileProviderClassName(grammar))
			.addTypeToType(Lexer.class.getName(), getLexerClassName(grammar))
			.addTypeToProviderInstance(getLexerClassName(grammar), "org.eclipse.xtext.parser.antlr.LexerProvider.create(" + getLexerClassName(grammar) + ".class)")
			.addConfiguredBinding("RuntimeLexer", 
					"binder.bind(" + Lexer.class.getName() + ".class)"+
					".annotatedWith(com.google.inject.name.Names.named(" +
					"org.eclipse.xtext.parser.antlr.LexerBindings.RUNTIME" +
					")).to(" + getLexerClassName(grammar) +".class)")
			.addTypeToType(ITokenDefProvider.class.getName(), AntlrTokenDefProvider.class.getName())
			.getBindings();
	}

	@Override
	public Set<Binding> getGuiceBindingsUi(Grammar grammar) {
		return new BindFactory()
			.addTypeToType("org.eclipse.jface.text.rules.ITokenScanner","org.eclipse.xtext.ui.common.editor.syntaxcoloring.antlr.AntlrTokenScanner")
			.addTypeToType("org.eclipse.xtext.ui.common.editor.contentassist.IProposalConflictHelper", "org.eclipse.xtext.ui.common.editor.contentassist.antlr.AntlrProposalConflictHelper")
			.addTypeToType("org.eclipse.xtext.ui.core.editor.IDamagerRepairer", "org.eclipse.xtext.ui.core.editor.XtextDamagerRepairer")
			.addConfiguredBinding("HighlightingLexer", 
					"binder.bind(" + Lexer.class.getName() + ".class)"+
					".annotatedWith(com.google.inject.name.Names.named(" +
					"org.eclipse.xtext.ui.core.LexerUIBindings.HIGHLIGHTING" +
					")).to(" + getLexerClassName(grammar) +".class)")
			.addConfiguredBinding("HighlightingTokenDefProvider", 
					"binder.bind(" + ITokenDefProvider.class.getName() + ".class)"+
					".annotatedWith(com.google.inject.name.Names.named(" +
					"org.eclipse.xtext.ui.core.LexerUIBindings.HIGHLIGHTING" +
					")).to(" + AntlrTokenDefProvider.class.getName() +".class)")
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
