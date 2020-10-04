/**
 * 앨범에서 선택한 사진이나 사진촬영으로 촬영한 사진을 보여주는 화면
 *
 * @since: 2019.10.01
 * @author: 류일웅
 */
package kr.ac.gachon.searchdogs.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kr.ac.gachon.searchdogs.R
import kr.ac.gachon.searchdogs.fragment.CameraFragment.Companion.INTENT_CAMERA_TAG
import kr.ac.gachon.searchdogs.fragment.GalleryFragment.Companion.INTENT_GALLERY_TAG
import id.zelory.compressor.Compressor
import kr.ac.gachon.searchdogs.service.DetailDict
import java.io.File
import java.net.Socket
import java.nio.file.Files

class DogImageActivity : AppCompatActivity() {

    private lateinit var mCoordinateLayout: CoordinatorLayout
    private lateinit var mImageView: ImageView
    private lateinit var resultView : TextView
    private lateinit var progressbar : ProgressBar
    private lateinit var RESULT : String

    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_image)
        
        mCoordinateLayout = findViewById(R.id.dogImageResult_cl)
        mImageView = findViewById(R.id.dogImage_img)
        resultView = findViewById(R.id.LoadingText)
        progressbar = findViewById(R.id.loading)

        if (intent.hasExtra(INTENT_GALLERY_TAG)) {
            showPickUpGalleryImage()
        }
        else if (intent.hasExtra(INTENT_CAMERA_TAG)) {
            showTakePhotoImage()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * ##################################################
     * 갤러리에서 가져온 이미지의 강아지 품종 결과를 보여주는 기능
     *
     * @since: 2019.10.01
     * @author: 류일웅, 이정묵
     * @param:
     * @return:
     * ##################################################
     */
    private fun showPickUpGalleryImage() {
        val bitmapStringURI = intent.getStringExtra(INTENT_GALLERY_TAG)
        val uri = Uri.parse(bitmapStringURI)
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

        val thread = Thread(Runnable {
            Handler(Looper.getMainLooper()).post{
                Toast.makeText(this, "결과 예측중. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show()
                progressbar.visibility = View.VISIBLE
            }

            // 19.11.06 갤러리  소켓 통신 구현 - 이정묵 > 19.11.07 Thread로 구현, 로딩 
            val path = getRealPathFromURI(uri)
            val file = File(path)
            val Comp = Compressor(this).compressToFile(file)
            val content = Files.readAllBytes(Comp.toPath())
            val ip = "121.169.158.111" // 192.168.0.0
            val port = 7070 // 여기에 port를 입력해주세요

            val socket = Socket(ip, port) // ip와 port를 입력하여 클라이언트 소켓을 만듭니다.
            val outStream = socket.outputStream // outputStream - 데이터를 내보내는 스트림입니다.

            val data = content.size.toString().toByteArray() + ":".toByteArray() + content// 데이터는 byteArray로 변경 할 수 있어야 합니다.

            outStream.write(data) // toByteArray() 파라미터로 charset를 설정할 수 있습니다. 기본값 utf-8

            val byteArr = ByteArray(1024)
            val ins = socket.getInputStream()
            val readByteCount = ins.read(byteArr)
            val msg = String(byteArr, 0, readByteCount)
            val test = Uri.parse("$msg").toString()
            RESULT = test

            Handler(Looper.getMainLooper()).post{
                resultView.text = RESULT
                progressbar.visibility = View.GONE

                resultView.setOnClickListener {
                    getDogDicData(RESULT)
                }
            }
        })

        thread.start()

        Glide
            .with(this)
            .asBitmap()
            .load(bitmap)
            .into(mImageView)
    }

    /**
     * ##################################################
     * 사진촬영한 이미지의 강아지 품종 결과를 보여주는 기능
     *
     * @since: 2019.10.01
     * @author: 류일웅, 이정묵
     * @param:
     * @return:
     * ##################################################
     */
    private fun showTakePhotoImage() {
        val bitmapStringURI = intent.getStringExtra(INTENT_CAMERA_TAG)
        
        val thread = Thread(Runnable {
            Handler(Looper.getMainLooper()).post{
                Toast.makeText(this, "결과 예측중. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show()
                progressbar.visibility = View.VISIBLE
            }
            // 19.11.06 사진촬영한 것 소켓 통신 구현 - 이정묵 > 19.11.07 Thread로 구현, 로딩 
            val file = File(bitmapStringURI)
            val Comp = Compressor(this).compressToFile(file)
            val content = Files.readAllBytes(Comp.toPath())
            val ip = "121.169.158.111" // 192.168.0.0
            val port = 7070 // 여기에 port를 입력해주세요

            val socket = Socket(ip, port) // ip와 port를 입력하여 클라이언트 소켓을 만듭니다.
            val outStream = socket.outputStream // outputStream - 데이터를 내보내는 스트림입니다.

            val data = content.size.toString().toByteArray() + ":".toByteArray() + content// 데이터는 byteArray로 변경 할 수 있어야 합니다.

            outStream.write(data) // toByteArray() 파라미터로 charset를 설정할 수 있습니다. 기본값 utf-8

            val byteArr = ByteArray(1024)
            val ins = socket.getInputStream()
            val readByteCount = ins.read(byteArr)
            val msg = String(byteArr, 0, readByteCount)
            val test = Uri.parse("$msg").toString()
            RESULT = test
            resultView.text = RESULT

            Handler(Looper.getMainLooper()).post{
                progressbar.visibility = View.GONE

                resultView.setOnClickListener {
                    getDogDicData(RESULT)
                }
            }
        })

        thread.start()

        Glide
            .with(this)
            .asBitmap()
            .load(bitmapStringURI)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(mImageView)
    }

    private fun getRealPathFromURI(contentUri: Uri):String {
        var res  = ""
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor  = this?.contentResolver?.query(contentUri, proj, null, null, null)
        if (cursor!!.moveToFirst())
        {
            val column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor.close()
        return res
    }

    private fun getDogDicData(resultDog: String) {
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        Log.d("CHECK", resultDog)

        db.firestoreSettings = settings
        val thread = Thread(Runnable {
            Handler(Looper.getMainLooper()).post{
                Toast.makeText(this, "사전 불러오는 중", Toast.LENGTH_SHORT).show()
            }

            val firstResultString = resultDog.split(",".toRegex())

            db.collection("DogBreeds").document(firstResultString[0]).get()
                .addOnSuccessListener { document ->
                    val intent = Intent(this, DetailDict::class.java)

                    if (document != null) {
                        intent.putExtra("name", document.data!!["name"] as String)
                        intent.putExtra("psn", document.data!!["psn"] as String)
                        intent.putExtra("birth", document.data!!["birth"] as String)
                        intent.putExtra("life", document.data!!["life"] as String)

                        startActivity(intent)
                    }
                    else
                        Log.d("CHECK", "No such document")
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
        })
        thread.start()
    }

}
