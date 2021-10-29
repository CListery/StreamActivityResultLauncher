package com.yh.sarl.internal

import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.yh.sarl.IActResultRegistryProvider

@PublishedApi
internal fun Fragment.actResultRegistryProvider(): IActResultRegistryProvider {
    return object : IActResultRegistryProvider {
        override fun provider(): ActivityResultRegistry {
            return (host as? ActivityResultRegistryOwner)?.activityResultRegistry
                ?: requireActivity().activityResultRegistry
        }
    }
}

@PublishedApi
internal fun FragmentActivity.actResultRegistryProvider(): IActResultRegistryProvider {
    return object : IActResultRegistryProvider {
        override fun provider(): ActivityResultRegistry {
            return activityResultRegistry
        }
    }
}
