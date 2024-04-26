package com.jerry.jetpack_take_select_photo_image_2.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerry.jetpack_take_select_photo_image_2.api.FileApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

class MainViewModel: ViewModel() {

    private lateinit var context: Context

    fun setContext(context: Context){
        this.context = context
    }

    fun upload(uri: Uri){
        val api  = provideFileApi()
        val path = getRealPathFromURI(uri = uri)

        val file = File(path)
        val image =  MultipartBody.Part
            .createFormData(
                "image",
                file.name,
                file.asRequestBody()
            )
        viewModelScope.launch {
//            val result = api.formWithImage(
//                name = "name",
//                image =  image,
//            )

            val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", "name")
                .addFormDataPart("avatarUrl", file.name, file.asRequestBody())
                .build()



            val result = api.formWithImage2(body = multipartBody)
//
            println("result:${result}")
        }
    }

    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    fun provideOKHttpClientLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    fun provideOKHttpClientInterceptor(): Interceptor {
        return object: Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {

                val original = chain.request()

                val newRequest = original.newBuilder()
                    //.removeHeader("User-Agent")
                    //.addHeader("User-Agent", "other-user-agent")

                    //.addHeader("Accept-Encoding", "deflate")
                    //.addHeader("Cache-Control", "no-cache")
                    .addHeader("Content-Type","application/json")

                    .build()

                return chain.proceed(newRequest)
            }
        }
    }

    fun provideOKHttpClient(logInterceptor: HttpLoggingInterceptor, interceptor: Interceptor): OkHttpClient {
        return  OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(logInterceptor)
            .addInterceptor(interceptor)
            .build()
    }

    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://postman-echo.com")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .build()
    }

    fun provideFileApi(): FileApi {
        val moshi: Moshi = provideMoshi()
        val logInterceptor: HttpLoggingInterceptor = provideOKHttpClientLoggingInterceptor()
        val client: OkHttpClient = provideOKHttpClient(
            logInterceptor = logInterceptor,
            interceptor = provideOKHttpClientInterceptor()
        )

        val retrofit: Retrofit = provideRetrofit(client = client, moshi = moshi)
        return retrofit.create(FileApi::class.java)

    }

    fun getRealPathFromURI(uri: Uri ): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex =  returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = returnCursor.getLong(sizeIndex).toString()
        val file = File(context.filesDir, name)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream?.available() ?: 0
            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream?.read(buffers).also {
                    if (it != null) {
                        read = it
                    }
                } != -1) {
                outputStream.write(buffers, 0, read)
            }
            Log.e("File Size", "Size " + file.length())
            inputStream?.close()
            outputStream.close()
            Log.e("File Path", "Path " + file.path)

        } catch (e: java.lang.Exception) {
            Log.e("Exception", e.message!!)
        }
        return file.path
    }


//    private fun fileFromContentUri(context: Context, contentUri: Uri): File {
//
//        val fileExtension = getFileExtension(context, contentUri)
//        val fileName = "temporary_file" + if (fileExtension != null) ".$fileExtension" else ""
//
//        val tempFile = File(context.cacheDir, fileName)
//        tempFile.createNewFile()
//
//        try {
//            val oStream = FileOutputStream(tempFile)
//            val inputStream = context.contentResolver.openInputStream(contentUri)
//
//            inputStream?.let {
//                copy(inputStream, oStream)
//            }
//
//            oStream.flush()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return tempFile
//    }
//
//    private fun getFileExtension( uri: Uri): String? {
//        val fileType: String? = context.contentResolver.getType(uri)
//        return context.MimeTypeMap .getSingleton().getExtensionFromMimeType(fileType)
//    }
//
//    @Throws(IOException::class)
//    private fun copy(source: InputStream, target: OutputStream) {
//        val buf = ByteArray(8192)
//        var length: Int
//        while (source.read(buf).also { length = it } > 0) {
//            target.write(buf, 0, length)
//        }
//    }
}