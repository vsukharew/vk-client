package vsukharew.vkclient.publishimage.caption.di

import org.koin.dsl.module
import vsukharew.vkclient.publishimage.caption.presentation.CaptionFragment

val captionScreenModule = module {
    scope<CaptionFragment> {  }
}