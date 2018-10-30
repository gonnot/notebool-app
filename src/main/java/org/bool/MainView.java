package org.bool;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasStyle;
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
import org.bool.engine.NoteBook;

import java.util.Objects;

@Route("")
@StyleSheet("styles/bool.css")
public class MainView extends HorizontalLayout implements HasUrlParameter<String> {
    private static final String CLICKED_CSS_CLASS = "clicked";
    private String fileName = "essai-save-2.txt";
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

        initNotebook(NoteBook.load("template.txt"));
    }

    private HorizontalLayout createTopToolbar() {
        HorizontalLayout topToolbar = new HorizontalLayout(
                new Image("jupiler-logo.png", "logo"),
                new Button("Save", VaadinIcon.WALLET.create(), event -> save()),
                new Button("Load", VaadinIcon.DOWNLOAD.create(), event -> load()));
        topToolbar.addClassName("notebook-header");
        return topToolbar;
    }

    private void save() {
        notebook.save(fileName);
    }

    private void load() {
        initNotebook(NoteBook.load(fileName));
    }

    private void initNotebook(NoteBook newNotebook) {
        if (this.notebook != null && this.notebook.isNotEmpty()) {
            this.notebook.forEach(block -> notebookContainer.remove(block.getComponent()));
        }

        this.notebook = newNotebook;

        ComponentEventListener<ClickEvent<?>> clicked = new ComponentEventListener<>() {
            @Override
            public void onComponentEvent(ClickEvent event) {
                notebook.forEach(block -> block.removeClassName(CLICKED_CSS_CLASS));
                ((HasStyle) event.getSource()).addClassName(CLICKED_CSS_CLASS);
            }
        };
        notebook.forEach(block -> block.addClickListener(clicked));

        notebook.forEach(block -> notebookContainer.add(block.getComponent()));
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        fileName = Objects.requireNonNullElse(parameter, "essai-save-2.txt");
        load();
    }
}
