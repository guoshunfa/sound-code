package java.beans;

public class PropertyVetoException extends Exception {
   private static final long serialVersionUID = 129596057694162164L;
   private PropertyChangeEvent evt;

   public PropertyVetoException(String var1, PropertyChangeEvent var2) {
      super(var1);
      this.evt = var2;
   }

   public PropertyChangeEvent getPropertyChangeEvent() {
      return this.evt;
   }
}
