package org.bool.engine;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RunSessionTest {
/*
    @Test
    @DisplayName("Name")
    void Name() {
        JShell jShell = JShell.builder()
                              .out(System.out)
                              .err(System.err)
                              .build();
//        SourceCodeAnalysis.Documentation
        System.out.println("isActive()");
        System.out.println("   Marche (true) = " +  jShell.eval("\"ee\".toString()").get(0).status().isActive());
        System.out.println("   Marche pas(false) = " + jShell.eval("\"ee\".toDDDDDDDString()").get(0).status().isActive());
        System.out.println("   Compile pas(false) = " +  jShell.eval("\"ee").get(0).status().isActive());

        System.out.println("diagnostic");
        SnippetEvent snippetEvent = jShell.eval("\"ee\".toDDDDDDDString()").get(0);
        Stream<Diag> diagnostics = jShell.diagnostics(snippetEvent.snippet());
        List<Diag> diagList = diagnostics.collect(Collectors.toList());

        System.out.println("   Marche pas(false) = " + diagList.get(0).isError());
    }
*/

    @Test
    void evaluate_simple_operation() {
        RunSession runSession = new RunSession();

        String result = runSession.evaluate("1+2").getOutput();

        assertThat(result.trim()).isEqualTo("3");
    }

    @Test
    void evaluate_invalid_operation() {
        RunSession runSession = new RunSession();

        RunSession.Result result = runSession.evaluate("new Integer(5).unknownMethod();");

        assertThat(result.getOutput()).isEqualTo(null);
        assertThat(result.hasError()).isEqualTo(true);
        assertThat(result.getErrorMessage()).isEqualTo("cannot find symbol\n" +
                                                       "  symbol:   method unknownMethod()\n" +
                                                       "  location: class java.lang.Integer");
    }

    @Test
    void evaluate_completion_operation() {
        RunSession runSession = new RunSession();

        int[] pos = new int[1];

        Set<String> completion = runSession.autoCompletion("new Integer(7).w", pos);

        assertThat(completion.size()).isEqualTo(1);
        assertThat(completion).containsAnyOf("wait(");
    }

    @Test
    void evaluate_method_parameters_operation() {
        RunSession runSession = new RunSession();

        Set<String> documentation = runSession.documentation("new Integer(7).wait(");
        assertThat(documentation.size()).isEqualTo(3);
        assertThat(documentation).containsAnyOf("void Object.wait() throws InterruptedException",
                                                "void Object.wait(long timeoutMillis, int nanos) throws InterruptedException",
                                                "void Object.wait(long timeoutMillis) throws InterruptedException");

    }

    @Test
    void evaluate_class_creation_operation() {
        RunSession runSession = new RunSession();

        RunSession.Result result = runSession.evaluate("public class MyClass{}");

        assertThat(result.getOutput()).isEqualTo("Step Ok");
        assertThat(result.hasError()).isEqualTo(false);
    }

    @Test
    void results_are_indexed() {
        RunSession runSession = new RunSession();

        assertThat(runSession.evaluate("1+2").getCount()).isEqualTo(1);
        assertThat(runSession.evaluate("2+3").getCount()).isEqualTo(2);
    }

}