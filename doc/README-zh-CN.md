# SARL

1. 简化对 registerForActivityResult 的使用.
2. 使用者将不再需要在特定的生命周期去管理 registerForActivityResult 的调用.
3. 支持流式调用，避免 callback hell.
4. 为了快速进行开发，内置了一个 SimpleLauncher，如果它不能满足需求，则请扩展 BaseLauncher.
5. 更多功能请参照 demo.

## 依赖

### Gradle

```kotlin
implementation("io.github.clistery:stream-arl:1.0.0")
```

## 使用

### BaseLauncher

```kotlin
val permissionsLauncher: PermissionsLauncher = bindLauncher(this)
```

### SimpleLauncher

```kotlin
val openMediaLauncher: SimpleLauncher<Array<String>, Uri> = simpleLauncher(ContractType.OpenDocument, this)
```

### 流式调用

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
