package com.apple.eawt;

import java.util.EventObject;

/** @deprecated */
@Deprecated
public class ApplicationEvent extends EventObject {
   private String fFilename = null;
   private boolean fHandled = false;

   ApplicationEvent(Object var1) {
      super(var1);
   }

   ApplicationEvent(Object var1, String var2) {
      super(var1);
      this.fFilename = var2;
   }

   /** @deprecated */
   @Deprecated
   public boolean isHandled() {
      return this.fHandled;
   }

   /** @deprecated */
   @Deprecated
   public void setHandled(boolean var1) {
      this.fHandled = var1;
   }

   /** @deprecated */
   @Deprecated
   public String getFilename() {
      return this.fFilename;
   }
}
