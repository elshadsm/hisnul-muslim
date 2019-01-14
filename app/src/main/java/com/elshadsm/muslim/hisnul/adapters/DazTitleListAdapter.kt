package com.elshadsm.muslim.hisnul.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.activities.DazViewActivity
import com.elshadsm.muslim.hisnul.models.DAZ_EXTRA_NAME
import com.elshadsm.muslim.hisnul.models.DazData

class DazTitleListAdapter(private val context: Context) :
    RecyclerView.Adapter<DazTitleListAdapter.RecyclerViewHolder>() {

  private val titleList: Array<String> = context.resources.getStringArray(R.array.daz_title_list)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
        .inflate(R.layout.daz_title_list_item, parent, false)
    return RecyclerViewHolder(layoutInflater)
  }

  override fun getItemCount(): Int {
    return titleList.size
  }

  override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
    holder.number.text = (position + 1).toString().padStart(context.resources.getInteger(R.integer.daz_title_number_pad))
    holder.title.text = titleList[position]
  }

  class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val number = itemView.findViewById<TextView>(R.id.daz_title_li_number)!!
    val title = itemView.findViewById<TextView>(R.id.daz_title_li_title)!!

    init {
      itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
      val intent = Intent(view.context, DazViewActivity::class.java)
      intent.putExtra(DAZ_EXTRA_NAME, DazData(
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
          "ət-Tirmizi, 5/473. Bax: «Səhihu-t-Tirmizi», 3/144.",
          "12/24"
      ))
      view.context.startActivity(intent)
    }
  }
}