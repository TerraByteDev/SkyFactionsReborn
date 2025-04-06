package net.skullian.skyfactions.common.libraries.flavor.mappings

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import net.skullian.skyfactions.api.library.flavor.service.Close
import net.skullian.skyfactions.api.library.flavor.service.Configure
import net.skullian.skyfactions.common.libraries.flavor.inject.Extract
import net.skullian.skyfactions.common.libraries.flavor.inject.Inject
import net.skullian.skyfactions.common.libraries.flavor.inject.condition.Named

/**
 * @author GrowlyX
 * @since 9/14/2022
 */
object AnnotationMappings
{
    private val mappings = mutableMapOf(
        AnnotationType.Inject to listOf(
            javax.inject.Inject::class.java,
            jakarta.inject.Inject::class.java,
            Inject::class.java
        ),
        AnnotationType.Named to listOf(
            javax.inject.Named::class.java,
            jakarta.inject.Named::class.java,
            Named::class.java
        ),
        AnnotationType.Extract to listOf(
            Extract::class.java
        ),
        AnnotationType.PostConstruct to listOf(
            Configure::class.java,
            PostConstruct::class.java,
        ),
        AnnotationType.PreDestroy to listOf(
            Close::class.java,
            PreDestroy::class.java,
        )
    )

    /**
     * Checks if any of the provided annotations match the specified annotation type.
     *
     * @param type the annotation type to check against
     * @param annotations the array of annotations to check
     * @return `true` if any annotation matches the specified type, `false` otherwise
     */
    fun matchesAny(
        type: AnnotationType,
        annotations: Array<Annotation>
    ): Boolean
    {
        val mapping = this.mappings[type]!!
        return annotations.any { it.javaClass in mapping }
    }
}