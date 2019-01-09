package org.bool.block;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.bool.engine.RunSession;

import java.util.Set;

@Tag("bool-code")
public class RunCodeBlock extends AbstractActionBlock {

    public RunCodeBlock() {
        super();
    }

    public RunCodeBlock(String text) {
        super(text);
    }

    @Override
    protected void evaluate(String input, Div outputComponent, Span evaluationCountComponent, Button runButton) {
        if (runSession != null) {
            RunSession.Result evaluationResult = runSession.evaluate(input);
            if (evaluationResult.hasError()) {
                outputComponent.setText(evaluationResult.getErrorMessage());
            }
            else {
                outputComponent.setText(evaluationResult.getOutput());
            }
            evaluationCountComponent.setText(" [ " + evaluationResult.getCount() + " ]:");
        }
        else {
            outputComponent.setText("Initialisation issue");
        }
    }

    @Override
    protected void completion(String input, Div outputComponent) {

        if (runSession != null) {
            StringBuilder output = new StringBuilder();
            int[] position = new int[1];
            Set<String> list;
            if (input.endsWith("(")) {
                list = runSession.documentation(input);
            }
            else {
                list = runSession.autoCompletion(input, position);
            }
            list.iterator().forEachRemaining(str -> output.append(str).append("\n"));
            outputComponent.setText(output.toString());
        }
    }
}
