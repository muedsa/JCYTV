package com.muedsa.model

data class LazyPagedList<Q, R>(
    val query: Q,
    val list: MutableList<R> = mutableListOf(),
    val page: Int = 0,
    val totalPage: Int = 0,
    val offset: Int = 0,
    val type: LazyType = LazyType.LOADING,
    val error: Throwable? = null
) {
    val nextPage get() = page + 1
    val hasNext get() = page == 0 || page < totalPage

    fun loadingNext() = LazyPagedList(
        query = query,
        list = list,
        page = page,
        totalPage = totalPage,
        type = LazyType.LOADING
    )

    fun successNext(appendList: List<R>, totalPage: Int) = LazyPagedList(
        query = query,
        offset = list.size,
        list = list.also { it.addAll(appendList) },
        page = nextPage,
        totalPage = totalPage,
        type = LazyType.SUCCESS
    )

    fun failNext(error: Throwable?) = LazyPagedList(
        query = query,
        list = list,
        page = page,
        totalPage = totalPage,
        type = LazyType.FAILURE,
        error = error
    )

    companion object {
        @JvmStatic
        fun <Q, R> new(query: Q): LazyPagedList<Q, R> =
            LazyPagedList(query = query, type = LazyType.SUCCESS)
    }
}