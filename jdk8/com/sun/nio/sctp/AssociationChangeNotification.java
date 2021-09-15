package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public abstract class AssociationChangeNotification implements Notification {
   protected AssociationChangeNotification() {
   }

   public abstract Association association();

   public abstract AssociationChangeNotification.AssocChangeEvent event();

   @Exported
   public static enum AssocChangeEvent {
      COMM_UP,
      COMM_LOST,
      RESTART,
      SHUTDOWN,
      CANT_START;
   }
}
