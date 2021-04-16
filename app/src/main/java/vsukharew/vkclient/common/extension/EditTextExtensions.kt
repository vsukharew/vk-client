package vsukharew.vkclient.common.extension

import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop

fun EditText.textChangesSkipFirst(): Flow<String> {
    val flow = MutableStateFlow(String.EMPTY)
    doOnTextChanged { text, _, _, _ ->
        text?.toString()?.let { flow.value = it }
    }
    return flow.drop(1)
}