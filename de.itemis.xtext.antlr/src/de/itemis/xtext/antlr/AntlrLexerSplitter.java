/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.itemis.xtext.antlr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Heiko Behrens - Initial contribution and API
 */
public class AntlrLexerSplitter {

	public final static String INDENT = "    ";
	public final static String INDENT2 = INDENT + INDENT;
	public static final Pattern METHOD_SIGNATURE_PATTERN = Pattern.compile("public void mTokens\\(\\) throws RecognitionException \\{", 0);
	public static final Pattern METHOD_END_PATTERN = Pattern.compile("^\\s{4}}", 0);
	public static final Pattern OUTER_BLOCK_END_PATTERN = Pattern.compile("^\\s{8}}", 0);
	public static final Pattern INDENT_LEVEL_PATTERN = Pattern.compile("^\\s{12}", 0);
	public static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("int (\\w+)=\\d+", 0);
	public static final Pattern DFA_ASSIGNMENT_PATTERN = Pattern.compile("\\w+ = dfa\\d+\\.predict\\(input\\);", 0);
	public static final Pattern BREAK_LINE_PATTERN = Pattern.compile("^\\s{12}\\s*break;", 0);
	public static final Pattern OUTER_SWITCH_PATTERN = Pattern.compile("^\\s{8}switch", 0);
	public static final Pattern OUTER_IF_PATTERN = Pattern.compile("^\\s{8}if", 0);
	
	private List<ExtractedMethod> extractedMethods = new ArrayList<ExtractedMethod>();
	
	public List<ExtractedMethod> getExtractedMethods() {
		return Collections.unmodifiableList(extractedMethods);
	}

	private StringBuilder stringBuilder;
	private final Scanner scanner;

	public AntlrLexerSplitter(String content) {
		scanner = new Scanner(content);
	}
	
	boolean copyUntilMethod() {
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			stringBuilder.append(line);
			stringBuilder.append("\n");
			if(METHOD_SIGNATURE_PATTERN.matcher(line).find())
				return true;
		}
		return false;
	}
	
	public String transform() {
		if(stringBuilder != null)
			return stringBuilder.toString();
		
		stringBuilder = new StringBuilder();
		if(copyUntilMethod()) {
			refacatorAndExtract();
			produceMethods();
		}
		copyTail();
		return stringBuilder.toString();
	}

	public void produceMethods() {
		for (ExtractedMethod m : extractedMethods) {
			m.writeTo(stringBuilder);
			stringBuilder.append("\n");
		}
	}
	
	
	public void refacatorAndExtract() {

//      {0}  // ../org.xtext.example.mydsl.ui/src-gen/org/xtext/example/contentassist/antlr/internal/InternalMyDsl.g:1:8: ( T11 | T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | RULE_ID | RULE_INT | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER )
//      {1}  int alt12=16;
//      {2a}  int LA12_0 = input.LA(1);
//		{2b}  alt12 = dfa12.predict(input); -> no extraction
//		{3}
//      {4}  if ( (LA12_0=='i') ) {
		
		//{0}
		stringBuilder.append(scanner.nextLine());
		stringBuilder.append("\n");
		//{1}
		String varDecl = scanner.nextLine();
		stringBuilder.append(varDecl);
		stringBuilder.append("\n");
		String varName = getVarnameFromDecl(varDecl);
		
		// some lexer mToken methods are just delegating to another method
		if(varName != null) {
			// {2}
			stringBuilder.append(scanner.nextLine());
			stringBuilder.append("\n");
	
			// try to identify outer switch/if statement withing the first 4 lines
			// if this fails - do nothing
			int lineNo = 2;
			do {
				lineNo++;
				String line = scanner.nextLine();
				stringBuilder.append(line);
				stringBuilder.append("\n");
				if(OUTER_SWITCH_PATTERN.matcher(line).find()) {
					extractMethodsFromSwitch(varName);
					break;
				} else if (OUTER_IF_PATTERN.matcher(line).find()) {
					Pattern switchPattern = Pattern.compile(String.format("^        switch \\(%s\\) \\{", varName));
					extractMethodsFromIf(varName, switchPattern);
					break;
				}
			} while (lineNo <= 4);
		}
		
		// leave tail of method body unmodified
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			stringBuilder.append(line);
			stringBuilder.append("\n");
			if(METHOD_END_PATTERN.matcher(line).find())
				break;
		}
	}

	private void extractMethodsFromSwitch(String varName) {
		// TODO: provide extraction for switch statements, too
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			stringBuilder.append(line);
			stringBuilder.append("\n");
			if(OUTER_BLOCK_END_PATTERN.matcher(line).find()) {
				break;
			}
		}
	}

	private void extractMethodsFromIf(String varName, Pattern endPattern) {
		// refactor if cascade - until final switch statement and collect methods
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if(INDENT_LEVEL_PATTERN.matcher(line).find()) {
				ExtractedMethod method = new ExtractedMethod(varName, extractedMethods.size() + 1);
				extractMethod(stringBuilder, scanner, varName, method, line);
				extractedMethods.add(method);
			} else { 
				stringBuilder.append(line);
				stringBuilder.append("\n");
			}
			
			if(endPattern.matcher(line).find()) {
				break;
			}
				
		}
	}

	static void extractMethod(StringBuilder sb, Scanner scanner, String varName, ExtractedMethod method, String firstLine) {
		method.addLine(firstLine);
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if(INDENT_LEVEL_PATTERN.matcher(line).find() || "".equals(line)) {
				method.addLine(line);
			} else {
				sb.append(INDENT2);
				sb.append(INDENT);
				sb.append(varName);
				sb.append(" = ");
				sb.append(method.getName());
				sb.append("();\n");
				sb.append(line);
				sb.append("\n");
				break;
			}
		}
	}

	private static String getVarnameFromDecl(String varDecl) {
		Matcher m = ASSIGNMENT_PATTERN.matcher(varDecl);
		if(!m.find())
			return null;
		return m.group(1);
	}

	void copyTail() {
		while(scanner.hasNextLine()) {
			stringBuilder.append(scanner.nextLine());
			if(scanner.hasNextLine())
				stringBuilder.append("\n");
		}
	}

	static public class ExtractedMethod {
	
		private final int index;
		private final List<String> lines = new ArrayList<String>();
		private final Pattern assignmentPattern;
	
		public ExtractedMethod(String resultVar, int index) {
			this.index = index;
			this.assignmentPattern = Pattern.compile(resultVar+"=(\\d+);", 0);
		}
	
		public void addLine(String line) {
			if(!BREAK_LINE_PATTERN.matcher(line).find())
				lines.add(line);
		}
	
		public Object getName() {
			return String.format("mTokensHelper%03d", index);
		}
		
		public void writeTo(StringBuilder sb) {
			// TODO remove breaks of lowest level (no performance hit but kind of ugly)
			sb.append(INDENT);
			sb.append("private int ");
			sb.append(getName());
			sb.append("() throws RecognitionException {\n");
			for(String s : lines) {
				sb.append(getAsExtractedLine(s));
				sb.append("\n");
			}
			sb.append(INDENT);
			sb.append("}\n");
		}
	
		public String getAsExtractedLine(String s) {
			return assignmentPattern.matcher(s).replaceFirst("return $1;").replaceFirst(INDENT, "");
		}
	}

}
