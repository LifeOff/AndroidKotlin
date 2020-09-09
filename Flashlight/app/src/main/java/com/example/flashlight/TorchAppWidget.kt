package com.example.flashlight

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class TorchAppWidget : AppWidgetProvider() { // 앱 위젯용 파일은 AppWidgetProvider 라는 일종의 브로드캐스트 리시버 클래스를 상속받습니다
    
    override fun onUpdate( // onUpdate() 메서드는 위젯이 업데이트되어야 할 때 호출됩니다
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // 위젯이 여러 개 배치되었다면 모든 위젯을 업데이트 합니다
        for (appWidgetId in appWidgetIds) { 
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    // 위젯이 처음 생성될때 호출됩니다
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }
    
    // 여러 개일 경우 마지막 위젯이 제거될 때 호출됩니다
    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

// 위젯을 업데이트 할 때 수행되는 코드입니다
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // RemoteViews 객체룰 구성합니다
    val views = RemoteViews(context.packageName, R.layout.torch_app_widget) // 위젯은 액티비티에서 레이아웃을 다르는 것과는 조금 다릅니다
                                                                            // 위젯에 배치하는 뷰는 따로있습니다 그것들은 RemoteViews 객체로 가져 올수 있습니다
    views.setTextViewText(R.id.appwidget_text, widgetText) // setTextViewText() 메서드는 RemoteViews 객체용으로 준비된 텍스트 값을 변경하는 메서드입니다
    
    // 추가로 작성할 부분 (여기서 위젯을 클릭 했을 때의 처리를 추가해야 합니다)

    // 실행할 Intent 작성
    val intent = Intent(context, TorchService::class.java)  // 서비스 인텐트
    val pendingIntent = PendingIntent.getService(context, 0, intent, 0) // TorchService 서비스를 실행하는 데 PendingIntent.getService() 메서드를 사용
                                                                                         // 전달하는 인자는 컨텍스트, 리퀘스트 코드, 서비스 인텐트, 플래그 4개입니다
                                                                                         // 리퀘스트 코드나 플래그 값은 사용하지 않기 때문에 0을 전달
    // 위젯을 클릭하면 위에서 정의한 Intent 실행
    views.setOnClickPendingIntent(R.id.appwidget_layout, pendingIntent) // 클릭 이벤트를 연결하려면 setOnClickPendingIntent() 메서드를 사용합니다
                                                                        // 여기에는 클릭이 발생할 뷰의 ID와 PendingIntent 객체가 필요합니다
    
    /*
        PendingIntent는 실행할 인텐트 정보를 가지고 있다가 수행해줍니다. 
        다음과 같이 어떤 인텐트를 실행할지에 따라서 다른 메서드를 사용해야 합니다

        PendingIntent.getActivity() : 액티비티 실행
        PendingIntent.getService() : 서비스 실행
        PendingIntent.getBroadcast() : 브로드캐스트 실행
    */

    // 위젯 관리자에게 위젯을 업데이트 하도록 지시
    appWidgetManager.updateAppWidget(appWidgetId, views) // 레이아웃을 모두 수정 했다면 appWidgetManager를 사용해 위젯을 업데이트 합니다
}