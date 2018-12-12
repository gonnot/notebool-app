package org.bool.uispec4j;

import com.bisam.vaadin.uispec.AbstractVUIComponent;
import com.bisam.vaadin.uispec.VPanel;
import com.bisam.vaadin.uispec.feature.HasStyleFeature;
import com.bisam.vaadin.uispec.feature.HasTextFeature;
import com.vaadin.flow.component.html.Span;

public class VSpan extends AbstractVUIComponent<Span> implements HasTextFeature<Span>, HasStyleFeature<Span> {
    private VSpan(Span component) {
        super(component);
    }

    public static VSpan get(VPanel<?> panel, String componentId) {
        return panel.getVaadinExtendedComponent(componentId, component -> new VSpan((Span)component), Span.class);
    }
}
