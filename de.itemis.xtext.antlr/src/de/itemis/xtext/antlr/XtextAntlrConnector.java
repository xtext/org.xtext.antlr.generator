/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.itemis.xtext.antlr;

import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.parser.ITokenToStringConverter;
import org.eclipse.xtext.parser.antlr.AntlrTokenDefProvider;
import org.eclipse.xtext.parser.antlr.AntlrTokenToStringConverter;
import org.eclipse.xtext.parser.antlr.ITokenDefProvider;
import org.eclipse.xtext.xtextgen.GenModel;
import org.eclipse.xtext.xtextgen.GenService;
import org.eclipse.xtext.xtextgen.IGenModelAssembler;
import org.eclipse.xtext.xtextgen.XtextgenFactory;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class XtextAntlrConnector implements IGenModelAssembler {

	public void assemble(GenModel model) {
		Grammar grammarModel = model.getGrammar();
		String languageName = GrammarUtil.getName(grammarModel);
		String namespace = GrammarUtil.getNamespace(grammarModel);
		if (!GrammarUtil.isAbstract(grammarModel)) {
			GenService parserService = XtextgenFactory.eINSTANCE.createGenService();
			parserService.setServiceInterfaceFQName("org.eclipse.xtext.parser.antlr.IAntlrParser");
			parserService.setGenClassFQName(namespace + ".parser.antlr." + languageName + "Parser");
			parserService.setTemplatePath("de::itemis::xtext::antlr::Parser::root");
			parserService.setExtensionPointID("org.eclipse.xtext.ui.parser");
			model.getServices().add(parserService);

			GenService tokenConverterService = XtextgenFactory.eINSTANCE.createGenService();
			tokenConverterService.setServiceInterfaceFQName(ITokenToStringConverter.class.getName());
			tokenConverterService.setGenClassFQName(AntlrTokenToStringConverter.class.getName());
			model.getServices().add(tokenConverterService);
			
			GenService tokenFileProviderService = XtextgenFactory.eINSTANCE.createGenService();
			tokenFileProviderService
					.setServiceInterfaceFQName("org.eclipse.xtext.parser.antlr.IAntlrTokenFileProvider");
			tokenFileProviderService.setGenClassFQName(namespace + ".parser.antlr." + languageName	+ "AntlrTokenFileProvider");
			tokenFileProviderService.setTemplatePath("de::itemis::xtext::antlr::AntlrTokenFileProvider::root");
			// tokenFileProviderService.setExtensionPointID(
			// "org.eclipse.xtext.ui.parser");
			model.getServices().add(tokenFileProviderService);
			
			GenService tokenScannerService = XtextgenFactory.eINSTANCE.createGenService();
			tokenScannerService.setServiceInterfaceFQName("org.eclipse.xtext.parser.antlr.Lexer");
			tokenScannerService.setGenClassFQName(namespace + ".parser.antlr.internal.Internal" + languageName	+ "Lexer");
			model.getServices().add(tokenScannerService);
			
			GenService tokenDefProvider = XtextgenFactory.eINSTANCE.createGenService();
			tokenDefProvider.setServiceInterfaceFQName(ITokenDefProvider.class.getName());
			tokenDefProvider.setGenClassFQName(AntlrTokenDefProvider.class.getName());
			model.getServices().add(tokenDefProvider);

			GenService tokenColorer = XtextgenFactory.eINSTANCE.createGenService();
			tokenColorer.setServiceInterfaceFQName("org.eclipse.xtext.ui.common.editor.syntaxcoloring.antlr.ITokenColorer");
			tokenColorer.setGenClassFQName("org.eclipse.xtext.ui.common.editor.syntaxcoloring.antlr.BuiltinAntlrTokenColorer");
			tokenColorer.setUiService(true);
			model.getServices().add(tokenColorer);
			
			GenService tokenScanner = XtextgenFactory.eINSTANCE.createGenService();
			tokenScanner.setServiceInterfaceFQName("org.eclipse.jface.text.rules.ITokenScanner");
			tokenScanner.setGenClassFQName("org.eclipse.xtext.ui.common.editor.syntaxcoloring.antlr.AntlrTokenScanner");
			tokenScanner.setUiService(true);
			model.getServices().add(tokenScanner);
		}
	}
	
}
