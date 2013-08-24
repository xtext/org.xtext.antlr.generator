/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package de.itemis.xtext.antlr.toolrunner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

import org.antlr.Tool;
import org.antlr.codegen.CodeGenerator;
import org.antlr.tool.CompositeGrammar;
import org.antlr.tool.Grammar;

/**
 * @author Jan Köhnlein - Initial contribution and API
 * @author Sebastian Zarnekow
 */
public class AntlrToolRunner {
	
	public static void runWithEncodingAndParams(String grammarFullPath, String explicitEncoding, String... furtherArgs) {
		final String encoding = explicitEncoding != null ? explicitEncoding : Charset.defaultCharset().name();
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
				Writer result = superGetOutputFile(g, fileName);
				if (fileName.endsWith(".java")) //$NON-NLS-1$
					result = new TailWriter(result, 2);
				return result;
			}
			
			/** Get a grammar mentioned on the command-line and any delegates */
		    @Override
			public Grammar getRootGrammar(String grammarFileName) throws IOException {
		        //StringTemplate.setLintMode(true);
		        // grammars mentioned on command line are either roots or single grammars.
		        // create the necessary composite in case it's got delegates; even
		        // single grammar needs it to get token types.
		        CompositeGrammar composite = new CompositeGrammar();
		        Grammar grammar = new Grammar(this, grammarFileName, composite);
		        composite.setDelegationRoot(grammar);
		        File f = null;

		        if (isSet("haveInputDir")) {
		            f = new File(this.<String>get("inputDirectory"), grammarFileName);
		        }
		        else {
		            f = new File(grammarFileName);
		        }

		        // Store the location of this grammar as if we import files, we can then
		        // search for imports in the same location as the original grammar as well as in
		        // the lib directory.
		        //
		        set("parentGrammarDirectory", f.getParent());

		        if (grammarFileName.lastIndexOf(File.separatorChar) == -1) {
		        	set("parentGrammarDirectory", ".");
		        }
		        else {
		        	set("parentGrammarDirectory", grammarFileName.substring(0, grammarFileName.lastIndexOf(File.separatorChar)));
		        }
		        FileInputStream inputStream = new FileInputStream(f);
		        InputStreamReader reader = new InputStreamReader(inputStream, encoding);
		        BufferedReader br = new BufferedReader(reader);
		        grammar.parseAndBuildAST(br);
		        composite.watchNFAConversion = internalOption_watchNFAConversion;
		        br.close();
		        reader.close();
		        inputStream.close();
		        return grammar;
		    }
		    
		    @Override
		    public void sortGrammarFiles() throws IOException {
		    	// nothing to do, we have only one grammar file
		    	// so it's save to skip the GrammarSpelunker which uses
		    	// the default encoding internally
		    }
			
			protected Writer superGetOutputFile(Grammar g, String fileName) throws IOException {
				if (getOutputDirectory() == null) {
		            return new StringWriter();
		        }
		        // output directory is a function of where the grammar file lives
		        // for subdir/T.g, you get subdir here.  Well, depends on -o etc...
		        // But, if this is a .tokens file, then we force the output to
		        // be the base output directory (or current directory if there is not a -o)
		        //
		        File outputDir;
		        if (fileName.endsWith(CodeGenerator.VOCAB_FILE_EXTENSION)) {
		            if (isSet("haveOutputDir")) {
		                outputDir = new File(getOutputDirectory());
		            }
		            else {
		                outputDir = new File(".");
		            }
		        }
		        else {
		            outputDir = getOutputDirectory(g.getFileName());
		        }
		        File outputFile = new File(outputDir, fileName);

		        if (!outputDir.exists()) {
		            outputDir.mkdirs();
		        }
		        // CHANGE is here
		        // pass the encoding to the file output stream
		        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		        OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, encoding);
		        return new BufferedWriter(writer);
			}
			
			private boolean isSet(String fieldName) {
				try {
					Field field = Tool.class.getDeclaredField(fieldName);
					field.setAccessible(true);
					boolean result = field.getBoolean(this);
					return result;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			private <T> T get(String fieldName) {
				try {
					Field field = Tool.class.getDeclaredField(fieldName);
					field.setAccessible(true);
					@SuppressWarnings("unchecked")
					T result = (T) field.get(this);
					return result;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
			private void set(String fieldName, Object value) {
				try {
					Field field = Tool.class.getDeclaredField(fieldName);
					field.setAccessible(true);
					field.set(this, value);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
        };
        antlr.process();
	}
	
	public static void runWithParams(String grammarFullPath, String... furtherArgs) {
		runWithEncodingAndParams(grammarFullPath, Charset.defaultCharset().name(), furtherArgs);
	}
	
    public static void run(String grammarFullPath) {
    	runWithParams(grammarFullPath);
    }
}
