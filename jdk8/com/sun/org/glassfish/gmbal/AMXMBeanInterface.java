package com.sun.org.glassfish.gmbal;

import java.util.Map;

@ManagedObject
@Description("Base interface for any MBean that works in the AMX framework")
public interface AMXMBeanInterface {
   Map<String, ?> getMeta();

   @ManagedAttribute(
      id = "Name"
   )
   @Description("Return the name of this MBean.")
   String getName();

   @ManagedAttribute(
      id = "Parent"
   )
   @Description("The container that contains this MBean")
   AMXMBeanInterface getParent();

   @ManagedAttribute(
      id = "Children"
   )
   @Description("All children of this AMX MBean")
   AMXMBeanInterface[] getChildren();
}
