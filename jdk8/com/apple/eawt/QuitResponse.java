package com.apple.eawt;

public class QuitResponse {
   final _AppEventHandler appEventHandler;

   QuitResponse(_AppEventHandler var1) {
      this.appEventHandler = var1;
   }

   public void performQuit() {
      if (this.appEventHandler.currentQuitResponse == this) {
         this.appEventHandler.performQuit();
      }
   }

   public void cancelQuit() {
      if (this.appEventHandler.currentQuitResponse == this) {
         this.appEventHandler.cancelQuit();
      }
   }
}
