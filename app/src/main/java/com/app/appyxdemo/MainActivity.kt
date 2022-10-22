package com.app.appyxdemo

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.appyxdemo.ui.theme.AppyxDemoTheme
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.ActivityIntegrationPoint
import com.bumble.appyx.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackSlider
import kotlinx.parcelize.Parcelize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppyxDemoTheme {
                NodeHost(
                    integrationPoint = ActivityIntegrationPoint(this, savedInstanceState),
                ){
                    RootNode(
                        buildContext = it
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppyxDemoTheme {
        Greeting("Android")
    }
}

sealed class Routing : Parcelable {

    @Parcelize
    object Home : Routing()

    @Parcelize
    object Detail : Routing()
}

class HomeNode(buildContext: BuildContext, val backStack: BackStack<Routing>) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        Column {
            Text(text = "Home")
            Button(onClick = {
                backStack.push(Routing.Detail)
            }) {
                Text(text = "Detail")
            }
        }
    }
}

class DetailNode(buildContext: BuildContext) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        Text(text = "Detail")
    }
}

class RootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = Routing.Home,
        savedStateMap = buildContext.savedStateMap,
    ),
) : ParentNode<Routing>(
    navModel = backStack,
    buildContext = buildContext
) {
    override fun resolve(navTarget: Routing, buildContext: BuildContext): Node {
        return when (navTarget) {
            Routing.Home -> HomeNode(buildContext, backStack)
            Routing.Detail -> DetailNode(buildContext)
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            navModel = backStack,
            transitionHandler = rememberBackstackSlider(
                transitionSpec = {
                    tween(300)
                }
            )
        )
    }
}