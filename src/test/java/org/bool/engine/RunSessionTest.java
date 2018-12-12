package org.bool.engine;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RunSessionTest {
    @Test
    void evaluate_simple_operation() {
        RunSession runSession = new RunSession();

        String result = runSession.evaluate("1+2").getOutput();

        assertThat(result.trim()).isEqualTo("3");
    }

    @Test
    void results_are_indexed() {
        RunSession runSession = new RunSession();

        assertThat(runSession.evaluate("1+2").getCount()).isEqualTo(1);
        assertThat(runSession.evaluate("2+3").getCount()).isEqualTo(2);
    }

}