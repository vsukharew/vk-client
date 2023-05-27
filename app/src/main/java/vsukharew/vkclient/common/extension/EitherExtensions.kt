package vsukharew.vkclient.common.extension

import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.domain.model.Left
import vsukharew.vkclient.common.domain.model.Right

fun <L, R, V> Either<L, R>.map(
    dataMapper: ((R) -> V),
): Either<L, V> {
    return when (this) {
        is Right -> Right(dataMapper.invoke(data))
        is Left -> this
    }
}

suspend fun <L, R, V> Either<L, R>.switchMap(
    dataMapper: suspend ((R) -> Either<L, V>),
): Either<L, V> {
    return when (this) {
        is Right -> dataMapper.invoke(data)
        is Left -> this
    }
}

fun <L, R> Either<L, R>.ifSuccess(block: (R) -> Unit): Either<L, R> {
    return when (this) {
        is Right -> this.also { block.invoke(data) }
        else -> this
    }
}

inline fun <L, R> Either<L, R>.doIfLeft(ifLeft: (L) -> Unit): Either<L, R> {
    return when (this) {
        is Right -> this
        is Left -> this.apply { ifLeft.invoke(this.data) }
    }
}

inline fun <L, R, V> Either<L, R>.fold(ifLeft: (L) -> V, ifRight: (R) -> V): V {
    return when (this) {
        is Right -> ifRight.invoke(this.data)
        is Left -> ifLeft.invoke(this.data)
    }
}

inline fun <L, R> Either<L, R>.resolve(ifLeft: (L) -> Unit, ifRight: (R) -> Unit) {
    when (this) {
        is Right -> ifRight.invoke(this.data)
        is Left -> ifLeft.invoke(this.data)
    }
}