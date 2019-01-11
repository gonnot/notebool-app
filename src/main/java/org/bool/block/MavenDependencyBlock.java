package org.bool.block;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.shared.artifact.filter.resolve.ScopeFilter;
import org.apache.maven.shared.artifact.resolve.ArtifactResult;
import org.apache.maven.shared.dependencies.DefaultDependableCoordinate;
import org.apache.maven.shared.dependencies.resolve.DependencyResolverException;
import org.apache.maven.shared.dependencies.resolve.internal.DefaultDependencyResolver;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.bool.Configuration;
import org.bool.engine.RunSession;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.context.DefaultContext;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.impl.ArtifactResolver;
import org.sonatype.aether.impl.internal.DefaultArtifactResolver;
import org.sonatype.aether.impl.internal.DefaultRemoteRepositoryManager;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManager;
import org.sonatype.aether.util.DefaultRepositorySystemSession;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Tag("bool-dependency")
public class MavenDependencyBlock extends AbstractActionBlock {

    public MavenDependencyBlock() {
    }

    public MavenDependencyBlock(String content) {
        super(content);
    }

    @Override
    protected void evaluate(String input, Div outputComponent, Span evaluationCountComponent, Button runButton) {
        Component icon = runButton.getIcon();
        runButton.setIcon(new Image("/loading.gif", "loading..."));
        runSession
                .evaluate(downloadDependency(input, runSession))
                .whenComplete(inUiAccess(displayDownloadResult(outputComponent, evaluationCountComponent)))
                .whenComplete(inUiAccess((result, throwable) -> runButton.setIcon(icon)))
        ;
    }

    @Override
    protected Collection<String> completion(String input) {
        return Collections.emptyList();
    }

    private static Callable<String> downloadDependency(String input, RunSession runSession) {
        return () -> {
            Iterable<ArtifactResult> results = new MavenDependencyDownloader(Configuration.REPOSITORY_PATH).download(input.trim());
            String output = StreamSupport.stream(results.spliterator(), false)
                                         .map(result -> result.getArtifact().toString())
                                         .collect(Collectors.joining("\n"));
            for (ArtifactResult artifact : results) {
                runSession.addToClasspath(artifact.getArtifact().getFile().getAbsolutePath());
            }
            return output;
        };
    }

    private static BiConsumer<RunSession.Result, Throwable> displayDownloadResult(Div outputComponent, Span evaluationCountComponent) {
        return (result, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                Notification.show(throwable.getMessage());
                return;
            }
            outputComponent.setText(result.getOutput());
            evaluationCountComponent.setText("[ " + result.getCount() + " ]:");
        };
    }

    private BiConsumer<RunSession.Result, Throwable> inUiAccess(BiConsumer<RunSession.Result, Throwable> command) {
        return (result, throwable) -> getUI().ifPresent(ui -> ui.access(() -> { command.accept(result, throwable); }));
    }

    interface DependencyDownloader {
        Iterable<ArtifactResult> download(String dependency) throws PlexusContainerException, ContextException, DependencyResolverException;
    }

    static class MavenDependencyDownloader implements DependencyDownloader {

        private final String repositoryPath;

        MavenDependencyDownloader(String repositoryPath) {
            this.repositoryPath = repositoryPath;
        }

        @Override
        public Iterable<ArtifactResult> download(String dependency) throws PlexusContainerException, ContextException, DependencyResolverException {
            DefaultDependableCoordinate coordinate = extractCoordinate(dependency);

            ArtifactRepositoryPolicy downloadOnlyIfNeeded = new ArtifactRepositoryPolicy(true,
                                                                                         ArtifactRepositoryPolicy.UPDATE_POLICY_NEVER,
                                                                                         ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN);

            DefaultProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(new DefaultProjectBuildingRequest());
            DefaultRepositorySystemSession repositorySession = new DefaultRepositorySystemSession();
            repositorySession.setLocalRepositoryManager(new SimpleLocalRepositoryManager(repositoryPath));

            //noinspection unchecked
            repositorySession.setSystemProperties((Map)System.getProperties());
            buildingRequest.setRepositorySession(repositorySession);

            List<ArtifactRepository> repoList = new ArrayList<>();
            repoList.add(new MavenArtifactRepository("central",
                                                     "https://repo.maven.apache.org/maven2/",
                                                     new DefaultRepositoryLayout(),
                                                     downloadOnlyIfNeeded,
                                                     downloadOnlyIfNeeded));
            buildingRequest.setRemoteRepositories(repoList);

            PlexusContainer plexusContainer = new DefaultPlexusContainer();

            DefaultArtifactResolver artifactResolver = new DefaultArtifactResolver();
            DefaultRemoteRepositoryManager remoteRepositoryManager = new DefaultRemoteRepositoryManager();
            remoteRepositoryManager.addRepositoryConnectorFactory(new WagonRepositoryConnectorFactory());
            artifactResolver.setRemoteRepositoryManager(remoteRepositoryManager);
            artifactResolver.initService(new org.apache.maven.repository.internal.DefaultServiceLocator());
            plexusContainer.addComponent(artifactResolver, ArtifactResolver.class, "maven3");

            LightweightHttpWagon lightweightHttpWagon = new LightweightHttpWagon();
            plexusContainer.addComponent(lightweightHttpWagon, Wagon.class, "http");

            DefaultDependencyResolver dependencyResolver = new DefaultDependencyResolver();

            DefaultContext context = new DefaultContext();
            context.put(PlexusConstants.PLEXUS_KEY, plexusContainer);
            dependencyResolver.contextualize(context);

            return dependencyResolver.resolveDependencies(buildingRequest, coordinate, ScopeFilter.excluding("test"));
        }

        private DefaultDependableCoordinate extractCoordinate(String dependency) {
            String[] split = dependency.split(":");
            DefaultDependableCoordinate coordinate = new DefaultDependableCoordinate();
            coordinate.setGroupId(split[0]);
            coordinate.setArtifactId(split[1]);
            coordinate.setType(split[2]);
            coordinate.setVersion(split[3]);
            return coordinate;
        }
    }
}
