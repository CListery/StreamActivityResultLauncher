package com.yh.sarl

import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun Fragment.actResultRegistryProvider(): IActResultRegistryProvider {
    return object : IActResultRegistryProvider {
        override fun provider(): ActivityResultRegistry {
            return (host as? ActivityResultRegistryOwner)?.activityResultRegistry
                ?: requireActivity().activityResultRegistry
        }
    }
}

fun FragmentActivity.actResultRegistryProvider(): IActResultRegistryProvider {
    return object : IActResultRegistryProvider {
        override fun provider(): ActivityResultRegistry {
            return activityResultRegistry
        }
    }
}
