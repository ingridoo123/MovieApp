package com.example.movieapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.domain.model.Search
import kotlinx.coroutines.delay
import java.io.IOException
import java.lang.Exception

class SearchMovieSource(private val apiService: MediaAPI, private val searchParams: String, private val includeAdult: Boolean): PagingSource<Int, Search>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Search> {
        return try {
            val nextPage = params.key ?: 1
            delay(3000L)
            val searchMovies = apiService.search(
                page = nextPage,
                searchParams = searchParams,
                includeAdult = includeAdult
            )
            LoadResult.Page(
                data = searchMovies.results,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (searchMovies.results.isEmpty()) null else searchMovies.page + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Search>): Int? = state.anchorPosition
}