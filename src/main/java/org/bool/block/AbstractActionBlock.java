package org.bool.block;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.bool.engine.Block;
import org.bool.engine.RunSession;
import org.bool.util.Focus;
import org.bool.util.KeyboardShortcut;

abstract class AbstractActionBlock extends Div implements Block {
    private final TextArea codeText;
    private final InternalEditionService internalEditionMode = new InternalEditionService();
    RunSession runSession;

    AbstractActionBlock() {
        codeText = new TextArea();
        Div outputText = new Div();
        outputText.setId("outputText");

        codeText.setValueChangeMode(ValueChangeMode.EAGER);

        Button button = new Button(VaadinIcon.STEP_FORWARD.create());
        button.getElement().setAttribute("theme", "tertiary");
        button.addClickListener(event -> evaluate(codeText.getValue(), outputText));

        //noinspection unchecked
        ComponentUtil.addListener(codeText, ClickEvent.class, (ComponentEventListener)event -> getEditionService().start());

        codeText.addKeyPressListener(keyPressEvent -> {
            if (KeyboardShortcut.isControlEnter(keyPressEvent)) {
                evaluate(codeText.getValue(), outputText);
                getEditionService().stop();
            }
        });

        add(codeText, outputText, button);
    }

    AbstractActionBlock(String text) {
        this();
        this.codeText.setValue(text);
    }

    protected abstract void evaluate(String script, Div outputText);

    @Override
    public EditionService getEditionService() {
        return internalEditionMode;
    }

    @Override
    public PersistenceService getPersistenceService() {
        return new PersistenceService() {
            @Override
            public String getContent() {
                return codeText.getValue();
            }

            @Override
            public void init(RunSession runSession) {
                AbstractActionBlock.this.runSession = runSession;
            }
        };
    }

    private class InternalEditionService implements EditionService {
        private boolean isEditing;

        @Override
        public void start() {
            System.out.println("RunCodeBlock.start");
            addClassName(EDITING_CSS_CLASS_NAME);
            codeText.addClassName(EDITING_CSS_CLASS_NAME);
            isEditing = true;
            Focus.requestFocus(codeText);
        }

        @Override
        public void stop() {
            System.out.println("RunCodeBlock.stop");
            removeClassName(EDITING_CSS_CLASS_NAME);
            codeText.removeClassName(EDITING_CSS_CLASS_NAME);
            isEditing = false;
            Focus.requestFocus(getComponent());
        }

        @Override
        public boolean isEditing() {
            return isEditing;
        }
    }
}
