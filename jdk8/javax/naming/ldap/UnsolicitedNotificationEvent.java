package javax.naming.ldap;

import java.util.EventObject;

public class UnsolicitedNotificationEvent extends EventObject {
   private UnsolicitedNotification notice;
   private static final long serialVersionUID = -2382603380799883705L;

   public UnsolicitedNotificationEvent(Object var1, UnsolicitedNotification var2) {
      super(var1);
      this.notice = var2;
   }

   public UnsolicitedNotification getNotification() {
      return this.notice;
   }

   public void dispatch(UnsolicitedNotificationListener var1) {
      var1.notificationReceived(this);
   }
}
