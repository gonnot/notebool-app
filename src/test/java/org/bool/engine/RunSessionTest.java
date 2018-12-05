package org.bool.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RunSessionTest {
    @Test
    @DisplayName("evaluate simple operation")
    void evaluate_simple_operation() {
        RunSession runSession = new RunSession();

        String result = runSession.evaluate("1+2");

        assertThat(result.trim()).isEqualTo("3");
    }

}