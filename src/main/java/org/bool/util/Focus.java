package org.bool.util;

import com.vaadin.flow.component.Component;

public class Focus {
    public static void requestFocus(Component component) {
        component.getElement().callFunction("focus");
    }
}
