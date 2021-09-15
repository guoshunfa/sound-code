package java.awt.print;

import java.awt.Graphics;

public interface Printable {
   int PAGE_EXISTS = 0;
   int NO_SUCH_PAGE = 1;

   int print(Graphics var1, PageFormat var2, int var3) throws PrinterException;
}
