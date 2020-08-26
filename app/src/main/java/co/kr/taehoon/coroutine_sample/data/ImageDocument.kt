package co.kr.taehoon.coroutine_sample.data
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

data class ImageDocument(
    @Expose
    @SerializedName("collection")
    val collection :String,
    @Expose
    @SerializedName("thumbnail_url")
    val thumbnailUrl :String?,
    @Expose
    @SerializedName("image_url")
    val imageUrl :String,
    @Expose
    @SerializedName("width")
    val width :Int,
    @Expose
    @SerializedName("height")
    val height :Int,
    @Expose
    @SerializedName("display_sitename")
    val displaySiteName :String,
    @Expose
    @SerializedName("doc_url")
    val docUrl :String,
    @Expose
    @SerializedName("datetime")
    val dateTime :Date
)