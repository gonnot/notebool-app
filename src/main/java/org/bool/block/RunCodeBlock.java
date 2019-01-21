package org.bool.block;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.bool.engine.RunSession;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Tag("bool-code")
//@JavaScript("http://www.mycdn.com/monaco-editor/min/vs/loader.js")
public class RunCodeBlock extends AbstractActionBlock {

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        //language=JavaScript
        convertHighlight(attachEvent
                                 .getUI());

    }

    private void convertHighlight(UI ui) {
//        String expression = "monaco.editor.create(document.getElementById(\"outputText\"),{value:text,language:\"java\"});";
//        ui.getPage().executeJavaScript(expression, getElement());
    }

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
                outputComponent.addClassName(CSS_ERROR_CLASS);
            }
            else {
                outputComponent.removeClassName(CSS_ERROR_CLASS);
                outputComponent.setText(evaluationResult.getOutput());
            }
            evaluationCountComponent.setText(" [ " + evaluationResult.getCount() + " ]:");
        }
        else {
            outputComponent.setText("Initialisation issue");
        }
    }

    @Override
    protected Collection<String> completion(String input) {
        Set<String> results = Collections.emptySet();
        if (runSession != null) {
            if (input.endsWith("(")) {
                results = runSession.documentation(input);
            }
            else {
                int[] position = new int[1];
                results = runSession.autoCompletion(input, position);
            }
        }
        return results;
    }
}
