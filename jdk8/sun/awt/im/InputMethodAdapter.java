package sun.awt.im;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.im.spi.InputMethod;

public abstract class InputMethodAdapter implements InputMethod {
   private Component clientComponent;

   void setClientComponent(Component var1) {
      this.clientComponent = var1;
   }

   protected Component getClientComponent() {
      return this.clientComponent;
   }

   protected boolean haveActiveClient() {
      return this.clientComponent != null && this.clientComponent.getInputMethodRequests() != null;
   }

   protected void setAWTFocussedComponent(Component var1) {
   }

   protected boolean supportsBelowTheSpot() {
      return false;
   }

   protected void stopListening() {
   }

   public void notifyClientWindowChange(Rectangle var1) {
   }

   public void reconvert() {
      throw new UnsupportedOperationException();
   }

   public abstract void disableInputMethod();

   public abstract String getNativeInputMethodInfo();
}
