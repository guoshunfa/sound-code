package com.apple.eawt;

public interface ScreenSleepListener extends AppEventListener {
   void screenAboutToSleep(AppEvent.ScreenSleepEvent var1);

   void screenAwoke(AppEvent.ScreenSleepEvent var1);
}
