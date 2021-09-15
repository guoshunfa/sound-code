package java.awt.print;

public interface Pageable {
   int UNKNOWN_NUMBER_OF_PAGES = -1;

   int getNumberOfPages();

   PageFormat getPageFormat(int var1) throws IndexOutOfBoundsException;

   Printable getPrintable(int var1) throws IndexOutOfBoundsException;
}
