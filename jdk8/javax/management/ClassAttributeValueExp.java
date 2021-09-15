package javax.management;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.security.AccessController;
import java.security.PrivilegedAction;

class ClassAttributeValueExp extends AttributeValueExp {
   private static final long oldSerialVersionUID = -2212731951078526753L;
   private static final long newSerialVersionUID = -1081892073854801359L;
   private static final long serialVersionUID;
   private String attr = "Class";

   public ClassAttributeValueExp() {
      super("Class");
   }

   public ValueExp apply(ObjectName var1) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
      Object var2 = this.getValue(var1);
      if (var2 instanceof String) {
         return new StringValueExp((String)var2);
      } else {
         throw new BadAttributeValueExpException(var2);
      }
   }

   public String toString() {
      return this.attr;
   }

   protected Object getValue(ObjectName var1) {
      try {
         MBeanServer var2 = QueryEval.getMBeanServer();
         return var2.getObjectInstance(var1).getClassName();
      } catch (Exception var3) {
         return null;
      }
   }

   static {
      boolean var0 = false;

      try {
         GetPropertyAction var1 = new GetPropertyAction("jmx.serial.form");
         String var2 = (String)AccessController.doPrivileged((PrivilegedAction)var1);
         var0 = var2 != null && var2.equals("1.0");
      } catch (Exception var3) {
      }

      if (var0) {
         serialVersionUID = -2212731951078526753L;
      } else {
         serialVersionUID = -1081892073854801359L;
      }

   }
}
