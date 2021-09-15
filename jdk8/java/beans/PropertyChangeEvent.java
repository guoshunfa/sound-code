package java.beans;

import java.util.EventObject;

public class PropertyChangeEvent extends EventObject {
   private static final long serialVersionUID = 7042693688939648123L;
   private String propertyName;
   private Object newValue;
   private Object oldValue;
   private Object propagationId;

   public PropertyChangeEvent(Object var1, String var2, Object var3, Object var4) {
      super(var1);
      this.propertyName = var2;
      this.newValue = var4;
      this.oldValue = var3;
   }

   public String getPropertyName() {
      return this.propertyName;
   }

   public Object getNewValue() {
      return this.newValue;
   }

   public Object getOldValue() {
      return this.oldValue;
   }

   public void setPropagationId(Object var1) {
      this.propagationId = var1;
   }

   public Object getPropagationId() {
      return this.propagationId;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(this.getClass().getName());
      var1.append("[propertyName=").append(this.getPropertyName());
      this.appendTo(var1);
      var1.append("; oldValue=").append(this.getOldValue());
      var1.append("; newValue=").append(this.getNewValue());
      var1.append("; propagationId=").append(this.getPropagationId());
      var1.append("; source=").append(this.getSource());
      return var1.append("]").toString();
   }

   void appendTo(StringBuilder var1) {
   }
}
