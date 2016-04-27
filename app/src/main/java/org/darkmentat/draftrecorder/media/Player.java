package org.darkmentat.draftrecorder.media;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.darkmentat.draftrecorder.domain.MusicComposition;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

@EBean
public class Player {

  public interface PlayerListener {
    void onPlayingStop();
  }

  public static final long TIMEOUT_US = 1000;

  private WeakReference<PlayerListener> mPlayerListener;

  private MediaPlayer mMediaPlayer;

  private static AudioTrack getAudioTrack(){
    int minSize = AudioTrack.getMinBufferSize( 16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
    return new AudioTrack(AudioManager.STREAM_MUSIC, 16000, AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT, minSize, AudioTrack.MODE_STREAM);
  }
  private static MediaCodec getCodec(MediaExtractor extractor) throws IOException {
    // Read track header
    MediaFormat format = null;
    String mime = "audio/mp4a-latm";
    int sampleRate = 16000;
    int channels = 1;
    int bitrate = 0;
    long duration = 0;

    format = extractor.getTrackFormat(0);
    mime = format.getString(MediaFormat.KEY_MIME);
    sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
    channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
    duration = format.getLong(MediaFormat.KEY_DURATION);

    MediaCodec codec = MediaCodec.createDecoderByType(mime);

    codec.configure(format, null, null, 0);

    return codec;
  }

  private MediaExtractor mExtractor;
  private MediaCodec mCodec;
  private ByteBuffer[] mCodecInputBuffers;
  private ByteBuffer[] mCodecOutputBuffers;
  private MediaCodec.BufferInfo mMediaCodecInfo;
  private boolean mSawInputEOS;
  private boolean mSawOutputEOS;
  private int mNoOutputCounter;
  private int mNoOutputCounterLimit;

  private long mPresentationTimeUs;

  private void startRecordReading(MusicComposition.Record record){
    try{
      mExtractor = new MediaExtractor();
      mExtractor.setDataSource(record.getFile().getAbsolutePath());
      mExtractor.selectTrack(0);

      mCodec = getCodec(mExtractor);
      mCodec.start();

      mCodecInputBuffers = mCodec.getInputBuffers();
      mCodecOutputBuffers = mCodec.getOutputBuffers();

      mSawInputEOS = false;
      mSawOutputEOS = false;

      mPresentationTimeUs = 0;
      mNoOutputCounter = 0;
      mNoOutputCounterLimit = 10;

      mMediaCodecInfo = new MediaCodec.BufferInfo();
    }catch(IOException e){
      e.printStackTrace();
    }
  }
  private byte[] readRecordChunk(){

    if(mSawOutputEOS)
      return null;

    if(mNoOutputCounter >= mNoOutputCounterLimit)
      return null;

    mNoOutputCounter++;

    int inputBufIndex = mCodec.dequeueInputBuffer(TIMEOUT_US);
    if (inputBufIndex >= 0) {
      ByteBuffer dstBuf = mCodecInputBuffers[inputBufIndex];
      int sampleSize = mExtractor.readSampleData(dstBuf, 0);
      if (sampleSize < 0) {
        mSawInputEOS = true;
        sampleSize = 0;
      } else {
        mPresentationTimeUs = mExtractor.getSampleTime();
      }

      mCodec.queueInputBuffer(inputBufIndex, 0, sampleSize, mPresentationTimeUs, mSawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);

      if (!mSawInputEOS) mExtractor.advance();
    }

    // decode to PCM and return it
    int res = mCodec.dequeueOutputBuffer(mMediaCodecInfo, TIMEOUT_US);

    byte[] chunk = new byte[mMediaCodecInfo.size];

    if (res >= 0) {
      if (mMediaCodecInfo.size > 0)  mNoOutputCounter = 0;

      int outputBufIndex = res;
      ByteBuffer buf = mCodecOutputBuffers[outputBufIndex];
      buf.get(chunk);
      buf.clear();

      if(chunk.length == 0)
        chunk = null;

      mCodec.releaseOutputBuffer(outputBufIndex, false);
      if ((mMediaCodecInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
        mSawOutputEOS = true;
      }
    }

    return chunk;
  }
  private void stopRecordReading(){
    mCodec.stop();
    mCodec.release();

    mExtractor = null;

    mCodec = null;

    mCodecInputBuffers = null;
    mCodecOutputBuffers = null;

    mSawInputEOS = false;
    mSawOutputEOS = false;

    mPresentationTimeUs = 0;

    mNoOutputCounter = 0;
    mNoOutputCounterLimit = 0;

    mMediaCodecInfo = null;
  }

  @Background
  public void playStart(MusicComposition composition) {
    AudioTrack audioTrack = getAudioTrack();

    audioTrack.play();

    MusicComposition.Record record = composition.getRegions().get(0).getTracks().get(0).getRecords().get(0);

    boolean stop = false;

    startRecordReading(record);

    while (!stop) {
      byte[] chunk = readRecordChunk();

      if(chunk == null)
        break;

      audioTrack.write(chunk,0,chunk.length);
    }

    stopRecordReading();

    audioTrack.flush();
    audioTrack.release();

    notifyListenerStop();

    stop = true;
  }

  public void playStart(String file) {
    try {
      releasePlayer();

      mMediaPlayer = new MediaPlayer();
      mMediaPlayer.setDataSource(file);
      mMediaPlayer.prepare();
      mMediaPlayer.start();

      mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override public void onCompletion(MediaPlayer mp) {
          notifyListenerStop();
        }
      });

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void playStop() {
    if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
      mMediaPlayer.stop();

      notifyListenerStop();
    }
  }

  private void notifyListenerStop() {
    if(mPlayerListener == null)
      return;

    PlayerListener listener = mPlayerListener.get();

    if(listener == null)
      return;

    listener.onPlayingStop();
  }

  public void releasePlayer() {
    if (mMediaPlayer != null) {
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
  }

  public void setPlayerListener(PlayerListener playerListener) {
    mPlayerListener = new WeakReference<>(playerListener);
  }
}
