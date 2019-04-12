package uk.gov.justice.services.eventsourcing.source.core;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.justice.subscription.domain.builders.EventSourceDefinitionBuilder.eventSourceDefinition;

import uk.gov.justice.services.cdi.QualifierAnnotationExtractor;
import uk.gov.justice.services.eventsourcing.source.core.annotation.EventSourceName;
import uk.gov.justice.subscription.domain.eventsource.EventSourceDefinition;
import uk.gov.justice.subscription.domain.eventsource.Location;
import uk.gov.justice.subscription.registry.EventSourceDefinitionRegistry;

import java.util.Optional;

import javax.enterprise.inject.CreationException;
import javax.enterprise.inject.spi.InjectionPoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PublishedEventSourceProducerTest {

    @Mock
    private EventSourceDefinitionRegistry eventSourceDefinitionRegistry;

    @Mock
    private JdbcPublishedEventSourceFactory jdbcPublishedEventSourceFactory;

    @Mock
    private QualifierAnnotationExtractor qualifierAnnotationExtractor;

    @InjectMocks
    private PublishedEventSourceProducer publishedEventSourceProducer;

    @Test
    public void shouldCreateDefaultEventSourceDefinitionWhenEventSourceNameIsEmpty() throws Exception {
        final EventSourceDefinition eventSourceDefinition = eventSourceDefinition()
                .withName("defaultEventSource")
                .withDefault(true)
                .withLocation(new Location("", "", Optional.of("dataSource")))
                .build();
        final InjectionPoint injectionPoint = mock(InjectionPoint.class);
        final EventSourceName eventSourceNameAnnotation = mock(EventSourceName.class);
        final JdbcBasedPublishedEventSource jdbcBasedPublishedEventSource = mock(JdbcBasedPublishedEventSource.class);

        when(qualifierAnnotationExtractor.getFrom(injectionPoint, EventSourceName.class)).thenReturn(eventSourceNameAnnotation);
        when(eventSourceNameAnnotation.value()).thenReturn("");
        when(eventSourceDefinitionRegistry.getDefaultEventSourceDefinition()).thenReturn(eventSourceDefinition);
        when(jdbcPublishedEventSourceFactory.create(eventSourceDefinition.getLocation().getDataSource().get())).thenReturn(jdbcBasedPublishedEventSource);

        assertThat(publishedEventSourceProducer.publishedEventSource(), is(jdbcBasedPublishedEventSource));
    }

    @Test
    public void shouldCreateDefaultEventSourceWhenNoEventSourceNameQualifierSet() throws Exception {

        final EventSourceDefinition eventSourceDefinition = eventSourceDefinition()
                .withName("defaultEventSource")
                .withDefault(true)
                .withLocation(new Location("", "", Optional.of("dataSource")))
                .build();

        final JdbcBasedPublishedEventSource jdbcBasedPublishedEventSource = mock(JdbcBasedPublishedEventSource.class);

        when(eventSourceDefinitionRegistry.getDefaultEventSourceDefinition()).thenReturn(eventSourceDefinition);
        when(jdbcPublishedEventSourceFactory.create(eventSourceDefinition.getLocation().getDataSource().get())).thenReturn(jdbcBasedPublishedEventSource);

        assertThat(publishedEventSourceProducer.publishedEventSource(), is(jdbcBasedPublishedEventSource));
    }

    @Test
    public void shouldCreateAnEventSourceUsingTheEventSourceNameAnnotation() throws Exception {

        final String eventSourceName = "eventSourceName";
        final String dataSource = "my-data-source";

        final InjectionPoint injectionPoint = mock(InjectionPoint.class);
        final EventSourceName eventSourceNameAnnotation = mock(EventSourceName.class);
        final EventSourceDefinition eventSourceDefinition = mock(EventSourceDefinition.class);
        final Location location = mock(Location.class);
        final JdbcBasedPublishedEventSource jdbcBasedPublishedEventSource = mock(JdbcBasedPublishedEventSource.class);

        when(qualifierAnnotationExtractor.getFrom(injectionPoint, EventSourceName.class)).thenReturn(eventSourceNameAnnotation);
        when(eventSourceNameAnnotation.value()).thenReturn(eventSourceName);
        when(eventSourceDefinitionRegistry.getEventSourceDefinitionFor(eventSourceName)).thenReturn(Optional.of(eventSourceDefinition));
        when(eventSourceDefinition.getLocation()).thenReturn(location);
        when(location.getDataSource()).thenReturn(of(dataSource));
        when(jdbcPublishedEventSourceFactory.create(dataSource)).thenReturn(jdbcBasedPublishedEventSource);

        assertThat(publishedEventSourceProducer.publishedEventSource(injectionPoint), is(jdbcBasedPublishedEventSource));
    }

    @Test
    public void shouldFailIfNoEventSourceFoundInTheEventSourceRegistry() throws Exception {

        final String eventSourceName = "my-event-source";

        final InjectionPoint injectionPoint = mock(InjectionPoint.class);
        final EventSourceName eventSourceNameAnnotation = mock(EventSourceName.class);

        when(qualifierAnnotationExtractor.getFrom(injectionPoint, EventSourceName.class)).thenReturn(eventSourceNameAnnotation);
        when(eventSourceNameAnnotation.value()).thenReturn(eventSourceName);
        when(eventSourceDefinitionRegistry.getEventSourceDefinitionFor(eventSourceName)).thenReturn(empty());

        try {
            publishedEventSourceProducer.publishedEventSource(injectionPoint);
            fail();
        } catch (final CreationException expected) {
            assertThat(expected.getMessage(), is("Failed to find EventSource named 'my-event-source' in event-sources.yaml"));
        }

        verifyZeroInteractions(jdbcPublishedEventSourceFactory);
    }

    @Test
    public void shouldFailIfNoDataSourceNameFoundInEventSourcesYaml() throws Exception {

        final String eventSourceName = "eventSourceName";
        final String dataSourceName = "my-data-source";

        final InjectionPoint injectionPoint = mock(InjectionPoint.class);
        final EventSourceName eventSourceNameAnnotation = mock(EventSourceName.class);
        final EventSourceDefinition eventSourceDefinition = mock(EventSourceDefinition.class);
        final Location location = mock(Location.class);

        when(qualifierAnnotationExtractor.getFrom(injectionPoint, EventSourceName.class)).thenReturn(eventSourceNameAnnotation);
        when(eventSourceNameAnnotation.value()).thenReturn(eventSourceName);
        when(eventSourceDefinitionRegistry.getEventSourceDefinitionFor(eventSourceName)).thenReturn(Optional.of(eventSourceDefinition));
        when(eventSourceDefinition.getLocation()).thenReturn(location);
        when(location.getDataSource()).thenReturn(empty());
        when(eventSourceDefinition.getName()).thenReturn(dataSourceName);

        try {
            publishedEventSourceProducer.publishedEventSource(injectionPoint);
            fail();
        } catch (final CreationException expected) {
            assertThat(expected.getMessage(), is("No DataSource specified for EventSource 'my-data-source' specified in event-sources.yaml"));
        }

        verifyZeroInteractions(jdbcPublishedEventSourceFactory);
    }
}