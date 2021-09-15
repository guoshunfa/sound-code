package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class AbstractNotificationHandler<T> implements NotificationHandler<T> {
   protected AbstractNotificationHandler() {
   }

   public HandlerResult handleNotification(Notification var1, T var2) {
      return HandlerResult.CONTINUE;
   }

   public HandlerResult handleNotification(AssociationChangeNotification var1, T var2) {
      return HandlerResult.CONTINUE;
   }

   public HandlerResult handleNotification(PeerAddressChangeNotification var1, T var2) {
      return HandlerResult.CONTINUE;
   }

   public HandlerResult handleNotification(SendFailedNotification var1, T var2) {
      return HandlerResult.CONTINUE;
   }

   public HandlerResult handleNotification(ShutdownNotification var1, T var2) {
      return HandlerResult.CONTINUE;
   }
}
