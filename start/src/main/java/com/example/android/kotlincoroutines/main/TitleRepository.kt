package com.example.android.kotlincoroutines.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.delay

/**
 * TitleRepository предоставляет интерфейс для получения заголовка или запроса создания нового.
 *
 * Модули репозитория обрабатывают операции с данными. Они предоставляют чистый API,
 * так что остальная часть приложения может легко получить эти данные.
 * Они знают, откуда брать данные и какие вызовы API делать
 * при обновлении данных. Вы можете рассматривать репозитории как посредников между разными данными.
 * sources, в нашем случае он является посредником между сетевым API и автономным кешем базы данных.
 */
class TitleRepository(val network: MainNetwork, val titleDao: TitleDao) {

    /**
     * [LiveData] для загрузки заголовка.
     *
     * Это основной интерфейс для загрузки заголовка. Заголовок загрузится из офлайн кеш.
     *
     * Observing не приведет к обновлению заголовка,
     * используйте TitleRepository.refreshTitle, чтобы обновить заголовок.
     */
    val title: LiveData<String?> = titleDao.titleLiveData.map { it?.title }

    /**
     * Когда вы закончите с этой кодовой лабораторией, вы обновите ее,
     * чтобы использовать Retrofit и Room, чтобы получить новый заголовок и
     * записать его в базу данных с помощью сопрограмм.
     * На данный момент он просто потратит 500 миллисекунд, притворившись,
     * что выполняет работу, а затем продолжит.
     */
    suspend fun refreshTitle() {
        // TODO: Refresh from network and write to database
        delay(500)
    }
}

/**
 * Thrown when there was a error fetching a new title
 *
 * @property message user ready error message
 * @property cause the original cause of this exception
 */
class TitleRefreshError(
    message: String,
    cause: Throwable?
) : Throwable(message, cause)
