package org.bool.block;

import com.bisam.vaadin.uispec.VPanel;
import org.apache.maven.shared.artifact.resolve.ArtifactResult;
import org.bool.block.MavenDependencyBlock.MavenDependencyDownloader;
import org.bool.engine.RunSession;
import org.bool.uispec4j.VDiv;
import org.bool.uispec4j.VSpan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bool.Configuration.REPOSITORY_PATH;

class MavenDependencyBlockTest {

    @Test
    @DisplayName("set joda-time and download")
    void set_jodatime_and_download() {
        RunSession runSession = new RunSession();
        MavenDependencyBlock dependencyBlock = new MavenDependencyBlock();
        dependencyBlock.getPersistenceService().init(runSession);

        VPanel<MavenDependencyBlock> panel = new VPanel<>(dependencyBlock);

        panel.getTextBox().setText("    joda-time:joda-time:2.10:jar\n\n\n");

        panel.getButton().click();

        VDiv resultDiv = VDiv.get(panel, "outputText");

        String text = resultDiv.getVaadinComponent().getText();
        assertThat(text).isEqualTo("joda-time:joda-time:jar:2.10\n" +
                                   "org.joda:joda-convert:jar:1.2");

        assertThat(runSession.evaluate("new org.joda.time.DateTime(0).getYear()").getOutput())
                .containsIgnoringCase("1970");

        VSpan evaluationCount = VSpan.get(panel, "evaluationCount");
        assertThat(evaluationCount.getVaadinComponent().getText().trim()).isEqualTo("[ 1 ]:");
    }

    @Test
    @DisplayName("load")
    void load() {
        RunSession runSession = new RunSession();
        MavenDependencyBlock dependencyBlock = new MavenDependencyBlock("joda-time:joda-time:2.10:jar");
        dependencyBlock.getPersistenceService().init(runSession);

        VPanel<MavenDependencyBlock> panel = new VPanel<>(dependencyBlock);

        assertThat(panel.getTextBox().getText()).isEqualTo("joda-time:joda-time:2.10:jar");
    }

    @Test
    @DisplayName("download dependency by code")
    void download_dependency_by_code() throws Exception {
        MavenDependencyDownloader downloader = new MavenDependencyDownloader(REPOSITORY_PATH);

        Iterable<ArtifactResult> results = downloader.download("joda-time:joda-time:2.10:jar");

        String resultAsString = StreamSupport.stream(results.spliterator(), false)
                                             .map(result -> result.getArtifact().toString())
                                             .collect(Collectors.joining("\n"));

        assertThat(resultAsString)
                .isEqualTo("joda-time:joda-time:jar:2.10\n" +
                           "org.joda:joda-convert:jar:1.2");

    }

}