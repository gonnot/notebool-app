package org.bool.block;

import com.bisam.vaadin.uispec.UISpecAssert;
import com.bisam.vaadin.uispec.VButton;
import com.bisam.vaadin.uispec.VPanel;
import org.apache.maven.shared.artifact.resolve.ArtifactResult;
import org.bool.block.MavenDependencyBlock.MavenDependencyDownloader;
import org.bool.engine.RunSession;
import org.bool.uispec4j.UISpec4JUtil;
import org.bool.uispec4j.VDiv;
import org.bool.uispec4j.VSpan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bool.Configuration.REPOSITORY_PATH;

class MavenDependencyBlockTest {

    @Nested
    class DownloadTest {
        private final RunSession runSession = new RunSession();
        private final MavenDependencyBlock dependencyBlock = new MavenDependencyBlock();
        private final VPanel<MavenDependencyBlock> dependencyBlockPanel = new VPanel<>(dependencyBlock);

        @BeforeEach
        void setUp() {
            dependencyBlock.getPersistenceService().init(runSession);
        }

        @Test
        @DisplayName("set joda-time and download")
        void set_jodatime_and_download() {
            dependencyBlockPanel.getTextBox().setText("    joda-time:joda-time:jar:2.10\n\n\n");

            dependencyBlockPanel.getButton().click();
            UISpec4JUtil.forceFlushUiAccessCommands();

            VDiv resultDiv = VDiv.get(dependencyBlockPanel, "outputText");

            String text = resultDiv.getVaadinComponent().getText();
            assertThat(text).isEqualTo("joda-time:joda-time:jar:2.10\n" +
                                       "org.joda:joda-convert:jar:1.2");

            VSpan evaluationCountForDependency = VSpan.get(dependencyBlockPanel, "evaluationCount");
            assertThat(evaluationCountForDependency.getVaadinComponent().getText().trim()).isEqualTo("[ 1 ]:");

            assertThat(runSession.evaluate("new org.joda.time.DateTime(0).getYear()").getOutput())
                    .containsIgnoringCase("1970");
        }

        @Test
        @DisplayName("loading gif while downloading")
        void loading_gif_while_downloading() {
            dependencyBlockPanel.getTextBox().setText("joda-time:joda-time:jar:2.10");
            VButton runButton = dependencyBlockPanel.getButton();

            UISpecAssert.assertTrue(runButton.iconNameEquals("vaadin:step-forward"));
            runButton.click();

            UISpecAssert.assertTrue(runButton.iconNameEquals("loading..."));

            UISpec4JUtil.forceFlushUiAccessCommands();
            UISpecAssert.assertTrue(runButton.iconNameEquals("vaadin:step-forward"));
        }
    }

    @Test
    @DisplayName("load block from jupifile")
    void load_block_from_jupifile() {
        RunSession runSession = new RunSession();
        MavenDependencyBlock dependencyBlock = new MavenDependencyBlock("joda-time:joda-time:jar:2.10");
        dependencyBlock.getPersistenceService().init(runSession);

        VPanel<MavenDependencyBlock> panel = new VPanel<>(dependencyBlock);

        assertThat(panel.getTextBox().getText()).isEqualTo("joda-time:joda-time:jar:2.10");
    }

    @Test
    @DisplayName("download dependency by code")
    void download_dependency_by_code() throws Exception {
        MavenDependencyDownloader downloader = new MavenDependencyDownloader(REPOSITORY_PATH);

        Iterable<ArtifactResult> results = downloader.download("joda-time:joda-time:jar:2.10");

        String resultAsString = StreamSupport.stream(results.spliterator(), false)
                                             .map(result -> result.getArtifact().toString())
                                             .collect(Collectors.joining("\n"));

        assertThat(resultAsString)
                .isEqualTo("joda-time:joda-time:jar:2.10\n" +
                           "org.joda:joda-convert:jar:1.2");

    }

}