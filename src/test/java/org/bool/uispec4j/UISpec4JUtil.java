package org.bool.uispec4j;

import com.vaadin.flow.component.UI;

public class UISpec4JUtil {
    public static void forceFlushUiAccessCommands() {
        UI.getCurrent().getSession().unlock();
    }
}