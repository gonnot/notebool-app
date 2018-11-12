package org.bool.block;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.bool.engine.Block;
import org.bool.engine.RunSession;
import org.bool.util.KeyboardShortcut;

@Tag("bool-code")
@HtmlImport("styles/block/RunCodeBlock.html")
public class RunCodeBlock extends Div implements Block {
    private final TextArea codeText;
    private final InternalEditionMode internalEditionMode = new InternalEditionMode();
    private RunSession runSession;

    public RunCodeBlock() {
        codeText = new TextArea();
        Div outputText = new Div();

        codeText.setValueChangeMode(ValueChangeMode.EAGER);

        Button button = new Button(VaadinIcon.STEP_FORWARD.create());
        button.getElement().setAttribute("theme", "tertiary");
        button.addClickListener(event -> evaluate(codeText.getValue(), outputText));

        //noinspection unchecked
        ComponentUtil.addListener(codeText, ClickEvent.class, (ComponentEventListener)event -> getEditionMode().start());

        codeText.addKeyPressListener(keyPressEvent -> {
            if (KeyboardShortcut.isControlEnter(keyPressEvent)) {
                evaluate(codeText.getValue(), outputText);
                getEditionMode().stop();
            }
        });

        add(codeText, outputText, button);
    }

    public RunCodeBlock(String text) {
        this();
        this.codeText.setValue(text);
    }

    @Override
    public void init(RunSession runSession) {
        this.runSession = runSession;
    }

    private void evaluate(String script, Div outputText) {
        if (runSession != null) {
            outputText.setText(runSession.evaluate(script));
        }
        else {
            outputText.setText("Initialisation issue");
        }
    }

    @Override
    public EditionMode getEditionMode() {
        return internalEditionMode;
    }

    @Override
    public String getContent() {
        return codeText.getValue();
    }

    private class InternalEditionMode implements EditionMode {
        private boolean isEditing;

        @Override
        public void start() {
            System.out.println("InternalEditionMode.start");
            addClassName(EDITING_CSS_CLASS_NAME);
            codeText.addClassName(EDITING_CSS_CLASS_NAME);
            isEditing = true;
        }

        @Override
        public void stop() {
            System.out.println("InternalEditionMode.stop");
            removeClassName(EDITING_CSS_CLASS_NAME);
            codeText.removeClassName(EDITING_CSS_CLASS_NAME);
            isEditing = false;
        }

        @Override
        public boolean isEditing() {
            return isEditing;
        }
    }
}
