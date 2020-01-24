package com.elshadsm.muslim.hisnul.views.search

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.elshadsm.muslim.hisnul.R
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

  private lateinit var viewModel: SearchViewModel
  private lateinit var viewModelFactory: SearchViewModelFactory
  private lateinit var adapter: SearchAdapter
  private lateinit var searchView: SearchView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initializeUi()
    applyConfiguration()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.search, menu)
    applySearchViewConfiguration(menu)
    registerSearchViewEventHandlers()
    return true
  }

  private fun initializeUi() {
    setContentView(R.layout.activity_search)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
  }

  private fun applyConfiguration() {
    viewModelFactory = SearchViewModelFactory(this)
    viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel::class.java)
    adapter = SearchAdapter(this, viewModel)
    recyclerView.adapter = adapter
    viewModel.titleList.observe(this, Observer { recyclerView.adapter?.notifyDataSetChanged() })
    viewModel.state.observe(this, Observer { handleStateChange(it) })
  }

  private fun applySearchViewConfiguration(menu: Menu) {
    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
    searchView = menu.findItem(R.id.option_search).actionView as SearchView
    searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
    searchView.maxWidth = Int.MAX_VALUE
    searchView.isIconified = false
  }

  private fun registerSearchViewEventHandlers() {
    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {
        viewModel.handleQuery(query)
        return false
      }

      override fun onQueryTextChange(query: String?): Boolean {
        viewModel.handleQuery(query)
        return false
      }
    })
    searchView.setOnCloseListener { true }
  }

  private fun handleStateChange(state: SearchState) {
    message.apply {
      visibility = if (state == SearchState.FOUND) View.GONE else View.VISIBLE
      text = when (state) {
        SearchState.EMPTY -> resources.getString(R.string.search_empty_message)
        SearchState.INCOMPLETE -> resources.getString(R.string.search_incomplete_message)
        SearchState.NOT_FOUND -> resources.getString(R.string.search_not_found_message)
        SearchState.FOUND -> resources.getString(R.string.empty)
      }
    }
  }

}
