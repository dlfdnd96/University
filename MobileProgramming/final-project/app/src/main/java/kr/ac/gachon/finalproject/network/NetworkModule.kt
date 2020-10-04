/**
 * 공공데이터를 불러오기 위한 네트워크 모듈
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.network

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

object NetworkModule {
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    /**
     * 공공데이터를 불러올 때 사용하는 사이트 URL 설정
     *
     * @since: 2019-06-04
     * @author: 류일웅
     * @param: pageCount
     * @return: HttpUrl
     */
    fun makeHttpUrl(pageCount: Int) : HttpUrl {
        return HttpUrl.Builder()
            .scheme("http")
            .host("openapi.seoul.go.kr")
            .port(8088)
            .addPathSegment("69645a7277646c66343063486e4c56") // api key
            .addPathSegment("json") // JSON으로 불러오기
            .addPathSegment("ListPriceModelStoreProductService")
            .addPathSegment(pageCount.toString()) // 최대 1000개의 데이터를 불러올 수 있다
            .addPathSegment((pageCount + 999).toString())
            .build()
    }

    fun makeHttpRequest(httpUrl: HttpUrl) : Request {
        return Request.Builder()
            .url(httpUrl)
            .get().build()
    }
}