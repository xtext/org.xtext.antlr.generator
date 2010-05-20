/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.itemis.xtext.antlr.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author huebner - Initial contribution and API
 */
public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for de.itemis.xtext.antlr.tests");
		return suite;
	}
}
