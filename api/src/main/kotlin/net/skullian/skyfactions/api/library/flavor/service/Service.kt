package net.skullian.skyfactions.api.library.flavor.service

/**
 * Marks a class as a flavor Service.
 *
 * If the [name] is blank, an identifier
 * will be automatically created.
 *
 * Services are sorted by their priority
 * when initialized.
 *
 * @author GrowlyX
 * @since 1/2/2022
 */
annotation class Service(
    val name: String = "",
    val priority: Int = 1
)