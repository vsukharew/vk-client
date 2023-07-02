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
import vsukharew.vkclient.databinding.DelegateImageBinding
import vsukharew.vkclient.publishimage.attach.presentation.delegate.ImageDelegate.Holder
import vsukharew.vkclient.publishimage.attach.presentation.model.ImageLoadingState
import vsukharew.vkclient.publishimage.attach.presentation.model.UIImage

class ImageDelegate(
    private val retryUploadListener: (UIImage.RealImage) -> Unit,
    private val onRemoveClickListener: (UIImage.RealImage) -> Unit
) : AnyTypeDelegate<UIImage.RealImage, DelegateImageBinding, Holder>() {

    override fun createViewHolder(itemView: View): Holder {
        return Holder(DelegateImageBinding.bind(itemView))
    }

    override fun getItemId(item: UIImage.RealImage): String = item.image.uri

    override fun getItemViewType(): Int = R.layout.delegate_image

    inner class Holder(
        private val binding: DelegateImageBinding
    ) : AnyTypeViewHolder<UIImage.RealImage, DelegateImageBinding>(binding) {

        private var item: UIImage.RealImage? = null

        init {
            binding.apply {
                retryUpload.setOnClickListener { item?.let(retryUploadListener::invoke) }
                removeImage.setOnClickListener { item?.let(onRemoveClickListener::invoke) }
            }
        }

        override fun bind(item: UIImage.RealImage) {
            this.item = item
            renderState(item)
            Glide.with(context)
                .load(Uri.parse(item.image.uri))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(binding.content)
        }

        private fun renderState(item: UIImage.RealImage) {
            when (val loadingState = item.loadingState) {
                is ImageLoadingState.Success -> renderSuccessState(binding)
                is ImageLoadingState.LoadingProgress -> renderLoadingState(binding, loadingState)
                is ImageLoadingState.Error -> renderErrorState(binding, loadingState, item)
                is ImageLoadingState.Pending -> renderPendingState(binding)
            }
        }

        private fun renderSuccessState(binding: DelegateImageBinding) {
            binding.apply {
                progressBar.isVisible = false
                retryUpload.isVisible = false
                removeImage.isVisible = true
            }
        }

        private fun renderLoadingState(
            binding: DelegateImageBinding,
            state: ImageLoadingState.LoadingProgress
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
            state: ImageLoadingState.Error,
            item: UIImage.RealImage
        ) {
            binding.apply {
                progressBar.apply {
                    isIndeterminate = false
                    isVisible = false
                }
                retryUpload.isVisible = true
                removeImage.isVisible = true
                if (state.error is AppError.DomainError) {
                    Snackbar.make(
                        itemView,
                        getErrorMessage(state.error),
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(R.string.delete_text) { onRemoveClickListener.invoke(item) }
                        .show()
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

        private fun getErrorMessage(error: AppError): Int {
            return when (error) {
                FileTooLargeError -> R.string.attach_image_fragment_file_too_large
                ImageResolutionTooLargeError -> R.string.attach_image_fragment_image_resolution_too_large
                else -> R.string.empty
            }
        }
    }
}