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
        void emptyNotebook() {
            assertFalse(noteBook.isNotEmpty());
        }
    }

    @Nested
    @DisplayName("Block cycles")
    class BlockCycles {
        private final NoteBook noteBook = new NoteBook();
        private final DummyBlock first = new DummyBlock("First Block");
        private final DummyBlock second = new DummyBlock("Second Block");

        @Test
        @DisplayName("Next block is the same")
        void nextBlockIsTheSame() {
            noteBook.add(first);
            assertThat(noteBook.nextOf(first)).isSameAs(first);
        }

        @Test
        @DisplayName("Next block")
        void nextBlock() {
            noteBook.add(first);
            noteBook.add(second);
            assertThat(noteBook.nextOf(first)).isSameAs(second);
        }

        @Test
        @DisplayName("Next block of last block is first")
        void nextBlockOfLastBlockIsFirst() {
            noteBook.add(first);
            noteBook.add(second);
            assertThat(noteBook.nextOf(second)).isSameAs(first);
        }

        @Test
        @DisplayName("Previous block is the same")
        void previousBlockIsTheSame() {
            noteBook.add(first);
            assertThat(noteBook.previousOf(first)).isSameAs(first);
        }

        @Test
        @DisplayName("Previous block")
        void previousBlock() {
            noteBook.add(first);
            noteBook.add(second);
            assertThat(noteBook.previousOf(second)).isSameAs(first);
        }

        @Test
        @DisplayName("Previous block of first block is last")
        void previousBlockOfFirstBlockIsLast() {
            noteBook.add(first);
            noteBook.add(second);
            assertThat(noteBook.previousOf(first)).isSameAs(second);
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

            assertContent(noteBook,
                          "DummyBlock(dummy block content)");
        }

        @Test
        @DisplayName("Can load a notebook previously saved")
        void canLoadANotebookPreviouslySaved(@TempDirectory.TempDir Path tempDir) {
            NoteBook previousNoteBook = new NoteBook();
            previousNoteBook.add(new DummyBlock("block content"));
            Path filePath = tempDir.resolve("notebook.jupiler");
            previousNoteBook.save(filePath);

            NoteBook noteBook = NoteBook.load(filePath);

            assertContent(noteBook,
                          "DummyBlock(block content)");
        }

        private void assertContent(NoteBook noteBook, String notebookExpectedContent) {
            StringBuilder builder = new StringBuilder();
            noteBook.forEach(block -> blockToString(builder, block));
            assertThat(builder.toString())
                    .isEqualTo(notebookExpectedContent);
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