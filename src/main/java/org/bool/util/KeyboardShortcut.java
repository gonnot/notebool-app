package org.bool.util;

import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.KeyPressEvent;

public class KeyboardShortcut {
    public static boolean isControlEnter(KeyPressEvent event) {
        return event.getModifiers().contains(KeyModifier.CONTROL) && event.getKey().matches("\n");
    }

    public static boolean isControlSpace(KeyPressEvent event) {
        return event.getModifiers().contains(KeyModifier.CONTROL) && event.getKey().matches(" ");
    }

    public static boolean isControlArrowDown(KeyDownEvent event) {
        return event.getModifiers().contains(KeyModifier.CONTROL) && isArrowDown(event);
    }

    public static boolean isControlArrowUp(KeyDownEvent event) {
        return event.getModifiers().contains(KeyModifier.CONTROL) && isArrowUp(event);
    }

    public static boolean isArrowUp(KeyDownEvent event) {
        return event.getKey().getKeys().contains("ArrowUp");
    }

    public static boolean isArrowDown(KeyDownEvent event) {
        return event.getKey().getKeys().contains("ArrowDown");
    }

    public static boolean isEnter(KeyDownEvent event) {
        return event.getKey().getKeys().contains("Enter");
    }

    public static boolean isEscape(KeyDownEvent event) {
        return event.getKey().getKeys().contains("Escape");
    }

}
