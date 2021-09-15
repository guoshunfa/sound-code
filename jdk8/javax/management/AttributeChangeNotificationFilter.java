package javax.management;

import java.util.Vector;

public class AttributeChangeNotificationFilter implements NotificationFilter {
   private static final long serialVersionUID = -6347317584796410029L;
   private Vector<String> enabledAttributes = new Vector();

   public synchronized boolean isNotificationEnabled(Notification var1) {
      String var2 = var1.getType();
      if (var2 != null && var2.equals("jmx.attribute.change") && var1 instanceof AttributeChangeNotification) {
         String var3 = ((AttributeChangeNotification)var1).getAttributeName();
         return this.enabledAttributes.contains(var3);
      } else {
         return false;
      }
   }

   public synchronized void enableAttribute(String var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("The name cannot be null.");
      } else {
         if (!this.enabledAttributes.contains(var1)) {
            this.enabledAttributes.addElement(var1);
         }

      }
   }

   public synchronized void disableAttribute(String var1) {
      this.enabledAttributes.removeElement(var1);
   }

   public synchronized void disableAllAttributes() {
      this.enabledAttributes.removeAllElements();
   }

   public synchronized Vector<String> getEnabledAttributes() {
      return this.enabledAttributes;
   }
}
