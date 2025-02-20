import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MyComposable(modifier: Modifier = Modifier) {
    Column {
        LazyColumn {
            items(items = listOf("")) { item ->

            }
        }
    }
}