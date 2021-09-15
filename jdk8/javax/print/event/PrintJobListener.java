package javax.print.event;

public interface PrintJobListener {
   void printDataTransferCompleted(PrintJobEvent var1);

   void printJobCompleted(PrintJobEvent var1);

   void printJobFailed(PrintJobEvent var1);

   void printJobCanceled(PrintJobEvent var1);

   void printJobNoMoreEvents(PrintJobEvent var1);

   void printJobRequiresAttention(PrintJobEvent var1);
}
