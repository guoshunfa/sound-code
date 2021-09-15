package sun.print;

import java.awt.print.PrinterJob;
import javax.print.attribute.PrintRequestAttribute;

public class PrinterJobWrapper implements PrintRequestAttribute {
   private static final long serialVersionUID = -8792124426995707237L;
   private PrinterJob job;

   public PrinterJobWrapper(PrinterJob var1) {
      this.job = var1;
   }

   public PrinterJob getPrinterJob() {
      return this.job;
   }

   public final Class getCategory() {
      return PrinterJobWrapper.class;
   }

   public final String getName() {
      return "printerjob-wrapper";
   }

   public String toString() {
      return "printerjob-wrapper: " + this.job.toString();
   }

   public int hashCode() {
      return this.job.hashCode();
   }
}
