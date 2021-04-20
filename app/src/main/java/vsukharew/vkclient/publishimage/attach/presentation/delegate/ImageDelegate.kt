package vsukharew.vkclient.publishimage.attach.presentation.delegate

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import vsukharev.anytypeadapter.delegate.AnyTypeDelegate
import vsukharev.anytypeadapter.holder.AnyTypeViewHolder
import vsukharew.vkclient.R
import vsukharew.vkclient.common.presentation.loadstate.UIState
import vsukharew.vkclient.databinding.DelegateImageBinding
import vsukharew.vkclient.publishimage.attach.presentation.delegate.ImageDelegate.Holder
import vsukharew.vkclient.publishimage.attach.presentation.model.UIImage

class ImageDelegate :
    AnyTypeDelegate<Pair<UIImage.RealImage, UIState<UIImage>>, DelegateImageBinding, Holder>() {

    override fun createViewHolder(itemView: View): Holder {
        return Holder(DelegateImageBinding.bind(itemView))
    }

    override fun getItemId(item: Pair<UIImage.RealImage, UIState<UIImage>>): String =
        item.first.image.uri

    override fun getItemViewType(): Int = R.layout.delegate_image

    inner class Holder(
        private val binding: DelegateImageBinding
    ) : AnyTypeViewHolder<Pair<UIImage.RealImage, UIState<UIImage>>, DelegateImageBinding>(binding) {

        override fun bind(item: Pair<UIImage.RealImage, UIState<UIImage>>) {
            val (image, state) = item

            Glide.with(context)
                .load(Uri.parse(image.image.uri))
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        renderState(state)
                        return false
                    }
                })
                .into(binding.image)
        }

        private fun <T> renderState(state: UIState<T>) {
            when (state) {
                is UIState.Success, is UIState.Error -> {
                    binding.progressBar.isVisible = false
                }
                is UIState.LoadingProgress -> {
                    binding.progressBar.isVisible = true
                }
                else -> {

                }
            }
        }
    }
}