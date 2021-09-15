package com.sun.media.sound;

public interface SoftProcess extends SoftControl {
   void init(SoftSynthesizer var1);

   double[] get(int var1, String var2);

   void processControlLogic();

   void reset();
}
