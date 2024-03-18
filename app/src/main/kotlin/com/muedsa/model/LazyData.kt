package com.muedsa.model

data class LazyData<T>(
    val data: T? = null,
    val type: LazyType = LazyType.LOADING,
    val error: Throwable? = null
) {
    companion object {
        @JvmStatic
        fun <T> success(data: T): LazyData<T> = LazyData(data = data, type = LazyType.SUCCESS)

        @JvmStatic
        fun <T> init(): LazyData<T> = LazyData()

        @JvmStatic
        fun <T> fail(error: Throwable?): LazyData<T> =
            LazyData(type = LazyType.FAILURE, error = error)
    }
}

enum class LazyType {
    LOADING,
    SUCCESS,
    FAILURE
}