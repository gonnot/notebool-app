package org.bool.engine;

import com.vaadin.flow.component.notification.Notification;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class NoteBook {
    private static final String SEPARATOR_STRING = "################################################";
    private final List<Block> content = new ArrayList<>();
    private final RunSession runSession;

    private NoteBook() {
        this.runSession = new RunSession();
    }

    public void forEach(Consumer<? super Block> action) {
        content.forEach(action);
    }

    public void save(final String fileName) {
        try (FileWriter fileWriter = new FileWriter("D:\\project\\sideprojects\\notebool-app\\" + fileName)) {
            Optional<String> result = content.stream()
                                             .map(block -> separator(block.getClass().getName())
                                                           + block.getPersistenceService().getContent())
                                             .reduce((first, second) -> first + "\n" + second);
            if (result.isPresent()) {
                fileWriter.write(result.get());
            }
            fileWriter.write("\n");
            fileWriter.write(separator("END"));
        }
        catch (IOException e) {
            e.printStackTrace();
            Notification.show("Oupss " + e.getLocalizedMessage());
        }
    }

    public static NoteBook load(final String fileName) {
        try {
            String content = new String(Files.readAllBytes(Paths.get("D:\\project\\sideprojects\\notebool-app\\" + fileName)));
            String[] split = content.split(SEPARATOR_STRING);
            NoteBook noteBook = new NoteBook();
            Stream.of(split)
                  .map(String::trim)
                  .filter(s -> !s.isEmpty())
                  .filter(s -> !"END".equals(s))
                  .map(contentStartingWithBlockClass -> {
                      try {
                          int endIndex = contentStartingWithBlockClass.indexOf('\n');
                          String className = contentStartingWithBlockClass.substring(0, endIndex).trim();
                          String blockContent = contentStartingWithBlockClass.substring(endIndex, contentStartingWithBlockClass.length()).trim();

                          //noinspection unchecked
                          Class<? extends Block> blockClass = (Class<? extends Block>)Class.forName(className);
                          Constructor<? extends Block> constructor = blockClass.getConstructor(String.class);
                          return constructor.newInstance(blockContent);

                      }
                      catch (Exception e) {
                          System.out.println("<FAIL TO HANDLE>\n" + contentStartingWithBlockClass);
                          e.printStackTrace();
                          return null;
                      }
                  })
                  .forEach(noteBook::add);

            return noteBook;

        }
        catch (Exception e) {
            e.printStackTrace();
            return new NoteBook();
        }
    }

    public boolean isNotEmpty() {
        return !content.isEmpty();
    }

    public void add(Block block) {
        if (block == null) {
            return;
        }
        content.add(block);
        block.getPersistenceService().init(runSession);
    }

    private static String separator(String separatorName) {
        return SEPARATOR_STRING + separatorName + "\n";
    }

    public Block nextOf(Block block) {
        int indexOf = content.indexOf(block);
        if (indexOf == content.size() - 1) {
            indexOf = 0;
        }
        else {
            indexOf++;
        }
        return content.get(indexOf);
    }

    public Block previousOf(Block block) {
        int indexOf = content.indexOf(block);
        if (indexOf != 0) {
            indexOf--;
        }
        else {
            indexOf = content.size() - 1;
        }
        return content.get(indexOf);
    }

}
