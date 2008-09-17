/*
 [The "BSD licence"]
 Copyright (c) 2005-2006 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.antlr.test;

import junit.framework.TestCase;
import org.antlr.Tool;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.tool.ErrorManager;
import org.antlr.tool.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseTest extends TestCase {

	public static final String jikes = null;//"/usr/bin/jikes";
	public static final String pathSep = System.getProperty("path.separator");
	public static final String CLASSPATH = System.getProperty("java.class.path");
	public static final String tmpdir = new File(System.getProperty("java.io.tmpdir"), "antlr3").getAbsolutePath();

	/** If error during execution, store stderr here */
	protected String stderr;

	protected Tool newTool() {
		Tool tool = new Tool();
		tool.setOutputDirectory(tmpdir);
		return tool;
	}

	protected boolean compile(String fileName) {
		String compiler = "javac";
		String classpathOption = "-classpath";

		if (jikes!=null) {
			compiler = jikes;
			classpathOption = "-bootclasspath";
		}

		String[] args = new String[] {
					compiler, "-d", tmpdir,
					classpathOption, tmpdir+pathSep+CLASSPATH,
					tmpdir+"/"+fileName
		};
		String cmdLine = compiler+" -d "+tmpdir+" "+classpathOption+" "+tmpdir+pathSep+CLASSPATH+" "+fileName;
		//System.out.println("compile: "+cmdLine);
		File outputDir = new File(tmpdir);
		try {
			Process process =
				Runtime.getRuntime().exec(args, null, outputDir);
			StreamVacuum stdout = new StreamVacuum(process.getInputStream());
			StreamVacuum stderr = new StreamVacuum(process.getErrorStream());
			stdout.start();
			stderr.start();
			process.waitFor();
			if ( stdout.toString().length()>0 ) {
				System.err.println("compile stderr from: "+cmdLine);
				System.err.println(stdout);
			}
			if ( stderr.toString().length()>0 ) {
				System.err.println("compile stderr from: "+cmdLine);
				System.err.println(stderr);
			}
			int ret = process.exitValue();
			return ret==0;
		}
		catch (Exception e) {
			System.err.println("can't exec compilation");
			e.printStackTrace(System.err);
			return false;
		}
	}

	/** Return true if all is ok, no errors */
	protected boolean antlr(String fileName, String grammarFileName, String grammarStr, boolean debug) {
		boolean allIsWell = true;
		mkdir(tmpdir);
		writeFile(tmpdir, fileName, grammarStr);
		try {
			final List options = new ArrayList();
			if ( debug ) {
				options.add("-debug");
			}
			options.add("-o");
			options.add(tmpdir);
			options.add("-lib");
			options.add(tmpdir);
			options.add(new File(tmpdir,grammarFileName).toString());
			final String[] optionsA = new String[options.size()];
			options.toArray(optionsA);
			final ErrorQueue equeue = new ErrorQueue();
			ErrorManager.setErrorListener(equeue);
			Tool antlr = new Tool(optionsA);
			antlr.process();
			if ( equeue.errors.size()>0 ) {
				allIsWell = false;
				System.err.println("antlr reports errors from "+options);
				for (int i = 0; i < equeue.errors.size(); i++) {
					Message msg = (Message) equeue.errors.get(i);
					System.err.println(msg);
				}
			}
		}
		catch (Exception e) {
			allIsWell = false;
			System.err.println("problems building grammar: "+e);
			e.printStackTrace(System.err);
		}
		return allIsWell;
	}

	protected String execParser(String grammarFileName,
									String grammarStr,
									String parserName,
									String lexerName,
									String startRuleName,
									String input, boolean debug)
	{
		eraseFiles(".class");
		eraseFiles(".java");

		rawGenerateAndBuildRecognizer(grammarFileName,
									  grammarStr,
									  parserName,
									  lexerName,
									  debug);
		writeFile(tmpdir, "input", input);
		boolean parserBuildsTrees =
			grammarStr.indexOf("output=AST")>=0 ||
			grammarStr.indexOf("output = AST")>=0;
		boolean parserBuildsTemplate =
			grammarStr.indexOf("output=template")>=0 ||
			grammarStr.indexOf("output = template")>=0;
		return rawExecRecognizer(parserName,
								 null,
								 lexerName,
								 startRuleName,
								 null,
								 parserBuildsTrees,
								 parserBuildsTemplate,
								 debug);
	}

	protected String execTreeParser(String parserGrammarFileName,
										String parserGrammarStr,
										String parserName,
										String treeParserGrammarFileName,
										String treeParserGrammarStr,
										String treeParserName,
										String lexerName,
										String parserStartRuleName,
										String treeParserStartRuleName,
										String input)
	{
		return execTreeParser(parserGrammarFileName,
							  parserGrammarStr,
							  parserName,
							  treeParserGrammarFileName,
							  treeParserGrammarStr,
							  treeParserName,
							  lexerName,
							  parserStartRuleName,
							  treeParserStartRuleName,
							  input,
							  false);
	}

	protected String execTreeParser(String parserGrammarFileName,
										String parserGrammarStr,
										String parserName,
										String treeParserGrammarFileName,
										String treeParserGrammarStr,
										String treeParserName,
										String lexerName,
										String parserStartRuleName,
										String treeParserStartRuleName,
										String input,
										boolean debug)
	{
		eraseFiles(".class");
		eraseFiles(".java");

		// build the parser
		rawGenerateAndBuildRecognizer(parserGrammarFileName,
									  parserGrammarStr,
									  parserName,
									  lexerName,
									  debug);

		// build the tree parser
		rawGenerateAndBuildRecognizer(treeParserGrammarFileName,
									  treeParserGrammarStr,
									  treeParserName,
									  lexerName,
									  debug);

		writeFile(tmpdir, "input", input);

		boolean parserBuildsTrees = parserGrammarStr.indexOf("output=AST")>=0;
		boolean parserBuildsTemplate = parserGrammarStr.indexOf("output=template")>=0;

		return rawExecRecognizer(parserName,
								 treeParserName,
								 lexerName,
								 parserStartRuleName,
								 treeParserStartRuleName,
								 parserBuildsTrees,
								 parserBuildsTemplate,
								 debug);
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
													String grammarStr,
													String parserName,
													String lexerName,
													boolean debug)
	{
		boolean allIsWell =
			antlr(grammarFileName, grammarFileName, grammarStr, debug);
		if ( lexerName!=null ) {
			boolean ok;
			if ( parserName!=null ) {
				ok = compile(parserName+".java");
				if ( !ok ) { allIsWell = false; }
			}
			ok = compile(lexerName+".java");
			if ( !ok ) { allIsWell = false; }
		}
		else {
			boolean ok = compile(parserName+".java");
			if ( !ok ) { allIsWell = false; }
		}
		return allIsWell;
	}

	protected String rawExecRecognizer(String parserName,
											  String treeParserName,
											  String lexerName,
											  String parserStartRuleName,
											  String treeParserStartRuleName,
											  boolean parserBuildsTrees,
											  boolean parserBuildsTemplate,
											  boolean debug)
	{
		if ( parserBuildsTrees ) {
			writeTreeTestFile(parserName,
							  treeParserName,
							  lexerName,
							  parserStartRuleName,
							  treeParserStartRuleName,
							  debug);
		}
		else if ( parserBuildsTemplate ) {
			writeTemplateTestFile(parserName,
								  lexerName,
								  parserStartRuleName,
								  debug);
		}
		else {
			writeTestFile(parserName,
						  lexerName,
						  parserStartRuleName,
						  debug);
		}

		compile("Test.java");
		try {
			String[] args = new String[] {
				"java", "-classpath", CLASSPATH+pathSep+tmpdir,
				"Test", new File(tmpdir, "input").getAbsolutePath()
			};
			String cmdLine = "java -classpath "+CLASSPATH+pathSep+tmpdir+" Test " + new File(tmpdir, "input").getAbsolutePath();
			//System.out.println("execParser: "+cmdLine);
			this.stderr = null;
			Process process =
				Runtime.getRuntime().exec(args, null, new File(tmpdir));
			StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
			StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
			stdoutVacuum.start();
			stderrVacuum.start();
			process.waitFor();
			stdoutVacuum.join();
			stderrVacuum.join();
			String output = null;
			output = stdoutVacuum.toString();
			if ( stderrVacuum.toString().length()>0 ) {
				this.stderr = stderrVacuum.toString();
				System.err.println("exec parser stderrVacuum: "+ stderrVacuum);
			}
			return output;
		}
		catch (Exception e) {
			System.err.println("can't exec parser");
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static class StreamVacuum implements Runnable {
		StringBuffer buf = new StringBuffer();
		BufferedReader in;
		Thread sucker;
		public StreamVacuum(InputStream in) {
			this.in = new BufferedReader( new InputStreamReader(in) );
		}
		public void start() {
			sucker = new Thread(this);
			sucker.start();
		}
		public void run() {
			try {
				String line = in.readLine();
				while (line!=null) {
					buf.append(line);
					buf.append('\n');
					line = in.readLine();
				}
			}
			catch (IOException ioe) {
				System.err.println("can't read output from process");
			}
		}
		/** wait for the thread to finish */
		public void join() throws InterruptedException {
			sucker.join();
		}
		public String toString() {
			return buf.toString();
		}
	}

	protected void writeFile(String dir, String fileName, String content) {
		try {
			File f = new File(dir, fileName);
			FileWriter w = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(w);
			bw.write(content);
			bw.close();
			w.close();
		}
		catch (IOException ioe) {
			System.err.println("can't write file");
			ioe.printStackTrace(System.err);
		}
	}

	protected void mkdir(String dir) {
		File f = new File(dir);
		f.mkdirs();
	}

	protected void writeTestFile(String parserName,
									 String lexerName,
									 String parserStartRuleName,
									 boolean debug)
	{
		StringTemplate outputFileST = new StringTemplate(
			"import org.antlr.runtime.*;\n" +
			"import org.antlr.runtime.tree.*;\n" +
			"import org.antlr.runtime.debug.*;\n" +
			"\n" +
			"class Profiler2 extends Profiler {\n" +
			"    public void terminate() { ; }\n" +
			"}\n"+
			"public class Test {\n" +
			"    public static void main(String[] args) throws Exception {\n" +
			"        CharStream input = new ANTLRFileStream(args[0]);\n" +
			"        $lexerName$ lex = new $lexerName$(input);\n" +
			"        CommonTokenStream tokens = new CommonTokenStream(lex);\n" +
			"        $createParser$\n"+
			"        parser.$parserStartRuleName$();\n" +
			"    }\n" +
			"}"
			);
		StringTemplate createParserST =
			new StringTemplate(
			"        Profiler2 profiler = new Profiler2();\n"+
			"        $parserName$ parser = new $parserName$(tokens,profiler);\n" +
			"        profiler.setParser(parser);\n");
		if ( !debug ) {
			createParserST =
				new StringTemplate(
				"        $parserName$ parser = new $parserName$(tokens);\n");
		}
		outputFileST.setAttribute("createParser", createParserST);
		outputFileST.setAttribute("parserName", parserName);
		outputFileST.setAttribute("lexerName", lexerName);
		outputFileST.setAttribute("parserStartRuleName", parserStartRuleName);
		writeFile(tmpdir, "Test.java", outputFileST.toString());
	}

	protected void writeTreeTestFile(String parserName,
										 String treeParserName,
										 String lexerName,
										 String parserStartRuleName,
										 String treeParserStartRuleName,
										 boolean debug)
	{
		StringTemplate outputFileST = new StringTemplate(
			"import org.antlr.runtime.*;\n" +
			"import org.antlr.runtime.tree.*;\n" +
			"import org.antlr.runtime.debug.*;\n" +
			"\n" +
			"class Profiler2 extends Profiler {\n" +
			"    public void terminate() { ; }\n" +
			"}\n"+
			"public class Test {\n" +
			"    public static void main(String[] args) throws Exception {\n" +
			"        CharStream input = new ANTLRFileStream(args[0]);\n" +
			"        $lexerName$ lex = new $lexerName$(input);\n" +
			"        TokenRewriteStream tokens = new TokenRewriteStream(lex);\n" +
			"        $createParser$\n"+
			"        $parserName$.$parserStartRuleName$_return r = parser.$parserStartRuleName$();\n" +
			"        $if(!treeParserStartRuleName)$\n" +
			"        if ( r.tree!=null )\n" +
			"            System.out.println(((Tree)r.tree).toStringTree());\n" +
			"        $else$\n" +
			"        CommonTreeNodeStream nodes = new CommonTreeNodeStream((Tree)r.tree);\n" +
			"        nodes.setTokenStream(tokens);\n" +
			"        $treeParserName$ walker = new $treeParserName$(nodes);\n" +
			"        walker.$treeParserStartRuleName$();\n" +
			"        $endif$\n" +
			"    }\n" +
			"}"
			);
		StringTemplate createParserST =
			new StringTemplate(
			"        Profiler2 profiler = new Profiler2();\n"+
			"        $parserName$ parser = new $parserName$(tokens,profiler);\n" +
			"        profiler.setParser(parser);\n");
		if ( !debug ) {
			createParserST =
				new StringTemplate(
				"        $parserName$ parser = new $parserName$(tokens);\n");
		}
		outputFileST.setAttribute("createParser", createParserST);
		outputFileST.setAttribute("parserName", parserName);
		outputFileST.setAttribute("treeParserName", treeParserName);
		outputFileST.setAttribute("lexerName", lexerName);
		outputFileST.setAttribute("parserStartRuleName", parserStartRuleName);
		outputFileST.setAttribute("treeParserStartRuleName", treeParserStartRuleName);
		writeFile(tmpdir, "Test.java", outputFileST.toString());
	}

	protected void writeTemplateTestFile(String parserName,
											 String lexerName,
											 String parserStartRuleName,
											 boolean debug)
	{
		StringTemplate outputFileST = new StringTemplate(
			"import org.antlr.runtime.*;\n" +
			"import org.antlr.stringtemplate.*;\n" +
			"import org.antlr.stringtemplate.language.*;\n" +
			"import org.antlr.runtime.debug.*;\n" +
			"import java.io.*;\n" +
			"\n" +
			"class Profiler2 extends Profiler {\n" +
			"    public void terminate() { ; }\n" +
			"}\n"+
			"public class Test {\n" +
			"    static String templates =\n" +
			"    		\"group test;\"+" +
			"    		\"foo(x,y) ::= \\\"<x> <y>\\\"\";\n"+
			"    static StringTemplateGroup group ="+
			"    		new StringTemplateGroup(new StringReader(templates)," +
			"					AngleBracketTemplateLexer.class);"+
			"    public static void main(String[] args) throws Exception {\n" +
			"        CharStream input = new ANTLRFileStream(args[0]);\n" +
			"        $lexerName$ lex = new $lexerName$(input);\n" +
			"        CommonTokenStream tokens = new CommonTokenStream(lex);\n" +
			"        $createParser$\n"+
			"		 parser.setTemplateLib(group);\n"+
			"        $parserName$.$parserStartRuleName$_return r = parser.$parserStartRuleName$();\n" +
			"        if ( r.st!=null )\n" +
			"            System.out.print(r.st.toString());\n" +
			"	 	 else\n" +
			"            System.out.print(\"\");\n" +
			"    }\n" +
			"}"
			);
		StringTemplate createParserST =
			new StringTemplate(
			"        Profiler2 profiler = new Profiler2();\n"+
			"        $parserName$ parser = new $parserName$(tokens,profiler);\n" +
			"        profiler.setParser(parser);\n");
		if ( !debug ) {
			createParserST =
				new StringTemplate(
				"        $parserName$ parser = new $parserName$(tokens);\n");
		}
		outputFileST.setAttribute("createParser", createParserST);
		outputFileST.setAttribute("parserName", parserName);
		outputFileST.setAttribute("lexerName", lexerName);
		outputFileST.setAttribute("parserStartRuleName", parserStartRuleName);
		writeFile(tmpdir, "Test.java", outputFileST.toString());
	}

	protected void eraseFiles(final String filesEndingWith) {
		File tmpdirF = new File(tmpdir);
		String[] files = tmpdirF.list();
		for(int i = 0; files!=null && i < files.length; i++) {
			if ( files[i].endsWith(filesEndingWith) ) {
        		new File(tmpdir+"/"+files[i]).delete();
			}
		}
	}

	public String getFirstLineOfException() {
		if ( this.stderr==null ) {
			return null;
		}
		String[] lines = this.stderr.split("\n");
		String prefix="Exception in thread \"main\" ";
		return lines[0].substring(prefix.length(),lines[0].length());
	}
}
