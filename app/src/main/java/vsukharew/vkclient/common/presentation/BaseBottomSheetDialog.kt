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
import org.koin.androidx.scope.fragmentScope
import org.koin.core.scope.Scope
import vsukharew.vkclient.common.di.ScopeManager

abstract class BaseBottomSheetDialog : BottomSheetDialogFragment(), AndroidScopeComponent {

    @get:LayoutRes
    protected abstract val layoutResId: Int
    protected abstract val binding: ViewBinding
    private val scopeManager by lazy { ScopeManager(getKoin()) }
    protected abstract val parentScopes: ScopeManager.() -> Array<Scope>
    override val scope: Scope by fragmentScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scope.linkTo(*parentScopes.invoke(scopeManager))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(layoutResId, container, false)
}