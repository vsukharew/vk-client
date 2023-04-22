package vsukharew.vkclient

import org.junit.Assert
import org.junit.Test
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.domain.model.Left
import vsukharew.vkclient.common.domain.model.Right
import vsukharew.vkclient.common.extension.safeNonNull
import vsukharew.vkclient.common.extension.sideEffect

class MonadComprehensionDomainTest {

    @Test
    fun `sideEffect - safeNonNull { } succeeded - should return result from either scope`() {
        val expected = User(ID)
        val either = sideEffect<AppError, User?> {
            val maybeUser = realRightRealUser().bind()
            safeNonNull {
                val user = maybeUser.bind()
                user
            }
        }
        val actual = (either as Right).data
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `sideEffect - safeNonNull { } failed - should return left with NullableBindingException`() {
        val either = sideEffect<AppError, User?> {
            val maybeUser = realRightFakeUser().bind()
            val user = safeNonNull { maybeUser.bind() }
            user
        }
        assert(either is Left)
    }

    private fun fakeUser(): User? = null
    private fun realUser(): User = User(ID)
    private fun realRightRealUser(): Either<AppError, User?> = Right(realUser())
    private fun realRightFakeUser(): Either<AppError, User?> = Right(fakeUser())

    private data class User(val id: Int)

    private companion object {
        private const val ID = 1
    }
}