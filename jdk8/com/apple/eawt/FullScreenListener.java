package com.apple.eawt;

import java.util.EventListener;

public interface FullScreenListener extends EventListener {
   void windowEnteringFullScreen(AppEvent.FullScreenEvent var1);

   void windowEnteredFullScreen(AppEvent.FullScreenEvent var1);

   void windowExitingFullScreen(AppEvent.FullScreenEvent var1);

   void windowExitedFullScreen(AppEvent.FullScreenEvent var1);
}
