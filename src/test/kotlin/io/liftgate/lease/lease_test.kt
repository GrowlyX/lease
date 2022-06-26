package io.liftgate.lease

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
class LeaseTests
{
    private var iteration = -1

    private val lease by lease(
        dependencies = listOf(LeaseTests::class)
    ) {
        println("wow this is cool")
        "hey${++iteration}"
    }

    @Test
    fun `lease implementation by iteration`()
    {
        println(lease)

        LeaseDependency
            .invalidate(
                LeaseTests::class
            )

        println(lease)
    }
}
