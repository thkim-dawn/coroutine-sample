package co.kr.taehoon.coroutine_sample.data

import android.util.Log
import co.kr.taehoon.coroutine_sample.SearchViewModel
import co.kr.taehoon.coroutine_sample.util.ApiService
import co.kr.taehoon.coroutine_sample.util.parsingData
import com.google.gson.JsonElement
import org.koin.core.KoinComponent
import org.koin.core.inject

class ImageRepository : KoinComponent {

    private val apiService: ApiService by inject()
    var lastJsonElement: JsonElement? = null
    private suspend fun searchImage(query: String, page: Int) =
        apiService.getSearchImage(query, page)

    suspend fun getAllImageDocuments(query: String, page: Int): List<ImageDocument> {
        var imageDocuments: List<ImageDocument>? = null
        lastJsonElement = searchImage(query, page)
        lastJsonElement?.let {
            imageDocuments = it.parsingData("documents")
        }
        return imageDocuments ?: listOf()
    }


    /**
     * @param sourceData filter를 진행할 list
     * @param selectedCollection 현재 선택된 카테고리
     */
    suspend fun getFilterImageDocuments(sourceData: List<ImageDocument>,selectedCollection: String):List<ImageDocument>{
        return if (selectedCollection.isEmpty() || selectedCollection == SearchViewModel.ALL) {
            sourceData
        } else {
            sourceData.filter { selectedCollection == it.collection }
        }.toMutableList()
    }
}