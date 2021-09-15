package sun.swing.text;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class CompoundPrintable implements CountingPrintable {
   private final Queue<CountingPrintable> printables;
   private int offset = 0;

   public CompoundPrintable(List<CountingPrintable> var1) {
      this.printables = new LinkedList(var1);
   }

   public int print(Graphics var1, PageFormat var2, int var3) throws PrinterException {
      int var4;
      for(var4 = 1; this.printables.peek() != null; this.offset += ((CountingPrintable)this.printables.poll()).getNumberOfPages()) {
         var4 = ((CountingPrintable)this.printables.peek()).print(var1, var2, var3 - this.offset);
         if (var4 == 0) {
            break;
         }
      }

      return var4;
   }

   public int getNumberOfPages() {
      return this.offset;
   }
}
