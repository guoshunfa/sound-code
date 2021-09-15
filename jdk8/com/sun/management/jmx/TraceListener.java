package com.sun.management.jmx;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.management.Notification;
import javax.management.NotificationListener;

/** @deprecated */
@Deprecated
public class TraceListener implements NotificationListener {
   protected PrintStream out;
   protected boolean needTobeClosed = false;
   protected boolean formated = false;

   public TraceListener() {
      this.out = System.out;
   }

   public TraceListener(PrintStream var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("An PrintStream object should be specified.");
      } else {
         this.out = var1;
      }
   }

   public TraceListener(String var1) throws IOException {
      this.out = new PrintStream(new FileOutputStream(var1, true));
      this.needTobeClosed = true;
   }

   public void setFormated(boolean var1) {
      this.formated = var1;
   }

   public void handleNotification(Notification var1, Object var2) {
      if (var1 instanceof TraceNotification) {
         TraceNotification var3 = (TraceNotification)var1;
         if (this.formated) {
            this.out.print("\nGlobal sequence number: " + var3.globalSequenceNumber + "     Sequence number: " + var3.sequenceNumber + "\nLevel: " + Trace.getLevel(var3.level) + "     Type: " + Trace.getType(var3.type) + "\nClass  Name: " + new String(var3.className) + "\nMethod Name: " + new String(var3.methodName) + "\n");
            if (var3.exception != null) {
               var3.exception.printStackTrace(this.out);
               this.out.println();
            }

            if (var3.info != null) {
               this.out.println("Information: " + var3.info);
            }
         } else {
            this.out.print("(" + var3.className + " " + var3.methodName + ") ");
            if (var3.exception != null) {
               var3.exception.printStackTrace(this.out);
               this.out.println();
            }

            if (var3.info != null) {
               this.out.println(var3.info);
            }
         }
      }

   }

   public void setFile(String var1) throws IOException {
      PrintStream var2 = new PrintStream(new FileOutputStream(var1, true));
      if (this.needTobeClosed) {
         this.out.close();
      }

      this.out = var2;
      this.needTobeClosed = true;
   }
}
