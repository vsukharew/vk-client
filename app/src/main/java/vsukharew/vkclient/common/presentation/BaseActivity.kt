package vsukharew.vkclient.common.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import vsukharew.vkclient.R
import vsukharew.vkclient.common.navigation.DeepLinkEndPoint

abstract class BaseActivity : AppCompatActivity() {

    protected abstract val binding: ViewBinding
    protected val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment)
            .navController
    }
    val deepLinkEndPoints = mutableListOf<DeepLinkEndPoint>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    protected fun addDeepLinkEndPoint(point: DeepLinkEndPoint) {
        deepLinkEndPoints.add(point)
    }
}