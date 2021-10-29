package com.yh.sarl.launcher

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.yh.sarl.IActResultRegistryProvider
import com.yh.sarl.IResultChecker
import com.yh.sarl.LauncherResult
import com.yh.sarl.internal.actResultRegistryProvider
import com.yh.sarl.throwable.ResultCheckFailException

abstract class BaseLauncher<I, O>(
    private val provider: IActResultRegistryProvider
) : ActivityResultCallback<O>, DefaultLifecycleObserver {

    protected lateinit var registry: ActivityResultRegistry
    protected lateinit var contract: ActivityResultContract<I, O>

    protected lateinit var activityResultLauncher: ActivityResultLauncher<I>
    protected val launcherResult: LauncherResult<O> = LauncherResult.waiting()

    @Suppress("unused")
    companion object {
        @JvmStatic
        inline fun <I, O, reified Launcher : BaseLauncher<I, O>> LifecycleOwner.bindLauncher(
            fragment: Fragment
        ): Launcher {
            return bindLauncher(fragment.actResultRegistryProvider(), Launcher::class.java)
        }

        @JvmStatic
        inline fun <I, O, reified Launcher : BaseLauncher<I, O>> LifecycleOwner.bindLauncher(
            activity: FragmentActivity
        ): Launcher {
            return bindLauncher(activity.actResultRegistryProvider(), Launcher::class.java)
        }

        @JvmStatic
        fun <I, O, Launcher : BaseLauncher<I, O>> LifecycleOwner.bindLauncher(
            provider: IActResultRegistryProvider,
            launcherClazz: Class<Launcher>
        ): Launcher {
            val launcher = launcherClazz.getConstructor(IActResultRegistryProvider::class.java)
                .newInstance(provider)
            lifecycle.addObserver(launcher)
            return launcher
        }
    }

    protected var inputData: I? = null
    protected var actOptionsCompat: ActivityOptionsCompat? = null
    protected var resultChecker: IResultChecker<O>? = null

    // 上一个
    protected var prevLauncher: BaseLauncher<*, *>? = null

    // 下一个
    protected var nextLauncher: BaseLauncher<*, *>? = null

    override fun onCreate(owner: LifecycleOwner) {
        registry = provider.provider()
        contract = createContract()
        activityResultLauncher =
            registry.register(
                "${this::class.java.name}_${System.identityHashCode(this).toString(16)}",
                owner,
                contract,
                this
            )
    }

    abstract fun createContract(): ActivityResultContract<I, O>

    open fun input(input: I): BaseLauncher<I, O> {
        inputData = input
        return this
    }

    open fun options(options: ActivityOptionsCompat): BaseLauncher<I, O> {
        actOptionsCompat = options
        return this
    }

    open fun checker(checker: IResultChecker<O>): BaseLauncher<I, O> {
        resultChecker = checker
        return this
    }

    open fun <NI, NO> next(launcher: BaseLauncher<NI, NO>): BaseLauncher<NI, NO> {
        launcher.prevLauncher = this
        nextLauncher = launcher
        launcher.launcherResult.reset()
        launcherResult.reset()
        return launcher
    }

    open fun launch(): LauncherResult<O> {
        if (true == prevLauncher?.launcherResult?.isWaiting) {
            prevLauncher?.launch()
            return launcherResult
        }
        if (!launcherResult.isWaiting) {
            launcherResult.reset()
        }
        activityResultLauncher.runCatching {
            launch(inputData, actOptionsCompat)
        }.onFailure {
            dispatchFail(it)
        }
        return launcherResult
    }

    override fun onActivityResult(result: O?) {
        if (null == resultChecker) {
            dispatchSuccess(result)
        } else {
            if (true == resultChecker?.onCheck(result)) {
                dispatchSuccess(result)
            } else {
                dispatchFail(ResultCheckFailException(contract, inputData, result))
            }
        }
    }

    private fun dispatchSuccess(result: O?) {
        launcherResult.success(result)
        nextLauncher?.launch()
        cleanLauncher()
    }

    private fun dispatchFail(exception: Throwable) {
        var next: BaseLauncher<*, *>? = nextLauncher
        if (null == next) {
            launcherResult.fail(exception)
        } else {
            var lastLauncher: BaseLauncher<*, *>? = next
            while (null != lastLauncher?.nextLauncher) {
                next = lastLauncher.nextLauncher
                lastLauncher.cleanLauncher()
                lastLauncher = next
            }
            lastLauncher?.launcherResult?.fail(exception)
        }
        cleanLauncher()
    }

    private fun cleanLauncher() {
        prevLauncher = null
        nextLauncher = null
    }
}
