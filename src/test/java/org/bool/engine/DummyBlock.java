package org.bool.engine;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.textfield.TextArea;

public class DummyBlock extends TextArea implements Block, ClickNotifier {
    private final InternalEditionService internalEditionService = new InternalEditionService();

    public DummyBlock(String value) {
        setValue(value);
    }

    @Override
    public EditionService getEditionService() {
        return internalEditionService;
    }

    @Override
    public PersistenceService getPersistenceService() {
        return new PersistenceService() {
            @Override
            public String getContent() {
                return getValue();
            }

            @Override
            public void init(RunSession runSession) {
            }
        };
    }

    private static class InternalEditionService implements EditionService {
        private boolean isEditing = false;

        @Override
        public void start() {
            isEditing = true;
        }

        @Override
        public void stop() {
            isEditing = false;
        }

        @Override
        public boolean isEditing() {
            return isEditing;
        }
    }
}
