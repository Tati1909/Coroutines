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
import com.example.android.kotlincoroutines.fakes.MainNetworkFake
import com.example.android.kotlincoroutines.fakes.TitleDaoFake
import com.example.android.kotlincoroutines.main.utils.MainCoroutineScopeRule
import com.example.android.kotlincoroutines.main.utils.getValueForTest
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Правило - это способ запуска кода до и после выполнения теста в JUnit.
 * Два правила используются,
 * чтобы позволить нам протестировать MainViewModel в тесте вне устройства.
 */
class MainViewModelTest {
    /**
     * MainCoroutineScopeRule- это настраиваемое правило в этой кодовой базе,
     * которое настраивается Dispatchers.Main на использование TestCoroutine Dispatcher
     * from kotlinx-coroutines-test. Это позволяет тестам продвигать виртуальные часы
     * для тестирования и позволяет использовать код Dispatchers.Main в модульных тестах.
     */
    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    /**
     * InstantTaskExecutorRule это правило JUnit,
     * которое настраивает LiveData синхронное выполнение каждой задачи
     */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var subject: MainViewModel

    /**
     * В этом setup методе новый экземпляр MainViewModel создается с
     * использованием подделок тестирования - это поддельные реализации сети и базы данных,
     * представленные в стартовом коде, чтобы помочь писать тесты без использования реальной сети
     * или базы данных. Для этого теста подделки нужны только для удовлетворения
     * зависимостей MainViewModel.
     */
    @Before
    fun setup() {
        subject = MainViewModel(
            TitleRepository(
                MainNetworkFake("OK"),
                TitleDaoFake("initial")
            )
        )
    }

    /**
     * При вызове onMainViewClicked будет запущена только что созданная сопрограмма.
     * Этот тест проверяет, что текст нажатий остается «0 нажатий» сразу после
     * onMainViewClicked вызова, а через 1 секунду он обновляется до «1 нажатий» .

    Этот тест использует виртуальное время для управления выполнением сопрограммы,
    запущенной onMainViewClicked. MainCoroutineScopeRule Позволяет приостановить,
    возобновить или контролировать выполнение сопрограмм, которые запускаются на Dispatchers.Main.
    Здесь мы вызываем, advanceTimeBy(1_000), что заставит главный диспетчер
    немедленно выполнить сопрограммы, которые должны возобновиться на 1 секунду позже.

    Этот тест полностью детерминирован, что означает, что он всегда будет выполняться одинаково.
    И, поскольку он полностью контролирует выполнение запускаемых сопрограмм, Dispatchers.Main
    ему не нужно ждать одну секунду, пока значение не будет установлено.
     */
    @Test
    fun whenMainClicked_updatesTaps() {
        subject.onMainViewClicked()
        Truth.assertThat(subject.taps.getValueForTest()).isEqualTo("0 taps")
        coroutineScope.advanceTimeBy(1000)
        Truth.assertThat(subject.taps.getValueForTest()).isEqualTo("1 taps")
    }
}