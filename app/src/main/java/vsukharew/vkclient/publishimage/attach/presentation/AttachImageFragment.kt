package vsukharew.vkclient.publishimage.attach.presentation

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import androidx.recyclerview.widget.SimpleItemAnimator
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import vsukharev.anytypeadapter.adapter.AnyTypeAdapter
import vsukharev.anytypeadapter.adapter.AnyTypeCollection
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.livedata.SingleLiveEvent
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
        viewModel.chooseImageSource()
    }
    private val imageDelegate = ImageDelegate(
        { viewModel.startLoading(it, true) },
        { viewModel.removeImage(it) }
    )
    private val viewModel: AttachImageViewModel by stateViewModel()
    private val flowCoordinator: PublishImageCoordinator by inject()
    private lateinit var cameraResultLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryResultLauncher: ActivityResultLauncher<String>

    override val scopeCreator: ScopeCreator by lazy { AttachImageScopeCreator(requireParentFragment().requireParentFragment()) }
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
            imagesStatesLiveData.observe(viewLifecycleOwner, ::observeImagesStates)
            isNextButtonAvailable.observe(viewLifecycleOwner, ::observeNextButtonAvailability)
            imageSourceChoice.observe(viewLifecycleOwner, ::observeImageSourceChoice)
            openCameraAction.observe(viewLifecycleOwner, ::observeOpenCameraAction)
        }
        flowCoordinator.apply {
            addObserverToBackStackEntry(R.id.attachImageFragment) {
                doIfKeyExists<ImageSource>(KEY_IMAGE_SOURCE) {
                    when (it) {
                        CAMERA -> {
                            viewModel.openCamera()
                        }
                        GALLERY -> {
                            galleryResultLauncher.launch("image/*")
                        }
                    }
                    removeKey<Int>(KEY_IMAGE_SOURCE)
                }
            }
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
                            add((it.key as UIImage.RealImage) to it.value, imageDelegate)
                        }
                    }
                }
            }
            .build()
            .let { anyTypeAdapter.setCollection(it) }
    }

    private fun observeNextButtonAvailability(isEnabled: Boolean) {
        binding.nextBtn.isEnabled = isEnabled
    }

    private fun observeImageSourceChoice(event: SingleLiveEvent<Unit>) {
        event.getContentIfNotHandled()?.let { flowCoordinator.openImageSourceScreen() }
    }

    private fun observeOpenCameraAction(event: SingleLiveEvent<Unit>) {
        event.getContentIfNotHandled()?.let {
            uri = Uri.parse(viewModel.getUriForFutureImage())
            cameraResultLauncher.launch(uri)
        }
    }

    private fun handleResult(isSuccess: Boolean) {
        if (isSuccess) {
            viewModel.startLoading(uri.toString(), false)
        }
    }

    private fun handleGalleryResult(uris: List<Uri>) {
        viewModel.startLoading(uris.map { it.toString() })
    }
}