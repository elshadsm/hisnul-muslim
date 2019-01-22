package com.elshadsm.muslim.hisnul.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.design.widget.AppBarLayout
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.elshadsm.muslim.hisnul.BR
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.adapters.DazViewAdapter
import com.elshadsm.muslim.hisnul.databinding.ActivityDazViewBinding
import com.elshadsm.muslim.hisnul.models.DAZ_ID_EXTRA_NAME
import com.elshadsm.muslim.hisnul.models.DazData

class DazViewActivity : AppCompatActivity() {

  private var menu: Menu? = null
  private lateinit var binding: ActivityDazViewBinding
  private lateinit var pagerAdapter: DazViewAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    init()
    applyConfiguration()
    registerEventHandlers()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.daz_view_actions, menu)
    this.menu = menu
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.action_hide_or_display_play -> return true
      R.id.action_bookmark -> return true
      R.id.action_share -> return true
    }
    return super.onOptionsItemSelected(item)
  }

  private fun init() {
    binding = DataBindingUtil.setContentView(this, R.layout.activity_daz_view)
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
  }

  private fun applyConfiguration() {
    if (intent.hasExtra(DAZ_ID_EXTRA_NAME)) {
      val data = intent.extras
      val dazId: Int? = data?.getInt(DAZ_ID_EXTRA_NAME)
      val dazDataList: List<DazData> = getMockData()
      pagerAdapter = DazViewAdapter(supportFragmentManager)
      pagerAdapter.setData(dazDataList)
      binding.viewPager.adapter = pagerAdapter
      binding.setVariable(BR.dazData, dazDataList[0])
      binding.executePendingBindings()
      updatePagination(1, pagerAdapter.count)
    }
  }

  private fun registerEventHandlers() {
    binding.appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
      var isShow = false
      var scrollRange = -1

      override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        if (scrollRange == -1) {
          scrollRange = appBarLayout.totalScrollRange
        }
        if (scrollRange + verticalOffset == 0) {
          hideActions()
          updateTitleAndPagination(true)
          isShow = true
        } else if (isShow) {
          showActions()
          updateTitleAndPagination(false)
          isShow = false
        }
      }
    })
    binding.viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
      override fun onPageSelected(position: Int) = updatePagination(position + 1, pagerAdapter.count)
    })
  }

  private fun hideActions() {
    arrayOf(R.id.action_hide_or_display_play, R.id.action_bookmark, R.id.action_share).forEach {
      menu?.findItem(it)?.isVisible = false
    }
  }

  private fun showActions() {
    arrayOf(R.id.action_hide_or_display_play, R.id.action_bookmark, R.id.action_share).forEach {
      menu?.findItem(it)?.isVisible = true
    }
  }

  private fun updateTitleAndPagination(toolbarCollapsed: Boolean) {
    binding.ctlTitle.visibility = if (toolbarCollapsed) View.INVISIBLE else View.VISIBLE
    binding.ctlPagination.visibility = if (toolbarCollapsed) View.INVISIBLE else View.VISIBLE
    binding.toolbarTitle.visibility = if (toolbarCollapsed) View.VISIBLE else View.INVISIBLE
    binding.toolbarPagination.visibility = if (toolbarCollapsed) View.VISIBLE else View.INVISIBLE
  }

  private fun updatePagination(currentPage: Int, totalPage: Int) {
    val pagination = String.format(resources.getString(R.string.daz_view_pagination), currentPage, totalPage)
    binding.toolbarPagination.text = pagination
    binding.ctlPagination.text = pagination
  }

  //  Remove the function below
  private fun getMockData(): ArrayList<DazData> {
    return arrayListOf(DazData(
        "(Nafilə) oruc tutan şəxsin yemək süfrəsi açıldığı zaman orucunu  pozmaq istəmədikdə etdiyi dua",
        "لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الحَمدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، سُبْحَانَ اللهِ، وَالحَمدُ للهِ، وَلَا إِلَهَ إِلَّا اللهُ وَاللهُ أَكْبَرُ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ الْعَلِيِّ الْعَظِيمِ، رَبِّ اغْفِرْ لِي",
        "Lə ilahə illəllahu vəhdahu lə şərikə" +
            "ləhu, ləhul-mulku və ləhul-həmdu" +
            "və huvə alə kulli şeyin qadir, subhanal-lahi, vəlhəmdulilləhi, və lə ilahə illallahu," +
            "vəllahu əkbər, və lə həulə və lə" +
            "quvvətə illə billəhil-aliyyil-azim, rabbiğfir" +
            "li",
        "Bədənimə salamatlıq verən, ruhumu" +
            "mənə qaytaran və mənə Onu" +
            "yad etməyə izin verən Allaha həmd" +
            "olsun!",
        "ət-Tirmizi, 5/473. Bax: «Səhihu-t-Tirmizi», 3/144."
    ),
        DazData(
            "(Nafilə) oruc tutan şəxsin yemək süfrəsi açıldığı zaman orucunu  pozmaq istəmədikdə etdiyi dua",
            "لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الحَمدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، سُبْحَانَ اللهِ، وَالحَمدُ للهِ، وَلَا إِلَهَ إِلَّا اللهُ وَاللهُ أَكْبَرُ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ الْعَلِيِّ الْعَظِيمِ، رَبِّ اغْفِرْ لِي",
            "Lə ilahə illəllahu vəhdahu lə şərikə" +
                "ləhu, ləhul-mulku və ləhul-həmdu" +
                "və huvə alə kulli şeyin qadir, subhanal-lahi, vəlhəmdulilləhi, və lə ilahə illallahu," +
                "vəllahu əkbər, və lə həulə və lə" +
                "quvvətə illə billəhil-aliyyil-azim, rabbiğfir" +
                "li",
            "Bədənimə salamatlıq verən, ruhumu" +
                "mənə qaytaran və mənə Onu" +
                "yad etməyə izin verən Allaha həmd" +
                "olsun!",
            "ət-Tirmizi, 5/473. Bax: «Səhihu-t-Tirmizi», 3/144."
        ),
        DazData(
            "(Nafilə) oruc tutan şəxsin yemək süfrəsi açıldığı zaman orucunu  pozmaq istəmədikdə etdiyi dua",
            "لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الحَمدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، سُبْحَانَ اللهِ، وَالحَمدُ للهِ، وَلَا إِلَهَ إِلَّا اللهُ وَاللهُ أَكْبَرُ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ الْعَلِيِّ الْعَظِيمِ، رَبِّ اغْفِرْ لِي",
            "Lə ilahə illəllahu vəhdahu lə şərikə" +
                "ləhu, ləhul-mulku və ləhul-həmdu" +
                "və huvə alə kulli şeyin qadir, subhanal-lahi, vəlhəmdulilləhi, və lə ilahə illallahu," +
                "vəllahu əkbər, və lə həulə və lə" +
                "quvvətə illə billəhil-aliyyil-azim, rabbiğfir" +
                "li",
            "Bədənimə salamatlıq verən, ruhumu" +
                "mənə qaytaran və mənə Onu" +
                "yad etməyə izin verən Allaha həmd" +
                "olsun!",
            "ət-Tirmizi, 5/473. Bax: «Səhihu-t-Tirmizi», 3/144."
        ))
  }
  //  Remove the function above

}