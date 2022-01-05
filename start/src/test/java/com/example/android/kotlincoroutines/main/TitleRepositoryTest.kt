/*
 * Copyright (C) 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.kotlincoroutines.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.kotlincoroutines.fakes.MainNetworkCompletableFake
import com.example.android.kotlincoroutines.fakes.MainNetworkFake
import com.example.android.kotlincoroutines.fakes.TitleDaoFake
import com.google.common.truth.Truth
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test

class TitleRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    /**
     * Средство выполнения тестов ничего не знает о сопрограммах,
     * поэтому мы не можем сделать этот тест suspend функцией.
     * В библиотеке kotlinx-coroutines-test есть runBlockingTest функция,
     * которая блокирует, пока вызывается suspend функция.
     *
     * В этом тесте используются предоставленные подделки для проверки того,
     * что "ОК" вставлено в базу данных с помощью refreshTitle.
     * Когда тест вызывает runBlockingTest, он будет блокироваться до тех пор,
     * пока не  завершится запуск сопрограммы .
     * Затем внутри, когда мы вызываем, refreshTitle он использует обычный механизм
     * приостановки и возобновления, чтобы дождаться добавления строки в нашу подделку базы данных.

    После завершения тестовой сопрограммы runBlockingTest возвращается.
     */
    @Test
    fun whenRefreshTitleSuccess_insertsRows() = runBlockingTest {
        val titleDao = TitleDaoFake("title")
        val subject = TitleRepository(
            MainNetworkFake("OK"),
            titleDao
        )

        subject.refreshTitle()
        Truth.assertThat(titleDao.nextInsertedOrNull()).isEqualTo("OK")
    }

    /**
     * Мы хотим добавить короткий таймаут к сетевому запросу.
     */
    @Test(expected = TitleRefreshError::class)
    fun whenRefreshTitleTimeout_throws() = runBlockingTest {
        val network = MainNetworkCompletableFake()
        val subject = TitleRepository(
            network,
            TitleDaoFake("title")
        )

        launch {
            subject.refreshTitle()
        }

        advanceTimeBy(5_000)
    }
}