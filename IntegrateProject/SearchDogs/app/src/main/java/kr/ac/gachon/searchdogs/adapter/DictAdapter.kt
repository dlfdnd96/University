package kr.ac.gachon.searchdogs.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kr.ac.gachon.searchdogs.R
import kr.ac.gachon.searchdogs.data.DogDTO
import kr.ac.gachon.searchdogs.service.DetailDict

class DictAdapter (val context: Context, val dogList : ArrayList<DogDTO>) : BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_item_dict, null)


        val dogPhoto = view.findViewById<ImageView>(R.id.img_Dogs)
        val dogName = view.findViewById<TextView>(R.id.txt_Dogs_name)


        val dog = dogList[p0]
        val resourceId = context.resources.getIdentifier(dog.photo, "drawable", context.packageName)
        dogName.text = dog.name
        dogPhoto.setImageResource(resourceId)

        view.setOnClickListener{
            val intent = Intent(view.context, DetailDict::class.java)
            intent.putExtra("name", dog.name)
            intent.putExtra("psn", dog.psn)
            intent.putExtra("birth", dog.birth)
            intent.putExtra("life", dog.life)
            intent.putExtra("photo", dog.photo)
            view.context.startActivity(intent)
        }

        return view
    }

    override fun getItem(p0: Int): Any {
        return dogList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return dogList.size
    }

}

