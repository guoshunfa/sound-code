package java.awt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import sun.util.logging.PlatformLogger;

public class ContainerOrderFocusTraversalPolicy extends FocusTraversalPolicy implements Serializable {
   private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.ContainerOrderFocusTraversalPolicy");
   private final int FORWARD_TRAVERSAL = 0;
   private final int BACKWARD_TRAVERSAL = 1;
   private static final long serialVersionUID = 486933713763926351L;
   private boolean implicitDownCycleTraversal = true;
   private transient Container cachedRoot;
   private transient java.util.List<Component> cachedCycle;

   private java.util.List<Component> getFocusTraversalCycle(Container var1) {
      ArrayList var2 = new ArrayList();
      this.enumerateCycle(var1, var2);
      return var2;
   }

   private int getComponentIndex(java.util.List<Component> var1, Component var2) {
      return var1.indexOf(var2);
   }

   private void enumerateCycle(Container var1, java.util.List<Component> var2) {
      if (var1.isVisible() && var1.isDisplayable()) {
         var2.add(var1);
         Component[] var3 = var1.getComponents();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            Component var5 = var3[var4];
            if (var5 instanceof Container) {
               Container var6 = (Container)var5;
               if (!var6.isFocusCycleRoot() && !var6.isFocusTraversalPolicyProvider()) {
                  this.enumerateCycle(var6, var2);
                  continue;
               }
            }

            var2.add(var5);
         }

      }
   }

   private Container getTopmostProvider(Container var1, Component var2) {
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
            if (var3 != null && log.isLoggable(PlatformLogger.Level.FINE)) {
               log.fine("### Transfered focus down-cycle to " + var3 + " in the focus cycle root " + var4);
            }
         } else if (var4.isFocusTraversalPolicyProvider()) {
            var3 = var2 == 0 ? var4.getFocusTraversalPolicy().getDefaultComponent(var4) : var4.getFocusTraversalPolicy().getLastComponent(var4);
            if (var3 != null && log.isLoggable(PlatformLogger.Level.FINE)) {
               log.fine("### Transfered focus to " + var3 + " in the FTP provider " + var4);
            }
         }
      }

      return var3;
   }

   public Component getComponentAfter(Container var1, Component var2) {
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         log.fine("### Searching in " + var1 + " for component after " + var2);
      }

      if (var1 != null && var2 != null) {
         if (!var1.isFocusTraversalPolicyProvider() && !var1.isFocusCycleRoot()) {
            throw new IllegalArgumentException("aContainer should be focus cycle root or focus traversal policy provider");
         } else if (var1.isFocusCycleRoot() && !((Component)var2).isFocusCycleRoot(var1)) {
            throw new IllegalArgumentException("aContainer is not a focus cycle root of aComponent");
         } else {
            synchronized(var1.getTreeLock()) {
               if (var1.isVisible() && var1.isDisplayable()) {
                  Component var4 = this.getComponentDownCycle((Component)var2, 0);
                  if (var4 != null) {
                     return var4;
                  } else {
                     Container var5 = this.getTopmostProvider(var1, (Component)var2);
                     if (var5 != null) {
                        if (log.isLoggable(PlatformLogger.Level.FINE)) {
                           log.fine("### Asking FTP " + var5 + " for component after " + var2);
                        }

                        FocusTraversalPolicy var6 = var5.getFocusTraversalPolicy();
                        Component var7 = var6.getComponentAfter(var5, (Component)var2);
                        if (var7 != null) {
                           if (log.isLoggable(PlatformLogger.Level.FINE)) {
                              log.fine("### FTP returned " + var7);
                           }

                           return var7;
                        }

                        var2 = var5;
                     }

                     java.util.List var10 = this.getFocusTraversalCycle(var1);
                     if (log.isLoggable(PlatformLogger.Level.FINE)) {
                        log.fine("### Cycle is " + var10 + ", component is " + var2);
                     }

                     int var11 = this.getComponentIndex(var10, (Component)var2);
                     if (var11 < 0) {
                        if (log.isLoggable(PlatformLogger.Level.FINE)) {
                           log.fine("### Didn't find component " + var2 + " in a cycle " + var1);
                        }

                        return this.getFirstComponent(var1);
                     } else {
                        ++var11;

                        while(var11 < var10.size()) {
                           var4 = (Component)var10.get(var11);
                           if (this.accept(var4)) {
                              return var4;
                           }

                           if ((var4 = this.getComponentDownCycle(var4, 0)) != null) {
                              return var4;
                           }

                           ++var11;
                        }

                        if (var1.isFocusCycleRoot()) {
                           this.cachedRoot = var1;
                           this.cachedCycle = var10;
                           var4 = this.getFirstComponent(var1);
                           this.cachedRoot = null;
                           this.cachedCycle = null;
                           return var4;
                        } else {
                           return null;
                        }
                     }
                  }
               } else {
                  return null;
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
            synchronized(var1.getTreeLock()) {
               if (var1.isVisible() && var1.isDisplayable()) {
                  Container var4 = this.getTopmostProvider(var1, (Component)var2);
                  if (var4 != null) {
                     if (log.isLoggable(PlatformLogger.Level.FINE)) {
                        log.fine("### Asking FTP " + var4 + " for component after " + var2);
                     }

                     FocusTraversalPolicy var5 = var4.getFocusTraversalPolicy();
                     Component var6 = var5.getComponentBefore(var4, (Component)var2);
                     if (var6 != null) {
                        if (log.isLoggable(PlatformLogger.Level.FINE)) {
                           log.fine("### FTP returned " + var6);
                        }

                        return var6;
                     }

                     var2 = var4;
                     if (this.accept(var4)) {
                        return var4;
                     }
                  }

                  java.util.List var11 = this.getFocusTraversalCycle(var1);
                  if (log.isLoggable(PlatformLogger.Level.FINE)) {
                     log.fine("### Cycle is " + var11 + ", component is " + var2);
                  }

                  int var12 = this.getComponentIndex(var11, (Component)var2);
                  if (var12 < 0) {
                     if (log.isLoggable(PlatformLogger.Level.FINE)) {
                        log.fine("### Didn't find component " + var2 + " in a cycle " + var1);
                     }

                     return this.getLastComponent(var1);
                  } else {
                     Component var7 = null;
                     Component var8 = null;
                     --var12;

                     while(var12 >= 0) {
                        var7 = (Component)var11.get(var12);
                        if (var7 != var1 && (var8 = this.getComponentDownCycle(var7, 1)) != null) {
                           return var8;
                        }

                        if (this.accept(var7)) {
                           return var7;
                        }

                        --var12;
                     }

                     if (var1.isFocusCycleRoot()) {
                        this.cachedRoot = var1;
                        this.cachedCycle = var11;
                        var7 = this.getLastComponent(var1);
                        this.cachedRoot = null;
                        this.cachedCycle = null;
                        return var7;
                     } else {
                        return null;
                     }
                  }
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
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         log.fine("### Getting first component in " + var1);
      }

      if (var1 == null) {
         throw new IllegalArgumentException("aContainer cannot be null");
      } else {
         synchronized(var1.getTreeLock()) {
            if (var1.isVisible() && var1.isDisplayable()) {
               java.util.List var2;
               if (this.cachedRoot == var1) {
                  var2 = this.cachedCycle;
               } else {
                  var2 = this.getFocusTraversalCycle(var1);
               }

               if (var2.size() == 0) {
                  if (log.isLoggable(PlatformLogger.Level.FINE)) {
                     log.fine("### Cycle is empty");
                  }

                  return null;
               } else {
                  if (log.isLoggable(PlatformLogger.Level.FINE)) {
                     log.fine("### Cycle is " + var2);
                  }

                  Iterator var4 = var2.iterator();

                  Component var5;
                  do {
                     if (!var4.hasNext()) {
                        return null;
                     }

                     var5 = (Component)var4.next();
                     if (this.accept(var5)) {
                        return var5;
                     }
                  } while(var5 == var1 || (var5 = this.getComponentDownCycle(var5, 0)) == null);

                  return var5;
               }
            } else {
               return null;
            }
         }
      }
   }

   public Component getLastComponent(Container var1) {
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         log.fine("### Getting last component in " + var1);
      }

      if (var1 == null) {
         throw new IllegalArgumentException("aContainer cannot be null");
      } else {
         synchronized(var1.getTreeLock()) {
            if (var1.isVisible() && var1.isDisplayable()) {
               java.util.List var2;
               if (this.cachedRoot == var1) {
                  var2 = this.cachedCycle;
               } else {
                  var2 = this.getFocusTraversalCycle(var1);
               }

               if (var2.size() == 0) {
                  if (log.isLoggable(PlatformLogger.Level.FINE)) {
                     log.fine("### Cycle is empty");
                  }

                  return null;
               } else {
                  if (log.isLoggable(PlatformLogger.Level.FINE)) {
                     log.fine("### Cycle is " + var2);
                  }

                  for(int var4 = var2.size() - 1; var4 >= 0; --var4) {
                     Component var5 = (Component)var2.get(var4);
                     if (this.accept(var5)) {
                        return var5;
                     }

                     if (var5 instanceof Container && var5 != var1) {
                        Container var6 = (Container)var5;
                        if (var6.isFocusTraversalPolicyProvider()) {
                           Component var7 = var6.getFocusTraversalPolicy().getLastComponent(var6);
                           if (var7 != null) {
                              return var7;
                           }
                        }
                     }
                  }

                  return null;
               }
            } else {
               return null;
            }
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

   protected boolean accept(Component var1) {
      if (!var1.canBeFocusOwner()) {
         return false;
      } else {
         if (!(var1 instanceof Window)) {
            for(Container var2 = var1.getParent(); var2 != null; var2 = var2.getParent()) {
               if (!var2.isEnabled() && !var2.isLightweight()) {
                  return false;
               }

               if (var2 instanceof Window) {
                  break;
               }
            }
         }

         return true;
      }
   }
}
