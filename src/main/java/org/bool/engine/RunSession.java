package org.bool.engine;

import jdk.jshell.Diag;
import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

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
                return CompletableFuture.completedFuture(Result.ok(command.call(), evaluationCount++));
            }
            catch (Exception e) {
                return CompletableFuture.failedFuture(e);
            }
        }
        else {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return Result.ok(command.call(), evaluationCount++);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executor);

        }
    }

    public Result evaluate(String script) {
        List<SnippetEvent> snippetEventList = jShell.eval(script);
        StringBuilder outputResult = new StringBuilder();

        SnippetEvent snippetEvent = snippetEventList.get(0);
        outputResult.append(snippetEvent.value()).append("\n");
        Stream<Diag> diagnostics = jShell.diagnostics(snippetEvent.snippet());

        Optional<Diag> first = diagnostics.findFirst();
        if (first.isPresent()) {
            String errorMessageResult = first.get().getMessage(Locale.FRENCH).trim();
            return Result.failing(errorMessageResult, evaluationCount++);
        }
        return Result.ok(outputResult.toString(), evaluationCount++);
    }

    public static class Result {
        private final String output;
        private final String errorMessage;
        private int count;

        static Result ok(String output, int count) {
            return new Result(output, count, null);
        }

        static Result failing(String errorMessage, int count) {
            return new Result(null, count, errorMessage);
        }

        private Result(String output, int count, String errorMessage) {
            this.output = output;
            this.count = count;
            this.errorMessage = errorMessage;
        }

        public String getOutput() {
            if (output == null) {
                return errorMessage;
            }
            return output;
        }

        public int getCount() {
            return count;
        }

        boolean hasError() {
            return errorMessage != null;
        }

        String getErrorMessage() {
            return errorMessage;
        }
    }
}
