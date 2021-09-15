package sun.lwawt.macosx;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class CAccessible extends CFRetainedResource implements Accessible {
   private static Field nativeAXResourceField = getNativeAXResourceField();
   private Accessible accessible;
   private AccessibleContext activeDescendant;

   static Field getNativeAXResourceField() {
      try {
         Field var0 = AccessibleContext.class.getDeclaredField("nativeAXResource");
         var0.setAccessible(true);
         return var0;
      } catch (Exception var1) {
         var1.printStackTrace();
         return null;
      }
   }

   public static CAccessible getCAccessible(Accessible var0) {
      if (var0 == null) {
         return null;
      } else {
         AccessibleContext var1 = var0.getAccessibleContext();

         try {
            CAccessible var2 = (CAccessible)nativeAXResourceField.get(var1);
            if (var2 != null) {
               return var2;
            } else {
               CAccessible var3 = new CAccessible(var0);
               nativeAXResourceField.set(var1, var3);
               return var3;
            }
         } catch (Exception var4) {
            var4.printStackTrace();
            return null;
         }
      }
   }

   private static native void unregisterFromCocoaAXSystem(long var0);

   private static native void valueChanged(long var0);

   private static native void selectedTextChanged(long var0);

   private static native void selectionChanged(long var0);

   private static native void menuOpened(long var0);

   private static native void menuClosed(long var0);

   private static native void menuItemSelected(long var0);

   private CAccessible(Accessible var1) {
      super(0L, true);
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.accessible = var1;
         if (var1 instanceof Component) {
            this.addNotificationListeners((Component)var1);
         }

      }
   }

   protected synchronized void dispose() {
      if (this.ptr != 0L) {
         unregisterFromCocoaAXSystem(this.ptr);
      }

      super.dispose();
   }

   public AccessibleContext getAccessibleContext() {
      return this.accessible.getAccessibleContext();
   }

   public void addNotificationListeners(Component var1) {
      if (var1 instanceof Accessible) {
         AccessibleContext var2 = ((Accessible)var1).getAccessibleContext();
         var2.addPropertyChangeListener(new CAccessible.AXChangeNotifier());
      }

      if (var1 instanceof JProgressBar) {
         JProgressBar var3 = (JProgressBar)var1;
         var3.addChangeListener(new CAccessible.AXProgressChangeNotifier());
      } else if (var1 instanceof JSlider) {
         JSlider var4 = (JSlider)var1;
         var4.addChangeListener(new CAccessible.AXProgressChangeNotifier());
      }

   }

   static Accessible getSwingAccessible(Accessible var0) {
      return var0 instanceof CAccessible ? ((CAccessible)var0).accessible : var0;
   }

   static AccessibleContext getActiveDescendant(Accessible var0) {
      return var0 instanceof CAccessible ? ((CAccessible)var0).activeDescendant : null;
   }

   private class AXProgressChangeNotifier implements ChangeListener {
      private AXProgressChangeNotifier() {
      }

      public void stateChanged(ChangeEvent var1) {
         if (CAccessible.this.ptr != 0L) {
            CAccessible.valueChanged(CAccessible.this.ptr);
         }

      }

      // $FF: synthetic method
      AXProgressChangeNotifier(Object var2) {
         this();
      }
   }

   private class AXChangeNotifier implements PropertyChangeListener {
      private AXChangeNotifier() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (CAccessible.this.ptr != 0L) {
            Object var3 = var1.getNewValue();
            Object var4 = var1.getOldValue();
            if (var2.compareTo("AccessibleCaret") == 0) {
               CAccessible.selectedTextChanged(CAccessible.this.ptr);
            } else if (var2.compareTo("AccessibleText") == 0) {
               CAccessible.valueChanged(CAccessible.this.ptr);
            } else if (var2.compareTo("AccessibleSelection") == 0) {
               CAccessible.selectionChanged(CAccessible.this.ptr);
            } else if (var2.compareTo("AccessibleActiveDescendant") == 0) {
               if (var3 instanceof AccessibleContext) {
                  CAccessible.this.activeDescendant = (AccessibleContext)var3;
               }
            } else if (var2.compareTo("AccessibleState") == 0) {
               AccessibleContext var5 = CAccessible.this.accessible.getAccessibleContext();
               AccessibleRole var6 = var5.getAccessibleRole();
               Accessible var7 = var5.getAccessibleParent();
               AccessibleRole var8 = null;
               if (var7 != null) {
                  var8 = var7.getAccessibleContext().getAccessibleRole();
               }

               if (var8 != AccessibleRole.COMBO_BOX) {
                  if (var6 == AccessibleRole.POPUP_MENU) {
                     if (var3 != null && (AccessibleState)var3 == AccessibleState.VISIBLE) {
                        CAccessible.menuOpened(CAccessible.this.ptr);
                     } else if (var4 != null && (AccessibleState)var4 == AccessibleState.VISIBLE) {
                        CAccessible.menuClosed(CAccessible.this.ptr);
                     }
                  } else if (var6 == AccessibleRole.MENU_ITEM && var3 != null && (AccessibleState)var3 == AccessibleState.FOCUSED) {
                     CAccessible.menuItemSelected(CAccessible.this.ptr);
                  }
               }
            }
         }

      }

      // $FF: synthetic method
      AXChangeNotifier(Object var2) {
         this();
      }
   }
}
