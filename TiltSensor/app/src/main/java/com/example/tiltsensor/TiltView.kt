package com.example.tiltsensor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.SensorEvent
import android.view.View

class TiltView(context: Context?) : View(context) {

    // Paint 객체 초기화
    // 그리기 메서드에는 Paint객체가 필요
    private val greenPaint : Paint = Paint()
    private val blackPaint : Paint = Paint()

    // 중점의 좌표
    private var cX : Float = 0f
    private var cY : Float = 0f

    // 센서값 받기
    private var xCoord : Float = 0f
    private var yCoord : Float = 0f

    fun onSensorEvent(event: SensorEvent){ // 이 메서드는 SensirEvent 값을 인자로 받습니다
        // 화면을 가로로 돌렸으므로 X축과 Y축을 서로 바꿈 // 화면의 방향을 가로 모드로 회전시켰기 때문에 X축과 Y축을 서로 바꿔야 이해하기 편합니다
        yCoord = event.values[0] * 20
        xCoord = event.values[1] * 20
        
        invalidate() // invalidate() 메서드는 뷰의 onDraw() 메서드를 다시 호출하는 메서드 입니다. 즉 뷰를 다시 그리게 됩니다
    }

    init{
        // 녹색 페인트
        greenPaint.color = Color.GREEN

        // 검은색 테두리 페인트
        blackPaint.style = Paint.Style.STROKE
    }

    // 중점의 좌표를 계산
    /*
        w : 변경된 가로 길이
        h : 변경된 세로 길이
        oldw : 변경 전 가로 길이
        oldh : 변경 전 세로 길이
    */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) { // 뷰의 크기가 결정되면 호출한 onSizeChanged() 메서드에서 중점좌표를 계산합니다
        cX = w / 2f
        cY = h / 2f
    }

    // onDraw() 메서드를 오버라이드해 Canvas 객체를 받습니다
    // 여기서 원하는 그림을 그리면 됩니다 
    // onDraw() 메서드는 인자로 넘어오는 Canvas 객체에 뷰의 모습을 그린다
    override fun onDraw(canvas: Canvas?) {
        // 그리기
        // 바깥 원
        canvas?.drawCircle(cX, cY, 100f, blackPaint) // x, y, 반지름, 색
        // 녹색 원
        canvas?.drawCircle(xCoord + cX, yCoord + cY, 100f, greenPaint) // 녹색원을 그릴 때 xCoord와 yCoord값을 X좌표와 Y좌표에 더했음
        // 가운데 십자가
        canvas?.drawLine(cX - 20, cY,cX + 20, cY, blackPaint) // x1, y1, x2, y2, 색
        canvas?.drawLine(cX, cY - 20,cX, cY + 20, blackPaint)
    }
}