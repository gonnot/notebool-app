package org.bool.engine;

import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;

import java.util.List;

public class RunSession {
    private final JShell jShell;
    private int evaluationCount = 1;

    public RunSession() {
        this.jShell = JShell.builder()
                            .out(System.out)
                            .err(System.err)
                            .build();
    }

    public void addToClasspath(String dependencyPath) {
        jShell.addToClasspath(dependencyPath);
    }

    public Result evaluate(String script) {
        List<SnippetEvent> eval = jShell.eval(script);
        StringBuilder builder = new StringBuilder();
        for (SnippetEvent snippetEvent : eval) {
            builder.append(snippetEvent.value()).append("\n");
        }
        return new Result(builder.toString(), evaluationCount++);
    }

    public static class Result {
        private final String output;
        private int count;

        Result(String output, int count) {
            this.output = output;
            this.count = count;
        }

        public String getOutput() {
            return output;
        }

        public int getCount() {
            return count;
        }
    }
}
