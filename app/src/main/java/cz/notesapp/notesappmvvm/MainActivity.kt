package cz.notesapp.notesappmvvm

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import cz.notesapp.notesappmvvm.navigation.NavRoute
import cz.notesapp.notesappmvvm.navigation.NotesNavHost
import cz.notesapp.notesappmvvm.ui.theme.NotesAppMVVMTheme
import cz.notesapp.notesappmvvm.ui.theme.ThemeState
import cz.notesapp.notesappmvvm.utils.DB_TYPE

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesAppMVVMTheme {
                val context = LocalContext.current
                val mViewModel: MainViewModel =
                    viewModel(factory = MainViewModelFactory(context.applicationContext as Application))
                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(onClick = {
                                        ThemeState.isDark = !ThemeState.isDark
                                        recreate();
                                    }) {
                                        Text(text = "Change Theme")
                                    }
                                    Text(text = "Notes App")
                                    if (DB_TYPE.value.isNotEmpty()) {
                                        Icon(
                                            imageVector = Icons.Default.ExitToApp,
                                            contentDescription = "",
                                            modifier = Modifier.clickable {
                                                mViewModel.signOut {
                                                    navController.navigate(NavRoute.Start.route) {
                                                        popUpTo(NavRoute.Start.route) {
                                                            inclusive = true
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            },
                            backgroundColor = Color.Blue,
                            contentColor = Color.White,
                            elevation = 12.dp
                        )
                    },
                    content = {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color =  MaterialTheme.colors.background
                        ) {
                            NotesNavHost(mViewModel, navController)
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NotesAppMVVMTheme {

    }
}