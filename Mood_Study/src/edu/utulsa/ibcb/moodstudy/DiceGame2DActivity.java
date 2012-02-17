package edu.utulsa.ibcb.moodstudy;

import edu.utulsa.ibcb.moodstudy.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class DiceGame2DActivity extends Activity {

    private SimulationView mSimulationView;
    private SensorManager mSensorManager;
    private PowerManager mPowerManager;
    private WindowManager mWindowManager;
    private Display mDisplay;
    private WakeLock mWakeLock;
    protected int actual;
    protected int prompt;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass()
                .getName());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        mSimulationView = new SimulationView(this);
        setContentView(mSimulationView);
        
        actual = getIntent().getExtras().getInt("actual", 0);
        prompt = getIntent().getExtras().getInt("prompt", 0);
        mSimulationView.setDice(prompt,actual);
    
    }
    
    public void onGameOver(){
    	setContentView(R.layout.main);
    	Intent iOver = new Intent(this,FinalSurveyActivity.class);
    	iOver.putExtra("won", actual==prompt);
    	startActivity(iOver);
    }
    
    @Override
	public void onBackPressed() {
    	startActivity(new Intent(this,FinalSurveyActivity.class));
	}

    @Override
    protected void onResume() {
        super.onResume();
        /*
		* when the activity is resumed, we acquire a wake-lock so that the
		* screen stays on, since the user will likely not be fiddling with the
		* screen or buttons.
		*/
        mWakeLock.acquire();

        // Start the simulation
        mSimulationView.startSimulation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
		* When the activity is paused, we make sure to stop the simulation,
		* release our sensor resources and wake locks
		*/

        // Stop the simulation
        mSimulationView.stopSimulation();

        // and release our wake-lock
        mWakeLock.release();
    }

    
    /**
	* Based on code Copyright (C) 2010 The Android Open Source Project, modifications have been made
	*
	* Licensed under the Apache License, Version 2.0 (the "License");
	* you may not use this file except in compliance with the License.
	* You may obtain a copy of the License at
	*
	* http://www.apache.org/licenses/LICENSE-2.0
	*
	* Unless required by applicable law or agreed to in writing, software
	* distributed under the License is distributed on an "AS IS" BASIS,
	* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	* See the License for the specific language governing permissions and
	* limitations under the License.
	*/
    class SimulationView extends View implements SensorEventListener {
        // diameter of the balls in meters
        private static final float sBallDiameter = 0.02f;
        private static final float sBallDiameter2 = sBallDiameter * sBallDiameter;

        // friction of the virtual table and air
        private static final float sFriction = 0.05f;

        private Sensor mAccelerometer;
        private long mLastT;
        private float mLastDeltaT;

        private float mXDpi;
        private float mYDpi;
        private float mMetersToPixelsX;
        private float mMetersToPixelsY;
        private Bitmap mBitmap;
        private Bitmap mBackground;
        private Bitmap mFrontCup;
        private Bitmap mBackCup;
        private int mW=0, mH=0, actual=0, prompt=0;
        private boolean endingAnimation = false;
        private float mXOrigin;
        private float mYOrigin;
        private float mSensorX;
        private float mSensorY;
        private long mSensorTimeStamp;
        private long mCpuTimeStamp;
        private float mHorizontalBound;
        private float mVerticalBound;
        private final ParticleSystem mParticleSystem = new ParticleSystem();

        /*
* Each of our particle holds its previous and current position, its
* acceleration. for added realism each particle has its own friction
* coefficient.
*/
        class Particle {
            private float mPosX;
            private float mPosY;
            private float mAccelX;
            private float mAccelY;
            private float mLastPosX;
            private float mLastPosY;
            private float mOneMinusFriction;

            Particle() {
                // make each particle a bit different by randomizing its
                // coefficient of friction
                final float r = ((float) Math.random() - 0.5f) * 0.2f;
                mOneMinusFriction = 1.0f - sFriction + r;
            }

            public void computePhysics(float sx, float sy, float dT, float dTC) {
                if(!endingAnimation)
                {
                	// Force of gravity applied to our virtual object
	                final float m = 1000.0f; // mass of our virtual object
	                final float gx = -sx * m;
	                final float gy = -sy * m;

	                /*
					* ·F = mA <=> A = ·F / m We could simplify the code by
					* completely eliminating "m" (the mass) from all the equations,
					* but it would hide the concepts from this sample code.
					*/
	                final float invm = 1.0f / m;
	                final float ax = gx * invm;
	                final float ay = gy * invm;

	                /*
					* Time-corrected Verlet integration The position Verlet
					* integrator is defined as x(t+Æt) = x(t) + x(t) - x(t-Æt) +
					* a(t)Ætö2 However, the above equation doesn't handle variable
					* Æt very well, a time-corrected version is needed: x(t+Æt) =
					* x(t) + (x(t) - x(t-Æt)) * (Æt/Æt_prev) + a(t)Ætö2 We also add
					* a simple friction term (f) to the equation: x(t+Æt) = x(t) +
					* (1-f) * (x(t) - x(t-Æt)) * (Æt/Æt_prev) + a(t)Ætö2
					*/
	                final float dTdT = dT * dT;
	                final float x = mPosX + mOneMinusFriction * dTC * (mPosX - mLastPosX) + mAccelX
	                        * dTdT;
	                final float y = mPosY + mOneMinusFriction * dTC * (mPosY - mLastPosY) + mAccelY
	                        * dTdT;
	                mLastPosX = mPosX;
	                mLastPosY = mPosY;
	                mPosX = x;
	                mPosY = y;
	                mAccelX = ax;
	                mAccelY = ay;
                }
                else{
                	mPosX *=.95;
                	mPosY *=.95;
                	if(Math.abs(mPosX)<.0001 && Math.abs(mPosY)<.0001)
                	{
                		onGameOver();
                	}
                }
            }

            /*
* Resolving constraints and collisions with the Verlet integrator
* can be very simple, we simply need to move a colliding or
* constrained particle in such way that the constraint is
* satisfied.
*/
            public void resolveCollisionWithBounds() {
                final float xmax = mHorizontalBound;
                final float ymax = mVerticalBound;
                final float x = mPosX;
                final float y = mPosY;
                if (x > xmax) {
                    mPosX = xmax;
                } else if (x < -xmax) {
                    mPosX = -xmax;
                }
                if (y > ymax) {
                    mPosY = -ymax;
                    endingAnimation=true;
                } else if (y < -ymax) {
                    mPosY = -ymax;
                }
            }
        }

        /*
* A particle system is just a collection of particles
*/
        class ParticleSystem {
            static final int NUM_PARTICLES = 1;
            private Particle mBalls[] = new Particle[NUM_PARTICLES];

            ParticleSystem() {
                /*
* Initially our particles have no speed or acceleration
*/
                for (int i = 0; i < mBalls.length; i++) {
                    mBalls[i] = new Particle();
                }
            }

            /*
* Update the position of each particle in the system using the
* Verlet integrator.
*/
            private void updatePositions(float sx, float sy, long timestamp) {
                final long t = timestamp;
                if (mLastT != 0) {
                    final float dT = (float) (t - mLastT) * (1.0f / 1000000000.0f);
                    if (mLastDeltaT != 0) {
                        final float dTC = dT / mLastDeltaT;
                        final int count = mBalls.length;
                        for (int i = 0; i < count; i++) {
                            Particle ball = mBalls[i];
                            ball.computePhysics(sx, sy, dT, dTC);
                        }
                    }
                    mLastDeltaT = dT;
                }
                mLastT = t;
            }

            /*
* Performs one iteration of the simulation. First updating the
* position of all the particles and resolving the constraints and
* collisions.
*/
            public void update(float sx, float sy, long now) {
                // update the system's positions
                updatePositions(sx, sy, now);

                // We do no more than a limited number of iterations
                final int NUM_MAX_ITERATIONS = 10;

                /*
* Resolve collisions, each particle is tested against every
* other particle for collision. If a collision is detected the
* particle is moved away using a virtual spring of infinite
* stiffness.
*/
                boolean more = true;
                final int count = mBalls.length;
                for (int k = 0; k < NUM_MAX_ITERATIONS && more; k++) {
                    more = false;
                    for (int i = 0; i < count; i++) {
                        Particle curr = mBalls[i];
                        for (int j = i + 1; j < count; j++) {
                            Particle ball = mBalls[j];
                            float dx = ball.mPosX - curr.mPosX;
                            float dy = ball.mPosY - curr.mPosY;
                            float dd = dx * dx + dy * dy;
                            // Check for collisions
                            if (dd <= sBallDiameter2) {
                                /*
* add a little bit of entropy, after nothing is
* perfect in the universe.
*/
                                dx += ((float) Math.random() - 0.5f) * 0.0001f;
                                dy += ((float) Math.random() - 0.5f) * 0.0001f;
                                dd = dx * dx + dy * dy;
                                // simulate the spring
                                final float d = (float) Math.sqrt(dd);
                                final float c = (0.5f * (sBallDiameter - d)) / d;
                                curr.mPosX -= dx * c;
                                curr.mPosY -= dy * c;
                                ball.mPosX += dx * c;
                                ball.mPosY += dy * c;
                                more = true;
                            }
                        }
                        /*
* Finally make sure the particle doesn't intersects
* with the walls.
*/
                        curr.resolveCollisionWithBounds();
                    }
                }
            }

            public int getParticleCount() {
                return mBalls.length;
            }

            public float getPosX(int i) {
                return mBalls[i].mPosX;
            }

            public float getPosY(int i) {
                return mBalls[i].mPosY;
            }
        }

        public void startSimulation() {
            /*
* It is not necessary to get accelerometer events at a very high
* rate, by using a slower rate (SENSOR_DELAY_UI), we get an
* automatic low-pass filter, which "extracts" the gravity component
* of the acceleration. As an added benefit, we use less power and
* CPU resources.
*/
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        public void setDice(int p, int a) {
			prompt = p;
			actual= a;
			int id = R.drawable.icon;
			switch(a){
			case 1: id = R.drawable.dice1; break;
			case 2: id = R.drawable.dice2; break;
			case 3: id = R.drawable.dice3; break;
			case 4: id = R.drawable.dice4; break;
			case 5: id = R.drawable.dice5; break;
			case 6: id = R.drawable.dice6; break;
			}
			
			
			// rescale the ball so it's about 2 cm on screen
            Bitmap ball = BitmapFactory.decodeResource(getResources(), id);
            final int dstWidth = (int) (sBallDiameter * mMetersToPixelsX + 0.5f);
            final int dstHeight = (int) (sBallDiameter * mMetersToPixelsY + 0.5f);
            mBitmap = Bitmap.createScaledBitmap(ball, dstWidth, dstHeight, true);
			
		}

		public void stopSimulation() {
            mSensorManager.unregisterListener(this);
        }

        public SimulationView(Context context) {
            super(context);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mXDpi = metrics.xdpi;
            mYDpi = metrics.ydpi;
            mMetersToPixelsX = mXDpi / 0.0254f;
            mMetersToPixelsY = mYDpi / 0.0254f;
            
            
            // rescale the ball so it's about 2 cm on screen
            Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
            final int dstWidth = (int) (sBallDiameter * mMetersToPixelsX + 0.5f);
            final int dstHeight = (int) (sBallDiameter * mMetersToPixelsY + 0.5f);
            mBitmap = Bitmap.createScaledBitmap(temp, dstWidth, dstHeight, true);

            Options opts = new Options();
            opts.inDither = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            temp = BitmapFactory.decodeResource(getResources(), R.drawable.dice_table, opts);
            mBackground = Bitmap.createScaledBitmap(temp,(int)(metrics.widthPixels),(int)(metrics.heightPixels), true);
            temp = BitmapFactory.decodeResource(getResources(), R.drawable.cup_bottom, opts);
            mFrontCup = Bitmap.createScaledBitmap(temp, metrics.widthPixels, (int)(temp.getHeight()*(metrics.widthPixels/(float)temp.getWidth())), true);
            temp = BitmapFactory.decodeResource(getResources(), R.drawable.cup_together, opts);
            mBackCup = Bitmap.createScaledBitmap(temp, metrics.widthPixels, (int)(temp.getHeight()*(metrics.widthPixels/(float)temp.getWidth())), true);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            // compute the origin of the screen relative to the origin of
            // the bitmap
        	mW=w;
        	mH=h;
            mXOrigin = (w - mBitmap.getWidth()) * 0.5f;
            mYOrigin = (h - mBitmap.getHeight()) * 0.5f;
            mHorizontalBound = ((w / mMetersToPixelsX - sBallDiameter) * 0.5f);
            mVerticalBound = ((h / mMetersToPixelsY - sBallDiameter) * 0.5f);
        }

// @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                return;
            /*
* record the accelerometer data, the event's timestamp as well as
* the current time. The latter is needed so we can calculate the
* "present" time during rendering. In this application, we need to
* take into account how the screen is rotated with respect to the
* sensors (which always return data in a coordinate space aligned
* to with the screen in its native orientation).
*/

            switch (mDisplay.getRotation()) {
                case Surface.ROTATION_0:
                    mSensorX = event.values[0];
                    mSensorY = event.values[1];
                    break;
                case Surface.ROTATION_90:
                    mSensorX = -event.values[1];
                    mSensorY = event.values[0];
                    break;
                case Surface.ROTATION_180:
                    mSensorX = -event.values[0];
                    mSensorY = -event.values[1];
                    break;
                case Surface.ROTATION_270:
                    mSensorX = event.values[1];
                    mSensorY = -event.values[0];
                    break;
            }

            mSensorTimeStamp = event.timestamp;
            mCpuTimeStamp = System.nanoTime();
        }

        @Override
        protected void onDraw(Canvas canvas) {

            /*
* draw the background
*/

            if(!endingAnimation)
            	canvas.drawBitmap(mBackCup, mW-mBackCup.getWidth(), mH-mBackCup.getHeight(), null);
            else
            	canvas.drawBitmap(mBackground, 0, 0, null);
            /*
* compute the new position of our object, based on accelerometer
* data and present time.
*/

            final ParticleSystem particleSystem = mParticleSystem;
            final long now = mSensorTimeStamp + (System.nanoTime() - mCpuTimeStamp);
            final float sx = mSensorX;
            final float sy = mSensorY;

            particleSystem.update(sx, sy, now);

            final float xc = mXOrigin;
            final float yc = mYOrigin;
            final float xs = mMetersToPixelsX;
            final float ys = mMetersToPixelsY;
            final Bitmap bitmap = mBitmap;
            final int count = particleSystem.getParticleCount();
            for (int i = 0; i < count; i++) {
                /*
* We transform the canvas so that the coordinate system matches
* the sensors coordinate system with the origin in the center
* of the screen and the unit is the meter.
*/

                final float x = xc + particleSystem.getPosX(i) * xs;
                final float y = yc - particleSystem.getPosY(i) * ys;
                canvas.drawBitmap(bitmap, x, y, null);
            }
            
            if(!endingAnimation)
            	canvas.drawBitmap(mFrontCup, mW-mFrontCup.getWidth(), mH-mFrontCup.getHeight(), null);
            
            // and make sure to redraw asap
            invalidate();
        }

// @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}