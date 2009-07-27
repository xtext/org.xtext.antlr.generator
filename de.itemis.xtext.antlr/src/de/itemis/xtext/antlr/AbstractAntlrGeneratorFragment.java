/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.itemis.xtext.antlr;

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

}