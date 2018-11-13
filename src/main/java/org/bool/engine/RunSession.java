package org.bool.engine;

import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;

import java.util.List;

public class RunSession {
    private final JShell jShell;

    RunSession() {
        this.jShell = JShell.builder()
                            .out(System.out)
                            .err(System.err)
                            .build();
        jShell.addToClasspath("D:\\cache\\maven\\repository\\joda-time\\joda-time\\2.9.4\\joda-time-2.9.4.jar");
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
