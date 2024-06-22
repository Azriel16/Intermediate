package com.dicoding.intermediate.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.intermediate.data.remote.response.ListStoryItem
import com.dicoding.intermediate.data.remote.retrofit.ApiService

class StoryPagingSource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, ListStoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: 1
            val response = apiService.getStories("Bearer $token", position, params.loadSize)
            val storyList = response.story

            LoadResult.Page(
                data = storyList,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (storyList.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition
    }
}
