package net.skullian.skyfactions.command.gems;

import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

@Command("gems")
public final class GemsCommand {

    @Command("command <required>")
    public void yourCommand(
            @Argument(value = "required", description = "A string") String string // Uses a name override!
    ) {

    }
}
