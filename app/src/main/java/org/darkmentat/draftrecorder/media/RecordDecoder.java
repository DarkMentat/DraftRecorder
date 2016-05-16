package org.darkmentat.draftrecorder.media;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import org.darkmentat.draftrecorder.domain.MusicComposition;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Thanks to: https://github.com/radhoo/android-openmxplayer
 */
public class RecordDecoder {
  private static final long TIMEOUT_US = 1000;

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

  private boolean mStarted = false;

  private long mPresentationTimeUs;
  private MusicComposition.Record mRecord;

  public RecordDecoder(MusicComposition.Record record) {
    mRecord = record;
  }

  public long getDuration(){
    MediaFormat format = mExtractor.getTrackFormat(0);
    return format.getLong(MediaFormat.KEY_DURATION);
  }
  public int getSampleRate(){
    MediaFormat format = mExtractor.getTrackFormat(0);
    return format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
  }
  public void startRecordReading(){
    try{
      mExtractor = new MediaExtractor();
      mExtractor.setDataSource(mRecord.getFile().getAbsolutePath());
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

      mStarted = true;
    }catch(IOException e){
      e.printStackTrace();
    }
  }
  public byte[] readRecordChunk(){

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
  public short[] readRecordChunkShorts(){
    byte[] bytes = readRecordChunk();

    if(bytes == null) return null;

    short[] shorts = new short[bytes.length/2];
    ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

    return shorts;
  }
  public void stopRecordReading(){
    if(!mStarted)
      return;

    mStarted = false;

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
}
