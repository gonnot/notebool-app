package org.bool;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import org.bool.block.MarkDownBlock;
import org.bool.engine.Block;
import org.bool.engine.NoteBook;
import org.bool.util.Focus;
import org.bool.util.KeyboardShortcut;

import java.util.Objects;

import static org.bool.engine.Block.CLICKED_CSS_CLASS;

@Route("")
@StyleSheet("styles/bool.css")
public class MainView extends HorizontalLayout implements HasUrlParameter<String> {

    private String fileName = "essai-save.txt";
    private NoteBook notebook;
    private final VerticalLayout notebookContainer;
    private final ComponentEventListener<ClickEvent<?>> clicked;

    /**
     * @noinspection WeakerAccess
     */
    public MainView() {
        this.setJustifyContentMode(JustifyContentMode.CENTER);
        this.setSizeFull();
        this.addClassName("root-container");

        clicked = event -> {
            Component clickedComponent = event.getSource();
            notebook.forEach(block -> {
                if (block.getComponent() != clickedComponent) {
                    block.getEditionService().stop();
                }
                block.getComponent().removeClassName(CLICKED_CSS_CLASS);
            });
            ((HasStyle)clickedComponent).addClassName(CLICKED_CSS_CLASS);
        };

        notebookContainer = new VerticalLayout();
        notebookContainer.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        notebookContainer.addClassName("notebook-container");
        notebookContainer.add(createTopToolbar());
        this.add(notebookContainer);

        loadNotebook();
    }

    private HorizontalLayout createTopToolbar() {
        HorizontalLayout topToolbar = new HorizontalLayout(
                new Image("jupiler-logo.png", "logo"),
                new Button("Save", VaadinIcon.DOWNLOAD.create(), event -> saveNotebook()),
                new Button("Load", VaadinIcon.WALLET.create(), event -> loadNotebook()),
                new Button("Add", VaadinIcon.ADD_DOCK.create(), event -> addBlock()));
        topToolbar.addClassName("notebook-header");
        return topToolbar;
    }

    private void addBlock() {
        MarkDownBlock block = new MarkDownBlock("Double Click to Edit");
        notebook.add(block);
        addContainer(block);

        //noinspection unchecked
        block.getComponent().addClickListener(clicked);
    }

    private void saveNotebook() {
        notebook.saveInStore(fileName);
    }

    private void loadNotebook() {
        displayNotebook(NoteBook.loadFromStore(fileName));
    }

    void displayNotebook(NoteBook newNotebook) {
        if (this.notebook != null && this.notebook.isNotEmpty()) {
            this.notebook.forEach(block -> notebookContainer.remove(block.getComponent()));
        }

        this.notebook = newNotebook;

        notebook.forEach(block -> {
            //noinspection unchecked
            block.getComponent().addClickListener(clicked);
        });

        notebook.forEach(this::addContainer);
        selectBlock(notebook, notebook.getFirstBlock());
    }

    private void addContainer(Block block) {
        Component component = block.getComponent();
        component.getElement().setAttribute("tabindex", "0");
        ComponentUtil.addListener(component, KeyDownEvent.class, event -> handleKeyStrokeOnBlock(block, event));

        notebookContainer.add(component);
    }

    private void handleKeyStrokeOnBlock(Block block, KeyDownEvent event) {
        if (block.getEditionService().isEditing()) {
            System.out.println("Block is in editing mode - do not handle arrow");
            return;
        }

        if (KeyboardShortcut.isArrowDown(event)) {
            selectBlock(notebook, notebook.nextOf(block));
        }
        else if (KeyboardShortcut.isArrowUp(event)) {
            selectBlock(notebook, notebook.previousOf(block));
        }
        else if (KeyboardShortcut.isEnter(event)) {
            block.getEditionService().start();
        }
    }

    private static void selectBlock(NoteBook notebook, Block blockToSelect) {
        notebook.forEach(block -> block.getComponent().removeClassName(CLICKED_CSS_CLASS));
        if (blockToSelect != null) {
            blockToSelect.getComponent().addClassName(CLICKED_CSS_CLASS);
            Focus.requestFocus(blockToSelect.getComponent());
        }
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        fileName = Objects.requireNonNullElse(parameter, "essai-save.txt");
        loadNotebook();
    }
}
