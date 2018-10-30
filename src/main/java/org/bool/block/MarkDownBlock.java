package org.bool.block;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.bool.engine.Block;
//
// Math Rendering : https://github.com/Khan/KaTeX
@Tag("bool-markdown")
@JavaScript("https://cdn.rawgit.com/showdownjs/showdown/1.8.7/dist/showdown.min.js")
public class MarkDownBlock extends Div implements Block {
    private String markdownText;
    private boolean isEditing = false;

    public MarkDownBlock(String markdownText) {
        this.markdownText = markdownText;
        setText(markdownText);
        addClickListener(new ComponentEventListener<ClickEvent<MarkDownBlock>>() {
            @Override
            public void onComponentEvent(ClickEvent clickEvent) {
                if (!isEditing && clickEvent.getClickCount() >= 2) {
                    isEditing = true;
                    setText(null);
                    TextArea textField = new TextArea();

                    textField.setValueChangeMode(ValueChangeMode.EAGER);

                    textField.addKeyPressListener(new ComponentEventListener<KeyPressEvent>() {
                        @Override
                        public void onComponentEvent(KeyPressEvent keyPressEvent) {
                            if (keyPressEvent.getModifiers().contains(KeyModifier.CONTROL) && keyPressEvent.getKey().matches("\n")) {
                                isEditing = false;
                                MarkDownBlock.this.markdownText = textField.getValue();
                                MarkDownBlock.this.remove(textField);
                                MarkDownBlock.this.setText(MarkDownBlock.this.markdownText);
                                getUI().ifPresent(MarkDownBlock.this::convertMarkDownToHtml);
                            } else {
                                System.out.println("keyPressEvent = " + keyPressEvent);
                            }
                        }
                    });

                    textField.setValue(MarkDownBlock.this.markdownText);

                    add(textField);
                }
            }
        });
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        //language=JavaScript
        convertMarkDownToHtml(attachEvent
                .getUI());

    }

    private void convertMarkDownToHtml(UI ui) {
        String expression = "" +
                "var converter = new showdown.Converter()," +
                "    text      = $0.innerHTML;" +
                "    $0.innerHTML = converter.makeHtml(text);";

        ui.getPage()
                .executeJavaScript(
                        expression,
                        getElement()
                );
    }

    @Override
    public String getContent() {
        return markdownText;
    }
}
