package com.apple.eawt;

public interface AppForegroundListener extends AppEventListener {
   void appRaisedToForeground(AppEvent.AppForegroundEvent var1);

   void appMovedToBackground(AppEvent.AppForegroundEvent var1);
}
