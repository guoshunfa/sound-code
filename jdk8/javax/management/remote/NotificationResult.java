package javax.management.remote;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class NotificationResult implements Serializable {
   private static final long serialVersionUID = 1191800228721395279L;
   private long earliestSequenceNumber;
   private long nextSequenceNumber;
   private TargetedNotification[] targetedNotifications;

   public NotificationResult(long var1, long var3, TargetedNotification[] var5) {
      validate(var5, var1, var3);
      this.earliestSequenceNumber = var1;
      this.nextSequenceNumber = var3;
      this.targetedNotifications = var5.length == 0 ? var5 : (TargetedNotification[])var5.clone();
   }

   public long getEarliestSequenceNumber() {
      return this.earliestSequenceNumber;
   }

   public long getNextSequenceNumber() {
      return this.nextSequenceNumber;
   }

   public TargetedNotification[] getTargetedNotifications() {
      return this.targetedNotifications.length == 0 ? this.targetedNotifications : (TargetedNotification[])this.targetedNotifications.clone();
   }

   public String toString() {
      return "NotificationResult: earliest=" + this.getEarliestSequenceNumber() + "; next=" + this.getNextSequenceNumber() + "; nnotifs=" + this.getTargetedNotifications().length;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();

      try {
         validate(this.targetedNotifications, this.earliestSequenceNumber, this.nextSequenceNumber);
         this.targetedNotifications = this.targetedNotifications.length == 0 ? this.targetedNotifications : (TargetedNotification[])this.targetedNotifications.clone();
      } catch (IllegalArgumentException var3) {
         throw new InvalidObjectException(var3.getMessage());
      }
   }

   private static void validate(TargetedNotification[] var0, long var1, long var3) throws IllegalArgumentException {
      if (var0 == null) {
         throw new IllegalArgumentException("Notifications null");
      } else if (var1 < 0L || var3 < 0L) {
         throw new IllegalArgumentException("Bad sequence numbers");
      }
   }
}
