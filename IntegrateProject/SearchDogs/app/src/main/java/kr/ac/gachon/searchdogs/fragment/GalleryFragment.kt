/**
 * 갤러리 화면을 구성
 *
 * @since: 2019.10.01
 * @author: 류일웅
 */
package kr.ac.gachon.searchdogs.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.ac.gachon.searchdogs.activity.DogImageActivity
import kr.ac.gachon.searchdogs.R
import kr.ac.gachon.searchdogs.service.Permission

class GalleryFragment : Fragment() {

    private val permission = Permission()

    private lateinit var mButtonView: Button

    private val imagePickCode = 1000
    private val intentGalleryType = "image/*"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)

        mButtonView = view.findViewById(R.id.fragmentGallery__btn_galleryUpload)

        view.findViewById<Button>(R.id.fragmentGallery__btn_galleryUpload).setOnClickListener {
            startGallery()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * ##################################################
     * 스마트폰의 갤러리를 실행하는 기능
     *
     * @since: 2019.10.01
     * @author: 류일웅
     * @param:
     * @return:
     * ##################################################
     */
    private fun startGallery() {
        if (permission.hasNoPermissions(activity!!)) {
            permission.requestPermission(activity!!)
        }else{
            val galleryIntent = Intent(Intent.ACTION_PICK)

            galleryIntent.type = intentGalleryType

            // fragment가 MainActivity에 존재하는지 확인
            if (isAdded) {
                startActivityForResult(galleryIntent, imagePickCode)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val intent = Intent(activity!!, DogImageActivity::class.java)

        if (requestCode == imagePickCode && resultCode == Activity.RESULT_OK) {
            val filePath = data?.data.toString()

            intent.putExtra(INTENT_GALLERY_TAG, filePath)

            startActivity(intent)
        }
    }

    companion object {
        const val INTENT_GALLERY_TAG = "GalleryBitmap"
    }

}
