package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;

public class DefaultFocusManager extends FocusManager {
   final FocusTraversalPolicy gluePolicy = new LegacyGlueFocusTraversalPolicy(this);
   private final FocusTraversalPolicy layoutPolicy = new LegacyLayoutFocusTraversalPolicy(this);
   private final LayoutComparator comparator = new LayoutComparator();

   public DefaultFocusManager() {
      this.setDefaultFocusTraversalPolicy(this.gluePolicy);
   }

   public Component getComponentAfter(Container var1, Component var2) {
      Container var3 = var1.isFocusCycleRoot() ? var1 : var1.getFocusCycleRootAncestor();
      if (var3 != null) {
         FocusTraversalPolicy var4 = var3.getFocusTraversalPolicy();
         if (var4 != this.gluePolicy) {
            return var4.getComponentAfter(var3, var2);
         } else {
            this.comparator.setComponentOrientation(var3.getComponentOrientation());
            return this.layoutPolicy.getComponentAfter(var3, var2);
         }
      } else {
         return null;
      }
   }

   public Component getComponentBefore(Container var1, Component var2) {
      Container var3 = var1.isFocusCycleRoot() ? var1 : var1.getFocusCycleRootAncestor();
      if (var3 != null) {
         FocusTraversalPolicy var4 = var3.getFocusTraversalPolicy();
         if (var4 != this.gluePolicy) {
            return var4.getComponentBefore(var3, var2);
         } else {
            this.comparator.setComponentOrientation(var3.getComponentOrientation());
            return this.layoutPolicy.getComponentBefore(var3, var2);
         }
      } else {
         return null;
      }
   }

   public Component getFirstComponent(Container var1) {
      Container var2 = var1.isFocusCycleRoot() ? var1 : var1.getFocusCycleRootAncestor();
      if (var2 != null) {
         FocusTraversalPolicy var3 = var2.getFocusTraversalPolicy();
         if (var3 != this.gluePolicy) {
            return var3.getFirstComponent(var2);
         } else {
            this.comparator.setComponentOrientation(var2.getComponentOrientation());
            return this.layoutPolicy.getFirstComponent(var2);
         }
      } else {
         return null;
      }
   }

   public Component getLastComponent(Container var1) {
      Container var2 = var1.isFocusCycleRoot() ? var1 : var1.getFocusCycleRootAncestor();
      if (var2 != null) {
         FocusTraversalPolicy var3 = var2.getFocusTraversalPolicy();
         if (var3 != this.gluePolicy) {
            return var3.getLastComponent(var2);
         } else {
            this.comparator.setComponentOrientation(var2.getComponentOrientation());
            return this.layoutPolicy.getLastComponent(var2);
         }
      } else {
         return null;
      }
   }

   public boolean compareTabOrder(Component var1, Component var2) {
      return this.comparator.compare(var1, var2) < 0;
   }
}
