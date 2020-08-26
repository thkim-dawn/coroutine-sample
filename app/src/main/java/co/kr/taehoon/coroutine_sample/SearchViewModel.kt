package co.kr.taehoon.coroutine_sample

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import co.kr.taehoon.coroutine_sample.data.ImageDocument
import co.kr.taehoon.coroutine_sample.data.ImageDocumentDataSource
import co.kr.taehoon.coroutine_sample.data.ImageRepository
import co.kr.taehoon.coroutine_sample.data.Meta
import co.kr.taehoon.coroutine_sample.util.default
import co.kr.taehoon.coroutine_sample.util.parsingData
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class SearchViewModel : ViewModel(), KoinComponent {
    private val TAG = "SearchViewModel"
    private val viewModelJob = Job()
    private val repository : ImageRepository by inject()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    val errorLiveData = MutableLiveData<String>()

    var totalImageDocuments = mutableListOf<ImageDocument>()//현재까지 검색된 list
    val searchTextLiveData = MutableLiveData<String>().default("")//검색어 two-way binding
    private var totalCollection = mutableListOf<String>()//현재까지 추출된 카테고리
    val collectionsLiveData = MutableLiveData<MutableList<String>>()//새로 추가 될 카테고리
    var selectedCollection = ALL//현재 선택된 카테고리

    //페이징 처리를 위한 변수들
    var lastQuery = ""
    var lastPage: Int = 1

    var isFilter: Boolean = false
    private var isEndPage: Boolean = false

    init {
        errorLiveData.value = ERROR_NO_INPUT
    }

    private val config = PagedList.Config.Builder()
        .setPageSize(PAGE_SIZE)
        .setInitialLoadSizeHint(PAGE_SIZE)
        .setPrefetchDistance(10)
        .setEnablePlaceholders(true)
        .build()

    val pagedListBuilder =
        RxPagedListBuilder<Int, ImageDocument>(object : DataSource.Factory<Int, ImageDocument>() {
            override fun create(): DataSource<Int, ImageDocument> {
                return ImageDocumentDataSource(this@SearchViewModel,repository)
            }
        }, config).buildObservable()

    suspend fun searchImage(query: String, page: Int) =
        //            emit(repository.getAllImage(query,page))
        withContext(viewModelScope.coroutineContext) {
            var imageDocuments: List<ImageDocument>? = null
            try {
                lastPage = page
                lastQuery = query

                repository.getAllImageDocuments(query, page).let {
                    totalImageDocuments.addAll(it)
                    collectionsLiveData.value = getCollection(it)
                    imageDocuments =  repository.getFilterImageDocuments(it, selectedCollection)
                }
                repository.lastJsonElement?.parsingData<Meta>("meta")?.let { meta ->
                    isEndPage = meta.isEnd
                }
                //처음에 검색되지 않았을경우 error 표시
                if (totalImageDocuments.isEmpty()) {
                    errorLiveData.value = ERROR_NO_RESULT
                } else {
                    errorLiveData.value = null
                }
            } catch (e: Exception) {
                errorLiveData.value = ERROR_NETWORK
                Log.e(TAG, "searchImage ${e.message}")
            }
            imageDocuments
        }

    /**
     * @param query 현재 입력된 검색어
     * @param lastQuery 마지막 입력된 검색어
     */
    fun isChangeQuery(query: String, lastQuery: String): Boolean {
        return query.isNotEmpty() && (query != lastQuery)
    }

    //마지막 페이지 기점으로 이후 페이지가 존재하면 +1 없으면 null
    fun getNextPage(): Int? {
        Log.d("isEndPage","$isEndPage")
        return if (!isEndPage) lastPage + 1 else null
    }

    //마지막 페이지 기점으로 이전 페이지가 존재하면 -1 없으면 null
    fun getPrePage(): Int? {
        val lp = lastPage
        return if (lp > 1) lp - 1 else null
    }

    fun initSearch() {
        lastQuery = ""
        lastPage = 1
        totalCollection.clear()
        totalImageDocuments.clear()
        selectedCollection = ALL
    }

    private fun getCollection(imageDocuments: List<ImageDocument>): MutableList<String> {
        val newCollections = mutableListOf<String>()
        imageDocuments.distinctBy {
            it.collection
        }.forEach { item ->
            if (!totalCollection.contains(item.collection)) {//기존 collections에 포함되어 있지않으면 추가
                newCollections.add(item.collection)
            }
        }
        totalCollection.addAll(newCollections)
        return newCollections
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        viewModelJob.cancel()
    }

    companion object {
        var PAGE_SIZE = 80
        const val ALL = "all"
        const val ERROR_NO_INPUT = "errorNoInput"
        const val ERROR_NETWORK = "errorNetwork"
        const val ERROR_NO_RESULT = "errorNoResult"
    }

}