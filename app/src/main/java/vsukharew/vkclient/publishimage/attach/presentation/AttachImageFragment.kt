package vsukharew.vkclient.publishimage.attach.presentation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import org.koin.androidx.viewmodel.ext.android.viewModel
import vsukharev.anytypeadapter.adapter.AnyTypeAdapter
import vsukharev.anytypeadapter.adapter.AnyTypeCollection
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.common.presentation.loadstate.UIState
import vsukharew.vkclient.databinding.FragmentAttachImageBinding
import vsukharew.vkclient.publishimage.attach.di.AttachImageScopeCreator
import vsukharew.vkclient.publishimage.attach.presentation.delegate.AddNewImageDelegate
import vsukharew.vkclient.publishimage.attach.presentation.delegate.ImageDelegate
import vsukharew.vkclient.publishimage.attach.presentation.model.UIImage

class AttachImageFragment :
    BaseFragment<FragmentAttachImageBinding>(R.layout.fragment_attach_image) {

    private lateinit var uri: Uri
    private val anyTypeAdapter = AnyTypeAdapter()
    private val addNewImageDelegate = AddNewImageDelegate {
        uri = Uri.parse(viewModel.getUriForFutureImage())
        cameraResultLauncher.launch(uri)
    }
    private val imageDelegate = ImageDelegate()
    private val viewModel: AttachImageViewModel by viewModel()
    private lateinit var cameraResultLauncher: ActivityResultLauncher<Uri>

    override val scopeCreator: ScopeCreator = AttachImageScopeCreator
    override val binding by fragmentViewBinding(FragmentAttachImageBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        observeData()
        cameraResultLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture(),
            ::handleResult
        )
    }

    private fun observeData() {
        viewModel.apply {
            imagesStatesLiveData.observe(viewLifecycleOwner, ::observeImagesStates)
        }
    }

    private fun observeImagesStates(imagesStates: Map<UIImage, UIState<UIImage>>) {
        AnyTypeCollection.Builder()
            .apply {
                Log.d("Progress", "Inside apply {...}")
                imagesStates.forEach {
                    when (it.key) {
                        UIImage.AddNewImagePlaceholder -> {
                            add(addNewImageDelegate)
                        }
                        is UIImage.RealImage -> {
                            add((it.key as UIImage.RealImage) to it.value, imageDelegate)
                        }
                    }
                }
            }
            .build()
            .let { anyTypeAdapter.setCollection(it) }
    }

    private fun initRecycler() {
        binding.attachedImages.apply {
            adapter = anyTypeAdapter
        }
    }

    private fun handleResult(isSuccess: Boolean) {
        if (isSuccess) {
            viewModel.startLoading(uri.toString())
        }
    }
}