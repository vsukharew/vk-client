package vsukharew.vkclient.common.network

import android.util.Log
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.BufferedSink
import vsukharew.vkclient.publishimage.attach.domain.infrastructure.DomainContentResolver
import vsukharew.vkclient.publishimage.attach.domain.model.Image

/**
 * Request body that notifies about current upload progress
 */
class ProgressRequestBody(
    private val delegate: RequestBody,
    private val image: Image,
    private val contentResolver: DomainContentResolver,
    private val onProgressUpdated: (Double) -> Unit,
) : RequestBody() {

    private var writeToInvocationTimes = 0

    /**
     * [writeTo] is called twice when [HttpLoggingInterceptor] is added
     * The first call occurs due to interceptor so [onProgressUpdated] should not be invoked
     * The second one is what you need to notify about
     */
    private val shouldNotifyUpdate: Boolean
        get() = writeToInvocationTimes == 2

    override fun contentLength(): Long = delegate.contentLength()

    override fun contentType(): MediaType = delegate.contentType()!!

    override fun writeTo(sink: BufferedSink) {
        writeToInvocationTimes++
        var previousProgressValue = .0
        var currentProgressValue = .0
        var bytesWritten = 0
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

        contentResolver.openInputStream(image.uri)!!
            .use {
                do {
                    val bytesRead = it.read(buffer)
                    if (bytesRead == -1) break
                    sink.write(buffer, 0, bytesRead)
                    bytesWritten += bytesRead
                    when {
                        currentProgressValue - previousProgressValue < .01 -> {
                            currentProgressValue = bytesWritten.toDouble() / contentLength()
                            if (currentProgressValue == 1.0 && shouldNotifyUpdate) {
                                Log.d("progress-body", currentProgressValue.toString())
                                onProgressUpdated.invoke(currentProgressValue)
                            }
                        }
                        else -> {
                            previousProgressValue = currentProgressValue
                            if (shouldNotifyUpdate) {
                                Log.d("progress-body", currentProgressValue.toString())
                                onProgressUpdated.invoke(currentProgressValue)
                            }
                        }
                    }
                } while (true)
                sink.flush()
            }
    }
}