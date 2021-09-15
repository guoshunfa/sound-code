package javax.management;

public class AttributeChangeNotification extends Notification {
   private static final long serialVersionUID = 535176054565814134L;
   public static final String ATTRIBUTE_CHANGE = "jmx.attribute.change";
   private String attributeName = null;
   private String attributeType = null;
   private Object oldValue = null;
   private Object newValue = null;

   public AttributeChangeNotification(Object var1, long var2, long var4, String var6, String var7, String var8, Object var9, Object var10) {
      super("jmx.attribute.change", var1, var2, var4, var6);
      this.attributeName = var7;
      this.attributeType = var8;
      this.oldValue = var9;
      this.newValue = var10;
   }

   public String getAttributeName() {
      return this.attributeName;
   }

   public String getAttributeType() {
      return this.attributeType;
   }

   public Object getOldValue() {
      return this.oldValue;
   }

   public Object getNewValue() {
      return this.newValue;
   }
}
