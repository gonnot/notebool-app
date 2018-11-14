package org.bool.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class NoteBookTest {
    @Nested
    @DisplayName("Block management")
    class BlockManagement {
        private final NoteBook noteBook = new NoteBook();

        @Test
        @DisplayName("Empty notebook")
        void EmptyNotebook() {
            assertFalse(noteBook.isNotEmpty());
        }
    }

    @Nested
    @ExtendWith(TempDirectory.class)
    class Persistence {
        @Test
        @DisplayName("Can load a notebook with one single block")
        void canLoadANotebookWithOneSingleBlock(@TempDirectory.TempDir Path tempDir) throws IOException {
            Path filePath = createdFile(tempDir,
                                        new DummyBlock("dummy block content"));

            NoteBook noteBook = NoteBook.load(filePath);

            StringBuilder builder = new StringBuilder();
            noteBook.forEach(block -> blockToString(builder, block));
            assertThat(builder.toString())
                    .isEqualTo("DummyBlock(dummy block content)");
        }

        private Path createdFile(Path tempDir, Block... blocks) throws IOException {
            String content =
                    Stream.of(blocks)
                          .map(block -> NoteBook.SEPARATOR_STRING + block.getClass().getName() + "\n" +
                                        block.getPersistenceService().getContent() + "\n")
                          .collect(Collectors.joining("", "", NoteBook.SEPARATOR_STRING + "END"));

            Path filePath = tempDir.resolve("my-file.txt");
            Files.write(filePath,
                        content.getBytes());
            return filePath;
        }
    }

    private static StringBuilder blockToString(StringBuilder builder, Block block) {
        return builder.append(block.getClass().getSimpleName())
                      .append("(")
                      .append(block.getPersistenceService().getContent())
                      .append(")");
    }
}