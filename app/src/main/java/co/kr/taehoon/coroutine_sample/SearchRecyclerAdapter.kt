package co.kr.taehoon.coroutine_sample

import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import co.kr.taehoon.coroutine_sample.data.ImageDocument
import co.kr.taehoon.coroutine_sample.databinding.ItemImageDocumentBinding

import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchRecyclerAdapter(context: Context) :
    PagedListAdapter<ImageDocument, SearchRecyclerAdapter.ImageDocumentViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageDocumentViewHolder{
        return LayoutInflater.from(parent.context).let {
            DataBindingUtil.inflate<ItemImageDocumentBinding>(it, R.layout.item_image_document, parent, false)
        }.run {
            lifecycleOwner = (parent.context as? LifecycleOwner)
            ImageDocumentViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ImageDocumentViewHolder, position: Int) {
        holder.bind(getItem(position),position)
    }


    inner class ImageDocumentViewHolder(private val binding: ItemImageDocumentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(itemData : ImageDocument?,position: Int){
            itemData?.let {
                binding.imageDocument = it
            }
        }
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ImageDocument>() {
            override fun areItemsTheSame(oldItem: ImageDocument, newItem: ImageDocument): Boolean {
                return oldItem.thumbnailUrl == newItem.thumbnailUrl
            }

            override fun areContentsTheSame(oldItem: ImageDocument, newItem: ImageDocument): Boolean {
                return oldItem.thumbnailUrl == newItem.thumbnailUrl
            }
        }
    }
}