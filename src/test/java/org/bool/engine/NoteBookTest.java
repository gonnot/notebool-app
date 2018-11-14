package org.bool.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        @DisplayName("Can load a notebook")
        void canLoadANotebook(@TempDirectory.TempDir Path tempDir) throws IOException {
            Path filePath = saveFile(tempDir,
                                     "################################################" + DummyBlock.class.getName() + "\n" +
                                     "Some Text\n" +
                                     "\n" +
                                     "################################################END\n");

            NoteBook noteBook = NoteBook.load(filePath);

            assertTrue(noteBook.isNotEmpty());
        }

        private Path saveFile(@TempDirectory.TempDir Path tempDir, String content) throws IOException {
            Path filePath = tempDir.resolve("my-file.txt");
            Files.write(filePath,
                        content.getBytes());
            return filePath;
        }
    }
}