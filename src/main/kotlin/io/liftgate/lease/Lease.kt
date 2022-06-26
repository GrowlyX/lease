package io.liftgate.lease

import java.util.concurrent.Executor
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author GrowlyX
 * @since 6/26/2022
 */
class Lease<T>(
    private val compute: () -> T,
    private val strategy: LeaseStrategy,
    private val executor: Executor,
    val dependencies: List<Any>
) : ReadOnlyProperty<Any?, T?>
{
    private var value: T? = null
    private var valueExpired: T? = null

    init
    {
        LeaseDependency.leases.add(this)

        if (this.strategy == LeaseStrategy.Eager)
        {
            runCatching {
                this.compute()
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun invalidate()
    {
        this.valueExpired = this.value
        this.value = null
    }

    private fun compute() =
        this.compute.invoke()
            .apply {
                value = this
            }

    private fun asyncCompute()
    {
        executor
            .execute {
                compute()
            }
    }

    private fun value(): T?
    {
        if (this.value == null)
        {
            return when (strategy)
            {
                LeaseStrategy.Compute, LeaseStrategy.Eager -> this.compute()
                LeaseStrategy.Expired ->
                {
                    this.asyncCompute()
                    this.valueExpired
                }
            }
        }

        return this.value
    }

    override fun getValue(
        thisRef: Any?, property: KProperty<*>
    ) = this.value()
}
