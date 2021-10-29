package com.yh.sarl.demo.launcher

import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.yh.sarl.IActResultRegistryProvider
import com.yh.sarl.launcher.BaseLauncher

class PermissionsLauncher(provider: IActResultRegistryProvider) :
    BaseLauncher<Array<String>, Map<String, Boolean>>(provider) {

    init {
        checker { permissionResults ->
            permissionResults?.all { it.value } ?: false
        }
    }

    override fun createContract(): ActivityResultContract<Array<String>, Map<String, Boolean>> {
        return ActivityResultContracts.RequestMultiplePermissions()
    }
}