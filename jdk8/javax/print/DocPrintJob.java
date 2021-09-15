package javax.print;

import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobListener;

public interface DocPrintJob {
   PrintService getPrintService();

   PrintJobAttributeSet getAttributes();

   void addPrintJobListener(PrintJobListener var1);

   void removePrintJobListener(PrintJobListener var1);

   void addPrintJobAttributeListener(PrintJobAttributeListener var1, PrintJobAttributeSet var2);

   void removePrintJobAttributeListener(PrintJobAttributeListener var1);

   void print(Doc var1, PrintRequestAttributeSet var2) throws PrintException;
}
