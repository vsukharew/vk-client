package vsukharew.vkclient.account

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import vsukharew.vkclient.account.data.model.ProfileInfoResponse
import vsukharew.vkclient.account.data.network.AccountApi
import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Left
import vsukharew.vkclient.common.domain.model.Right
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

    private companion object {
        private const val PROFILE_INFO_SOME_PROPERTIES_ABSENT =
            "api/account/profile_info_some_properties_absent.json"
    }
}