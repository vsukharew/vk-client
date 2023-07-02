package vsukharew.vkclient.publishimage.attach.presentation

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import vsukharev.anytypeadapter.adapter.AnyTypeAdapter
import vsukharev.anytypeadapter.adapter.AnyTypeCollection
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentAttachImageBinding
import vsukharew.vkclient.publishimage.attach.di.AttachImageScopeCreator
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource.CAMERA
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource.GALLERY
import vsukharew.vkclient.publishimage.attach.presentation.delegate.AddNewImageDelegate
import vsukharew.vkclient.publishimage.attach.presentation.delegate.ImageDelegate
import vsukharew.vkclient.publishimage.attach.presentation.dialog.ImageSourceBottomSheetDialog.Companion.KEY_IMAGE_SOURCE
import vsukharew.vkclient.publishimage.attach.presentation.model.UIImage
import vsukharew.vkclient.publishimage.attach.presentation.state.AttachImageUIState
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
        flowCoordinator.openImageSourceScreen()
    }
    private val imageDelegate by lazy {
        ImageDelegate(viewModel::retryLoadingNew, viewModel::removeImage)
    }
    private val flowCoordinator: PublishImageCoordinator by inject()
    private var cameraResultLauncher: ActivityResultLauncher<Uri>? = null
    private var galleryResultLauncher: ActivityResultLauncher<String>? = null

    override val scopeCreator: ScopeCreator by lazy {
        AttachImageScopeCreator(requireParentFragment().requireParentFragment())
    }
    override val viewModel: AttachImageViewModel by stateViewModel()
    override val binding by fragmentViewBinding(FragmentAttachImageBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerCallbacks()
        initRecycler()
        setListeners()
        observeData()
    }

    private fun registerCallbacks() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    flowCoordinator.currentStage.onBackClick()
                }
            })
        cameraResultLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture(),
            ::handleResult
        )
        galleryResultLauncher = registerForActivityResult(
            ActivityResultContracts.GetMultipleContents(),
            ::handleGalleryResult
        )
    }

    private fun initRecycler() {
        binding.attachedImages.apply {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            adapter = anyTypeAdapter
        }
    }

    private fun setListeners() {
        binding.apply { nextBtn.setOnClickListener { viewModel.goToNextStage() } }
    }

    private fun observeData() {
        viewModel.apply {
            viewLifecycleOwner.lifecycle.apply {
                lifecycleScope.launch {
                    uiState.flowWithLifecycle(lifecycle)
                        .map(AttachImageUIState::isNextButtonAvailable::get)
                        .collectLatest(::collectNextButtonAvailability)
                }
                lifecycleScope.launch {
                    uiState.flowWithLifecycle(lifecycle)
                        .map(AttachImageUIState::allImages::get)
                        .collectLatest(::observeImagesStates)
                }
            }
        }
        flowCoordinator.apply {
            addObserverToBackStackEntry(R.id.attachImageFragment) {
                doIfKeyExists<ImageSource>(KEY_IMAGE_SOURCE) {
                    when (it) {
                        CAMERA -> {
                            uri = Uri.parse(viewModel.getUriForFutureImage())
                            cameraResultLauncher?.launch(uri)
                        }
                        GALLERY -> {
                            galleryResultLauncher?.launch("image/*")
                        }
                        else -> {}
                    }
                    removeKey<Int>(KEY_IMAGE_SOURCE)
                }
            }
        }
    }

    private fun observeImagesStates(images: List<UIImage>) {
        AnyTypeCollection.Builder()
            .apply {
                images.forEach {
                    when (it) {
                        UIImage.AddNewImagePlaceholder -> {
                            add(addNewImageDelegate)
                        }
                        is UIImage.RealImage -> {
                            add(it, imageDelegate)
                        }
                    }
                }
            }
            .build()
            .let(anyTypeAdapter::setCollection)
    }

    private fun collectNextButtonAvailability(isEnabled: Boolean) {
        binding.nextBtn.isEnabled = isEnabled
    }

    private fun handleResult(isSuccess: Boolean) {
        if (isSuccess) {
            viewModel.loadCameraImage(uri.toString())
        }
    }

    private fun handleGalleryResult(uris: List<Uri>) {
        viewModel.loadGalleryImages(uris.map { it.toString() })
    }
}