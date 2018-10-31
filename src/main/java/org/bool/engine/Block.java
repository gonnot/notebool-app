package org.bool.engine;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;

public interface Block {
    default <T extends Component & HasStyle & ClickNotifier> T getComponent() {
        //noinspection unchecked
        return (T)this;
    }

    String getContent();

    default void init(RunSession runSession) {
    }
}
