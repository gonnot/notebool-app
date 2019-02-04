package org.bool.component;

import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.KeyUpEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.polymertemplate.EventHandler;
import com.vaadin.flow.component.polymertemplate.ModelItem;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.bool.util.Focus;
import org.bool.util.KeyboardShortcut;

@Tag("jupiler-textarea")
@HtmlImport("component/jupiler-textarea.html")
public class JupilerTextarea extends PolymerTemplate<JupilerTextarea.JupilerTextareaModel> {
    private final ListBox<String> completionListBox;

    public JupilerTextarea(ListBox<String> completionListBox) {
        this.completionListBox = completionListBox;
        getModel().setMyvalue("From Java Code");

        completionListBox.setItems("toString(", "hashCode(");
        completionListBox.addValueChangeListener(event -> {
            getElement().callFunction("appendTextAtCursorPosition", event.getValue());
            Focus.requestFocus(JupilerTextarea.this);
        });
/*
        addKeyPressListener(keyPressEvent -> {
            System.out.println("JupilerTextarea.KEY (" + keyPressEvent.getSource() + ") " + getModel().getMyvalue());
            if (KeyboardShortcut.isControlSpace(keyPressEvent)) {
                Focus.requestFocus(completionListBox);
                // cursorPosition
//                System.out.println("getModel().getCursorPosition() = " + getModel().getCursorPosition());
            }
        });
*/
    }

    public void setValue(String value) {
        getModel().setMyvalue(value);
    }

    @EventHandler
    private void handleClick(@ModelItem("event.jupyler.value") String value) {
        System.out.println("Handle click event " + value);
    }

    @EventHandler
    private void handleKeyPress(@ModelItem("event.jupyler.cursorPosition") Integer cursorPosition,
                                @EventData("event.key") String key,
                                @EventData("event.location") int location,
                                @EventData("event.ctrlKey") boolean ctrlKey,
                                @EventData("event.shiftKey") boolean shiftKey,
                                @EventData("event.altKey") boolean altKey,
                                @EventData("event.metaKey") boolean metaKey,
                                @EventData("event.repeat") boolean repeat,
                                @EventData("event.isComposing") boolean composing) {
        KeyUpEvent event = new KeyUpEvent(this, true, key, location, ctrlKey, shiftKey, altKey, metaKey, repeat, composing);
        System.out.println("Keypress " + cursorPosition + " - " + getModel().getMyvalue());
        if (KeyboardShortcut.isControlSpace(event)) {
            Focus.requestFocus(completionListBox);
            // cursorPosition
//                System.out.println("getModel().getCursorPosition() = " + getModel().getCursorPosition());
        }
    }

    public String getValue() {
        return getModel().getMyvalue();
    }

    public interface JupilerTextareaModel extends TemplateModel {
//        String getCursorPosition();

        String getMyvalue();

        void setMyvalue(String value);
    }
}
