package org.bool.engine;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;

public interface Block {
    String EDITING_CSS_CLASS_NAME = "editing";
    static final String CLICKED_CSS_CLASS = "clicked";

    default <T extends Component & HasStyle & ClickNotifier> T getComponent() {
        //noinspection unchecked
        return (T)this;
    }

    // ---- Edition Service
    EditionService getEditionService();

    interface EditionService {
        void start();

        void stop();

        boolean isEditing();
    }

    // ---- Init / Persistence Service
    PersistenceService getPersistenceService();

    interface PersistenceService {
        String getContent();

        void init(RunSession runSession);
    }
}
