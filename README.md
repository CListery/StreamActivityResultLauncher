# SARL

[中文文档](./doc/README-zh-CN.md)

1. Simplify the use of registerForActivityResult.
2. The user will no longer need to manage the call of registerForActivityResult in a specific life cycle.
3. Support streaming call to avoid callback hell.
4. In order to develop quickly, a SimpleLauncher is built-in. If it does not meet the requirements, please extend BaseLauncher.
5. For more functions, please refer to the demo.

## Dependencies

### Gradle

```kotlin
implementation("io.github.clistery:stream-arl:1.0.0")
```

## USEG

### BaseLauncher

```kotlin
val permissionsLauncher: PermissionsLauncher = bindLauncher(this)
```

### SimpleLauncher

```kotlin
val openMediaLauncher: SimpleLauncher<Array<String>, Uri> = simpleLauncher(ContractType.OpenDocument, this)
```

### Streaming call

```kotlin
permissionsLauncher
    .input(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))
    .next(openMediaLauncher)
    .input(arrayOf("image/*"))
    .checker { null != it }
    .launch()
    .onSuccess {
        setResultTxt(it.toString())
    }.onFailure {
        setResultTxt(it.stackTraceToString())
    }
```
