package org.bool;

import com.bisam.vaadin.uispec.UISpecAssert;
import com.bisam.vaadin.uispec.VPanel;
import com.bisam.vaadin.uispec.VTextBox;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Key;
import org.bool.engine.Block;
import org.bool.engine.DummyBlock;
import org.bool.engine.NoteBook;
import org.bool.uispec4j.VaadinUIExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VaadinUIExtension.class)
class MainViewTest {
    private final MainView mainView = new MainView();
    private final VPanel<MainView> panel = new VPanel<>(mainView);

    @Nested
    @DisplayName("Load Notebook")
    class LoadNotebook {
        @Test
        @DisplayName("When Load Notebook then select first block")
        void WhenLoadNotebookThenSelectFirstBlock() {
            NoteBook notebook = new NoteBook(new DummyBlock("block 1"),
                                             new DummyBlock("block 2"));

            mainView.displayNotebook(notebook);

            VTextBox textBox = panel.getTextBox("block 1");
            UISpecAssert.assertTrue(textBox.hasCssClassName(Block.CLICKED_CSS_CLASS));
        }

        @Test
        @DisplayName("When Load Empty Notebook then select nothing")
        void WhenLoadEmptyNotebookThenSelectNothing() {
            mainView.displayNotebook(new NoteBook());
            mainView.getChildren()
                    .filter(component -> component instanceof HasStyle)
                    .forEach(component -> assertThat(((HasStyle)component).hasClassName(Block.CLICKED_CSS_CLASS)).isFalse());
        }

    }

    @Nested
    @DisplayName("Key Strokes")
    class KeyStrokeTest {

        @Test
        @DisplayName("When press enter then edit selected block")
        void WhenPressEnterThenEditSelectedBox() {
            DummyBlock block = new DummyBlock("block 1");

            mainView.displayNotebook(new NoteBook(block));

            VTextBox textBox = panel.getTextBox("block 1");
            textBox.pressKeyDown(Key.ENTER);

            assertThat(block.getEditionService().isEditing()).isTrue();
        }
    }
}