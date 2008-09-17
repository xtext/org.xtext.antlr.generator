package org.antlr.test;

import org.antlr.Tool;
import org.antlr.codegen.CodeGenerator;
import org.antlr.codegen.ActionTranslatorLexer;
import org.antlr.tool.*;


public class TestMessages extends BaseTest {

	/** Public default constructor used by TestRig */
	public TestMessages() {
	}


	public void testMessageStringificationIsConsistent() throws Exception {
		String action = "$other.tree = null;";
		ErrorQueue equeue = new ErrorQueue();
		ErrorManager.setErrorListener(equeue);
		Grammar g = new Grammar(
			"grammar a;\n" +
			"options { output = AST;}" +
			"otherrule\n" +
			"    : 'y' ;" +
			"rule\n" +
			"    : other=otherrule {" + action +"}\n" +
			"    ;");
		Tool antlr = newTool();
		CodeGenerator generator = new CodeGenerator(antlr, g, "Java");
		g.setCodeGenerator(generator);
		generator.genRecognizer(); // forces load of templates
		ActionTranslatorLexer translator = new ActionTranslatorLexer(generator,
																	"rule",
																	new antlr.CommonToken(ANTLRParser.ACTION,action),1);
		String rawTranslation =
			translator.translate();

		int expectedMsgID = ErrorManager.MSG_WRITE_TO_READONLY_ATTR;
		Object expectedArg = "other";
		Object expectedArg2 = "tree";
		GrammarSemanticsMessage expectedMessage =
			new GrammarSemanticsMessage(expectedMsgID, g, null, expectedArg, expectedArg2);
		String expectedMessageString = expectedMessage.toString();
		assertEquals(expectedMessageString, expectedMessage.toString());
	}
}
