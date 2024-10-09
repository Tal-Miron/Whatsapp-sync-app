package com.talmiron.lishcaautoinvities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class ManageMessage extends AppCompatActivity {

    Result result;
    ContactAdapter adapter;
    ArrayList<Contact> items = new ArrayList<>();
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("myTag", "Manage Msg OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_message);
        et = findViewById(R.id.editMsgText);
        DraggableCircleView dcv = new DraggableCircleView(this);
        Intent i = getIntent();
        Log.d("myTag", "Manage Msg Is Ok #1");
        result = (Result) i.getSerializableExtra("result");
        FrameLayout container = findViewById(R.id.SwipeToSendLayout);
        DraggableCircleView draggableCircleView = new DraggableCircleView(this);
        container.addView(draggableCircleView);
        Log.d("myTag", "Manage Msg Is Ok #2");

        for (Contact c:
             result.contacts) {
            items.add(c);
        }

         adapter = new ContactAdapter(this, items);

        ListView listView = (ListView) findViewById(R.id.ContactsListview);
        listView.setAdapter(adapter);

        et.setText(result.messageText);
    }

    public void Send(){
        Intent i = new Intent(this, SendMsgActivity.class);
        result.messageText = et.getText().toString();
        i.putExtra("result", result);
        startActivity(i);
    }


    public class DraggableCircleView extends View {
        private Paint paint;
        private float circleRadius;
        private float circleX;
        private float circleY;
        private boolean dragging = false;
        private float originalCircleX;

        public DraggableCircleView(Context context) {
            super(context);
            init();
        }

        private void init() {
            paint = new Paint();
            paint.setColor(0xFF292929); // black color
            //paint.setColor(0xFFFF0000); // Red color
            originalCircleX = circleX; // Store the original position
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            // Adjust circle size to match layout height while maintaining aspect ratio
            circleRadius = h / 2 - 10;
            circleX = circleRadius + 10; // Start from left edge
            circleY = h / 2; // Center vertically
            originalCircleX = circleX; // Store the original position
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawCircle(circleX, circleY, circleRadius, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (Math.abs(x - circleX) <= circleRadius) {
                        dragging = true;
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (dragging) {
                        // Ensure the circle stays within the layout bounds
                        if (x - circleRadius >= 5 && x + circleRadius <= getWidth() - 9) {
                            circleX = x;
                            invalidate(); // Redraw the view
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    dragging = false;
                    // Animate the circle back to its original position
                    ValueAnimator animator = ValueAnimator.ofFloat(circleX, originalCircleX);
                    animator.setDuration(300); // Animation duration in milliseconds
                    animator.addUpdateListener(animation -> {
                        circleX = (float) animation.getAnimatedValue();
                        invalidate(); // Redraw the view
                    });
                    animator.start();

                    // Check if the swipe direction is to the right
                    if (x > getWidth() - circleRadius) {
                        Send();
                    }
                    return true;
            }
            return super.onTouchEvent(event);
        }


    }


























}