package javax.swing.text.html.parser;

import java.io.OutputStream;
import java.io.PrintWriter;

class NPrintWriter extends PrintWriter {
   private int numLines = 5;
   private int numPrinted = 0;

   public NPrintWriter(int var1) {
      super((OutputStream)System.out);
      this.numLines = var1;
   }

   public void println(char[] var1) {
      if (this.numPrinted < this.numLines) {
         Object var2 = null;

         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3] == '\n') {
               ++this.numPrinted;
            }

            if (this.numPrinted == this.numLines) {
               System.arraycopy(var1, 0, var2, 0, var3);
            }
         }

         if (var2 != null) {
            super.print((char[])var2);
         }

         if (this.numPrinted != this.numLines) {
            super.println(var1);
            ++this.numPrinted;
         }
      }
   }
}
