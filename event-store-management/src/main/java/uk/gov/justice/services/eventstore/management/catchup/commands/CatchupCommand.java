package uk.gov.justice.services.eventstore.management.catchup.commands;

import uk.gov.justice.services.jmx.command.BaseSystemCommand;

public class CatchupCommand extends BaseSystemCommand {

    public static final String CATCHUP = "CATCHUP";
    private static final String DESCRIPTION = "Catches up and republishes all Events since the last know event";

    public CatchupCommand() {
        super(CATCHUP, DESCRIPTION);
    }
}
