package sun.lwawt.macosx;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleBundle;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import sun.lwawt.LWWindowPeer;

class CAccessibility implements PropertyChangeListener {
   private static Set<String> ignoredRoles;
   static CAccessibility sAccessibility;
   private static final Field FIELD_KEY;
   static final int JAVA_AX_ALL_CHILDREN = -1;
   static final int JAVA_AX_SELECTED_CHILDREN = -2;
   static final int JAVA_AX_VISIBLE_CHILDREN = -3;

   static synchronized CAccessibility getAccessibility(String[] var0) {
      if (sAccessibility != null) {
         return sAccessibility;
      } else {
         sAccessibility = new CAccessibility();
         if (var0 != null) {
            ignoredRoles = new HashSet(var0.length);
            String[] var1 = var0;
            int var2 = var0.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               String var4 = var1[var3];
               ignoredRoles.add(var4);
            }
         } else {
            ignoredRoles = new HashSet();
         }

         return sAccessibility;
      }
   }

   private CAccessibility() {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", this);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      Object var2 = var1.getNewValue();
      if (var2 != null) {
         if (var2 instanceof Accessible) {
            AccessibleContext var3 = ((Accessible)var2).getAccessibleContext();
            AccessibleRole var4 = var3.getAccessibleRole();
            if (!ignoredRoles.contains(roleKey(var4))) {
               this.focusChanged();
            }
         }

      }
   }

   private native void focusChanged();

   static <T> T invokeAndWait(Callable<T> var0, Component var1) {
      try {
         return LWCToolkit.invokeAndWait(var0, var1);
      } catch (Exception var3) {
         var3.printStackTrace();
         return null;
      }
   }

   static <T> T invokeAndWait(Callable<T> var0, Component var1, T var2) {
      Object var3 = null;

      try {
         var3 = LWCToolkit.invokeAndWait(var0, var1);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return var3 != null ? var3 : var2;
   }

   static void invokeLater(Runnable var0, Component var1) {
      try {
         LWCToolkit.invokeLater(var0, var1);
      } catch (InvocationTargetException var3) {
         var3.printStackTrace();
      }

   }

   public static String getAccessibleActionDescription(final AccessibleAction var0, final int var1, Component var2) {
      return var0 == null ? null : (String)invokeAndWait(new Callable<String>() {
         public String call() throws Exception {
            return var0.getAccessibleActionDescription(var1);
         }
      }, var2);
   }

   public static void doAccessibleAction(final AccessibleAction var0, final int var1, Component var2) {
      if (var0 != null) {
         invokeLater(new Runnable() {
            public void run() {
               var0.doAccessibleAction(var1);
            }
         }, var2);
      }
   }

   public static Dimension getSize(final AccessibleComponent var0, Component var1) {
      return var0 == null ? null : (Dimension)invokeAndWait(new Callable<Dimension>() {
         public Dimension call() throws Exception {
            return var0.getSize();
         }
      }, var1);
   }

   public static AccessibleSelection getAccessibleSelection(final AccessibleContext var0, Component var1) {
      return var0 == null ? null : (AccessibleSelection)invokeAndWait(new Callable<AccessibleSelection>() {
         public AccessibleSelection call() throws Exception {
            return var0.getAccessibleSelection();
         }
      }, var1);
   }

   public static Accessible ax_getAccessibleSelection(final AccessibleContext var0, final int var1, Component var2) {
      return var0 == null ? null : (Accessible)invokeAndWait(new Callable<Accessible>() {
         public Accessible call() throws Exception {
            AccessibleSelection var1x = var0.getAccessibleSelection();
            return var1x == null ? null : var1x.getAccessibleSelection(var1);
         }
      }, var2);
   }

   public static void addAccessibleSelection(final AccessibleContext var0, final int var1, Component var2) {
      if (var0 != null) {
         invokeLater(new Runnable() {
            public void run() {
               AccessibleSelection var1x = var0.getAccessibleSelection();
               if (var1x != null) {
                  var1x.addAccessibleSelection(var1);
               }
            }
         }, var2);
      }
   }

   public static AccessibleContext getAccessibleContext(final Accessible var0, Component var1) {
      return var0 == null ? null : (AccessibleContext)invokeAndWait(new Callable<AccessibleContext>() {
         public AccessibleContext call() throws Exception {
            return var0.getAccessibleContext();
         }
      }, var1);
   }

   public static boolean isAccessibleChildSelected(final Accessible var0, final int var1, Component var2) {
      return var0 == null ? false : (Boolean)invokeAndWait(new Callable<Boolean>() {
         public Boolean call() throws Exception {
            AccessibleContext var1x = var0.getAccessibleContext();
            if (var1x == null) {
               return Boolean.FALSE;
            } else {
               AccessibleSelection var2 = var1x.getAccessibleSelection();
               return var2 == null ? Boolean.FALSE : new Boolean(var2.isAccessibleChildSelected(var1));
            }
         }
      }, var2, false);
   }

   public static AccessibleStateSet getAccessibleStateSet(final AccessibleContext var0, Component var1) {
      return var0 == null ? null : (AccessibleStateSet)invokeAndWait(new Callable<AccessibleStateSet>() {
         public AccessibleStateSet call() throws Exception {
            return var0.getAccessibleStateSet();
         }
      }, var1);
   }

   public static boolean contains(final AccessibleContext var0, final AccessibleState var1, Component var2) {
      return var0 != null && var1 != null ? (Boolean)invokeAndWait(new Callable<Boolean>() {
         public Boolean call() throws Exception {
            AccessibleStateSet var1x = var0.getAccessibleStateSet();
            return var1x == null ? null : var1x.contains(var1);
         }
      }, var2, false) : false;
   }

   static Field getAccessibleBundleKeyFieldWithReflection() {
      try {
         Field var0 = AccessibleBundle.class.getDeclaredField("key");
         var0.setAccessible(true);
         return var0;
      } catch (SecurityException var1) {
         var1.printStackTrace();
      } catch (NoSuchFieldException var2) {
         var2.printStackTrace();
      }

      return null;
   }

   static String getAccessibleRoleFor(Accessible var0) {
      AccessibleContext var1 = var0.getAccessibleContext();
      if (var1 == null) {
         return null;
      } else {
         AccessibleRole var2 = var1.getAccessibleRole();

         try {
            return (String)FIELD_KEY.get(var2);
         } catch (IllegalArgumentException var4) {
            var4.printStackTrace();
         } catch (IllegalAccessException var5) {
            var5.printStackTrace();
         }

         return null;
      }
   }

   public static String getAccessibleRole(final Accessible var0, Component var1) {
      return var0 == null ? null : (String)invokeAndWait(new Callable<String>() {
         public String call() throws Exception {
            Accessible var1 = CAccessible.getSwingAccessible(var0);
            String var2 = CAccessibility.getAccessibleRoleFor(var0);
            if (!"text".equals(var2)) {
               return var2;
            } else {
               return !(var1 instanceof JTextArea) && !(var1 instanceof JEditorPane) ? var2 : "textarea";
            }
         }
      }, var1);
   }

   public static Point getLocationOnScreen(final AccessibleComponent var0, Component var1) {
      return var0 == null ? null : (Point)invokeAndWait(new Callable<Point>() {
         public Point call() throws Exception {
            return var0.getLocationOnScreen();
         }
      }, var1);
   }

   public static int getCharCount(final AccessibleText var0, Component var1) {
      return var0 == null ? 0 : (Integer)invokeAndWait(new Callable<Integer>() {
         public Integer call() throws Exception {
            return var0.getCharCount();
         }
      }, var1, 0);
   }

   public static Accessible getAccessibleParent(final Accessible var0, Component var1) {
      return var0 == null ? null : (Accessible)invokeAndWait(new Callable<Accessible>() {
         public Accessible call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            return var1 == null ? null : var1.getAccessibleParent();
         }
      }, var1);
   }

   public static int getAccessibleIndexInParent(final Accessible var0, Component var1) {
      return var0 == null ? -1 : (Integer)invokeAndWait(new Callable<Integer>() {
         public Integer call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            return var1 == null ? null : var1.getAccessibleIndexInParent();
         }
      }, var1, -1);
   }

   public static AccessibleComponent getAccessibleComponent(final Accessible var0, Component var1) {
      return var0 == null ? null : (AccessibleComponent)invokeAndWait(new Callable<AccessibleComponent>() {
         public AccessibleComponent call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            return var1 == null ? null : var1.getAccessibleComponent();
         }
      }, var1);
   }

   public static AccessibleValue getAccessibleValue(final Accessible var0, Component var1) {
      return var0 == null ? null : (AccessibleValue)invokeAndWait(new Callable<AccessibleValue>() {
         public AccessibleValue call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return null;
            } else {
               AccessibleValue var2 = var1.getAccessibleValue();
               return var2;
            }
         }
      }, var1);
   }

   public static String getAccessibleName(final Accessible var0, Component var1) {
      return var0 == null ? null : (String)invokeAndWait(new Callable<String>() {
         public String call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return null;
            } else {
               String var2 = var1.getAccessibleName();
               return var2 == null ? var1.getAccessibleDescription() : var2;
            }
         }
      }, var1);
   }

   public static AccessibleText getAccessibleText(final Accessible var0, Component var1) {
      return var0 == null ? null : (AccessibleText)invokeAndWait(new Callable<AccessibleText>() {
         public AccessibleText call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return null;
            } else {
               AccessibleText var2 = var1.getAccessibleText();
               return var2;
            }
         }
      }, var1);
   }

   public static String getAccessibleDescription(final Accessible var0, final Component var1) {
      return var0 == null ? null : (String)invokeAndWait(new Callable<String>() {
         public String call() throws Exception {
            AccessibleContext var1x = var0.getAccessibleContext();
            if (var1x == null) {
               return null;
            } else {
               String var2 = var1x.getAccessibleDescription();
               if (var2 == null && var1 instanceof JComponent) {
                  String var3 = ((JComponent)var1).getToolTipText();
                  if (var3 != null) {
                     return var3;
                  }
               }

               return var2;
            }
         }
      }, var1);
   }

   public static boolean isFocusTraversable(final Accessible var0, Component var1) {
      return var0 == null ? false : (Boolean)invokeAndWait(new Callable<Boolean>() {
         public Boolean call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return null;
            } else {
               AccessibleComponent var2 = var1.getAccessibleComponent();
               return var2 == null ? null : var2.isFocusTraversable();
            }
         }
      }, var1, false);
   }

   public static Accessible accessibilityHitTest(final Container var0, final float var1, final float var2) {
      return (Accessible)invokeAndWait(new Callable<Accessible>() {
         public Accessible call() throws Exception {
            Point var1x = var0.getLocationOnScreen();
            Point var2x = new Point((int)((double)var1 - var1x.getX()), (int)((double)var2 - var1x.getY()));
            Component var3 = var0.findComponentAt(var2x);
            if (var3 == null) {
               return null;
            } else {
               AccessibleContext var4 = var3.getAccessibleContext();
               if (var4 == null) {
                  return null;
               } else {
                  AccessibleComponent var5 = var4.getAccessibleComponent();
                  if (var5 == null) {
                     return null;
                  } else {
                     int var6 = var4.getAccessibleChildrenCount();
                     if (var6 > 0) {
                        Point var7 = var5.getLocationOnScreen();
                        Point var8 = new Point((int)((double)var1 - var7.getX()), (int)((double)var2 - var7.getY()));
                        return CAccessible.getCAccessible(var5.getAccessibleAt(var8));
                     } else {
                        return !(var3 instanceof Accessible) ? null : CAccessible.getCAccessible((Accessible)var3);
                     }
                  }
               }
            }
         }
      }, var0);
   }

   public static AccessibleAction getAccessibleAction(final Accessible var0, Component var1) {
      return var0 == null ? null : (AccessibleAction)invokeAndWait(new Callable<AccessibleAction>() {
         public AccessibleAction call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            return var1 == null ? null : var1.getAccessibleAction();
         }
      }, var1);
   }

   public static boolean isEnabled(final Accessible var0, Component var1) {
      return var0 == null ? false : (Boolean)invokeAndWait(new Callable<Boolean>() {
         public Boolean call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return null;
            } else {
               AccessibleComponent var2 = var1.getAccessibleComponent();
               return var2 == null ? null : var2.isEnabled();
            }
         }
      }, var1, false);
   }

   public static void requestFocus(final Accessible var0, Component var1) {
      if (var0 != null) {
         invokeLater(new Runnable() {
            public void run() {
               AccessibleContext var1 = var0.getAccessibleContext();
               if (var1 != null) {
                  AccessibleComponent var2 = var1.getAccessibleComponent();
                  if (var2 != null) {
                     var2.requestFocus();
                  }
               }
            }
         }, var1);
      }
   }

   public static void requestSelection(final Accessible var0, Component var1) {
      if (var0 != null) {
         invokeLater(new Runnable() {
            public void run() {
               AccessibleContext var1 = var0.getAccessibleContext();
               if (var1 != null) {
                  int var2 = var1.getAccessibleIndexInParent();
                  if (var2 != -1) {
                     Accessible var3 = var1.getAccessibleParent();
                     AccessibleContext var4 = var3.getAccessibleContext();
                     if (var4 != null) {
                        AccessibleSelection var5 = var4.getAccessibleSelection();
                        if (var5 != null) {
                           var5.addAccessibleSelection(var2);
                        }
                     }
                  }
               }
            }
         }, var1);
      }
   }

   public static Number getMaximumAccessibleValue(final Accessible var0, Component var1) {
      return var0 == null ? null : (Number)invokeAndWait(new Callable<Number>() {
         public Number call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return null;
            } else {
               AccessibleValue var2 = var1.getAccessibleValue();
               return var2 == null ? null : var2.getMaximumAccessibleValue();
            }
         }
      }, var1);
   }

   public static Number getMinimumAccessibleValue(final Accessible var0, Component var1) {
      return var0 == null ? null : (Number)invokeAndWait(new Callable<Number>() {
         public Number call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return null;
            } else {
               AccessibleValue var2 = var1.getAccessibleValue();
               return var2 == null ? null : var2.getMinimumAccessibleValue();
            }
         }
      }, var1);
   }

   public static String getAccessibleRoleDisplayString(final Accessible var0, Component var1) {
      return var0 == null ? null : (String)invokeAndWait(new Callable<String>() {
         public String call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return null;
            } else {
               AccessibleRole var2 = var1.getAccessibleRole();
               return var2 == null ? null : var2.toDisplayString();
            }
         }
      }, var1);
   }

   public static Number getCurrentAccessibleValue(final AccessibleValue var0, Component var1) {
      return var0 == null ? null : (Number)invokeAndWait(new Callable<Number>() {
         public Number call() throws Exception {
            Number var1 = var0.getCurrentAccessibleValue();
            return var1;
         }
      }, var1);
   }

   public static Accessible getFocusOwner(Component var0) {
      return (Accessible)invokeAndWait(new Callable<Accessible>() {
         public Accessible call() throws Exception {
            Component var1 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            return var1 != null && var1 instanceof Accessible ? CAccessible.getCAccessible((Accessible)var1) : null;
         }
      }, var0);
   }

   public static boolean[] getInitialAttributeStates(final Accessible var0, Component var1) {
      final boolean[] var2 = new boolean[7];
      return var0 == null ? var2 : (boolean[])invokeAndWait(new Callable<boolean[]>() {
         public boolean[] call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return var2;
            } else {
               AccessibleComponent var2x = var1.getAccessibleComponent();
               var2[0] = var2x != null;
               var2[1] = var2x != null && var2x.isFocusTraversable();
               var2[2] = var1.getAccessibleValue() != null;
               var2[3] = var1.getAccessibleText() != null;
               AccessibleStateSet var3 = var1.getAccessibleStateSet();
               var2[4] = var3.contains(AccessibleState.HORIZONTAL) || var3.contains(AccessibleState.VERTICAL);
               var2[5] = var1.getAccessibleName() != null;
               var2[6] = var1.getAccessibleChildrenCount() > 0;
               return var2;
            }
         }
      }, var1);
   }

   public static Object[] getChildrenAndRoles(final Accessible var0, Component var1, final int var2, final boolean var3) {
      return var0 == null ? null : (Object[])invokeAndWait(new Callable<Object[]>() {
         public Object[] call() throws Exception {
            ArrayList var1 = new ArrayList();
            CAccessibility._addChildren(var0, var2, var3, var1);
            if (var2 == -2 && !var1.isEmpty()) {
               AccessibleContext var2x = CAccessible.getActiveDescendant(var0);
               if (var2x != null) {
                  String var3x = var2x.getAccessibleName();
                  AccessibleRole var4 = var2x.getAccessibleRole();
                  ArrayList var5 = new ArrayList();
                  int var6 = var1.size();
                  Accessible var7 = null;
                  AccessibleContext var8 = null;
                  String var9 = null;
                  AccessibleRole var10 = null;

                  for(int var11 = 0; var11 < var6; var11 += 2) {
                     var7 = (Accessible)var1.get(var11);
                     var8 = var7.getAccessibleContext();
                     var9 = var8.getAccessibleName();
                     var10 = (AccessibleRole)var1.get(var11 + 1);
                     if (var9.equals(var3x) && var10.equals(var4)) {
                        var5.add(0, var7);
                        var5.add(1, var10);
                     } else {
                        var5.add(var7);
                        var5.add(var10);
                     }
                  }

                  var1 = var5;
               }
            }

            return var2 >= 0 && var2 * 2 < var1.size() ? new Object[]{var1.get(var2 * 2), var1.get(var2 * 2 + 1)} : var1.toArray();
         }
      }, var1);
   }

   private static AccessibleRole getAccessibleRoleForLabel(JLabel var0, AccessibleRole var1) {
      String var2 = var0.getText();
      if (var2 != null && var2.length() > 0) {
         return var1;
      } else {
         Icon var3 = var0.getIcon();
         return var3 != null ? AccessibleRole.ICON : var1;
      }
   }

   private static AccessibleRole getAccessibleRole(Accessible var0) {
      AccessibleContext var1 = var0.getAccessibleContext();
      AccessibleRole var2 = var1.getAccessibleRole();
      Accessible var3 = CAccessible.getSwingAccessible(var0);
      if (var2 == null) {
         return null;
      } else {
         String var4 = var2.toString();
         return "label".equals(var4) && var3 instanceof JLabel ? getAccessibleRoleForLabel((JLabel)var3, var2) : var2;
      }
   }

   private static void _addChildren(Accessible var0, int var1, boolean var2, ArrayList<Object> var3) {
      if (var0 != null) {
         AccessibleContext var4 = var0.getAccessibleContext();
         if (var4 != null) {
            int var5 = var4.getAccessibleChildrenCount();

            for(int var6 = 0; var6 < var5; ++var6) {
               Accessible var7 = var4.getAccessibleChild(var6);
               if (var7 != null) {
                  AccessibleContext var8 = var7.getAccessibleContext();
                  if (var8 != null) {
                     if (var1 == -3) {
                        AccessibleComponent var9 = var8.getAccessibleComponent();
                        if (var9 == null || !var9.isVisible()) {
                           continue;
                        }
                     } else if (var1 == -2) {
                        AccessibleSelection var10 = var4.getAccessibleSelection();
                        if (var10 == null || !var10.isAccessibleChildSelected(var6)) {
                           continue;
                        }
                     }

                     if (!var2) {
                        AccessibleRole var11 = var8.getAccessibleRole();
                        if (var11 != null && ignoredRoles != null && ignoredRoles.contains(roleKey(var11))) {
                           _addChildren(var7, var1, false, var3);
                        } else {
                           var3.add(var7);
                           var3.add(getAccessibleRole(var7));
                        }
                     } else {
                        var3.add(var7);
                        var3.add(getAccessibleRole(var7));
                     }

                     if (var1 >= 0 && var3.size() / 2 >= var1 + 1) {
                        return;
                     }
                  }
               }
            }

         }
      }
   }

   private static native String roleKey(AccessibleRole var0);

   public static Object[] getChildren(final Accessible var0, Component var1) {
      return var0 == null ? null : (Object[])invokeAndWait(new Callable<Object[]>() {
         public Object[] call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return null;
            } else {
               int var2 = var1.getAccessibleChildrenCount();
               Object[] var3 = new Object[var2];

               for(int var4 = 0; var4 < var2; ++var4) {
                  var3[var4] = var1.getAccessibleChild(var4);
               }

               return var3;
            }
         }
      }, var1);
   }

   private static long getAWTView(Accessible var0) {
      final Accessible var1 = CAccessible.getSwingAccessible(var0);
      return !(var1 instanceof Component) ? 0L : (Long)invokeAndWait(new Callable<Long>() {
         public Long call() throws Exception {
            Object var1x;
            for(var1x = (Component)var1; var1x != null && !(var1x instanceof Window); var1x = ((Component)var1x).getParent()) {
            }

            if (var1x != null) {
               LWWindowPeer var2 = (LWWindowPeer)((Component)var1x).getPeer();
               if (var2 != null) {
                  return ((CPlatformWindow)var2.getPlatformWindow()).getContentView().getAWTView();
               }
            }

            return 0L;
         }
      }, (Component)var1);
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("awt");
            return null;
         }
      });
      FIELD_KEY = getAccessibleBundleKeyFieldWithReflection();
   }
}
