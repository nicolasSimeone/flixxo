package com.flixxo.apps.flixxoapp

import com.flixxo.apps.flixxoapp.utils.TorrentHelper
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun getMagnetURL() {

        val magnet = TorrentHelper.getMagnetForTorrent("22222", "11111")

        val hdMagnet = "magnet:?xt=urn:btih:22222&tr=11111"
        assertEquals(hdMagnet, magnet)
    }
}
