package com.charmi.mycanvas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {


    View mView;
    private Paint mPaint;
    SeekBar mSeekbar;
    int penColor = Color.BLACK;
    int bgColor = Color.WHITE;
    int StrokeWidth = 12;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LinearLayout layout = (LinearLayout) findViewById(R.id.myDrawing);
        mView = new DrawingView(this);
        mSeekbar = (SeekBar) findViewById(R.id.seek);

        mSeekbar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        mSeekbar.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        layout.addView(mView, new ActionBar.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        ((DrawingView) mView).init();
        penSize();


    }

    public void undo()
    {
        ((DrawingView)mView).remove();
    }

    public void penSize() {
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                ((DrawingView) mView).changePenSize(StrokeWidth);
                StrokeWidth = i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                ((DrawingView) mView).changePenSize(StrokeWidth);


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.clear) {

            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

            alert.setTitle("Do you really want to clear ?\nYou can save before clearing.");

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((DrawingView) mView).ClearPath();
                    Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            alert.show();

            return true;

        } else if (id == R.id.bgColor) {
            bgColor();

        } else if (id == R.id.penColor) {
            penColor();
        } else if (id == R.id.screenshot) {
            takeScreenshot();

            return true;
        }

        else if(id== R.id.undo)
        {
            undo();
        }

        return super.onOptionsItemSelected(item);
    }

    private void penColor() {

        AmbilWarnaDialog dialog = new AmbilWarnaDialog(MainActivity.this, Color.BLACK, new AmbilWarnaDialog.OnAmbilWarnaListener() {


            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                penColor = color;
                ((DrawingView) mView).changePenColor();

            }


            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }
        });

        dialog.show();


    }

    private void bgColor() {


        AmbilWarnaDialog dialog = new AmbilWarnaDialog(MainActivity.this, Color.BLACK, new AmbilWarnaDialog.OnAmbilWarnaListener() {


            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                bgColor = color;
                ((DrawingView) mView).setBGColor(bgColor);

            }


            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }
        });

        dialog.show();

    }


    private void takeScreenshot()
    {

        mView.setDrawingCacheEnabled(true);

        String filename=  UUID.randomUUID().toString() + ".png";

        String imgSaved = MediaStore.Images.Media.insertImage(
                getContentResolver(), mView.getDrawingCache(),
                filename, "drawing");


        if (imgSaved != null) {
            Toast.makeText(getApplicationContext(),
                    "Drawing saved to Gallery! ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Oops! Image could not be saved.", Toast.LENGTH_SHORT).show();
        }
        mView.destroyDrawingCache();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(imgSaved)));

    }



    class DrawingView extends View {

        ArrayList<DrawingPath> paths;
        private DrawingPath drawingPath;
        private Bitmap mBitmap;
        private Canvas mCanvas;


        public DrawingView(Context context) {
            super(context);

            paths = new ArrayList<>();
            mBitmap = Bitmap.createBitmap(820, 480, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            this.setBackgroundColor(bgColor);
            mPaint = new Paint();
            mPaint.setDither(true);
            mPaint.setColor(penColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(StrokeWidth);


        }

        public void changePenSize(int size) {
            StrokeWidth = size;
            mPaint.setStrokeWidth(StrokeWidth);

        }



        public void remove()
        {
            if(paths.size()>=1)
            {
                paths.remove(paths.size()-1);
                invalidate();
            }

        }

        public void init() {
            mPaint = new Paint();
            mPaint.setDither(true);
            mPaint.setColor(penColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(StrokeWidth);

        }

        public void setBGColor(int color) {
            bgColor = color;
            this.setBackgroundColor(bgColor);
            invalidate();


        }

        public void ClearPath() {
            paths.clear();
            bgColor = Color.WHITE;
            penColor = Color.BLACK;
            init();
            invalidate();
        }

        public void changePenColor() {
            mPaint.setColor(penColor);

        }




        @Override
        public boolean onTouchEvent(MotionEvent event) {

            float touchX = event.getX();
            float touchY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawingPath = new DrawingPath((int) mPaint.getStrokeWidth(), mPaint.getColor());
                    paths.add(drawingPath);
                    drawingPath.path.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawingPath.path.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:

                    mCanvas.drawPath(drawingPath.path,mPaint);
                    break;
                default:
                    return false;
            }

            invalidate();
            return true;
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            for (DrawingPath drawingPath : paths) {
                mPaint.setStrokeWidth(drawingPath.strokeWidth);
                mPaint.setColor(drawingPath.penColor);
                canvas.drawPath(drawingPath.path, mPaint);
            }



        }


    }




}




