package sun.nio.ch.sctp;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.Notification;

interface SctpNotification extends Notification {
   int assocId();

   void setAssociation(Association var1);
}
