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
import org.bool.engine.Block;
import org.bool.engine.NoteBook;

import java.util.Objects;

@Route("")
@StyleSheet("styles/bool.css")
public class MainView extends HorizontalLayout implements HasUrlParameter<String> {
    private static final String CLICKED_CSS_CLASS = "clicked";
    private String fileName = "essai-save.txt";
    private NoteBook notebook;
    private final VerticalLayout notebookContainer;

    public MainView() {
        this.setJustifyContentMode(JustifyContentMode.CENTER);
        this.setSizeFull();
        this.addClassName("root-container");

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
                new Button("Load", VaadinIcon.WALLET.create(), event -> loadNotebook()));
        topToolbar.addClassName("notebook-header");
        return topToolbar;
    }

    private void saveNotebook() {
        notebook.save(fileName);
    }

    private void loadNotebook() {
        initNotebook(NoteBook.load(fileName));
    }

    private void initNotebook(NoteBook newNotebook) {
        if (this.notebook != null && this.notebook.isNotEmpty()) {
            this.notebook.forEach(block -> notebookContainer.remove(block.getComponent()));
        }

        this.notebook = newNotebook;

        ComponentEventListener<ClickEvent<?>> clicked = (ComponentEventListener<ClickEvent<?>>)event -> {
            notebook.forEach(block -> block.getComponent().removeClassName(CLICKED_CSS_CLASS));
            ((HasStyle)event.getSource()).addClassName(CLICKED_CSS_CLASS);
        };

        notebook.forEach(block -> {
            //noinspection unchecked
            block.getComponent().addClickListener(clicked);
        });

        notebook.forEach(block -> {
            Component component = block.getComponent();
            component.getElement().setAttribute("tabindex", "0");
            ComponentUtil.addListener(component,
                                      KeyDownEvent.class,
                                      event -> {
                                          Block nextBlock = null;
                                          if (event.getKey().getKeys().contains("ArrowDown")) {
                                              nextBlock = notebook.nextOf(block);
                                          }
                                          if (event.getKey().getKeys().contains("ArrowUp")) {
                                              nextBlock = notebook.previousOf(block);
                                          }

                                          if (nextBlock != null) {
                                              notebook.forEach(block1 -> block1.getComponent().removeClassName(CLICKED_CSS_CLASS));
                                              nextBlock.getComponent().addClassName(CLICKED_CSS_CLASS);
                                          }
                                      });

            notebookContainer.add(component);
        });
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        fileName = Objects.requireNonNullElse(parameter, "essai-save.txt");
        loadNotebook();
    }
}
