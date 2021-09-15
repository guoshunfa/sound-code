package javax.swing;

import java.awt.DefaultFocusTraversalPolicy;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.KeyboardFocusManager;

public abstract class FocusManager extends DefaultKeyboardFocusManager {
   public static final String FOCUS_MANAGER_CLASS_PROPERTY = "FocusManagerClassName";
   private static boolean enabled = true;

   public static FocusManager getCurrentManager() {
      KeyboardFocusManager var0 = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      return (FocusManager)(var0 instanceof FocusManager ? (FocusManager)var0 : new DelegatingDefaultFocusManager(var0));
   }

   public static void setCurrentManager(FocusManager var0) throws SecurityException {
      Object var1 = var0 instanceof DelegatingDefaultFocusManager ? ((DelegatingDefaultFocusManager)var0).getDelegate() : var0;
      KeyboardFocusManager.setCurrentKeyboardFocusManager((KeyboardFocusManager)var1);
   }

   /** @deprecated */
   @Deprecated
   public static void disableSwingFocusManager() {
      if (enabled) {
         enabled = false;
         KeyboardFocusManager.getCurrentKeyboardFocusManager().setDefaultFocusTraversalPolicy(new DefaultFocusTraversalPolicy());
      }

   }

   /** @deprecated */
   @Deprecated
   public static boolean isFocusManagerEnabled() {
      return enabled;
   }
}
