package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Window;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

final class LegacyGlueFocusTraversalPolicy extends FocusTraversalPolicy implements Serializable {
   private transient FocusTraversalPolicy delegatePolicy;
   private transient DefaultFocusManager delegateManager;
   private HashMap<Component, Component> forwardMap = new HashMap();
   private HashMap<Component, Component> backwardMap = new HashMap();

   LegacyGlueFocusTraversalPolicy(FocusTraversalPolicy var1) {
      this.delegatePolicy = var1;
   }

   LegacyGlueFocusTraversalPolicy(DefaultFocusManager var1) {
      this.delegateManager = var1;
   }

   void setNextFocusableComponent(Component var1, Component var2) {
      this.forwardMap.put(var1, var2);
      this.backwardMap.put(var2, var1);
   }

   void unsetNextFocusableComponent(Component var1, Component var2) {
      this.forwardMap.remove(var1);
      this.backwardMap.remove(var2);
   }

   public Component getComponentAfter(Container var1, Component var2) {
      Component var3 = var2;
      HashSet var5 = new HashSet();

      do {
         Component var4 = var3;
         var3 = (Component)this.forwardMap.get(var3);
         if (var3 == null) {
            if (this.delegatePolicy != null && var4.isFocusCycleRoot(var1)) {
               return this.delegatePolicy.getComponentAfter(var1, var4);
            } else {
               return this.delegateManager != null ? this.delegateManager.getComponentAfter(var1, var2) : null;
            }
         }

         if (var5.contains(var3)) {
            return null;
         }

         var5.add(var3);
      } while(!this.accept(var3));

      return var3;
   }

   public Component getComponentBefore(Container var1, Component var2) {
      Component var3 = var2;
      HashSet var5 = new HashSet();

      do {
         Component var4 = var3;
         var3 = (Component)this.backwardMap.get(var3);
         if (var3 == null) {
            if (this.delegatePolicy != null && var4.isFocusCycleRoot(var1)) {
               return this.delegatePolicy.getComponentBefore(var1, var4);
            } else {
               return this.delegateManager != null ? this.delegateManager.getComponentBefore(var1, var2) : null;
            }
         }

         if (var5.contains(var3)) {
            return null;
         }

         var5.add(var3);
      } while(!this.accept(var3));

      return var3;
   }

   public Component getFirstComponent(Container var1) {
      if (this.delegatePolicy != null) {
         return this.delegatePolicy.getFirstComponent(var1);
      } else {
         return this.delegateManager != null ? this.delegateManager.getFirstComponent(var1) : null;
      }
   }

   public Component getLastComponent(Container var1) {
      if (this.delegatePolicy != null) {
         return this.delegatePolicy.getLastComponent(var1);
      } else {
         return this.delegateManager != null ? this.delegateManager.getLastComponent(var1) : null;
      }
   }

   public Component getDefaultComponent(Container var1) {
      return this.delegatePolicy != null ? this.delegatePolicy.getDefaultComponent(var1) : this.getFirstComponent(var1);
   }

   private boolean accept(Component var1) {
      if (var1.isVisible() && var1.isDisplayable() && var1.isFocusable() && var1.isEnabled()) {
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
      } else {
         return false;
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.delegatePolicy instanceof Serializable) {
         var1.writeObject(this.delegatePolicy);
      } else {
         var1.writeObject((Object)null);
      }

      if (this.delegateManager instanceof Serializable) {
         var1.writeObject(this.delegateManager);
      } else {
         var1.writeObject((Object)null);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.delegatePolicy = (FocusTraversalPolicy)var1.readObject();
      this.delegateManager = (DefaultFocusManager)var1.readObject();
   }
}
