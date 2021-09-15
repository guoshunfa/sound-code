package com.apple.eawt;

public interface UserSessionListener extends AppEventListener {
   void userSessionDeactivated(AppEvent.UserSessionEvent var1);

   void userSessionActivated(AppEvent.UserSessionEvent var1);
}
