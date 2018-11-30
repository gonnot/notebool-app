package org.bool.engine;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.repository.internal.DefaultServiceLocator;
import org.apache.maven.shared.artifact.filter.resolve.ScopeFilter;
import org.apache.maven.shared.artifact.resolve.ArtifactResult;
import org.apache.maven.shared.dependencies.DefaultDependableCoordinate;
import org.apache.maven.shared.dependencies.resolve.internal.DefaultDependencyResolver;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.DefaultContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.impl.ArtifactResolver;
import org.sonatype.aether.impl.internal.DefaultArtifactResolver;
import org.sonatype.aether.impl.internal.DefaultRemoteRepositoryManager;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManager;
import org.sonatype.aether.util.DefaultRepositorySystemSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class RunSessionTest {

    @Test
    @DisplayName("gET ototo")
    void tauto() throws Exception {
        DefaultDependableCoordinate coordinate = new DefaultDependableCoordinate();
        coordinate.setGroupId("joda-time");
        coordinate.setArtifactId("joda-time");
        coordinate.setVersion("2.10");
//        coordinate.setVersion("2.9.4");
        coordinate.setType("jar");

        ArtifactRepositoryPolicy downloadOnlyIfNeeded = new ArtifactRepositoryPolicy(true,
                                                                                     ArtifactRepositoryPolicy.UPDATE_POLICY_NEVER,
                                                                                     ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN);

        DefaultProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(new DefaultProjectBuildingRequest());
        DefaultRepositorySystemSession repositorySession = new DefaultRepositorySystemSession();
        repositorySession.setLocalRepositoryManager(new SimpleLocalRepositoryManager("D:\\project\\sideprojects\\notebool-app\\fakerepo"));
//        repositorySession.setLocalRepositoryManager(new SimpleLocalRepositoryManager("D:\\cache\\maven\\repository"));

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
        artifactResolver.initService(new DefaultServiceLocator());
        plexusContainer.addComponent(artifactResolver, ArtifactResolver.class, "maven3");

        LightweightHttpWagon lightweightHttpWagon = new LightweightHttpWagon();
//        lightweightHttpWagon.setAuthenticator(new LightweightHttpWagonAuthenticator());
        plexusContainer.addComponent(lightweightHttpWagon, Wagon.class, "http");

        DefaultDependencyResolver dependencyResolver = new DefaultDependencyResolver();

        DefaultContext context = new DefaultContext();
        context.put(PlexusConstants.PLEXUS_KEY, plexusContainer);
        dependencyResolver.contextualize(context);

        Iterable<ArtifactResult> artifactResults = dependencyResolver.resolveDependencies(buildingRequest, coordinate, ScopeFilter.excluding("test"));

        for (ArtifactResult artifactResult : artifactResults) {
            System.out.println(">   " + artifactResult.getArtifact());
            System.out.println("    " + artifactResult.getArtifact().getScope());
            System.out.println("    " + artifactResult.getArtifact().getFile());
        }
    }

}