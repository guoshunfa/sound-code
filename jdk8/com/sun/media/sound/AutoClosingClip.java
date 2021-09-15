package com.sun.media.sound;

import javax.sound.sampled.Clip;

interface AutoClosingClip extends Clip {
   boolean isAutoClosing();

   void setAutoClosing(boolean var1);
}
