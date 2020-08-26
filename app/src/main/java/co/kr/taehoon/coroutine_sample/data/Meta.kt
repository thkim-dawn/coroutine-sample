package co.kr.taehoon.coroutine_sample.data
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Meta(
    @Expose
    @SerializedName("total_count")
    val totalCount: Int,
    @Expose
    @SerializedName("pageable_count")
    val pageableCount: Int,
    @SerializedName("is_end")
    val isEnd: Boolean
)