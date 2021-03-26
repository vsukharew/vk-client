package vsukharew.vkclient

import vsukharew.vkclient.common.delegation.activityViewBinding
import vsukharew.vkclient.common.presentation.BaseActivity
import vsukharew.vkclient.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    override val binding by activityViewBinding(ActivityMainBinding::inflate)
}