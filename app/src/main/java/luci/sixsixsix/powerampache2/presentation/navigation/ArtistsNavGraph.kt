package luci.sixsixsix.powerampache2.presentation.navigation

import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph
@NavGraph
annotation class ArtistsNavGraph(
    val start: Boolean = false
)
