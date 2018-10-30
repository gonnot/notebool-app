package org.bool.engine;

import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;

import java.util.List;

public class RunSession {
    private final JShell jShell;

    public RunSession() {
        this.jShell = JShell.builder()
                .out(System.out)
                .err(System.err)
                .build();
    }

    public String evaluate(String script) {
        List<SnippetEvent> eval = jShell.eval(script);
        StringBuilder builder = new StringBuilder();
        for (SnippetEvent snippetEvent : eval) {
            builder.append(snippetEvent.value()).append("\n");
        }
        return builder.toString();
    }

}
