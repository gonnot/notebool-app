package org.bool.block;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.bool.engine.Block;
import org.bool.engine.RunSession;
import org.bool.util.Focus;
import org.bool.util.KeyboardShortcut;

import java.util.Collection;

@HtmlImport("styles/block/AbstractActionBlock.html")
abstract class AbstractActionBlock extends Div implements Block {
    private static final String CSS_BLOCK_CLASS_NAME = "action-block";
    private final TextArea codeText = new TextArea();
    private final InternalEditionService internalEditionMode = new InternalEditionService();
    RunSession runSession;

    AbstractActionBlock() {
        addClassName(CSS_BLOCK_CLASS_NAME);

        Span evaluationCountSpan = new Span("[ ]:");
        evaluationCountSpan.setId("evaluationCount");
        evaluationCountSpan.addClassName("evaluationCount");

        Div outputText = new Div();
        outputText.setId("outputText");
        outputText.addClassName("outputText");

        codeText.setValueChangeMode(ValueChangeMode.EAGER);

        Button runButton = new Button(VaadinIcon.STEP_FORWARD.create());
        runButton.getElement().setAttribute("theme", "tertiary");
        runButton.addClickListener(event -> evaluate(codeText.getValue(), outputText, evaluationCountSpan, runButton));

        ListBox<String> completionListBox = new ListBox<>();
        Div completionContainer = new Div(completionListBox);
        completionContainer.addClassNames("completion", "completion-hidden");

        //noinspection unchecked
        ComponentUtil.addListener(codeText, ClickEvent.class, (ComponentEventListener)event -> getEditionService().start());

        codeText.addKeyPressListener(keyPressEvent -> {
            if (KeyboardShortcut.isControlEnter(keyPressEvent)) {
                evaluate(codeText.getValue(), outputText, evaluationCountSpan, runButton);
                getEditionService().stop();
            }
            else if (KeyboardShortcut.isControlSpace(keyPressEvent)) {
                completionListBox.clear();
                Collection<String> completions = completion(codeText.getValue());
                if (completions.isEmpty()) {
                    completionContainer.addClassName("completion-hidden");
                }
                else {
                    completionListBox.setItems(completions);
                    completionContainer.removeClassName("completion-hidden");
                }
            }
        });

        add(codeText, completionContainer, outputText, runButton, evaluationCountSpan);
    }

    AbstractActionBlock(String text) {
        this();
        this.codeText.setValue(text);
    }

    protected abstract void evaluate(String input, Div outputComponent, Span evaluationCountComponent, Button runButton);

    protected abstract Collection<String> completion(String input);

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
