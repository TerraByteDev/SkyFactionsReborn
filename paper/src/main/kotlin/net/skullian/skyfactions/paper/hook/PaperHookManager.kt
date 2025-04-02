package net.skullian.skyfactions.paper.hook

import net.skullian.skyfactions.common.hook.HookManager

/**
 * The paper implementation of [HookManager].
 * As discussed before, [getHookLocation] must be implemented for each platform,
 * as each platform will have different "hooks".
 */
class PaperHookManager : HookManager {

    override fun getHookLocation(): String {
        return "net.skullian.skyfactions.paper.hook.hooks"
    }

}