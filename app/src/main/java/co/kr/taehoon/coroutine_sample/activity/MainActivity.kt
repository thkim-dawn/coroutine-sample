package co.kr.taehoon.coroutine_sample.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.core.view.isEmpty
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.kr.taehoon.coroutine_sample.R
import co.kr.taehoon.coroutine_sample.SearchRecyclerAdapter
import co.kr.taehoon.coroutine_sample.SearchViewModel
import co.kr.taehoon.coroutine_sample.SearchViewModel.Companion.ALL
import co.kr.taehoon.coroutine_sample.databinding.ActivityMainBinding
import co.kr.taehoon.coroutine_sample.util.GridSpaceDecoration
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.reactivex.rxkotlin.addTo
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_main
    private val searchViewModel: SearchViewModel by viewModel()
    private lateinit var recyclerSearch: RecyclerView
    private lateinit var searchRecyclerAdapter: SearchRecyclerAdapter
    private lateinit var chipGroup: ChipGroup

    override fun onStart() {
        super.onStart()
        searchViewModel.let { sv ->
            sv.pagedListBuilder.subscribe {
                searchRecyclerAdapter.submitList(it)
            }.addTo(compositeDisposable)

            sv.searchTextLiveData.observe(this, Observer {
                viewDataBinding.btnSearch.isEnabled =
                    searchViewModel.isChangeQuery(it, searchViewModel.lastQuery)
            })

            sv.collectionsLiveData.observe(this, Observer {
                if (it.isNotEmpty()) {
                    addChip(it, chipGroup)
                    it.clear()
                }
            })
        }

    }

    override fun initAfterBinding() {
//        searchViewModel.searchImage("test", 1).observe(this, Observer {
//            Log.d("Asdad", "${it}")
//        }
//        )

        viewDataBinding.searchViewModel = searchViewModel
        searchRecyclerAdapter = SearchRecyclerAdapter(this)

        chipGroup = viewDataBinding.cgCollection

        recyclerSearch = viewDataBinding.recyclerSearch.apply {
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            addItemDecoration(GridSpaceDecoration(10,10,3))
            adapter = searchRecyclerAdapter
        }
        viewDataBinding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    true
                }
                else -> false
            }
        }

    }

    /**
     * 검색 버튼 이나 키보드 search action 실행시
     * 현재까지 모든 이미지 data를 clear하고 이전 DataSource detach
     */
    fun onClickSearch() {
        chipGroup.removeAllViews()
        searchViewModel.initSearch()
        viewDataBinding.btnSearch.isEnabled = false
        (recyclerSearch.adapter as SearchRecyclerAdapter).currentList?.dataSource?.invalidate()
    }

    /**
     * @param items 추가해야될 Chip Text List
     * @param chipGroup chip이 추가될 ChipGroup instance
     */
    private fun addChip(items: List<String>, chipGroup: ChipGroup) {
        //chipGroup이 비어 있으면 해당 검색어의 첫 호출로 판단 all추가와 함께 isChecked == true
        if (chipGroup.isEmpty()) {
            val chip = makeChip(ALL)
            chipGroup.addView(chip)
            chip.isChecked = true
        }

        for (item in items) {
            val chip = makeChip(item)
            chipGroup.addView(chip, chipGroup.childCount)
        }
    }

    /**
     * @param chipText chip에 입력될 String
     */
    private fun makeChip(chipText: String): Chip {
        return (LayoutInflater.from(this)
            .inflate(R.layout.item_chip, null) as Chip).apply {
            text = chipText
            textSize = 12f
        }.also {
            it.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    recyclerSearch.run {
                        (adapter as SearchRecyclerAdapter).currentList?.let { pagedList ->
                            if (pagedList.isNotEmpty() && searchViewModel.selectedCollection != chipText) {
                                searchViewModel.run {
                                    selectedCollection = chipText
                                    isFilter = true
                                }
                                pagedList.dataSource.invalidate()
                                scrollToPosition(0)
                            }
                        }
                    }
                }
            }
        }
    }

}