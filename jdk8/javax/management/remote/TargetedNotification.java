package javax.management.remote;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.management.Notification;

public class TargetedNotification implements Serializable {
   private static final long serialVersionUID = 7676132089779300926L;
   private Notification notif;
   private Integer id;

   public TargetedNotification(Notification var1, Integer var2) {
      validate(var1, var2);
      this.notif = var1;
      this.id = var2;
   }

   public Notification getNotification() {
      return this.notif;
   }

   public Integer getListenerID() {
      return this.id;
   }

   public String toString() {
      return "{" + this.notif + ", " + this.id + "}";
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();

      try {
         validate(this.notif, this.id);
      } catch (IllegalArgumentException var3) {
         throw new InvalidObjectException(var3.getMessage());
      }
   }

   private static void validate(Notification var0, Integer var1) throws IllegalArgumentException {
      if (var0 == null) {
         throw new IllegalArgumentException("Invalid notification: null");
      } else if (var1 == null) {
         throw new IllegalArgumentException("Invalid listener ID: null");
      }
   }
}
