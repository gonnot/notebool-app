package org.bool.block;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.bool.engine.Block;
import org.bool.util.KeyboardShortcut;

// Math Rendering    : https://github.com/Khan/KaTeX
// Code Highlighting : https://highlightjs.org/
@Tag("bool-markdown")
@JavaScript("https://cdn.rawgit.com/showdownjs/showdown/1.8.7/dist/showdown.min.js")
public class MarkDownBlock extends Div implements Block {
    private String markdownText;
    private boolean isEditing = false;

    public MarkDownBlock(String markdownText) {
        this.markdownText = markdownText;
        setText(markdownText);
        //noinspection unchecked
        addClickListener((ComponentEventListener<ClickEvent<Component>>)clickEvent -> {
            if (!isEditing && clickEvent.getClickCount() >= 2) {
                startEditingMode();
            }
        });
    }

    private void startEditingMode() {
        isEditing = true;
        setText(null);
        TextArea textField = new TextArea();
        textField.setValue(MarkDownBlock.this.markdownText);
        textField.setValueChangeMode(ValueChangeMode.EAGER);

        textField.addKeyPressListener((ComponentEventListener<KeyPressEvent>)keyPressEvent -> {
            if (KeyboardShortcut.isControlEnter(keyPressEvent)) {
                stopEditingMode(textField);
            }
        });

        add(textField);
    }

    private void stopEditingMode(TextArea textField) {
        isEditing = false;
        MarkDownBlock.this.markdownText = textField.getValue();
        MarkDownBlock.this.remove(textField);
        MarkDownBlock.this.setText(MarkDownBlock.this.markdownText);
        getUI().ifPresent(MarkDownBlock.this::convertMarkDownToHtml);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        //language=JavaScript
        convertMarkDownToHtml(attachEvent
                                      .getUI());

    }

    private void convertMarkDownToHtml(UI ui) {
        String expression = "const converter = new showdown.Converter()," +
                            "    text      = $0.innerHTML;" +
                            "    $0.innerHTML = converter.makeHtml(text);";

        ui.getPage().executeJavaScript(expression, getElement());
    }

    @Override
    public String getContent() {
        return markdownText;
    }
}
