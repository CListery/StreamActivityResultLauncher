package com.yh.sarl

import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

interface IActResultRegistryProvider {
    fun provider(): ActivityResultRegistry
}
