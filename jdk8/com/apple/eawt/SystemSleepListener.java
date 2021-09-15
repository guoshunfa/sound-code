package com.apple.eawt;

public interface SystemSleepListener extends AppEventListener {
   void systemAboutToSleep(AppEvent.SystemSleepEvent var1);

   void systemAwoke(AppEvent.SystemSleepEvent var1);
}
