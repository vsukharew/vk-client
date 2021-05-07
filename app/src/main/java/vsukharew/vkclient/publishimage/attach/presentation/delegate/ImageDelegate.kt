package vsukharew.vkclient.publishimage.attach.presentation.delegate

import android.net.Uri
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import vsukharev.anytypeadapter.delegate.AnyTypeDelegate
import vsukharev.anytypeadapter.holder.AnyTypeViewHolder
import vsukharew.vkclient.R
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.domain.model.Result.Error.DomainError.FileTooLargeError
import vsukharew.vkclient.common.domain.model.Result.Error.DomainError.ImageResolutionTooLargeError
import vsukharew.vkclient.databinding.DelegateImageBinding
import vsukharew.vkclient.publishimage.attach.presentation.delegate.ImageDelegate.Holder
import vsukharew.vkclient.publishimage.attach.presentation.model.UIImage
import vsukharew.vkclient.publishimage.attach.presentation.state.ImageUIState

class ImageDelegate(
    private val retryUploadListener: (UIImage.RealImage) -> Unit,
    private val onRemoveClickListener: (Pair<UIImage.RealImage, ImageUIState>) -> Unit
) : AnyTypeDelegate<Pair<UIImage.RealImage, ImageUIState>, DelegateImageBinding, Holder>() {

    override fun createViewHolder(itemView: View): Holder {
        return Holder(DelegateImageBinding.bind(itemView))
    }

    override fun getItemId(item: Pair<UIImage.RealImage, ImageUIState>): String =
        item.first.image.uri

    override fun getItemViewType(): Int = R.layout.delegate_image

    inner class Holder(
        private val binding: DelegateImageBinding
    ) : AnyTypeViewHolder<Pair<UIImage.RealImage, ImageUIState>, DelegateImageBinding>(binding) {

        var item: Pair<UIImage.RealImage,ImageUIState> ? = null

        init {
            binding.apply {
                retryUpload.setOnClickListener { item?.let { retryUploadListener.invoke(it.first) } }
                removeImage.setOnClickListener { item?.let { onRemoveClickListener.invoke(it) } }
            }
        }

        override fun bind(item: Pair<UIImage.RealImage, ImageUIState>) {
            this.item = item
            val (image, _) = item
            renderState(item)

            Glide.with(context)
                .load(Uri.parse(image.image.uri))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(binding.content)
        }

        private fun renderState(item: Pair<UIImage.RealImage, ImageUIState>) {
            val (_, state) = item
            binding.apply {
                when (state) {
                    is ImageUIState.Success -> {
                        progressBar.apply {
                            postDelayed(
                                {
                                    isVisible = false
                                    retryUpload.isVisible = false
                                    removeImage.isVisible = true
                                },
                                500L
                            )
                        }
                    }
                    is ImageUIState.LoadingProgress -> {
                        progressBar.apply {
                            isIndeterminate = false
                            isVisible = true
                            progress = state.progress
                        }
                        retryUpload.isVisible = false
                        removeImage.isVisible = false
                    }
                    is ImageUIState.Error -> {
                        progressBar.apply {
                            isIndeterminate = false
                            isVisible = false
                        }
                        retryUpload.isVisible = true
                        removeImage.isVisible = true
                        with(state.error) {
                            if (peekContent is Result.Error.DomainError) {
                                Snackbar.make(
                                    itemView,
                                    getErrorMessage(peekContent),
                                    Snackbar.LENGTH_INDEFINITE
                                ).setAction(R.string.delete_text) { onRemoveClickListener.invoke(item) }
                                    .show()
                            }
                        }
                    }
                    is ImageUIState.Pending -> {
                        retryUpload.isVisible = false
                        removeImage.isVisible = false
                        progressBar.apply {
                            isVisible = false
                            isIndeterminate = true
                            isVisible = true
                        }
                    }
                }
            }
        }

        private fun getErrorMessage(error: Result.Error): Int {
            return when (error) {
                FileTooLargeError -> R.string.attach_image_fragment_file_too_large
                ImageResolutionTooLargeError -> R.string.attach_image_fragment_image_resolution_too_large
                else -> R.string.empty
            }
        }
    }
}