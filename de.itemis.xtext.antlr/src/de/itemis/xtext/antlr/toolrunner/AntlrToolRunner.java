/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package de.itemis.xtext.antlr.toolrunner;

import java.io.IOException;
import java.io.Writer;

import org.antlr.Tool;
import org.antlr.tool.Grammar;

/**
 * @author Jan Köhnlein - Initial contribution and API
 * @author Sebastian Zarnekow
 */
public class AntlrToolRunner {
    
	public static void runWithParams(String grammarFullPath, String... furtherArgs) {
		String[] args = new String[furtherArgs.length + 1];
		System.arraycopy(furtherArgs, 0, args, 0, furtherArgs.length);
		args[furtherArgs.length] = grammarFullPath;
		Tool antlr = new Tool(args) {
        	/**
        	 * Use a writer that suppresses the first comment line of java files, because
        	 * we don't want to have a timestamp in our generated parsers and lexers.
        	 */
			@Override
			public Writer getOutputFile(Grammar g, String fileName) throws IOException {
				Writer result = super.getOutputFile(g, fileName);
				if (fileName.endsWith(".java")) //$NON-NLS-1$
					result = new TailWriter(result, 2);
				return result;
			}
        };
        antlr.process();
	}
	
    public static void run(String grammarFullPath) {
    	runWithParams(grammarFullPath);
    }
}
