package com.example.movieapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.movieapp.data.remote.MediaAPI
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.util.Constants.discoverListScreen
import com.example.movieapp.util.Constants.nowPlayingAllListScreen
import com.example.movieapp.util.Constants.popularAllListScreen
import com.example.movieapp.util.Constants.upcomingListScreen
import kotlinx.coroutines.delay
import java.lang.Exception

class MoviePagingSource(private val apiService: MediaAPI, private val tags: String): PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val nextPage = params.key ?: 1
            delay(3000L)
            val response = when(tags) {
                nowPlayingAllListScreen -> {
                    apiService.getNowPlayingMovies(page = nextPage)
                }
                discoverListScreen -> {
                    apiService.getDiscoverMovies(page = nextPage)
                }
                upcomingListScreen -> {
                    apiService.getUpcomingMovies(page = nextPage)
                }

                popularAllListScreen -> {
                    apiService.getPopularMovies(page = nextPage)
                }
                else -> {
                    apiService.getPopularMovies(page = nextPage)
                }
            }
            LoadResult.Page(
                data = response.results,
                prevKey = if(nextPage == 1) null else nextPage-1,
                nextKey = if (response.page >= response.total_pages) null else response.page+1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1) ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}