package com.yh.sarl

class LauncherResult<T> private constructor() {
    companion object {
        fun <T> waiting() = LauncherResult<T>()
    }

    @JvmField
    @PublishedApi
    internal var internalValue: Any? = null
    private fun update(any: Any?) {
        this.internalValue = any
        if (isWaiting) {
            return
        }
        if (isSuccess) {
            successAction?.onAction(value)
        } else {
            failureAction?.onAction(exception)
            if (autoPrintException) {
                exception.printStackTrace()
            }
        }
    }

    @PublishedApi
    internal var successAction: Action<T>? = null

    @PublishedApi
    internal var failureAction: Action<Throwable>? = null

    @PublishedApi
    internal var isWaiting = true

    @PublishedApi
    internal val isSuccess
        get() = !isWaiting && internalValue !is Failure

    @PublishedApi
    internal val isFailure
        get() = !isWaiting && internalValue is Failure

    @PublishedApi
    internal val value
        get() = internalValue as T

    @PublishedApi
    internal val exception
        get() = (internalValue as Failure).exception

    private var autoPrintException = true

    override fun toString(): String =
        when (internalValue) {
            isWaiting -> "Waiting..."
            is Failure -> internalValue.toString() // "Failure($exception)"
            else -> "Success($internalValue)"
        }

    data class Failure(
        @JvmField
        val exception: Throwable
    )

    internal interface Action<T> {
        fun onAction(value: T)
    }

    fun disableAutoPrintException(): LauncherResult<T> {
        autoPrintException = false
        return this
    }

    fun reset(): LauncherResult<T> {
        isWaiting = true
        autoPrintException = true
        update(null)
        return this
    }

    fun success(value: T?) {
        isWaiting = false
        update(value)
    }

    fun fail(exception: Throwable) {
        isWaiting = false
        update(Failure(exception))
    }

    fun fail(message: String) {
        isWaiting = false
        update(Failure(RuntimeException(message)))
    }

}

inline fun <T> LauncherResult<T>.onFailure(crossinline action: (exception: Throwable) -> Unit): LauncherResult<T> {
    if (isSuccess) {
        return this
    }
    if (isFailure) {
        exception.let(action)
        return this
    }
    failureAction = object : LauncherResult.Action<Throwable> {
        override fun onAction(value: Throwable) {
            value.let(action)
        }
    }
    return this
}

inline fun <T> LauncherResult<T>.onSuccess(crossinline action: (value: T) -> Unit): LauncherResult<T> {
    if (isFailure) {
        return this
    }
    if (isSuccess) {
        value.let(action)
        return this
    }
    successAction = object : LauncherResult.Action<T> {
        override fun onAction(value: T) {
            value.let(action)
        }
    }
    return this
}
