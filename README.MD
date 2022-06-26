# Liftgate Lease
A basic Kotlin implementation of [Facebook's](https://meta.com) memcache lease functionality.

## How it works
If the cache key is empty or has been invalidated recently, the first client to access the cache will be directed to grab this value from the database.

During the process, any future client (before the most recent value is set) will be directed to use a stale (or out-of-date) value for this key.

**For additional information on Facebook's implementation of this in memcache:**
 - [TimiLearning's Lecture Transcript](https://timilearning.com/posts/mit-6.824/lecture-16-memcache-at-facebook/#leases-and-stale-sets)
 - [QCon "Scaling Instagram Infrastructure" Talk](https://youtu.be/hnpzNAPiC0E?t=845)

## Lease Strategy
Liftgate's lease implemention allows you to choose multiple lease strategies.
1. **Expired**: Returns the outdated value as the newest value is computed asynchronously.
2. **Compute**: Computes the latest value if the current value is non-existent.
3. **Eager**: Does the computational logic above, while also eagerly computing on class creation.

## Lease Dependencies 
Liftgate's lease implementation has a dependency implemention that allows a user to easily invalidate dependant leases.

````kotlin
val lease by lease(
    dependencies = listOf(LeaseTests::class)
) {
    // your computational logic here
}

...
LeaseDependency
    .invalidate(
        LeaseTests::class
    )
````

## Lease Delegate
Liftgate's lease class implements Kotlin's [ReadOnlyProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.properties/-read-only-property/).

```kotlin
val lease by lease {
    // your computational logic here
}
```
