package org.bool.engine;

import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunSession {
    private final JShell jShell;
    private final ExecutionMode executionMode;
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private int evaluationCount = 1;

    public enum ExecutionMode {
        SYNC,
        ASYNC
    }

    public RunSession() {
        this(ExecutionMode.SYNC);
    }

    public RunSession(ExecutionMode executionMode) {
        this.executionMode = executionMode;
        this.jShell = JShell.builder()
                            .out(System.out)
                            .err(System.err)
                            .build();
    }

    public void addToClasspath(String dependencyPath) {
        jShell.addToClasspath(dependencyPath);
    }

    public CompletableFuture<Result> evaluate(Callable<String> command) {
        if (executionMode == ExecutionMode.SYNC) {
            try {
                return CompletableFuture.completedFuture(new Result(command.call(), evaluationCount++));
            }
            catch (Exception e) {
                return CompletableFuture.failedFuture(e);
            }
        }
        else {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return new Result(command.call(), evaluationCount++);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executor);

        }
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
