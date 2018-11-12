package org.bool.engine;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;

public interface Block {
    public static final String EDITING_CSS_CLASS_NAME = "editing";

    default <T extends Component & HasStyle & ClickNotifier> T getComponent() {
        //noinspection unchecked
        return (T)this;
    }

    // ---- Edition Mode
    EditionMode getEditionMode();

    interface EditionMode {
        void start();

        void stop();

        boolean isEditing();
    }

    // ---- Save / Load
    String getContent();

    default void init(RunSession runSession) {
    }
}
