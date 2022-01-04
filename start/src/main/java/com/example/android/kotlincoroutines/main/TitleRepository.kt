package com.example.android.kotlincoroutines.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
     * Этот код по-прежнему использует блокирующие вызовы.
     * Оба вызова execute и insertTitle будут блокировать один из потоков IO(ввода-вывода).

     * Сопрограмма (viewModelScope.launch {} в refreshTitle в MainViewModel), которая вызвала эту функцию,
     * будет приостановлена до завершения withContext.
     * withContext возвращает свой результат обратно диспетчеру, который его вызвал Dispatchers.Main
     */
    suspend fun refreshTitle() {
        withContext(Dispatchers.IO) {
            val result = try {
                // Делаем сетевой запрос, используя блокирующий вызов
                network.fetchNextTitle().execute()
            } catch (cause: Throwable) {
                // If the network throws an exception, inform the caller
                throw TitleRefreshError("Unable to refresh title", cause)
            }

            if (result.isSuccessful) {
                // Сохраняем в БД
                titleDao.insertTitle(Title(result.body()!!))
            } else {
                // If it's not successful, inform the callback of the error
                throw TitleRefreshError("Unable to refresh title", null)
            }
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
