package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

public class SortingFocusTraversalPolicy extends InternalFrameFocusTraversalPolicy {
   private Comparator<? super Component> comparator;
   private boolean implicitDownCycleTraversal = true;
   private PlatformLogger log = PlatformLogger.getLogger("javax.swing.SortingFocusTraversalPolicy");
   private transient Container cachedRoot;
   private transient List<Component> cachedCycle;
   private static final SwingContainerOrderFocusTraversalPolicy fitnessTestPolicy = new SwingContainerOrderFocusTraversalPolicy();
   private final int FORWARD_TRAVERSAL = 0;
   private final int BACKWARD_TRAVERSAL = 1;
   private static final boolean legacySortingFTPEnabled = "true".equals(AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.legacySortingFTPEnabled", "true"))));
   private static final Method legacyMergeSortMethod;

   protected SortingFocusTraversalPolicy() {
   }

   public SortingFocusTraversalPolicy(Comparator<? super Component> var1) {
      this.comparator = var1;
   }

   private List<Component> getFocusTraversalCycle(Container var1) {
      ArrayList var2 = new ArrayList();
      this.enumerateAndSortCycle(var1, var2);
      return var2;
   }

   private int getComponentIndex(List<Component> var1, Component var2) {
      int var3;
      try {
         var3 = Collections.binarySearch(var1, var2, this.comparator);
      } catch (ClassCastException var5) {
         if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
            this.log.fine("### During the binary search for " + var2 + " the exception occurred: ", (Throwable)var5);
         }

         return -1;
      }

      if (var3 < 0) {
         var3 = var1.indexOf(var2);
      }

      return var3;
   }

   private void enumerateAndSortCycle(Container var1, List<Component> var2) {
      if (var1.isShowing()) {
         this.enumerateCycle(var1, var2);
         if (!legacySortingFTPEnabled || !this.legacySort(var2, this.comparator)) {
            Collections.sort(var2, this.comparator);
         }
      }

   }

   private boolean legacySort(List<Component> var1, Comparator<? super Component> var2) {
      if (legacyMergeSortMethod == null) {
         return false;
      } else {
         Object[] var3 = var1.toArray();

         try {
            legacyMergeSortMethod.invoke((Object)null, var3, var2);
         } catch (InvocationTargetException | IllegalAccessException var9) {
            return false;
         }

         ListIterator var4 = var1.listIterator();
         Object[] var5 = var3;
         int var6 = var3.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Object var8 = var5[var7];
            var4.next();
            var4.set((Component)var8);
         }

         return true;
      }
   }

   private void enumerateCycle(Container var1, List<Component> var2) {
      if (var1.isVisible() && var1.isDisplayable()) {
         var2.add(var1);
         Component[] var3 = var1.getComponents();
         Component[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Component var7 = var4[var6];
            if (var7 instanceof Container) {
               Container var8 = (Container)var7;
               if (!var8.isFocusCycleRoot() && !var8.isFocusTraversalPolicyProvider() && (!(var8 instanceof JComponent) || !((JComponent)var8).isManagingFocus())) {
                  this.enumerateCycle(var8, var2);
                  continue;
               }
            }

            var2.add(var7);
         }

      }
   }

   Container getTopmostProvider(Container var1, Component var2) {
      Container var3 = var2.getParent();

      Container var4;
      for(var4 = null; var3 != var1 && var3 != null; var3 = var3.getParent()) {
         if (var3.isFocusTraversalPolicyProvider()) {
            var4 = var3;
         }
      }

      return var3 == null ? null : var4;
   }

   private Component getComponentDownCycle(Component var1, int var2) {
      Component var3 = null;
      if (var1 instanceof Container) {
         Container var4 = (Container)var1;
         if (var4.isFocusCycleRoot()) {
            if (!this.getImplicitDownCycleTraversal()) {
               return null;
            }

            var3 = var4.getFocusTraversalPolicy().getDefaultComponent(var4);
            if (var3 != null && this.log.isLoggable(PlatformLogger.Level.FINE)) {
               this.log.fine("### Transfered focus down-cycle to " + var3 + " in the focus cycle root " + var4);
            }
         } else if (var4.isFocusTraversalPolicyProvider()) {
            var3 = var2 == 0 ? var4.getFocusTraversalPolicy().getDefaultComponent(var4) : var4.getFocusTraversalPolicy().getLastComponent(var4);
            if (var3 != null && this.log.isLoggable(PlatformLogger.Level.FINE)) {
               this.log.fine("### Transfered focus to " + var3 + " in the FTP provider " + var4);
            }
         }
      }

      return var3;
   }

   public Component getComponentAfter(Container var1, Component var2) {
      if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
         this.log.fine("### Searching in " + var1 + " for component after " + var2);
      }

      if (var1 != null && var2 != null) {
         if (!var1.isFocusTraversalPolicyProvider() && !var1.isFocusCycleRoot()) {
            throw new IllegalArgumentException("aContainer should be focus cycle root or focus traversal policy provider");
         } else if (var1.isFocusCycleRoot() && !((Component)var2).isFocusCycleRoot(var1)) {
            throw new IllegalArgumentException("aContainer is not a focus cycle root of aComponent");
         } else {
            Component var3 = this.getComponentDownCycle((Component)var2, 0);
            if (var3 != null) {
               return var3;
            } else {
               Container var4 = this.getTopmostProvider(var1, (Component)var2);
               if (var4 != null) {
                  if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                     this.log.fine("### Asking FTP " + var4 + " for component after " + var2);
                  }

                  FocusTraversalPolicy var5 = var4.getFocusTraversalPolicy();
                  Component var6 = var5.getComponentAfter(var4, (Component)var2);
                  if (var6 != null) {
                     if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                        this.log.fine("### FTP returned " + var6);
                     }

                     return var6;
                  }

                  var2 = var4;
               }

               List var7 = this.getFocusTraversalCycle(var1);
               if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                  this.log.fine("### Cycle is " + var7 + ", component is " + var2);
               }

               int var8 = this.getComponentIndex(var7, (Component)var2);
               if (var8 < 0) {
                  if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                     this.log.fine("### Didn't find component " + var2 + " in a cycle " + var1);
                  }

                  return this.getFirstComponent(var1);
               } else {
                  ++var8;

                  while(var8 < var7.size()) {
                     var3 = (Component)var7.get(var8);
                     if (this.accept(var3)) {
                        return var3;
                     }

                     if ((var3 = this.getComponentDownCycle(var3, 0)) != null) {
                        return var3;
                     }

                     ++var8;
                  }

                  if (var1.isFocusCycleRoot()) {
                     this.cachedRoot = var1;
                     this.cachedCycle = var7;
                     var3 = this.getFirstComponent(var1);
                     this.cachedRoot = null;
                     this.cachedCycle = null;
                     return var3;
                  } else {
                     return null;
                  }
               }
            }
         }
      } else {
         throw new IllegalArgumentException("aContainer and aComponent cannot be null");
      }
   }

   public Component getComponentBefore(Container var1, Component var2) {
      if (var1 != null && var2 != null) {
         if (!var1.isFocusTraversalPolicyProvider() && !var1.isFocusCycleRoot()) {
            throw new IllegalArgumentException("aContainer should be focus cycle root or focus traversal policy provider");
         } else if (var1.isFocusCycleRoot() && !((Component)var2).isFocusCycleRoot(var1)) {
            throw new IllegalArgumentException("aContainer is not a focus cycle root of aComponent");
         } else {
            Container var3 = this.getTopmostProvider(var1, (Component)var2);
            if (var3 != null) {
               if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                  this.log.fine("### Asking FTP " + var3 + " for component after " + var2);
               }

               FocusTraversalPolicy var4 = var3.getFocusTraversalPolicy();
               Component var5 = var4.getComponentBefore(var3, (Component)var2);
               if (var5 != null) {
                  if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                     this.log.fine("### FTP returned " + var5);
                  }

                  return var5;
               }

               var2 = var3;
               if (this.accept(var3)) {
                  return var3;
               }
            }

            List var8 = this.getFocusTraversalCycle(var1);
            if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
               this.log.fine("### Cycle is " + var8 + ", component is " + var2);
            }

            int var9 = this.getComponentIndex(var8, (Component)var2);
            if (var9 < 0) {
               if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
                  this.log.fine("### Didn't find component " + var2 + " in a cycle " + var1);
               }

               return this.getLastComponent(var1);
            } else {
               --var9;

               Component var6;
               while(var9 >= 0) {
                  var6 = (Component)var8.get(var9);
                  Component var7;
                  if (var6 != var1 && (var7 = this.getComponentDownCycle(var6, 1)) != null) {
                     return var7;
                  }

                  if (this.accept(var6)) {
                     return var6;
                  }

                  --var9;
               }

               if (var1.isFocusCycleRoot()) {
                  this.cachedRoot = var1;
                  this.cachedCycle = var8;
                  var6 = this.getLastComponent(var1);
                  this.cachedRoot = null;
                  this.cachedCycle = null;
                  return var6;
               } else {
                  return null;
               }
            }
         }
      } else {
         throw new IllegalArgumentException("aContainer and aComponent cannot be null");
      }
   }

   public Component getFirstComponent(Container var1) {
      if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
         this.log.fine("### Getting first component in " + var1);
      }

      if (var1 == null) {
         throw new IllegalArgumentException("aContainer cannot be null");
      } else {
         List var2;
         if (this.cachedRoot == var1) {
            var2 = this.cachedCycle;
         } else {
            var2 = this.getFocusTraversalCycle(var1);
         }

         if (var2.size() == 0) {
            if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
               this.log.fine("### Cycle is empty");
            }

            return null;
         } else {
            if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
               this.log.fine("### Cycle is " + var2);
            }

            Iterator var3 = var2.iterator();

            Component var4;
            do {
               if (!var3.hasNext()) {
                  return null;
               }

               var4 = (Component)var3.next();
               if (this.accept(var4)) {
                  return var4;
               }
            } while(var4 == var1 || (var4 = this.getComponentDownCycle(var4, 0)) == null);

            return var4;
         }
      }
   }

   public Component getLastComponent(Container var1) {
      if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
         this.log.fine("### Getting last component in " + var1);
      }

      if (var1 == null) {
         throw new IllegalArgumentException("aContainer cannot be null");
      } else {
         List var2;
         if (this.cachedRoot == var1) {
            var2 = this.cachedCycle;
         } else {
            var2 = this.getFocusTraversalCycle(var1);
         }

         if (var2.size() == 0) {
            if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
               this.log.fine("### Cycle is empty");
            }

            return null;
         } else {
            if (this.log.isLoggable(PlatformLogger.Level.FINE)) {
               this.log.fine("### Cycle is " + var2);
            }

            for(int var3 = var2.size() - 1; var3 >= 0; --var3) {
               Component var4 = (Component)var2.get(var3);
               if (this.accept(var4)) {
                  return var4;
               }

               if (var4 instanceof Container && var4 != var1) {
                  Container var5 = (Container)var4;
                  if (var5.isFocusTraversalPolicyProvider()) {
                     Component var6 = var5.getFocusTraversalPolicy().getLastComponent(var5);
                     if (var6 != null) {
                        return var6;
                     }
                  }
               }
            }

            return null;
         }
      }
   }

   public Component getDefaultComponent(Container var1) {
      return this.getFirstComponent(var1);
   }

   public void setImplicitDownCycleTraversal(boolean var1) {
      this.implicitDownCycleTraversal = var1;
   }

   public boolean getImplicitDownCycleTraversal() {
      return this.implicitDownCycleTraversal;
   }

   protected void setComparator(Comparator<? super Component> var1) {
      this.comparator = var1;
   }

   protected Comparator<? super Component> getComparator() {
      return this.comparator;
   }

   protected boolean accept(Component var1) {
      return fitnessTestPolicy.accept(var1);
   }

   static {
      legacyMergeSortMethod = legacySortingFTPEnabled ? (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
         public Method run() {
            try {
               Class var1 = Class.forName("java.util.Arrays");
               Method var2 = var1.getDeclaredMethod("legacyMergeSort", Object[].class, Comparator.class);
               var2.setAccessible(true);
               return var2;
            } catch (NoSuchMethodException | ClassNotFoundException var3) {
               return null;
            }
         }
      }) : null;
   }
}
