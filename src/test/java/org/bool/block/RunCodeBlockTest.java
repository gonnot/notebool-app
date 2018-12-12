package org.bool.block;

import com.bisam.vaadin.uispec.VPanel;
import org.bool.engine.RunSession;
import org.bool.uispec4j.VDiv;
import org.bool.uispec4j.VSpan;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RunCodeBlockTest {

    @Test
    void simple_evaluation() {
        RunSession runSession = new RunSession();
        RunCodeBlock runCodeBlock = new RunCodeBlock();
        runCodeBlock.getPersistenceService().init(runSession);
        VPanel<RunCodeBlock> panel = new VPanel<>(runCodeBlock);

        panel.getTextBox().setText("1+2");
        panel.getButton().click();

        VDiv resultDiv = VDiv.get(panel, "outputText");
        assertThat(resultDiv.getVaadinComponent().getText().trim()).isEqualTo("3");

        VSpan evaluationCount = VSpan.get(panel, "evaluationCount");
        assertThat(evaluationCount.getVaadinComponent().getText().trim()).isEqualTo("[ 1 ]:");
    }
}