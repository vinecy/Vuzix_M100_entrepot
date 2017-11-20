/*
MainActivity.java

Copyright (c) 2015, Vuzix Corporation
        All rights reserved.

        Redistribution and use in source and binary forms, with or without
        modification, are permitted provided that the following conditions
        are met:

        *  Redistributions of source code must retain the above copyright
        notice, this list of conditions and the following disclaimer.

        *  Redistributions in binary form must reproduce the above copyright
        notice, this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.

        *  Neither the name of Vuzix Corporation nor the names of
        its contributors may be used to endorse or promote products derived
        from this software without specific prior written permission.

        THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
        AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
        THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
        PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
        CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
        EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
        PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
        OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
        WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
        OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
        EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

//
// Simple Activity class that allows the user to use Vuzix Voice Control
// to create shapes (circle or square) on the screen and then manipulate
// the shapes.   Here are the voice commands:
//
// create (circle, square)
// select #
// move (up, down, left, right)
// color (red, green, blue, yellow, cyan, magenta, black, gray)
// delete
// bigger
// smaller
//

package com.vuzix.entrepot;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;
import android.widget.LinearLayout;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import com.vuzix.speech.VoiceControl;


//
// A simple activity class that allows us to use Vuzix Voice Control
// to create and manipulate circles and squares (graphically)
//
public class MainActivity extends Activity {

    private ColorDemoSpeechRecognizer mSR;
    private Canvas mCanvas;
    private Bitmap mBG;
    private ArrayList<ShapeInfo> mShapes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up speech recognizer
        mSR = new ColorDemoSpeechRecognizer(this);
        if (mSR == null)
        {
            Toast.makeText(this,"Unable to create Speech Recognizer",Toast.LENGTH_SHORT).show();
            return;
        }

        loadCustomGrammar();

        Toast.makeText(this,"Speech Recognizer CREATED, turning on",Toast.LENGTH_SHORT).show();
        android.util.Log.i("VUZIX-VoiceDemo","Turning on Speech Recognition");
        mSR.on();

        // Set up drawing canvas
        mBG = Bitmap.createBitmap(432,200,Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBG);

        mShapes = new ArrayList<ShapeInfo>();

    }

    @Override
    public void onDestroy()
    {
        // If we have an active Speech Recognition object, make sure we destroy the
        // speech recognizer resource before our activity is destroyed
        if (mSR != null)
        {
            mSR.destroy();
        }

        super.onDestroy();
    }

    @Override
    public void onPause()
    {
        android.util.Log.i("VUZIX-VoiceDemo","onPause(), stopping speech recognition");
        // If we have an active Speech Recognition object, make sure we turn off
        // speech recogniztion before losing context/focus.
        if (mSR != null)
        {
            mSR.off();
        }

        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        android.util.Log.i("VUZIX-VoiceDemo", "onResume(), restarting speech recognition");

        // If we have an active Speech Recognition object, make sure we turn
        // speech recogniztion back on after regaining context/focus.
        if (mSR != null)
        {
            mSR.on();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // draw a shape.   Takes an instance of ShapeInfo and a color.
    // We could pull the color from the ShapeInfo structure except in the
    // case where we are calling drawShape() to ERASE the shape.
    private void drawShape(ShapeInfo shape, int color)
    {
        Paint paint = new Paint();
        paint.setColor(color);

        if (shape.shape == ShapeInfo.CIRCLE)
            mCanvas.drawCircle(shape.mXcoord, shape.mYcoord, shape.size, paint);
        else
            mCanvas.drawRect(shape.mXcoord, shape.mYcoord,
                    shape.mXcoord+shape.size,
                    shape.mYcoord+shape.size, paint);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.rect);
        linearLayout.setBackgroundDrawable(new BitmapDrawable(mBG));
    }

    // load our custom speech recogniztion grammar file so that we can
    // respond to specific commands for our application
    private void loadCustomGrammar(){
        android.util.Log.i("VUZIX-VoiceDemo", "copyGrammar()");
        final android.content.res.Resources resources = getResources();
        final android.content.res.AssetManager assets = resources.getAssets();

        try
        {
            final java.io.InputStream fi = assets.open("drawing_custom.lcf");

            byte[] buf = new byte[fi.available()];
            while (fi.read(buf) > 0){ }
            fi.close();

            mSR.addGrammar(buf,"Color Demo Grammar");
        }
        catch(java.io.IOException ex)
        {
            android.util.Log.e("VUZIX-VoiceDemo",""+ex);
        }
        android.util.Log.i("VUZIX-VoiceDemo", "Done writing grammar files.\n");
    }

    // a simple class to store information about a shape
    private class ShapeInfo {
        public int mXcoord;
        public int mYcoord;
        public int size;  // radius for circles, width & height for squares
        public int shape;
        public int color;

        public static final int CIRCLE = 0;
        public static final int SQUARE = 1;

        public ShapeInfo(int argX,int argY,int argSize, int argColor, int argShape)
        {
            mXcoord = argX;
            mYcoord = argY;
            size = argSize;
            color = argColor;
            shape = argShape;
        }

    }

    // our custom extension of the com.vuzix.speech.VoiceControl class.    This class
    // will contain the callback (onRecognition()) which is used to handle spoken
    // commands detected.
    public class ColorDemoSpeechRecognizer extends VoiceControl
    {
        private int mSelectedItem = 0;

        public ColorDemoSpeechRecognizer(Context context) {
            super(context);
        }

        protected void onRecognition(String result)
        {
            android.util.Log.i("VUZIX-VoiceDemo","onRecognition: "+result);
            Toast.makeText(MainActivity.this,"Speech Command Received: "+result,Toast.LENGTH_SHORT).show();

            // take action based on command spoken
            if (result.startsWith("new square"))
            {
                // the SQUARE command
                Paint paint = new Paint();
                paint.setColor(Color.GREEN);
                mCanvas.drawRect(20, 20, 60, 60, paint);

                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.rect);
                linearLayout.setBackgroundDrawable(new BitmapDrawable(mBG));

                mShapes.add(new ShapeInfo(20,20,40,Color.GREEN,ShapeInfo.SQUARE));
           }
            else if (result.startsWith("new circle"))
            {
                // It's legit, create a new circle
                Paint paint = new Paint();
                paint.setColor(Color.BLUE);
                mCanvas.drawCircle(20, 100, 20, paint);

                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.rect);
                linearLayout.setBackgroundDrawable(new BitmapDrawable(mBG));

                mShapes.add(new ShapeInfo(20,100,20,Color.BLUE,ShapeInfo.CIRCLE));
            }
            else if (result.startsWith("select")) {
                // Make sure there is another argument for select (the item number)
                if (result.length() > 7) {
                    // get selection #
                    String numberString = result.substring(7);
                    int itemNumber = Integer.parseInt(numberString);

                    if ((itemNumber > 0) && (itemNumber <= mShapes.size())) {
                        mSelectedItem = itemNumber;
                        Toast.makeText(MainActivity.this, "Shape #" + itemNumber + " SELECTED", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "SELECT SHAPE: item #" + itemNumber + " does not exist", Toast.LENGTH_SHORT).show();
                        mSelectedItem = 0;
                    }
                }
            }
            else if (result.startsWith("color")) {
                if (mSelectedItem <= 0)
                {
                    Toast.makeText(MainActivity.this,"No item selected",Toast.LENGTH_SHORT).show();
                    return;
                }

                // Make sure there is another argument for select (the item number)
                if (result.length() > 7) {

                    ShapeInfo selectedShape = (ShapeInfo) mShapes.get(mSelectedItem-1);
                    String colorString = result.substring(6);

                    if (colorString.startsWith("black"))
                            selectedShape.color = Color.BLACK;
                    else if (colorString.startsWith("blue"))
                        selectedShape.color = Color.BLUE;
                    else if (colorString.startsWith("cyan"))
                            selectedShape.color = Color.CYAN;
                    else if (colorString.startsWith("gray"))
                            selectedShape.color = Color.GRAY;
                    else if (colorString.startsWith("green"))
                            selectedShape.color = Color.GREEN;
                    else if (colorString.startsWith("magenta"))
                            selectedShape.color = Color.MAGENTA;
                    else if (colorString.startsWith("red"))
                            selectedShape.color = Color.RED;
                    else if (colorString.startsWith("yellow"))
                            selectedShape.color = Color.YELLOW;

                    drawShape(selectedShape, selectedShape.color);
                    mShapes.set(mSelectedItem-1,selectedShape);
                }

            }
            else if (result.startsWith("delete")) {
                if (mSelectedItem <= 0)
                {
                    Toast.makeText(MainActivity.this,"No item selected",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ShapeInfo selectedShape = (ShapeInfo) mShapes.get(mSelectedItem-1);
                    drawShape(selectedShape, Color.WHITE);
                    mShapes.remove(mSelectedItem-1);
                    mSelectedItem = 0;
                }
            }
            else if (result.startsWith("smaller")) {
                // Make selected item smaller
                if (mSelectedItem <= 0) {
                    Toast.makeText(MainActivity.this, "No item selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                ShapeInfo selectedShape = (ShapeInfo) mShapes.get(mSelectedItem - 1);

                if (selectedShape.size <= 20)
                {
                    // don't go any smaller!
                    return;
                }

                // Erase existing item
                drawShape(selectedShape, Color.WHITE);

                // Adjust size and redraw
                selectedShape.size -= 20;
                drawShape(selectedShape, selectedShape.color);

                mShapes.set(mSelectedItem - 1,selectedShape);
            }
            else if (result.startsWith("bigger")) {
                // Make selected item smaller
                if (mSelectedItem <= 0) {
                    Toast.makeText(MainActivity.this, "No item selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                ShapeInfo selectedShape = (ShapeInfo) mShapes.get(mSelectedItem - 1);

                if (selectedShape.size >= 100)
                {
                    // don't go any bigger!
                    return;
                }

                // Erase existing item
                drawShape(selectedShape, Color.WHITE);

                // Adjust size and redraw
                selectedShape.size += 20;
                drawShape(selectedShape, selectedShape.color);

                mShapes.set(mSelectedItem - 1,selectedShape);
            }
            else if (result.startsWith("move"))
            {
                // We are implementing one of the move commands, make sure we have an item selected
                if (mSelectedItem <= 0)
                {
                    Toast.makeText(MainActivity.this,"No item selected",Toast.LENGTH_SHORT).show();
                    return;
                }

                ShapeInfo selectedShape = mShapes.get(mSelectedItem-1);

                android.util.Log.i("VUZIX-VoiceDemo", "Erasing selected shape at " + selectedShape.mXcoord + "," + selectedShape.mYcoord + " -- with size: " + selectedShape.size);
                drawShape(selectedShape, Color.WHITE);

                if (result.startsWith("move up"))
                {
                    android.util.Log.i("VUZIX-VoiceDemo","Processing MOVE UP");
                    if (selectedShape.mYcoord > 20)
                        selectedShape.mYcoord -= 20;
                }
                else if (result.startsWith("move down"))
                {
                    android.util.Log.i("VUZIX-VoiceDemo","Processing MOVE DOWN");
                    if (selectedShape.mYcoord < 220)
                        selectedShape.mYcoord += 20;
                }
                else if (result.startsWith("move left"))
                {
                    android.util.Log.i("VUZIX-VoiceDemo","Processing MOVE LEFT");
                    if (selectedShape.mXcoord > 20)
                        selectedShape.mXcoord -= 20;
                }
                else if (result.startsWith("move right"))
                {
                    android.util.Log.i("VUZIX-VoiceDemo","Processing MOVE RIGHT");
                    if (selectedShape.mXcoord < 300)
                        selectedShape.mXcoord += 20;
                }

                drawShape(selectedShape,selectedShape.color);

                android.util.Log.i("VUZIX-VoiceDemo", "Storing selected shape  " + selectedShape.mXcoord + "," + selectedShape.mYcoord + " -- with size: "+selectedShape.size);

                mShapes.set(mSelectedItem-1,selectedShape);

            }
            else {
                Toast.makeText(MainActivity.this, "Speech Command UNHANDLED: " + result, Toast.LENGTH_SHORT).show();
            }
        }

    }

}
