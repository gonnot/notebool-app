package org.bool.block;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;

@Tag("bool-code")
@HtmlImport("styles/block/RunCodeBlock.html")
public class RunCodeBlock extends AbstractActionBlock {

    public RunCodeBlock() {
        super();
    }

    public RunCodeBlock(String text) {
        super(text);
    }

    @Override
    protected void evaluate(String script, Div outputText) {
        if (runSession != null) {
            outputText.setText(runSession.evaluate(script));
        }
        else {
            outputText.setText("Initialisation issue");
        }
    }

}
