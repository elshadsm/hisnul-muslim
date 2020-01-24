package com.elshadsm.muslim.hisnul.views.main

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.views.search.SearchActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

  private lateinit var viewModel: MainViewModel
  private lateinit var viewModelFactory: MainViewModelFactory

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)
    applyConfigurations()
    registerEventHandlers()
  }

  override fun onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_search -> {
        startActivity(Intent(this, SearchActivity::class.java))
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.nav_camera -> {
      }
      R.id.nav_gallery -> {
      }
      R.id.nav_slideshow -> {
      }
      R.id.nav_manage -> {
      }
      R.id.nav_share -> {
      }
      R.id.nav_send -> {
      }
    }
    drawerLayout.closeDrawer(GravityCompat.START)
    return true
  }

  private fun applyConfigurations() {
    viewModelFactory = MainViewModelFactory(this)
    viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
        R.string.navigation_drawer_open, R.string.navigation_drawer_close)
    drawerLayout.addDrawerListener(toggle)
    toggle.syncState()
    navigationView.setNavigationItemSelectedListener(this)
    dhikrTitleListRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    dhikrTitleListRecyclerView.adapter = DhikrTitleListAdapter(this, viewModel)
  }

  private fun registerEventHandlers() {
    viewModel.titleList.observe(this, Observer { dhikrTitleListRecyclerView.adapter?.notifyDataSetChanged() })
    fab.setOnClickListener { view ->
      Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
          .setAction("Action", null).show()
    }
  }

}
