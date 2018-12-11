package org.bool.block;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;

@Tag("bool-code")
public class RunCodeBlock extends AbstractActionBlock {

    public RunCodeBlock() {
        super();
    }

    public RunCodeBlock(String text) {
        super(text);
    }

    @Override
    protected void evaluate(String input, Div outputComponent) {
        if (runSession != null) {
            outputComponent.setText(runSession.evaluate(input));
        }
        else {
            outputComponent.setText("Initialisation issue");
        }
    }

}
