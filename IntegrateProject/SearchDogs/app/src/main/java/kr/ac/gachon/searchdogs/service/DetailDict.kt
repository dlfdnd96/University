package kr.ac.gachon.searchdogs.service

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import babushkatext.BabushkaText
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.hdodenhof.circleimageview.CircleImageView
import kr.ac.gachon.searchdogs.R

class DetailDict : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_dict)

        val intent = getIntent()
        val recName = intent.getStringExtra("name")
        val recPsn = intent.getStringExtra("psn")
        val recBirth = intent.getStringExtra("birth")
        val recLife = intent.getStringExtra("life")
        val recPhoto = intent.getStringExtra("photo")

        val dogImage = findViewById<ImageView>(R.id.img_Dt)
        val dogName = findViewById<BabushkaText>(R.id.txt_Dt_name)
        val dogPsn = findViewById<BabushkaText>(R.id.txt_Dt_psn)
        val dogBirth = findViewById<BabushkaText>(R.id.txt_Dt_birth)
        val dogLife = findViewById<BabushkaText>(R.id.txt_Dt_life)
        val name = findViewById<BabushkaText>(R.id.Name)
        val psn = findViewById<BabushkaText>(R.id.Psn)
        val birth = findViewById<BabushkaText>(R.id.Birth)
        val life = findViewById<BabushkaText>(R.id.Life)

        val resourceId = resources.getIdentifier(recPhoto, "drawable", packageName)

        Glide
            .with(this)
            .asBitmap()
            .load(resourceId)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(dogImage!!)

        dogName.addPiece(BabushkaText.Piece.Builder(recName)
            .textColor(Color.BLACK)
            .textSizeRelative(1.2f)
            .build())
        dogPsn.addPiece(BabushkaText.Piece.Builder(recPsn)
            .textColor(Color.BLACK)
            .textSizeRelative(1.2f)
            .build())
        dogBirth.addPiece(BabushkaText.Piece.Builder(recBirth)
            .textColor(Color.BLACK)
            .textSizeRelative(1.2f)
            .build())
        dogLife.addPiece(BabushkaText.Piece.Builder(recLife)
            .textColor(Color.BLACK)
            .textSizeRelative(1.2f)
            .build())
        name.addPiece(BabushkaText.Piece.Builder("이름")
            .textColor(Color.BLACK)
            .textSizeRelative(1.5f)
            .style(Typeface.BOLD)
            .build())
        psn.addPiece(BabushkaText.Piece.Builder("성격")
            .textColor(Color.BLACK)
            .textSizeRelative(1.5f)
            .style(Typeface.BOLD)
            .build())
        birth.addPiece(BabushkaText.Piece.Builder("출생지")
            .textColor(Color.BLACK)
            .textSizeRelative(1.5f)
            .style(Typeface.BOLD)
            .build())
        life.addPiece(BabushkaText.Piece.Builder("평균 수명")
            .textColor(Color.BLACK)
            .textSizeRelative(1.5f)
            .style(Typeface.BOLD)
            .build())

        dogName.display()
        dogPsn.display()
        dogBirth.display()
        dogLife.display()
        name.display()
        psn.display()
        birth.display()
        life.display()


//        val dogName = findViewById<TextView>(R.id.txt_Dt_name)
//        val dogPsn = findViewById<TextView>(R.id.txt_Dt_psn)
//        val dogBirth = findViewById<TextView>(R.id.txt_Dt_birth)
//        val dogLife = findViewById<TextView>(R.id.txt_Dt_life)
//
//
//        dogName.text = recName
//        dogPsn.text = recPsn
//        dogBirth.text = recBirth
//        dogLife.text = recLife

    }

}
