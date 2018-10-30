package org.bool.block;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.bool.engine.Block;
import org.bool.engine.RunSession;

@Tag("bool-code")
public class RunCodeBlock extends Div implements Block {
    private final TextArea scriptText;
    private RunSession runSession;

    public RunCodeBlock() {
        scriptText = new TextArea();
        Div outputText = new Div();

        scriptText.setValueChangeMode(ValueChangeMode.EAGER);

        Button button = new Button(VaadinIcon.STEP_FORWARD.create());
        button.getElement().setAttribute("theme", "tertiary");
        button.addClickListener(event -> evaluate(scriptText.getValue(), outputText));

        scriptText.addKeyPressListener(new ComponentEventListener<KeyPressEvent>() {
            @Override
            public void onComponentEvent(KeyPressEvent keyPressEvent) {
                if (keyPressEvent.getModifiers().contains(KeyModifier.CONTROL) && keyPressEvent.getKey().matches("\n")) {
                    evaluate(scriptText.getValue(), outputText);
                }
            }
        });

        add(scriptText, outputText, button);
    }

    public RunCodeBlock(String text) {
        this();
        this.scriptText.setValue(text);
    }

    @Override
    public void init(RunSession runSession) {
        this.runSession = runSession;
    }

    private void evaluate(String script, Div outputText) {
        if (runSession != null) {
            outputText.setText(runSession.evaluate(script));
        } else {
            outputText.setText("Initialisation issue");
        }
    }

    @Override
    public String getContent() {
        return scriptText.getValue();
    }
}
