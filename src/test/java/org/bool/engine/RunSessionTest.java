package org.bool.engine;

import org.junit.jupiter.api.Test;

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
    void results_are_indexed() {
        RunSession runSession = new RunSession();

        assertThat(runSession.evaluate("1+2").getCount()).isEqualTo(1);
        assertThat(runSession.evaluate("2+3").getCount()).isEqualTo(2);
    }

}