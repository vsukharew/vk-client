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
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.AppError.DomainError.FileTooLargeError
import vsukharew.vkclient.common.domain.model.AppError.DomainError.ImageResolutionTooLargeError
import vsukharew.vkclient.common.domain.model.Either
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

        private var item: Pair<UIImage.RealImage,ImageUIState>? = null

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
            when (state) {
                is ImageUIState.Success -> renderSuccessState(binding)
                is ImageUIState.LoadingProgress -> renderLoadingState(binding, state)
                is ImageUIState.Error -> renderErrorState(binding, state, item)
                is ImageUIState.Pending -> renderPendingState(binding)
            }
        }

        private fun renderSuccessState(binding: DelegateImageBinding) {
            binding.apply {
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
        }

        private fun renderLoadingState(
            binding: DelegateImageBinding,
            state: ImageUIState.LoadingProgress
        ) {
            binding.apply {
                progressBar.apply {
                    isIndeterminate = state.progress == 0
                    isVisible = true
                    if (!isIndeterminate) {
                        progress = state.progress
                    }
                }
                retryUpload.isVisible = false
                removeImage.isVisible = false
            }
        }

        private fun renderErrorState(
            binding: DelegateImageBinding,
            state: ImageUIState.Error,
            item: Pair<UIImage.RealImage, ImageUIState>
        ) {
            binding.apply {
                progressBar.apply {
                    isIndeterminate = false
                    isVisible = false
                }
                retryUpload.isVisible = true
                removeImage.isVisible = true
                with(state.error) {
                    if (peekContent.data is AppError.DomainError) {
                        Snackbar.make(
                            itemView,
                            getErrorMessage(peekContent),
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(R.string.delete_text) { onRemoveClickListener.invoke(item) }
                            .show()
                    }
                }
            }
        }

        private fun renderPendingState(binding: DelegateImageBinding) {
            binding.apply {
                retryUpload.isVisible = false
                removeImage.isVisible = false
                progressBar.apply {
                    isVisible = false
                    isIndeterminate = true
                    isVisible = true
                }
            }
        }

        private fun getErrorMessage(error: Either.Left<AppError>): Int {
            return when (error.data) {
                FileTooLargeError -> R.string.attach_image_fragment_file_too_large
                ImageResolutionTooLargeError -> R.string.attach_image_fragment_image_resolution_too_large
                else -> R.string.empty
            }
        }
    }
}