package org.bool.block;

import org.bool.engine.NoteBook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MavenDependencyBlockTest {
    private final NoteBook noteBook = new NoteBook();

    @Test
    @DisplayName("s")
    void s() {
        noteBook.add(new MavenDependencyBlock());
    }
}