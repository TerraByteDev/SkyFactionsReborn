package net.skullian.skyfactions.common.util;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;

public class ErrorUtil {

    public static void handleError(Object sender, String operation, String debug, Throwable err) {
        err.printStackTrace();
        Messages.ERROR.send(sender, sender instanceof SkyUser ? SkyApi.getInstance().getPlayerAPI().getLocale(((SkyUser) sender).getUniqueId()) : Messages.getDefaulLocale(), "operation", operation, "debug", debug);
    }

    public static void handleDatabaseError(Exception error) {
        SLogger.fatal("----------------------- DATABASE EXCEPTION -----------------------");
        SLogger.fatal("There was an error while performing database actions:");
        SLogger.fatal(error.getMessage());
        SLogger.fatal("Please see https://docs.terrabytedev.com/skyfactions/errors-and-debugging for more information.");
        SLogger.fatal("Please contact the devs.");
        SLogger.fatal("----------------------- DATABASE EXCEPTION -----------------------");
    }

    public static void handleDatabaseSetupError(Exception error) {
        SLogger.setup("----------------- DATABASE EXCEPTION -----------------", true);
        SLogger.setup("There was an error while initialising the database.", true);
        SLogger.setup(error.getMessage(), true);
        SLogger.setup("Please see https://docs.terrabytedev.com/skyfactions/errors-and-debugging for more information.", true);
        SLogger.setup("Please contact the devs - SkyFactions will now disable.", true);
        SLogger.setup("----------------- DATABASE EXCEPTION -----------------", true);
        SkyApi.disablePlugin();
    }

}
