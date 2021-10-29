package com.yh.sarl.throwable

import androidx.activity.result.contract.ActivityResultContract

class ResultCheckFailException(
    contract: ActivityResultContract<*, *>,
    inputData: Any?,
    result: Any?
) :
    RuntimeException(
        arrayOf(
            "checker result failed!",
            "result:    $result",
            "contract:  ${contract::class.qualifiedName}",
            "input:     $inputData",
        ).joinToString("\n    ")
    )