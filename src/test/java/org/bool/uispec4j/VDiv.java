package org.bool.uispec4j;

import com.bisam.vaadin.uispec.AbstractVUIComponent;
import com.bisam.vaadin.uispec.VPanel;
import com.bisam.vaadin.uispec.feature.HasStyleFeature;
import com.bisam.vaadin.uispec.feature.HasTextFeature;
import com.vaadin.flow.component.html.Div;

public class VDiv extends AbstractVUIComponent<Div> implements HasTextFeature<Div>, HasStyleFeature<Div> {
    private VDiv(Div component) {
        super(component);
    }

    public static VDiv get(VPanel<?> panel, String componentId) {
        return panel.getVaadinExtendedComponent(componentId, component -> new VDiv((Div)component), Div.class);
    }
}
