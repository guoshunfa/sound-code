package javax.print;

public interface CancelablePrintJob extends DocPrintJob {
   void cancel() throws PrintException;
}
