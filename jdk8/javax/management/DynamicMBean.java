package javax.management;

public interface DynamicMBean {
   Object getAttribute(String var1) throws AttributeNotFoundException, MBeanException, ReflectionException;

   void setAttribute(Attribute var1) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException;

   AttributeList getAttributes(String[] var1);

   AttributeList setAttributes(AttributeList var1);

   Object invoke(String var1, Object[] var2, String[] var3) throws MBeanException, ReflectionException;

   MBeanInfo getMBeanInfo();
}
