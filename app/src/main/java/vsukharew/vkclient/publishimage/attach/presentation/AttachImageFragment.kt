package vsukharew.vkclient.publishimage.attach.presentation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import androidx.recyclerview.widget.SimpleItemAnimator
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import vsukharev.anytypeadapter.adapter.AnyTypeAdapter
import vsukharev.anytypeadapter.adapter.AnyTypeCollection
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentAttachImageBinding
import vsukharew.vkclient.publishimage.attach.di.AttachImageScopeCreator
import vsukharew.vkclient.publishimage.attach.presentation.delegate.AddNewImageDelegate
import vsukharew.vkclient.publishimage.attach.presentation.delegate.ImageDelegate
import vsukharew.vkclient.publishimage.attach.presentation.model.UIImage
import vsukharew.vkclient.publishimage.attach.presentation.state.ImageUIState
import vsukharew.vkclient.publishimage.navigation.PublishImageCoordinator

class AttachImageFragment :
    BaseFragment<FragmentAttachImageBinding>(R.layout.fragment_attach_image) {

    private lateinit var uri: Uri
    private val anyTypeAdapter =
        AnyTypeAdapter().apply {
            diffStrategy = AnyTypeAdapter.DiffStrategy.Queue
            stateRestorationPolicy = PREVENT_WHEN_EMPTY
        }
    private val addNewImageDelegate = AddNewImageDelegate {
        uri = Uri.parse(viewModel.getUriForFutureImage())
        cameraResultLauncher.launch(uri)
    }
    private val imageDelegate = ImageDelegate(
        { viewModel.startLoading(it, true) },
        { viewModel.removeImage(it) }
    )
    private val viewModel: AttachImageViewModel by viewModel()
    private val flowCoordinator: PublishImageCoordinator by inject()
    private lateinit var cameraResultLauncher: ActivityResultLauncher<Uri>

    override val scopeCreator: ScopeCreator by lazy {
        AttachImageScopeCreator(requireParentFragment().requireParentFragment())
    }
    override val binding by fragmentViewBinding(FragmentAttachImageBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerCallbacks()
        initRecycler()
        observeData()
    }

    private fun registerCallbacks() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    isEnabled = false
                    flowCoordinator.onBackClick()
                }
            })
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

    private fun observeImagesStates(imagesStates: Map<UIImage, ImageUIState>) {
        AnyTypeCollection.Builder()
            .apply {
                imagesStates.forEach {
                    when (it.key) {
                        UIImage.AddNewImagePlaceholder -> {
                            add(addNewImageDelegate)
                        }
                        is UIImage.RealImage -> {
                            if (it.value is ImageUIState.LoadingProgress) {
                                Log.d(
                                    "progress-adapter: ",
                                    (it.value as ImageUIState.LoadingProgress).progress.toString()
                                )
                            }
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
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            adapter = anyTypeAdapter
        }
    }

    private fun handleResult(isSuccess: Boolean) {
        if (isSuccess) {
            viewModel.startLoading(uri.toString(), false)
        }
    }
}