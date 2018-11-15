package org.bool.uispec4j;

import com.bisam.vaadin.uispec.internal.UIBinder;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class VaadinUIExtension implements BeforeEachCallback, AfterEachCallback {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(VaadinUIExtension.class);
    private static final String TEST_CONTEXT_KEY = "guiTestContext";

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        extensionContext.getRoot().getStore(NAMESPACE).remove(TEST_CONTEXT_KEY);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        extensionContext.getRoot().getStore(NAMESPACE).put(TEST_CONTEXT_KEY, UIBinder.initUI());
    }
}
