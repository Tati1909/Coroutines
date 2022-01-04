package com.example.android.kotlincoroutines.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.map

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
     * Используем Retrofit и Room, чтобы получить новый заголовок из сети и
     * записать его в базу данных с помощью сопрограмм.
     *
     * Мы избавились от файла withContext.
     * Поскольку и Room, и Retrofit предоставляют main-safe suspend функции,
     * можно безопасно организовать эту асинхронную работу из Dispatchers.Main.
     */
    suspend fun refreshTitle() {
        try {
            // Делаем сетевой запрос, используя блокирующий вызов
            network.fetchNextTitle()
            // Сохраняем в БД
            titleDao.insertTitle(Title(result))
        } catch (cause: Throwable) {
            // If the network throws an exception, inform the caller
            throw TitleRefreshError("Unable to refresh title", cause)
        }
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
