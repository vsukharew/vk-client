package vsukharew.vkclient.common.extension

import vsukharew.vkclient.common.domain.model.Result

fun <T, R> Result<T>.map(
    dataMapper: ((T) -> R),
) : Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(dataMapper.invoke(this.data))
        is Result.SuccessNoBody -> Result.SuccessNoBody
        is Result.Error.HttpError.ServerError -> Result.Error.HttpError.ServerError(this.httpCode, this.errorBody)
        is Result.Error.HttpError.ClientError -> Result.Error.HttpError.ClientError(this.httpCode)
        is Result.Error.HttpError.OtherHttpError -> Result.Error.HttpError.OtherHttpError(this.httpCode)
        is Result.Error.NetworkError -> Result.Error.NetworkError(this.e)
        is Result.Error.UnknownError -> Result.Error.UnknownError(this.e)
    }
}