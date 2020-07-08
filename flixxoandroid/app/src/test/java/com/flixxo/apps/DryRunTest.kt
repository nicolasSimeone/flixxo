package com.flixxo.apps

import com.flixxo.apps.flixxoapp.di.dbModule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.checkModules

class DryRunTest : KoinTest {

    @Test
    fun `check modules integrity`() {
        checkModules(listOf(dbModule))
    }
}