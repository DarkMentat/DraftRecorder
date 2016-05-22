package org.darkmentat.draftrecorder.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.darkmentat.draftrecorder.R;
import org.darkmentat.draftrecorder.utils.AudioUtils;
import org.darkmentat.draftrecorder.utils.SamplingUtils;

import java.util.LinkedList;

/**
 * Thanks to: https://github.com/newventuresoftware/WaveformControl
 */
public class WaveformView extends View implements GestureDetector.OnGestureListener {
  public static final int MODE_RECORDING = 1;
  public static final int MODE_PLAYBACK = 2;

  private static final int HISTORY_SIZE = 6;

  private final GestureDetector mGestureDetector = new GestureDetector(getContext(), this);

//  private TextPaint mTextPaint;
  private Paint mStrokePaint, mFillPaint, mMarkerPaint, mTempoGridPaint;

  // Used in draw
  private int brightness;
  private Rect drawRect;

  private int width, height;
  private float xStep, centerY;
  private int mMode, mAudioLength, mMarkerPosition, mSampleRate, mChannels;
  private short[] mSamples;
  private LinkedList<float[]> mHistoricalData;
  private Picture mCachedWaveform;
  private Bitmap mCachedWaveformBitmap;
  private int colorDelta = 255 / (HISTORY_SIZE + 1);

  private int mTempoBpm;
  private int mTempoBeats;
  private int mTempoBeatLength;

  private boolean mCanChangeTempoGrid = false;
  private float mOffsetTempoGridSeconds = 0.0f;
  private float mLastSecond = -1f;

  private boolean mShowCutEnds = false;

  public WaveformView(Context context) {
    super(context);
    init(context, null, 0);
  }

  public WaveformView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, 0);
  }

  public WaveformView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context, attrs, defStyle);
  }

  private void init(Context context, AttributeSet attrs, int defStyle) {
    // Load attributes
    final TypedArray a = getContext().obtainStyledAttributes(
        attrs, R.styleable.WaveformView, defStyle, 0);

    mMode = a.getInt(R.styleable.WaveformView_mode, MODE_PLAYBACK);

    float strokeThickness = a.getFloat(R.styleable.WaveformView_waveformStrokeThickness, 1f);
    int mStrokeColor = a.getColor(R.styleable.WaveformView_waveformColor,
        ContextCompat.getColor(context, R.color.default_waveform));
    int mFillColor = a.getColor(R.styleable.WaveformView_waveformFillColor,
        ContextCompat.getColor(context, R.color.default_waveformFill));
    int mMarkerColor = a.getColor(R.styleable.WaveformView_playbackIndicatorColor,
        ContextCompat.getColor(context, R.color.default_playback_indicator));
    int mTextColor = a.getColor(R.styleable.WaveformView_timecodeColor,
        ContextCompat.getColor(context, R.color.default_timecode));
    int mTempoGridColor = a.getColor(R.styleable.WaveformView_tempogridColor,
        ContextCompat.getColor(context, R.color.default_tempogrid));

    a.recycle();

//    mTextPaint = new TextPaint();
//    mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
//    mTextPaint.setTextAlign(Paint.Align.CENTER);
//    mTextPaint.setColor(mTextColor);
//    mTextPaint.setTextSize(TextUtils.getFontSize(getContext(),
//        android.R.attr.textAppearanceSmall));

    mStrokePaint = new Paint();
    mStrokePaint.setColor(mStrokeColor);
    mStrokePaint.setStyle(Paint.Style.STROKE);
    mStrokePaint.setStrokeWidth(strokeThickness);
    mStrokePaint.setAntiAlias(true);

    mFillPaint = new Paint();
    mFillPaint.setStyle(Paint.Style.FILL);
    mFillPaint.setAntiAlias(true);
    mFillPaint.setColor(mFillColor);

    mMarkerPaint = new Paint();
    mMarkerPaint.setStyle(Paint.Style.STROKE);
    mMarkerPaint.setStrokeWidth(0);
    mMarkerPaint.setAntiAlias(true);
    mMarkerPaint.setColor(mMarkerColor);

    mTempoGridPaint = new Paint();
    mTempoGridPaint.setStyle(Paint.Style.STROKE);
    mTempoGridPaint.setStrokeWidth(1);
    mTempoGridPaint.setAntiAlias(true);
    mTempoGridPaint.setColor(mTempoGridColor);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    width = getMeasuredWidth();
    height = getMeasuredHeight();
    xStep = width / (mAudioLength * 1.0f);
    centerY = height / 2f;
    drawRect = new Rect(0, 0, width, height);

    if (mHistoricalData != null) {
      mHistoricalData.clear();
    }
    if (mMode == MODE_PLAYBACK) {
      createPlaybackWaveform();
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    LinkedList<float[]> temp = mHistoricalData;
    if (mMode == MODE_RECORDING && temp != null) {
      brightness = colorDelta;
      for (float[] p : temp) {
        mStrokePaint.setAlpha(brightness);
        canvas.drawLines(p, mStrokePaint);
        brightness += colorDelta;
      }
    } else if (mMode == MODE_PLAYBACK) {
      if (mCachedWaveform != null) {
        canvas.drawPicture(mCachedWaveform);
      } else if (mCachedWaveformBitmap != null) {
        canvas.drawBitmap(mCachedWaveformBitmap, null, drawRect, null);
      }
      if (mMarkerPosition > -1 && mMarkerPosition < mAudioLength)
        canvas.drawLine(xStep * mMarkerPosition, 0, xStep * mMarkerPosition, height, mMarkerPaint);

      drawTempoGrid(canvas, width);
    }
  }

  public int getMode() {
    return mMode;
  }

  public void setMode(int mMode) {
    mMode = mMode;
  }

  public short[] getSamples() {
    return mSamples;
  }

  public void setSamples(short[] samples) {
    mSamples = samples;
    calculateAudioLength();
    onSamplesChanged();
  }

  public void setTempo(int bpm, int beats, int BeatLength){
    mTempoBpm = bpm;
    mTempoBeats = beats;
    mTempoBeatLength = BeatLength;
  }

  public int getMarkerPosition() {
    return mMarkerPosition;
  }

  public void setMarkerPosition(int markerPosition) {
    mMarkerPosition = markerPosition;
    postInvalidate();
  }

  public int getAudioLength() {
    return mAudioLength;
  }

  public int getSampleRate() {
    return mSampleRate;
  }

  public void setSampleRate(int sampleRate) {
    mSampleRate = sampleRate;
    calculateAudioLength();
  }

  public int getChannels() {
    return mChannels;
  }

  public void setChannels(int channels) {
    mChannels = channels;
    calculateAudioLength();
  }

  private void calculateAudioLength() {
    if (mSamples == null || mSampleRate == 0 || mChannels == 0)
      return;

    if(mShowCutEnds || mLastSecond < 0 || mOffsetTempoGridSeconds < 0.0000001f){
      mAudioLength = AudioUtils.calculateAudioLength(mSamples.length, mSampleRate, mChannels);

      if(mLastSecond < 0)
        mLastSecond = mAudioLength / 1000.0f;
    }else{
      mAudioLength = (int) ((mLastSecond - mOffsetTempoGridSeconds) * 1000);
    }

    if(mLastSecond < 0)
      mLastSecond = mAudioLength / 1000.0f;
  }

  private void onSamplesChanged() {
    if (mMode == MODE_RECORDING) {
      if (mHistoricalData == null)
        mHistoricalData = new LinkedList<>();
      LinkedList<float[]> temp = new LinkedList<>(mHistoricalData);

      // For efficiency, we are reusing the array of points.
      float[] waveformPoints;
      if (temp.size() == HISTORY_SIZE) {
        waveformPoints = temp.removeFirst();
      } else {
        waveformPoints = new float[width * 4];
      }

      drawRecordingWaveform(mSamples, waveformPoints);
      temp.addLast(waveformPoints);
      mHistoricalData = temp;
      postInvalidate();
    } else if (mMode == MODE_PLAYBACK) {
      mMarkerPosition = -1;
      createPlaybackWaveform();
    }
  }

  void drawRecordingWaveform(short[] buffer, float[] waveformPoints) {
    float lastX = -1;
    float lastY = -1;
    int pointIndex = 0;
    float max = Short.MAX_VALUE;

    // For efficiency, we don't draw all of the samples in the buffer, but only the ones
    // that align with pixel boundaries.
    for (int x = 0; x < width; x++) {
      int index = (int) (((x * 1.0f) / width) * buffer.length);
      short sample = buffer[index];
      float y = centerY - ((sample / max) * centerY);

      if (lastX != -1) {
        waveformPoints[pointIndex++] = lastX;
        waveformPoints[pointIndex++] = lastY;
        waveformPoints[pointIndex++] = x;
        waveformPoints[pointIndex++] = y;
      }

      lastX = x;
      lastY = y;
    }
  }

  Path drawPlaybackWaveform(int width, int height, short[] buffer) {
    Path waveformPath = new Path();
    float centerY = height / 2f;
    float max = Short.MAX_VALUE;

    int startIndex = mShowCutEnds ? 0 : AudioUtils.getIndexOfSecond(mOffsetTempoGridSeconds, mSampleRate, mChannels);
    int endIndex = mShowCutEnds ? buffer.length-1 : AudioUtils.getIndexOfSecond(mLastSecond, mSampleRate, mChannels);

    short[][] extremes = SamplingUtils.getExtremes(buffer, width, startIndex, endIndex);

    // draw maximums
    for (int x = 0; x < width; x++) {
      short sample = extremes[x][0];
      float y = centerY - ((sample / max) * centerY);
      waveformPath.lineTo(x, y);
    }

    // draw minimums
    for (int x = width - 1; x >= 0; x--) {
      short sample = extremes[x][1];
      float y = centerY - ((sample / max) * centerY);
      waveformPath.lineTo(x, y);
    }

    waveformPath.close();

    return waveformPath;
  }

  private void createPlaybackWaveform() {
    if (width <= 0 || height <= 0 || mSamples == null)
      return;

    Canvas cacheCanvas;
    if (Build.VERSION.SDK_INT >= 23 && isHardwareAccelerated()) {
      mCachedWaveform = new Picture();
      cacheCanvas = mCachedWaveform.beginRecording(width, height);
    } else {
      mCachedWaveformBitmap = Bitmap.createBitmap(width/3, height/3, Bitmap.Config.ARGB_8888);
      cacheCanvas = new Canvas(mCachedWaveformBitmap);
    }

    Path mWaveform = drawPlaybackWaveform(width/3, height/3, mSamples);
    cacheCanvas.drawPath(mWaveform, mFillPaint);
    cacheCanvas.drawPath(mWaveform, mStrokePaint);

    //drawAxis(cacheCanvas, width);

    if (mCachedWaveform != null)
      mCachedWaveform.endRecording();
  }

  private void drawAxis(Canvas canvas, int width) {
//    int seconds = mAudioLength / 1000;
//    float xStep = width / (mAudioLength / 1000f);
//    float textHeight = mTextPaint.getTextSize();
//    float textWidth = mTextPaint.measureText("10.00");
//    int secondStep = (int)(textWidth * seconds * 2) / width;
//    secondStep = Math.max(secondStep, 1);
//    for (float i = 0; i <= seconds; i += secondStep) {
//      canvas.drawText(String.format("%.2f", i), i * xStep, textHeight, mTextPaint);
//    }
  }

  private void drawTempoGrid(Canvas canvas, int width){
    float seconds = mAudioLength / 1000.0f;

    float secondsStep = 60.0f / (mTempoBpm * mTempoBeatLength / 4);

    float segmentSecond = width / seconds;

    int height = canvas.getHeight();

    float start = mOffsetTempoGridSeconds;
    float end = mLastSecond;

    if(!mShowCutEnds){
      start = 0;
      end = seconds;
    }

    for (float i = secondsStep; i <= seconds+secondsStep; i += secondsStep) {
      float left = segmentSecond*(start + i - secondsStep);
      float right = segmentSecond*(start + i < end ? start + i : mLastSecond);

      canvas.drawRect(left, 0, right, height, mTempoGridPaint);
    }
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    if(mCanChangeTempoGrid)//noinspection PointlessBooleanExpression
      return mGestureDetector.onTouchEvent(event) || true;
    else
      return super.onTouchEvent(event);
  }
  @Override public boolean onDown(MotionEvent e) {
    return false;
  }
  @Override public void onShowPress(MotionEvent e) {

  }
  @Override public boolean onSingleTapUp(MotionEvent e) {
    return false;
  }
  @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

    if(distanceX > 20 || distanceX < -20)
      return true;

    float seconds = mAudioLength / 1000.0f;
    float segmentSecond = width / seconds;

    boolean changingStartOffset = e1.getX() < 2*mLastSecond*segmentSecond/3;

    if(changingStartOffset){

      mOffsetTempoGridSeconds -= distanceX / segmentSecond;

      if(mOffsetTempoGridSeconds < 0)
        mOffsetTempoGridSeconds = 0.0f;

    }else{

      mLastSecond -= distanceX / segmentSecond;

      if(mLastSecond > seconds)
        mLastSecond = seconds;

      if(mLastSecond < mOffsetTempoGridSeconds + 0.5f)
        mLastSecond = mOffsetTempoGridSeconds + 0.5f;

    }

    Log.d("Scroll", "distanceX: "+distanceX);
    Log.d("Scroll", "mOffsetTempoGridSeconds: "+mOffsetTempoGridSeconds);
    Log.d("Scroll", "mLastSecond: "+mLastSecond);
    Log.d("Scroll", "");

    invalidate();
    return true;
  }

  @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    return false;
  }
  @Override public void onLongPress(MotionEvent e) {

  }

  public void setCanChangeTempoGrid(boolean canChangeTempoGrid) {
    mCanChangeTempoGrid = canChangeTempoGrid;
  }
  public float getStartCutSeconds(){
    return mOffsetTempoGridSeconds;
  }
  public void setStartCutSeconds(float seconds){
    mOffsetTempoGridSeconds = seconds;
  }
  public float getLastSecond() {
    return mLastSecond;
  }
  public void setLastSecond(float lastSecond) {
    if(lastSecond < 0)
      return;

    mLastSecond = lastSecond;
  }

  public void setShowCutEnds(boolean showCutEnds) {
    mShowCutEnds = showCutEnds;
    calculateAudioLength();
  }
}
