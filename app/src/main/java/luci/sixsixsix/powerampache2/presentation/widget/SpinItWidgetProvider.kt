package luci.sixsixsix.powerampache2.presentation.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.MainActivity

// @AndroidEntryPoint
class SpinItWidgetProvider : AppWidgetProvider() {

//    @Inject
//    lateinit var musicRepository: MusicRepository

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        appWidgetIds?.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget_spinit_layout)

            // Set up the click event for the widget
            val intent = Intent(context.applicationContext, MainActivity::class.java).apply {
                action = WIDGET_ACTION_SPIN_IT
                // putExtra("extra_data", "SomeData")
            }

            val pendingIntent = PendingIntent.getActivity(context, WIDGET_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            views.setOnClickPendingIntent(R.id.widget_image, pendingIntent)
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }

        // Launch a coroutine to handle background work
//        CoroutineScope(Dispatchers.Main).launch {
//            println("aaaa ping start")
//            musicRepository.ping()
//            println("aaaa ping end")
//        }
    }

    companion object {
        const val WIDGET_INTENT_REQUEST_CODE = 72464
        const val WIDGET_ACTION_SPIN_IT = "luci.sixsixsix.powerampache2.widget.action.SpinIt"
    }
}
