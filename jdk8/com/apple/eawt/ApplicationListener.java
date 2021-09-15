package com.apple.eawt;

import java.util.EventListener;

/** @deprecated */
@Deprecated
public interface ApplicationListener extends EventListener {
   /** @deprecated */
   @Deprecated
   void handleAbout(ApplicationEvent var1);

   /** @deprecated */
   @Deprecated
   void handleOpenApplication(ApplicationEvent var1);

   /** @deprecated */
   @Deprecated
   void handleOpenFile(ApplicationEvent var1);

   /** @deprecated */
   @Deprecated
   void handlePreferences(ApplicationEvent var1);

   /** @deprecated */
   @Deprecated
   void handlePrintFile(ApplicationEvent var1);

   /** @deprecated */
   @Deprecated
   void handleQuit(ApplicationEvent var1);

   /** @deprecated */
   @Deprecated
   void handleReOpenApplication(ApplicationEvent var1);
}
