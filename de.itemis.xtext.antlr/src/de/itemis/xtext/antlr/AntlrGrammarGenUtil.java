/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.itemis.xtext.antlr;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.Grammar;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class AntlrGrammarGenUtil {

	public static String getClasspathURI(Grammar grammar, EObject object) {
		String fragment = EcoreUtil.getURI(object).fragment();
		return "classpath:/" + grammar.getName().replace('.', '/') + ".xmi#" + fragment;
	}
}
