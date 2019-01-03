package org.bool.engine;

import com.vaadin.flow.component.notification.Notification;
import org.bool.Configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class NoteBook {
    static final String SEPARATOR_STRING = "################################################";
    private final List<Block> content = new ArrayList<>();
    private final RunSession runSession;

    public NoteBook() {
        this.runSession = new RunSession(RunSession.ExecutionMode.ASYNC);
    }

    public NoteBook(Block... blocks) {
        this();
        Stream.of(blocks).forEach(this::add);
    }

    public void forEach(Consumer<? super Block> action) {
        blocks().forEach(action);
    }

    public Stream<Block> blocks() {
        return content.stream();
    }

    public void forEachIndexed(BiConsumer<Integer, ? super Block> action) {
        for (int i = 0; i < content.size(); i++) {
            Block block = content.get(i);
            action.accept(i, block);
        }
    }

    public void saveInStore(final String fileName) {
        save(new File(Configuration.JUPIFILE_PATH + fileName).toPath());
    }

    void save(Path filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath.toFile())) {
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

    public static NoteBook loadFromStore(final String fileName) {
        return load(Paths.get(Configuration.JUPIFILE_PATH + fileName));
    }

    static NoteBook load(Path notebookFilePath) {
        try {
            String content = new String(Files.readAllBytes(notebookFilePath));
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
                          String blockContent = contentStartingWithBlockClass.substring(endIndex).trim();

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

    public Block getFirstBlock() {
        if (content.isEmpty()) {
            return null;
        }
        return content.get(0);
    }

    public Integer indexOf(Block block) {
        return content.indexOf(block);
    }

    public void move(Block block, Integer nextIndex) {
        content.remove(block);
        content.add(nextIndex, block);
    }
}
