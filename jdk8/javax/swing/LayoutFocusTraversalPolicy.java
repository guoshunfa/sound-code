package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import sun.awt.SunToolkit;

public class LayoutFocusTraversalPolicy extends SortingFocusTraversalPolicy implements Serializable {
   private static final SwingDefaultFocusTraversalPolicy fitnessTestPolicy = new SwingDefaultFocusTraversalPolicy();

   public LayoutFocusTraversalPolicy() {
      super(new LayoutComparator());
   }

   LayoutFocusTraversalPolicy(Comparator<? super Component> var1) {
      super(var1);
   }

   public Component getComponentAfter(Container var1, Component var2) {
      if (var1 != null && var2 != null) {
         Comparator var3 = this.getComparator();
         if (var3 instanceof LayoutComparator) {
            ((LayoutComparator)var3).setComponentOrientation(var1.getComponentOrientation());
         }

         return super.getComponentAfter(var1, var2);
      } else {
         throw new IllegalArgumentException("aContainer and aComponent cannot be null");
      }
   }

   public Component getComponentBefore(Container var1, Component var2) {
      if (var1 != null && var2 != null) {
         Comparator var3 = this.getComparator();
         if (var3 instanceof LayoutComparator) {
            ((LayoutComparator)var3).setComponentOrientation(var1.getComponentOrientation());
         }

         return super.getComponentBefore(var1, var2);
      } else {
         throw new IllegalArgumentException("aContainer and aComponent cannot be null");
      }
   }

   public Component getFirstComponent(Container var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("aContainer cannot be null");
      } else {
         Comparator var2 = this.getComparator();
         if (var2 instanceof LayoutComparator) {
            ((LayoutComparator)var2).setComponentOrientation(var1.getComponentOrientation());
         }

         return super.getFirstComponent(var1);
      }
   }

   public Component getLastComponent(Container var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("aContainer cannot be null");
      } else {
         Comparator var2 = this.getComparator();
         if (var2 instanceof LayoutComparator) {
            ((LayoutComparator)var2).setComponentOrientation(var1.getComponentOrientation());
         }

         return super.getLastComponent(var1);
      }
   }

   protected boolean accept(Component var1) {
      if (!super.accept(var1)) {
         return false;
      } else if (SunToolkit.isInstanceOf((Object)var1, "javax.swing.JTable")) {
         return true;
      } else if (SunToolkit.isInstanceOf((Object)var1, "javax.swing.JComboBox")) {
         JComboBox var4 = (JComboBox)var1;
         return var4.getUI().isFocusTraversable(var4);
      } else {
         if (var1 instanceof JComponent) {
            JComponent var2 = (JComponent)var1;

            InputMap var3;
            for(var3 = var2.getInputMap(0, false); var3 != null && var3.size() == 0; var3 = var3.getParent()) {
            }

            if (var3 != null) {
               return true;
            }
         }

         return fitnessTestPolicy.accept(var1);
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeObject(this.getComparator());
      var1.writeBoolean(this.getImplicitDownCycleTraversal());
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.setComparator((Comparator)var1.readObject());
      this.setImplicitDownCycleTraversal(var1.readBoolean());
   }
}
