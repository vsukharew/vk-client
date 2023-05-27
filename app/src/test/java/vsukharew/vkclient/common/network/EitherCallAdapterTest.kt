package vsukharew.vkclient.common.network

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.java.KoinJavaComponent.getKoin
import retrofit2.Retrofit
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.AppError.RemoteError.*
import vsukharew.vkclient.common.domain.model.AppError.RemoteError.ErrorBody.RequestParam
import vsukharew.vkclient.common.domain.model.Left
import vsukharew.vkclient.common.domain.model.Right
import vsukharew.vkclient.common.network.calladapter.utils.INTERNAL_SERVER_ERROR
import vsukharew.vkclient.common.network.calladapter.utils.TOO_MUCH_REQUESTS_PER_SECOND
import vsukharew.vkclient.common.network.response.DEFAULT_STRING
import vsukharew.vkclient.features.CoroutineDispatcherRule
import java.net.HttpURLConnection.HTTP_OK
import java.nio.charset.StandardCharsets
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@ExperimentalCoroutinesApi
open class EitherCallAdapterTest {

    private val httpClient by lazy { OkHttpClient.Builder().build() }
    protected val mockServer = MockWebServer()
    protected val retrofit by lazy { getKoin().get<Retrofit>() }

    @get:Rule
    val rule = CoroutineDispatcherRule()

    @Before
    fun setUp() {
        mockServer.start()
        startKoin { modules(networkModule(mockServer.url("/").toString(), httpClient)) }
    }

    @After
    fun shutDown() {
        mockServer.shutdown()
        stopKoin()
    }

    //region success
    @Test
    fun `either call - successful response with valid json - should return result`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HTTP_OK)
                .setBody(responseBody(VALID_RESPONSE_FILENAME))
        )
        val api = createApi<TestApi>()
        val result = runBlocking { api.getFoo() }
        assertTrue { result is Right }
    }

    @Test
    fun `either call - successful response with valid 'response' and 'error' fields - should return result from 'response'`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HTTP_OK)
                .setBody(responseBody(RESPONSE_WITH_BOTH_DATA_AND_ERROR_FILENAME))
        )
        val api = createApi<TestApi>()
        val result = runBlocking { api.getFoo() }
        assertTrue { result is Right }
    }
    //endregion

    //region expected errors
    @Test
    fun `either call - successful response with completely empty json - should return UnknownError`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HTTP_OK)
                .setBody(responseBody(COMPLETELY_EMPTY_RESPONSE_FILENAME))
        )
        val expected = Left(UnknownError)
        val api = createApi<TestApi>()
        val actual = runBlocking { api.getFoo() }
        assertEquals(expected, actual)
    }

    @Test
    fun `either call - error response with error code '1' - should return UnknownError`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HTTP_OK)
                .setBody(responseBody(UNKNOWN_ERROR_RESPONSE_FILENAME))
        )
        val expected = Left(UnknownError)
        val api = createApi<TestApi>()
        val actual = runBlocking { api.getFoo() }
        assertEquals(expected, actual)
    }

    @Test
    fun `either call - error response with error code '5' - should return UnauthorizedError`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HTTP_OK)
                .setBody(responseBody(UNAUTHORIZED_RESPONSE_FILENAME))
        )
        val expected = Left(Unauthorized)
        val api = createApi<TestApi>()
        val actual = runBlocking { api.getFoo() }
        assertEquals(expected, actual)
    }

    @Test
    fun `either call - error response with error code '6' - should return TooMuchRequestsPerSecond`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HTTP_OK)
                .setBody(responseBody(TOO_MANY_REQUESTS_RESPONSE_FILENAME))
        )
        val expected = Left(
            TooMuchRequestsPerSecond(
                ErrorBody(
                    TOO_MUCH_REQUESTS_PER_SECOND,
                    "unknown error",
                    listOf(
                        RequestParam(
                            "screen_name",
                            "vk_user"
                        ),
                        RequestParam(
                            "method",
                            "utils.resolveScreenName"
                        ),
                    )
                )
            )
        )
        val api = createApi<TestApi>()
        val actual = runBlocking { api.getFoo() }
        assertEquals(expected, actual)
    }

    @Test
    fun `either call - error response with error code '10' - should return ServerError`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HTTP_OK)
                .setBody(responseBody(INTERNAL_SERVER_ERROR_RESPONSE_FILENAME))
        )
        val expected = Left(
            ServerError(
                ErrorBody(
                    INTERNAL_SERVER_ERROR,
                    "unknown error",
                    listOf(
                        RequestParam(
                            "screen_name",
                            "vk_user"
                        )
                    )
                )
            )
        )
        val api = createApi<TestApi>()
        val actual = runBlocking { api.getFoo() }
        assertEquals(expected, actual)
    }

    @Test
    fun `either call - error response with arbitrary error code - should return UnknownError`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HTTP_OK)
                .setBody(responseBody(INTERNAL_SERVER_ERROR_RESPONSE_FILENAME))
        )
        val expected = Left(
            ServerError(
                ErrorBody(
                    INTERNAL_SERVER_ERROR,
                    "unknown error",
                    listOf(
                        RequestParam(
                            "screen_name",
                            "vk_user"
                        )
                    )
                )
            )
        )
        val api = createApi<TestApi>()
        val actual = runBlocking { api.getFoo() }
        assertEquals(expected, actual)
    }
    //endregion

    //region unexpected errors
    @Test
    fun `either call - successful response with invalid json - should return network exception`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HTTP_OK)
                .setBody(responseBody(MALFORMED_JSON_FILENAME))
        )
        val api = createApi<TestApi>()
        val result = runBlocking { api.getFoo() }
        assertTrue { result is Left && result.data is AppError.UnknownError }
    }

    @Test
    fun `either call - error response, error code '10', no error message and request params - should not fail and return ServerError`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HTTP_OK)
                .setBody(responseBody(ERROR_RESPONSE_WITHOUT_MESSAGE_AND_PARAMS_FILENAME))
        )
        val expected = Left(
            ServerError(
                ErrorBody(
                    10,
                    DEFAULT_STRING,
                    emptyList()
                )
            )
        )
        val api = createApi<TestApi>()
        val actual = runBlocking { api.getFoo() }
        assertEquals(expected, actual)
    }

    @Test
    fun `either call - json hierarchy is different than return type - should return UnknownError`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HTTP_OK)
                .setBody(responseBody(PROFILE_INFO_VALID_RESPONSE_FILENAME))
        )
        val api = createApi<TestApi>()
        val result = runBlocking { api.getFoo() }
        assertTrue { result is Left && result.data is AppError.UnknownError }
    }
    //endregion

    protected fun responseBody(jsonResourceName: String): String {
        return javaClass.classLoader
            ?.getResourceAsStream(jsonResourceName)
            ?.source()
            ?.buffer()
            ?.readString(StandardCharsets.UTF_8) ?: fail(BUFFER_IS_NULL_MESSAGE)
    }

    protected inline fun <reified T> createApi(): T = retrofit.create(T::class.java)

    private companion object {
        private const val BUFFER_IS_NULL_MESSAGE = "buffer is null"
        private const val MALFORMED_JSON_FILENAME =
            "api/common/error/response_with_malformed_json.json"
        private const val VALID_RESPONSE_FILENAME = "api/common/success/valid_response.json"
        private const val PROFILE_INFO_VALID_RESPONSE_FILENAME =
            "api/account/profile_info_valid_response.json"
        private const val RESPONSE_WITH_BOTH_DATA_AND_ERROR_FILENAME =
            "api/common/success/response_with_both_data_and_error.json"
        private const val COMPLETELY_EMPTY_RESPONSE_FILENAME =
            "api/common/success/completely_empty_response.json"
        private const val UNAUTHORIZED_RESPONSE_FILENAME =
            "api/common/error/unauthorized_error_response.json"
        private const val TOO_MANY_REQUESTS_RESPONSE_FILENAME =
            "api/common/error/too_many_requests_per_second_error_response.json"
        private const val INTERNAL_SERVER_ERROR_RESPONSE_FILENAME =
            "api/common/error/internal_server_error_response.json"
        private const val UNKNOWN_ERROR_RESPONSE_FILENAME =
            "api/common/error/unknown_error_response.json"
        private const val ERROR_RESPONSE_WITHOUT_MESSAGE_AND_PARAMS_FILENAME =
            "api/common/error/error_response_without_message_and_params.json"
    }
}