package com.yh.sarl.launcher

import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.yh.sarl.IActResultRegistryProvider
import com.yh.sarl.internal.actResultRegistryProvider

class SimpleLauncher<I, O>(
    private val activityResultContract: ActivityResultContract<I, O>,
    provider: IActResultRegistryProvider
) : BaseLauncher<I, O>(provider) {

    @Suppress("unused")
    companion object {
        @JvmStatic
        fun <I, O> LifecycleOwner.simpleLauncher(
            contractType: ContractType,
            fragment: Fragment
        ): SimpleLauncher<I, O> {
            return simpleLauncher(fragment.actResultRegistryProvider(), contractType.cast())
        }

        @JvmStatic
        fun <I, O> LifecycleOwner.simpleLauncher(
            contractType: ContractType,
            activity: FragmentActivity
        ): SimpleLauncher<I, O> {
            return simpleLauncher(activity.actResultRegistryProvider(), contractType.cast())
        }

        @JvmStatic
        fun <I, O> LifecycleOwner.simpleLauncher(
            contract: ActivityResultContract<I, O>,
            fragment: Fragment
        ): SimpleLauncher<I, O> {
            return simpleLauncher(fragment.actResultRegistryProvider(), contract)
        }

        @JvmStatic
        fun <I, O> LifecycleOwner.simpleLauncher(
            contract: ActivityResultContract<I, O>,
            activity: FragmentActivity
        ): SimpleLauncher<I, O> {
            return simpleLauncher(activity.actResultRegistryProvider(), contract)
        }

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <I, O> LifecycleOwner.simpleLauncher(
            provider: IActResultRegistryProvider,
            contract: ActivityResultContract<I, O>,
        ): SimpleLauncher<I, O> {
            val launcher = SimpleLauncher::class.java.getConstructor(
                ActivityResultContract::class.java,
                IActResultRegistryProvider::class.java
            ).newInstance(contract, provider)
            lifecycle.addObserver(launcher)
            return launcher as SimpleLauncher<I, O>
        }
    }

    override fun createContract(): ActivityResultContract<I, O> = activityResultContract
}

@Suppress("unused")
enum class ContractType(private val contract: () -> ActivityResultContract<*, *>) {
    StartActivityForResult({ ActivityResultContracts.StartActivityForResult() }),
    StartIntentSenderForResult({ ActivityResultContracts.StartIntentSenderForResult() }),
    RequestMultiplePermissions({ ActivityResultContracts.RequestMultiplePermissions() }),
    RequestPermission({ ActivityResultContracts.RequestPermission() }),
    TakePicturePreview({ ActivityResultContracts.TakePicturePreview() }),
    TakePicture({ ActivityResultContracts.TakePicture() }),
    TakeVideo({ ActivityResultContracts.TakeVideo() }),
    PickContact({ ActivityResultContracts.PickContact() }),
    GetContent({ ActivityResultContracts.GetContent() }),
    GetMultipleContents({ ActivityResultContracts.GetMultipleContents() }),
    OpenDocument({ ActivityResultContracts.OpenDocument() }),
    OpenMultipleDocuments({ ActivityResultContracts.OpenMultipleDocuments() }),
    OpenDocumentTree({ ActivityResultContracts.OpenDocumentTree() }),
    CreateDocument({ ActivityResultContracts.CreateDocument() });

    fun <I, O> cast(): ActivityResultContract<I, O> {
        @Suppress("UNCHECKED_CAST")
        return contract.invoke() as ActivityResultContract<I, O>
    }
}
