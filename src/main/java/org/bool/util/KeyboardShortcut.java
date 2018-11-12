package org.bool.util;

import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.KeyPressEvent;

public class KeyboardShortcut {
    public static boolean isControlEnter(KeyPressEvent keyPressEvent) {
        return keyPressEvent.getModifiers().contains(KeyModifier.CONTROL) && keyPressEvent.getKey().matches("\n");
    }


    public static boolean isArrowUp(KeyDownEvent event) {
        return event.getKey().getKeys().contains("ArrowUp");
    }

    public static boolean isArrowDown(KeyDownEvent event) {
        return event.getKey().getKeys().contains("ArrowDown");
    }
}
