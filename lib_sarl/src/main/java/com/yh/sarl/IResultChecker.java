package com.yh.sarl;

import androidx.annotation.Nullable;

public interface IResultChecker<T> {
    boolean onCheck(@Nullable T result);
}
