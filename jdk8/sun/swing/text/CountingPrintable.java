package sun.swing.text;

import java.awt.print.Printable;

public interface CountingPrintable extends Printable {
   int getNumberOfPages();
}
