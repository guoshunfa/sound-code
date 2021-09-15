package java.awt.im;

import java.awt.AWTEvent;
import java.awt.Component;
import java.beans.Transient;
import java.util.Locale;
import sun.awt.im.InputMethodContext;

public class InputContext {
   protected InputContext() {
   }

   public static InputContext getInstance() {
      return new InputMethodContext();
   }

   public boolean selectInputMethod(Locale var1) {
      return false;
   }

   public Locale getLocale() {
      return null;
   }

   public void setCharacterSubsets(Character.Subset[] var1) {
   }

   public void setCompositionEnabled(boolean var1) {
   }

   @Transient
   public boolean isCompositionEnabled() {
      return false;
   }

   public void reconvert() {
   }

   public void dispatchEvent(AWTEvent var1) {
   }

   public void removeNotify(Component var1) {
   }

   public void endComposition() {
   }

   public void dispose() {
   }

   public Object getInputMethodControlObject() {
      return null;
   }
}
