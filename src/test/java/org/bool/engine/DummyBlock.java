package org.bool.engine;

import com.vaadin.flow.component.textfield.TextArea;

public class DummyBlock extends TextArea implements Block {
    public DummyBlock(String value) {
        setValue(value);
    }

    @Override
    public EditionService getEditionService() {
        return new EditionService() {
            @Override
            public void start() {
            }

            @Override
            public void stop() {
            }

            @Override
            public boolean isEditing() {
                return false;
            }
        };
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
}
