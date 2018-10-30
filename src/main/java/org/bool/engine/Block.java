package org.bool.engine;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;

public interface Block extends HasStyle, ClickNotifier {
    default Component getComponent() {
        return (Component) this;
    }

    String getContent();

    default void init(RunSession runSession) {

    }
}
