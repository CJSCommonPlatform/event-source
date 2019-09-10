package uk.gov.justice.services.eventsourcing.repository.jdbc.event;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MissingEventNumberExceptionTest {

    @Test
    public void shouldCreateMissingEventNumberException() {
        final MissingEventNumberException missingEventNumberException = new MissingEventNumberException("message");

        assertThat(missingEventNumberException.getMessage(), is("message"));
        assertThat(missingEventNumberException, instanceOf(RuntimeException.class));
    }
}