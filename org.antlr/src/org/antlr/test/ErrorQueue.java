package org.antlr.test;

import org.antlr.tool.ANTLRErrorListener;
import org.antlr.tool.Message;
import org.antlr.tool.ToolMessage;

import java.util.List;
import java.util.LinkedList;

public class ErrorQueue implements ANTLRErrorListener {
	List infos = new LinkedList();
	List errors = new LinkedList();
	List warnings = new LinkedList();

	public void info(String msg) {
		infos.add(msg);
	}

	public void error(Message msg) {
		errors.add(msg);
	}

	public void warning(Message msg) {
		warnings.add(msg);
	}

	public void error(ToolMessage msg) {
		errors.add(msg);
	}

	public int size() {
		return infos.size() + errors.size() + warnings.size();
	}

	public String toString() {
		return "infos: "+infos+
			"errors: "+errors+
			"warnings: "+warnings;
	}
}

