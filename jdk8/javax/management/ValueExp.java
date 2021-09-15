package javax.management;

import java.io.Serializable;

public interface ValueExp extends Serializable {
   ValueExp apply(ObjectName var1) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException;

   /** @deprecated */
   @Deprecated
   void setMBeanServer(MBeanServer var1);
}
