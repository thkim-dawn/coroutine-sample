package co.kr.taehoon.coroutine_sample.data

import android.media.Image
import android.util.Log
import androidx.lifecycle.Observer
import androidx.paging.PageKeyedDataSource
import co.kr.taehoon.coroutine_sample.SearchViewModel
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

class ImageDocumentDataSource(private val searchViewModel: SearchViewModel) :
    PageKeyedDataSource<Int, ImageDocument>() {
    private val TAG = "ImageDocumentDataSource"

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, ImageDocument>
    ) {

        /*
        searchViewModel.isFilter -> 카테고리를 클릭했을 경우 이전 Datasource를 초기화 한다.
         */
        if (searchViewModel.isFilter) {// 카테고리 선택으로 인입
            val filter = searchViewModel.filterImageDocuments(
                searchViewModel.totalImageDocuments,
                searchViewModel.selectedCollection
            )
            searchViewModel.isFilter = false
            callback.onResult(filter, null, searchViewModel.getNextPage())
        } else {//버튼 클릭으로 인입
            val query = searchViewModel.searchTextLiveData.value
            if (query.isNullOrEmpty()) {
                return
            }

            CoroutineScope(Dispatchers.Default).launch {
                searchViewModel.searchImage(
                    query,
                    searchViewModel.lastPage
                )?.let {
                    callback.onResult(
                        it,
                        searchViewModel.getPrePage(),
                        searchViewModel.getNextPage()
                    )
                }
            }

        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ImageDocument>) {
        val lastQuery = searchViewModel.lastQuery
        var imageDocuments: List<ImageDocument>?

        CoroutineScope(Dispatchers.Default).launch {
            searchViewModel.searchImage(
                lastQuery,
                params.key
            )?.let {
                imageDocuments = it
                val nextPage = searchViewModel.getNextPage()
                if (it.isEmpty() && nextPage != null) {
                    imageDocuments = searchViewModel.searchImage(lastQuery, nextPage)
                }
                imageDocuments?.let {list->
                    callback.onResult(list, searchViewModel.getNextPage())
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ImageDocument>) {

        val lastQuery = searchViewModel.lastQuery
        var imageDocuments: List<ImageDocument>?

        CoroutineScope(Dispatchers.Default).launch {
            searchViewModel.searchImage(
                lastQuery,
                params.key
            )?.let {
                imageDocuments = it
                val prePage = searchViewModel.getPrePage()
                if (it.isEmpty() && prePage != null) {
                    imageDocuments = searchViewModel.searchImage(lastQuery, prePage)
                }
                imageDocuments?.let {list->
                    callback.onResult(list, searchViewModel.getPrePage())
                }
            }
        }
    }
}