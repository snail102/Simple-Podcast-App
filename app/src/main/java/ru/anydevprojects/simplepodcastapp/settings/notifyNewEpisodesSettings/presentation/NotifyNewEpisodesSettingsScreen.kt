// package ru.anydevprojects.simplepodcastapp.settings.notifyNewEpisodesSettings.presentation
//
// import android.annotation.SuppressLint
// import androidx.compose.foundation.layout.Arrangement
// import androidx.compose.foundation.layout.Column
// import androidx.compose.foundation.layout.Row
// import androidx.compose.foundation.layout.Spacer
// import androidx.compose.foundation.layout.fillMaxWidth
// import androidx.compose.foundation.layout.height
// import androidx.compose.material3.Scaffold
// import androidx.compose.material3.SingleChoiceSegmentedButtonRow
// import androidx.compose.material3.Text
// import androidx.compose.material3.TextButton
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.unit.dp
// import org.koin.androidx.compose.koinViewModel
// import ru.anydevprojects.simplepodcastapp.ui.components.TitleAppBar
//
// @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
// @Composable
// fun NotifyNewEpisodesSettingsScreen(
//    onBackClick: () -> Unit,
//    viewModel: NotifyNewEpisodesSettingsViewModel = koinViewModel()
// ) {
//    Scaffold(
//        topBar = {
//            TitleAppBar(
//                title = "Настройки уведомлений",
//                onBackClick = onBackClick
//            )
//        }
//    ) { paddingValues ->
//        Column {
//            Text(text = "Подкасты, для которых будет проверяться наличие новых эпизодов")
//            Spacer(Modifier.height(32.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth().,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                TextButton(
//                    onClick = {
//                    }
//                ) {
//                    Text("Убрать все")
//                }
//                TextButton(
//                    onClick = {
//                    }
//                ) {
//                    Text("Выбрать все")
//                }
//            }
//        }
//    }
// }
