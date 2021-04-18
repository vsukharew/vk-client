package vsukharew.vkclient.publishimage.attach.presentation.delegate

import android.view.View
import vsukharev.anytypeadapter.delegate.NoDataDelegate
import vsukharev.anytypeadapter.holder.NoDataViewHolder
import vsukharew.vkclient.R
import vsukharew.vkclient.databinding.DelegateAddNewImageBinding

class AddNewImageDelegate : NoDataDelegate<DelegateAddNewImageBinding>() {

    override fun createViewHolder(itemView: View): NoDataViewHolder<DelegateAddNewImageBinding> {
        return Holder(DelegateAddNewImageBinding.bind(itemView))
    }

    override fun getItemViewType(): Int = R.layout.delegate_add_new_image

    class Holder(
        binding: DelegateAddNewImageBinding
    ) : NoDataViewHolder<DelegateAddNewImageBinding>(binding)
}