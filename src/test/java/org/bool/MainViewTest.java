package org.bool;

import com.bisam.vaadin.uispec.UISpecAssert;
import com.bisam.vaadin.uispec.VPanel;
import com.bisam.vaadin.uispec.VTextBox;
import com.vaadin.flow.component.*;
import org.bool.engine.Block;
import org.bool.engine.DummyBlock;
import org.bool.engine.NoteBook;
import org.bool.uispec4j.VaadinUIExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.stream.Collectors;

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
            NoteBook notebook = NoteBook.of(new DummyBlock("block 1"),
                                            new DummyBlock("block 2"));

            mainView.displayNotebook(notebook);

            VTextBox textBox = panel.getTextBox("block 1");
            UISpecAssert.assertTrue(textBox.hasCssClassName(Block.CLICKED_CSS_CLASS));
        }

        @Test
        @DisplayName("When Load Empty Notebook then select nothing")
        void WhenLoadEmptyNotebookThenSelectNothing() {
            mainView.displayNotebook(NoteBook.empty());
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

            mainView.displayNotebook(NoteBook.of(block));

            VTextBox textBox = panel.getTextBox("block 1");
            textBox.pressKeyDown(Key.ENTER);

            assertThat(block.getEditionService().isEditing()).isTrue();
        }

        @Test
        @DisplayName("When press esc then stop edition of selected block")
        void WhenPressEscThenStopEditOfSelectedBox() {
            DummyBlock block = new DummyBlock("block 1");

            mainView.displayNotebook(NoteBook.of(block));
            block.getEditionService().start();

            VTextBox textBox = panel.getTextBox("block 1");
            textBox.pressKeyDown(Key.ESCAPE);

            assertThat(block.getEditionService().isEditing()).isFalse();
        }
    }

    @Nested
    @DisplayName("Click mouse")
    class ClickMouseTest {

        @Test
        @DisplayName("When click then select the block")
        void WhenClickThenSelectTheBlock() {
            DummyBlock block1 = new DummyBlock("block 1");
            DummyBlock block2 = new DummyBlock("block 2");

            mainView.displayNotebook(NoteBook.of(block1, block2));

            UISpecAssert.assertTrue(panel.getTextBox("block 1").hasCssClassName(Block.CLICKED_CSS_CLASS));

            click(block1.getComponent());

            UISpecAssert.assertTrue(panel.getTextBox("block 1").hasCssClassName(Block.CLICKED_CSS_CLASS));
            UISpecAssert.assertFalse(panel.getTextBox("block 2").hasCssClassName(Block.CLICKED_CSS_CLASS));
        }

    }

    @Nested
    @DisplayName("Notebook edition")
    class NotebookEditionTest {
        @Test
        @DisplayName("When press Ctrl+ArrowDown then move block down")
        void WhenPressCtrlArrowDownThenMoveBlockDown() {
            DummyBlock block1 = new DummyBlock("block 1");
            DummyBlock block2 = new DummyBlock("block 2");
            NoteBook notebook = NoteBook.of(block1, block2);

            mainView.displayNotebook(notebook);
            block1.getEditionService().start();

            pressCtrlWithKey(block1, "ArrowDown");

            assertThat(notebook.blocks().collect(Collectors.toList())).containsSequence(block2, block1);
            assertThat(block2.getStyle().get("order")).isEqualTo("0");
            assertThat(block1.getStyle().get("order")).isEqualTo("1");
        }

        @Test
        @DisplayName("When move last block down Then becomes first")
        void WhenMoveLastBlockDownThenBecomesFirst() {
            DummyBlock block1 = new DummyBlock("block 1");
            DummyBlock block2 = new DummyBlock("block 2");
            DummyBlock block3 = new DummyBlock("block 3");
            NoteBook notebook = NoteBook.of(block1, block2, block3);

            mainView.displayNotebook(notebook);
            block3.getEditionService().start();

            pressCtrlWithKey(block3, "ArrowDown");

            assertThat(notebook.blocks().collect(Collectors.toList())).containsSequence(block3, block1, block2);
            assertThat(block3.getStyle().get("order")).isEqualTo("0");
            assertThat(block1.getStyle().get("order")).isEqualTo("1");
            assertThat(block2.getStyle().get("order")).isEqualTo("2");
        }

        @Test
        @DisplayName("When press Ctrl+ArrowUp then move block up")
        void WhenPressCtrlArrowUpThenMoveBlockUp() {
            DummyBlock block1 = new DummyBlock("block 1");
            DummyBlock block2 = new DummyBlock("block 2");
            NoteBook notebook = NoteBook.of(block1, block2);

            mainView.displayNotebook(notebook);
            block2.getEditionService().start();

            pressCtrlWithKey(block1, "ArrowUp");

            assertThat(notebook.blocks().collect(Collectors.toList())).containsSequence(block2, block1);
            assertThat(block2.getStyle().get("order")).isEqualTo("0");
            assertThat(block1.getStyle().get("order")).isEqualTo("1");
        }

        @Test
        @DisplayName("When move first block up Then becomes last")
        void WhenMoveFirstBlockUpThenBecomesLast() {
            DummyBlock block1 = new DummyBlock("block 1");
            DummyBlock block2 = new DummyBlock("block 2");
            DummyBlock block3 = new DummyBlock("block 3");
            NoteBook notebook = NoteBook.of(block1, block2, block3);

            mainView.displayNotebook(notebook);
            block1.getEditionService().start();

            pressCtrlWithKey(block1, "ArrowUp");

            assertThat(notebook.blocks().collect(Collectors.toList())).containsSequence(block2, block3, block1);
            assertThat(block2.getStyle().get("order")).isEqualTo("0");
            assertThat(block3.getStyle().get("order")).isEqualTo("1");
            assertThat(block1.getStyle().get("order")).isEqualTo("2");
        }

        private void pressCtrlWithKey(DummyBlock block, String key) {
            KeyDownEvent keyDownEvent = new KeyDownEvent(block, true, key, 0, true, false, false, false, false, false);
            ComponentUtil.fireEvent(block, keyDownEvent);
        }

    }

    private static void click(Component component) {
        if (component == null) {
            return;
        }
        ComponentUtil.fireEvent(component, new ClickEvent<>(component));

        click(component.getParent().orElse(null));
    }
}