package vsukharew.vkclient.account

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import vsukharew.vkclient.account.data.model.ProfileInfoResponse
import vsukharew.vkclient.account.data.model.ScreenNameResponse
import vsukharew.vkclient.account.data.network.AccountApi
import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.domain.model.Left
import vsukharew.vkclient.common.domain.model.Right
import vsukharew.vkclient.common.extension.EMPTY
import vsukharew.vkclient.common.network.EitherCallAdapterTest
import vsukharew.vkclient.common.network.TestApi
import vsukharew.vkclient.common.network.response.DEFAULT_STRING
import vsukharew.vkclient.common.network.response.ResponseWrapper
import java.net.HttpURLConnection
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class AccountApiTest : EitherCallAdapterTest() {

    @Test
    fun `getProfileInfo - expected properties were not received - should terminate successfully with default values`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(responseBody(PROFILE_INFO_SOME_PROPERTIES_ABSENT))
        )
        val expected = Right(
            ResponseWrapper(
                ProfileInfoResponse(
                    DEFAULT_STRING,
                    DEFAULT_STRING,
                    null
                ),
                null
            )
        )
        val api = createApi<AccountApi>()
        val actual = runBlocking { api.getProfileInfo() }
        assertEquals(expected, actual)
    }

    @Test
    fun `resolveScreenName - empty list json - should return UnresolvedScreenNameResponse`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(responseBody(RESOLVE_SCREEN_NAME_NAME_AVAILABLE_FILENAME))
        )
        val expected: Either<AppError, ResponseWrapper<ScreenNameResponse>> = Right(
            ResponseWrapper(
                ScreenNameResponse.UnresolvedScreenNameResponse,
                null
            )
        )
        val api = createApi<AccountApi>()
        val actual = runBlocking { api.resolveScreenName(String.EMPTY) }
        assertEquals(expected, actual)
    }

    @Test
    fun `resolveScreenName - json with data - should return ResolvedScreenNameResponse`() {
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(responseBody(RESOLVE_SCREEN_NAME_NAME_BUSY_FILENAME))
        )
        val expected: Either<AppError, ResponseWrapper<ScreenNameResponse>> = Right(
            ResponseWrapper(
                ScreenNameResponse.ResolvedScreenNameResponse,
                null
            )
        )
        val api = createApi<AccountApi>()
        val actual = runBlocking { api.resolveScreenName(String.EMPTY) }
        assertEquals(expected, actual)
    }

    private companion object {
        private const val PROFILE_INFO_SOME_PROPERTIES_ABSENT =
            "api/account/profile_info_expected_properties_absent.json"
        private const val RESOLVE_SCREEN_NAME_NAME_AVAILABLE_FILENAME =
            "api/account/resolve_screen_name_name_available.json"
        private const val RESOLVE_SCREEN_NAME_NAME_BUSY_FILENAME =
            "api/account/resolve_screen_name_name_busy.json"
    }
}