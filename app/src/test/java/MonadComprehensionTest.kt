import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.verify
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.domain.model.Left
import vsukharew.vkclient.common.domain.model.Right
import vsukharew.vkclient.common.extension.sideEffect

class MonadComprehensionTest {

    @Test
    fun `sideEffect - bind() succeeded - should return right`() {
        val either = sideEffect<String, User?> { realRight().bind() }
        assert(either is Right)
    }

    @Test
    fun `sideEffect - bind() succeeded - should return data from scope`() {
        val expected = User(ID)
        val either = sideEffect<String, User?> { realRight().bind() }
        val actual = (either as Right).data
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `sideEffect - bind() failed - should return left`() {
        val either = sideEffect<String, User?> { fakeRight(NETWORK_ERROR).bind() }
        assert(either is Left)
    }

    @Test
    fun `sideEffect - bind() failed - assert result has error from first failed bind()`() {
        val expected = Left(NETWORK_ERROR)
        val actual = sideEffect<String, User?> {
            fakeRight(NETWORK_ERROR).bind()
            realRight().bind()
            fakeRight(SERVER_ERROR).bind()
        }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `nested sideEffect calls - most inner call returns left - should propagate left to the peek of the call stack`() {
        val either = sideEffect<String, User?> {
            sideEffect<String, User?> {
                sideEffect<String, User?> {
                    fakeRight(SERVER_ERROR).bind()
                }.bind()
            }.bind()
        }
        assert(either is Left)
    }

    @Test(expected = NullPointerException::class)
    fun `sideEffect - force take non-null value - should throw NPE`() {
        sideEffect<String, User> { fakeUser()!! }
    }

    private fun fakeRight(error: String): Either<String, User?> = Left(error)
    private fun realRight(): Either<String, User?> = Right(User(ID))
    private fun fakeUser(): User? = null

    private data class User(val id: Int)

    private companion object {
        private const val SERVER_ERROR = "server_error"
        private const val NETWORK_ERROR = "network_error"
        private const val ID = 1
    }
}