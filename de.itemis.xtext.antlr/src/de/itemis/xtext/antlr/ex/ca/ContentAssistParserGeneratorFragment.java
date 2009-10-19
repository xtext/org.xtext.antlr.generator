/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.itemis.xtext.antlr.ex.ca;

import java.util.Set;

import org.eclipse.xpand2.XpandExecutionContext;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.generator.BindFactory;
import org.eclipse.xtext.generator.Binding;
import org.eclipse.xtext.generator.Generator;

import de.itemis.xtext.antlr.AntlrToolRunner;
import de.itemis.xtext.antlr.ex.common.AbstractAntlrGeneratorFragmentEx;
import de.itemis.xtext.antlr.ex.common.KeywordHelper;

/**
 * A {@link IGeneratorFragment} to generate a lightweight AntLR based parser used in content assist.
 * 
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class ContentAssistParserGeneratorFragment extends AbstractAntlrGeneratorFragmentEx {

	@Override
	public void generate(Grammar grammar, XpandExecutionContext ctx) {
		KeywordHelper helper = new KeywordHelper(grammar);
		super.generate(grammar, ctx);
		final String srcGenPath = ctx.getOutput().getOutlet(Generator.SRC_GEN_UI).getPath();
		String libPath = srcGenPath + "/" + getFragmentHelper().getContentAssistLexerGrammarFileName(grammar).replace('.', '/');
		libPath = libPath.substring(0, libPath.lastIndexOf('/'));
		AntlrToolRunner.runWithParams(srcGenPath+"/"+getFragmentHelper().getContentAssistLexerGrammarFileName(grammar).replace('.', '/')+".g");
		AntlrToolRunner.runWithParams(srcGenPath+"/"+getFragmentHelper().getContentAssistParserGrammarFileName(grammar).replace('.', '/')+".g", "-lib", libPath);
		helper.discardHelper(grammar);
	}

	@Override
	public Set<Binding> getGuiceBindingsUi(Grammar grammar) {
		return new BindFactory().addTypeToType(
				"org.eclipse.xtext.ui.core.editor.contentassist.ContentAssistContext.Factory",
				"org.eclipse.xtext.ui.common.editor.contentassist.antlr.ParserBasedContentAssistContextFactory")
				.addTypeToType("org.eclipse.xtext.ui.common.editor.contentassist.antlr.IContentAssistParser",
						getFragmentHelper().getContentAssistParserClassName(grammar)).getBindings();
	}

	@Override
	public String[] getRequiredBundlesUi(Grammar grammar) {
		return new String[] { "org.antlr.runtime" };
	}

	@Override
	public String[] getExportedPackagesUi(Grammar grammar) {
		return new String[] { GrammarUtil.getNamespace(grammar) + ".contentassist.antlr" };
	}

}
