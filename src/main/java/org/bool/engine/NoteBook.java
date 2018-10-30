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

    public NoteBook() {
        this.runSession = new RunSession();
    }


    public NoteBook(Block... blocks) {
        this.runSession = new RunSession();
        Stream.of(blocks).forEach(this::add);
    }

    public void forEach(Consumer<? super Block> action) {
        content.forEach(action);
    }

    public void save(final String fileName) {
        try (FileWriter fileWriter = new FileWriter("D:\\project\\sideprojects\\notebool-app\\" + fileName)) {
            Optional<String> result = content.stream()
                    .map(block -> separator(block.getClass().getName())
                            + block.getContent())
                    .reduce((first, second) -> first + "\n" + second);
            if (result.isPresent()) {
                fileWriter.write(result.get());
            }
            fileWriter.write("\n");
            fileWriter.write(separator("END"));
        } catch (IOException e) {
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
                            Class<? extends Block> blockClass = (Class<? extends Block>) Class.forName(className);
                            Constructor<? extends Block> constructor = blockClass.getConstructor(String.class);
                            return constructor.newInstance(blockContent);

                        } catch (Exception e) {
                            System.out.println("<FAIL TO HANDLE>\n" + contentStartingWithBlockClass);
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .forEach(noteBook::add);

            return noteBook;

        } catch (Exception e) {
            e.printStackTrace();
            return new NoteBook();
        }
    }

    public boolean isNotEmpty() {
        return !content.isEmpty();
    }

    void add(Block block) {
        if (block == null) return;
        content.add(block);
        block.init(runSession);
    }

    private static String separator(String separatorName) {
        return SEPARATOR_STRING + separatorName + "\n";
    }

/*

    private static StringBuilder extract(List<Block> notebook, String line, StringBuilder builder) {
        if (line.startsWith(SEPARATOR_STRING)) {
            if (builder.length() > 0) {
                if (currentLine != null && currentLine.contains("org.bool.block.MarkDownBlock")) {
                    notebook.add(new MarkDownBlock(builder.toString()));
                } else if (currentLine != null && currentLine.contains("org.bool.block.RunCodeBlock")) {
                    JShell jShell = JShell.builder()
                            .out(System.out)
                            .err(System.err)
                            .build();
                    RunCodeBlock runCodeBlock = new RunCodeBlock();
                    notebook.add(runCodeBlock);
                }
                builder = new StringBuilder();
            } else {
                builder = new StringBuilder();
            }
            currentLine = line;
        } else {
            builder.append(line);
        }
        return builder;
    }
*/
}
