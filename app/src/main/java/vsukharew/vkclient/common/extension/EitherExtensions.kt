package vsukharew.vkclient.common.extension

import vsukharew.vkclient.common.domain.model.Either

fun <L, R, V> Either<L, R>.map(
    dataMapper: ((R) -> V),
): Either<L, V> {
    return when (this) {
        is Either.Right -> Either.Right(dataMapper.invoke(data))
        is Either.Left -> this
    }
}

suspend fun <L, R, V> Either<L, R>.switchMap(
    dataMapper: suspend ((R) -> Either<L, V>),
): Either<L, V> {
    return when (this) {
        is Either.Right -> dataMapper.invoke(data)
        is Either.Left -> this
    }
}

fun <L, R> Either<L, R>.ifSuccess(block: (R) -> Unit): Either<L, R> {
    return when (this) {
        is Either.Right -> this.also { block.invoke(data) }
        else -> this
    }
}