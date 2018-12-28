package uk.gov.justice.services.event.sourcing.subscription.startup;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.subscription.domain.subscriptiondescriptor.Subscription;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.SubscriptionsDescriptor;
import uk.gov.justice.subscription.registry.SubscriptionsDescriptorsRegistry;

import java.util.Set;

import javax.enterprise.concurrent.ManagedExecutorService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class EventCatchupStartUpBeanTest {

    @Mock
    private EventCatchupProcessorBean eventCatchupProcessorBean;

    @Mock
    private SubscriptionsDescriptorsRegistry subscriptionsDescriptorsRegistry;

    @Mock
    private ManagedExecutorService managedExecutorService;

    @Mock
    private Logger logger;

    @InjectMocks
    private EventCatchupStartUpBean eventCatchupStartUpBean;

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformEventCatchupForAllSubscriptions() {

        final String componentName_1 = "componentName_1";
        final String componentName_2 = "componentName_2";

        final SubscriptionsDescriptor subscriptionsDescriptor_1 = mock(SubscriptionsDescriptor.class);
        final SubscriptionsDescriptor subscriptionsDescriptor_2 = mock(SubscriptionsDescriptor.class);

        final Subscription subscription_1_1 = mock(Subscription.class);
        final Subscription subscription_1_2 = mock(Subscription.class);
        final Subscription subscription_2_1 = mock(Subscription.class);
        final Subscription subscription_2_2 = mock(Subscription.class);

        final Set<SubscriptionsDescriptor> subscriptionsDescriptors = newHashSet(subscriptionsDescriptor_1, subscriptionsDescriptor_2);

        when(subscriptionsDescriptorsRegistry.getAll()).thenReturn(subscriptionsDescriptors);

        when(subscriptionsDescriptor_1.getServiceComponent()).thenReturn(componentName_1);
        when(subscriptionsDescriptor_2.getServiceComponent()).thenReturn(componentName_2);

        when(subscriptionsDescriptor_1.getSubscriptions()).thenReturn(asList(subscription_1_1, subscription_1_2));
        when(subscriptionsDescriptor_2.getSubscriptions()).thenReturn(asList(subscription_2_1, subscription_2_2));

        eventCatchupStartUpBean.start();

        verify(managedExecutorService).execute(new EventCatchupTask(componentName_1, subscription_1_1, eventCatchupProcessorBean));
        verify(managedExecutorService).execute(new EventCatchupTask(componentName_1, subscription_1_2, eventCatchupProcessorBean));
        verify(managedExecutorService).execute(new EventCatchupTask(componentName_2, subscription_2_1, eventCatchupProcessorBean));
        verify(managedExecutorService).execute(new EventCatchupTask(componentName_2, subscription_2_2, eventCatchupProcessorBean));

    }
}
