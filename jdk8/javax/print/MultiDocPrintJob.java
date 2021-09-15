package javax.print;

import javax.print.attribute.PrintRequestAttributeSet;

public interface MultiDocPrintJob extends DocPrintJob {
   void print(MultiDoc var1, PrintRequestAttributeSet var2) throws PrintException;
}
