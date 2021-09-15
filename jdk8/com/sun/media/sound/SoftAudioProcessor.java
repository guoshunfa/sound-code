package com.sun.media.sound;

public interface SoftAudioProcessor {
   void globalParameterControlChange(int[] var1, long var2, long var4);

   void init(float var1, float var2);

   void setInput(int var1, SoftAudioBuffer var2);

   void setOutput(int var1, SoftAudioBuffer var2);

   void setMixMode(boolean var1);

   void processAudio();

   void processControlLogic();
}
