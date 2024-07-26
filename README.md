# CreateConfigurationConfig Bug

## Updating application's configuration

Changing application's context configuration with `Context#createConfigurationContext(Configuration)` causes the application ignore system dark mode updates

```
    override fun attachBaseContext(base: Context) {
        val updatedContext = context.createConfigurationContext(context.resources.configuration)
        super.attachBaseContext(updatedContext)
    }
```

Using the old deprecated way of updating the configuration via `Resources#updateConfiguration(Configuration, DisplayMetrics)` doesn't causes this issue.

```
    override fun attachBaseContext(context: Context) {
        context.resources.updateConfiguration(
            context.resources.configuration, 
            context.resources.displayMetrics
        )
        super.attachBaseContext(context)
    }
```
Any actual changes to the config are ommited from examples just for demonstration(behavior is the same).

## Hacky possible workaround

Let the activity handle configuration change for uiMode and in `onConfigurationChange` force local night mode based on combination of `localNightMode`, `getDefaultNightMode` and having a `boolean resource` in the night resource bucket.

One pointer might be that the `Configuration` in the `onConfigurationChange` has `uiMode` mask for the correct mode but UI is not recreated(`recreate()`)

# Previously reported bug

There is previous issue that was labeled as fixed in 2019 `appcompat v1.1.0` - [111345020](https://issuetracker.google.com/issues/111345020).

But the last comment in the issue mentions that the bug is still there, even in the `appcompat 1.7.0` which I am guessing could be the same case as this one(trying to update configuration).


# References

- [DayNight — Adding a dark theme to your app](https://medium.com/androiddevelopers/appcompat-v23-2-daynight-d10f90c83e94) - referencing that the `attachBaseContext` is the place to update the config since even before `onCreate` there is a lot of calls to resources
- [IssueTracker 111345020](https://issuetracker.google.com/issues/111345020).
