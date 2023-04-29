package vsukharew.vkclient.common.network

import retrofit2.http.GET
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.network.response.ResponseWrapper

interface TestApi {

    @GET("foo")
    suspend fun getFoo(): Either<AppError, ResponseWrapper<Int>>
}