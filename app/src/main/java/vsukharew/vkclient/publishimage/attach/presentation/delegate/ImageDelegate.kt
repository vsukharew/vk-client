package vsukharew.vkclient.publishimage.attach.presentation.delegate

import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import vsukharev.anytypeadapter.delegate.AnyTypeDelegate
import vsukharev.anytypeadapter.holder.AnyTypeViewHolder
import vsukharew.vkclient.R
import vsukharew.vkclient.databinding.DelegateImageBinding
import vsukharew.vkclient.publishimage.attach.presentation.delegate.ImageDelegate.Holder
import vsukharew.vkclient.publishimage.attach.presentation.model.UIImage
import vsukharew.vkclient.publishimage.attach.presentation.state.ImageUIState

class ImageDelegate(
    private val retryUploadListener: (UIImage.RealImage) -> Unit,
    private val onRemoveClickListener: (UIImage.RealImage) -> Unit
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

        var image: UIImage.RealImage? = null

        init {
            binding.apply {
                retryUpload.setOnClickListener { image?.let { retryUploadListener.invoke(it) } }
                removeImage.setOnClickListener { image?.let { onRemoveClickListener.invoke(it) } }
            }
        }

        override fun bind(item: Pair<UIImage.RealImage, ImageUIState>) {
            image = item.first
            val (image, state) = item
            renderState(state)

            Glide.with(context)
                .load(Uri.parse(image.image.uri))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(binding.content)
        }

        private fun renderState(state: ImageUIState) {
            binding.apply {
                when (state) {
                    is ImageUIState.Success -> {
                        progressBar.apply {
                            Log.d("progressBar.progress: ", 100.toString())
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
                        Log.d("progressBar.progress: ", state.progress.toString())
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
                            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                            isVisible = false
                            retryUpload.isVisible = true
                            removeImage.isVisible = true
                        }
                    }
                    ImageUIState.Pending -> {
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
    }
}