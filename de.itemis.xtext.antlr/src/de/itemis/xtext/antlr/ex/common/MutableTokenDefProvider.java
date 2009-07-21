/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.itemis.xtext.antlr.ex.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.eclipse.xtext.parser.antlr.AntlrTokenDefProvider;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class MutableTokenDefProvider extends AntlrTokenDefProvider {
	
	public void writeTokenFile(PrintWriter out) throws IOException {
		int i = 0;
		for(Map.Entry<Integer, String> entry: getTokenDefMap().entrySet()) {
			if (i != 0) {
				out.println();
			}
			i++;
			out.print(entry.getValue());
			out.print('=');
			out.print(entry.getKey());
		}
		out.close();
	}
	
}
