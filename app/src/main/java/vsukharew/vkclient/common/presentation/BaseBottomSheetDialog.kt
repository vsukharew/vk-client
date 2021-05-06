package vsukharew.vkclient.common.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.AndroidScopeComponent
import org.koin.core.scope.Scope
import vsukharew.vkclient.common.di.ScopeCreator

abstract class BaseBottomSheetDialog : BottomSheetDialogFragment(), AndroidScopeComponent {

    @get:LayoutRes
    protected abstract val layoutResId: Int
    protected abstract val binding: ViewBinding
    protected abstract val scopeCreator: ScopeCreator
    override val scope: Scope by lazy { scopeCreator.getScope(this, getKoin()).value }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(layoutResId, container, false)
}