package com.apple.eawt;

public interface AppHiddenListener extends AppEventListener {
   void appHidden(AppEvent.AppHiddenEvent var1);

   void appUnhidden(AppEvent.AppHiddenEvent var1);
}
