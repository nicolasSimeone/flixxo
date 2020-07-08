package com.flixxo.apps.flixxoapp.utils


import android.content.Context
import android.content.res.AssetManager
import android.os.Environment
import android.system.Os.mkdir
import com.flixxo.apps.flixxoapp.App
import com.flixxo.apps.flixxoapp.view.EpisodesItem
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.masterwok.simplevlcplayer.fragments.SerieInteractivePlayer
import org.koin.dsl.module.applicationContext
import timber.log.Timber
import java.io.BufferedOutputStream
import java.io.File
import java.util.*

class FileHelper {
    companion object {
        private val torrentDownloadedDirectory: File = File(Environment.getExternalStorageDirectory(), "/Flixxo")

        fun getTorrentDirectory(context: Context, uuid: String): File {
            getTorrentDownloadedDirectory(context).apply {
                if (!exists()) mkdir()
            }

            return File(getTorrentDownloadedDirectory(context), uuid).apply {
                if (!exists()) mkdir()

            }

        }

        fun getTorrentDownloadedDirectory(context: Context): File {
            return File(context.filesDir, "/Flixxo")

        }

        fun getMedia(context: Context): File {
            val filepath = getTorrentDownloadedDirectory(context).absolutePath + "/"
            val file = File("$filepath.nomedia")
            file.apply {
                if (!exists()) file.createNewFile()
            }
            return file
        }

        fun getNone(context: Context): File {
            val filepath = getTorrentDownloadedDirectory(context).absolutePath + "/"
            val file = File(filepath + "none.vtt")
            file.apply {
                if (!exists()) {
                    file.createNewFile()
                    file.printWriter().use { out ->
                        out.println("WEBVTT")
                        out.println((""))
                        out.println("1")
                        out.println("00:00:01.000 --> 00:00:02.000")
                        out.println((" "))
                    }
                }
            }
            return file
        }

        fun deleteTorrentFolder(context: Context): Boolean = getTorrentDownloadedDirectory(context).deleteRecursively()

        fun deleteCacheByTime(context: Context) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_MONTH, -2)
            getTorrentDownloadedDirectory(context).walkTopDown().forEach {
                if ((!it.absolutePath.equals(getTorrentDownloadedDirectory(context).absolutePath) && it.isDirectory && Date(
                        it.lastModified()
                    ).before(cal.time)) or Date(it.lastModified()).after(Calendar.getInstance().time)
                ) {
                    it.deleteRecursively()
                }
            }
        }

        fun isEnoughSpace(context: Context, lenghtFile: Long): Boolean {
            var freeBytesInternal: Long = File(context.filesDir.absoluteFile.toString()).freeSpace
            return lenghtFile < freeBytesInternal
        }

        fun getSeriesNavigation(assetManager: AssetManager) : ArrayList<SerieInteractivePlayer> {
            val data = assetManager.readAssetFile("alt_esc.json")
            val type = object : TypeToken<ArrayList<SerieInteractivePlayer>>() {}.type
            return Gson().fromJson<ArrayList<SerieInteractivePlayer>>(data, type)
        }
    }
}