package vsukharew.vkclient.common.extension

import vsukharew.vkclient.common.domain.model.Result
import java.lang.IllegalArgumentException

fun <T, R> Result<T>.map(
    dataMapper: ((T) -> R),
) : Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(dataMapper.invoke(data))
        else -> mapUnsuccessfulResults()
    }
}

suspend fun <T, R> Result<T>.switchMap(
    dataMapper: suspend ((T) -> Result<R>),
) : Result<R> {
    return when (this) {
        is Result.Success -> dataMapper.invoke(data)
        else -> mapUnsuccessfulResults()
    }
}

fun <T> Result<T>.ifSuccess(block: (T) -> Unit): Result<T> {
    return when (this) {
        is Result.Success -> this.also { block.invoke(data) }
        else -> this
    }
}

private fun <T, R> Result<T>.mapUnsuccessfulResults() : Result<R> {
    return when (this) {
        is Result.Error.HttpError.ServerError ->
            Result.Error.HttpError.ServerError(this.httpCode, this.errorBody)
        is Result.Error.HttpError.ClientError.OtherClientError ->
            Result.Error.HttpError.OtherHttpError(this.httpCode)
        is Result.Error.HttpError.ClientError.UnauthorizedError ->
            Result.Error.HttpError.ClientError.UnauthorizedError
        is Result.Error.HttpError.OtherHttpError ->
            Result.Error.HttpError.OtherHttpError(this.httpCode)
        is Result.Error.NetworkError -> Result.Error.NetworkError(this.e)
        is Result.Error.UnknownError -> Result.Error.UnknownError(this.e)
        else -> throw IllegalArgumentException("Result is Result.Success")
    }
}