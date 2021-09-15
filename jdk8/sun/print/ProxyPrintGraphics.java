package sun.print;

import java.awt.Graphics;
import java.awt.PrintGraphics;
import java.awt.PrintJob;

public class ProxyPrintGraphics extends ProxyGraphics implements PrintGraphics {
   private PrintJob printJob;

   public ProxyPrintGraphics(Graphics var1, PrintJob var2) {
      super(var1);
      this.printJob = var2;
   }

   public PrintJob getPrintJob() {
      return this.printJob;
   }

   public Graphics create() {
      return new ProxyPrintGraphics(this.getGraphics().create(), this.printJob);
   }

   public Graphics create(int var1, int var2, int var3, int var4) {
      Graphics var5 = this.getGraphics().create(var1, var2, var3, var4);
      return new ProxyPrintGraphics(var5, this.printJob);
   }

   public Graphics getGraphics() {
      return super.getGraphics();
   }

   public void dispose() {
      super.dispose();
   }
}
