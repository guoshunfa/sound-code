package javax.management;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class NotificationFilterSupport implements NotificationFilter {
   private static final long serialVersionUID = 6579080007561786969L;
   private List<String> enabledTypes = new Vector();

   public synchronized boolean isNotificationEnabled(Notification var1) {
      String var2 = var1.getType();
      if (var2 == null) {
         return false;
      } else {
         try {
            Iterator var3 = this.enabledTypes.iterator();

            String var4;
            do {
               if (!var3.hasNext()) {
                  return false;
               }

               var4 = (String)var3.next();
            } while(!var2.startsWith(var4));

            return true;
         } catch (NullPointerException var5) {
            return false;
         }
      }
   }

   public synchronized void enableType(String var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("The prefix cannot be null.");
      } else {
         if (!this.enabledTypes.contains(var1)) {
            this.enabledTypes.add(var1);
         }

      }
   }

   public synchronized void disableType(String var1) {
      this.enabledTypes.remove(var1);
   }

   public synchronized void disableAllTypes() {
      this.enabledTypes.clear();
   }

   public synchronized Vector<String> getEnabledTypes() {
      return (Vector)this.enabledTypes;
   }
}
