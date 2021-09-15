package sun.print;

import java.awt.Frame;
import javax.print.attribute.PrintRequestAttribute;

public final class DialogOwner implements PrintRequestAttribute {
   private Frame dlgOwner;

   public DialogOwner(Frame var1) {
      this.dlgOwner = var1;
   }

   public Frame getOwner() {
      return this.dlgOwner;
   }

   public final Class getCategory() {
      return DialogOwner.class;
   }

   public final String getName() {
      return "dialog-owner";
   }
}
