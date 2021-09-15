package java.lang;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Throwable implements Serializable {
   private static final long serialVersionUID = -3042686055658047285L;
   private transient Object backtrace;
   private String detailMessage;
   private static final StackTraceElement[] UNASSIGNED_STACK = new StackTraceElement[0];
   private Throwable cause = this;
   private StackTraceElement[] stackTrace;
   private static final List<Throwable> SUPPRESSED_SENTINEL = Collections.unmodifiableList(new ArrayList(0));
   private List<Throwable> suppressedExceptions;
   private static final String NULL_CAUSE_MESSAGE = "Cannot suppress a null exception.";
   private static final String SELF_SUPPRESSION_MESSAGE = "Self-suppression not permitted";
   private static final String CAUSE_CAPTION = "Caused by: ";
   private static final String SUPPRESSED_CAPTION = "Suppressed: ";
   private static final Throwable[] EMPTY_THROWABLE_ARRAY = new Throwable[0];

   public Throwable() {
      this.stackTrace = UNASSIGNED_STACK;
      this.suppressedExceptions = SUPPRESSED_SENTINEL;
      this.fillInStackTrace();
   }

   public Throwable(String var1) {
      this.stackTrace = UNASSIGNED_STACK;
      this.suppressedExceptions = SUPPRESSED_SENTINEL;
      this.fillInStackTrace();
      this.detailMessage = var1;
   }

   public Throwable(String var1, Throwable var2) {
      this.stackTrace = UNASSIGNED_STACK;
      this.suppressedExceptions = SUPPRESSED_SENTINEL;
      this.fillInStackTrace();
      this.detailMessage = var1;
      this.cause = var2;
   }

   public Throwable(Throwable var1) {
      this.stackTrace = UNASSIGNED_STACK;
      this.suppressedExceptions = SUPPRESSED_SENTINEL;
      this.fillInStackTrace();
      this.detailMessage = var1 == null ? null : var1.toString();
      this.cause = var1;
   }

   protected Throwable(String var1, Throwable var2, boolean var3, boolean var4) {
      this.stackTrace = UNASSIGNED_STACK;
      this.suppressedExceptions = SUPPRESSED_SENTINEL;
      if (var4) {
         this.fillInStackTrace();
      } else {
         this.stackTrace = null;
      }

      this.detailMessage = var1;
      this.cause = var2;
      if (!var3) {
         this.suppressedExceptions = null;
      }

   }

   public String getMessage() {
      return this.detailMessage;
   }

   public String getLocalizedMessage() {
      return this.getMessage();
   }

   public synchronized Throwable getCause() {
      return this.cause == this ? null : this.cause;
   }

   public synchronized Throwable initCause(Throwable var1) {
      if (this.cause != this) {
         throw new IllegalStateException("Can't overwrite cause with " + Objects.toString(var1, "a null"), this);
      } else if (var1 == this) {
         throw new IllegalArgumentException("Self-causation not permitted", this);
      } else {
         this.cause = var1;
         return this;
      }
   }

   public String toString() {
      String var1 = this.getClass().getName();
      String var2 = this.getLocalizedMessage();
      return var2 != null ? var1 + ": " + var2 : var1;
   }

   public void printStackTrace() {
      this.printStackTrace(System.err);
   }

   public void printStackTrace(PrintStream var1) {
      this.printStackTrace((Throwable.PrintStreamOrWriter)(new Throwable.WrappedPrintStream(var1)));
   }

   private void printStackTrace(Throwable.PrintStreamOrWriter var1) {
      Set var2 = Collections.newSetFromMap(new IdentityHashMap());
      var2.add(this);
      synchronized(var1.lock()) {
         var1.println(this);
         StackTraceElement[] var4 = this.getOurStackTrace();
         StackTraceElement[] var5 = var4;
         int var6 = var4.length;

         int var7;
         for(var7 = 0; var7 < var6; ++var7) {
            StackTraceElement var8 = var5[var7];
            var1.println("\tat " + var8);
         }

         Throwable[] var11 = this.getSuppressed();
         var6 = var11.length;

         for(var7 = 0; var7 < var6; ++var7) {
            Throwable var13 = var11[var7];
            var13.printEnclosedStackTrace(var1, var4, "Suppressed: ", "\t", var2);
         }

         Throwable var12 = this.getCause();
         if (var12 != null) {
            var12.printEnclosedStackTrace(var1, var4, "Caused by: ", "", var2);
         }

      }
   }

   private void printEnclosedStackTrace(Throwable.PrintStreamOrWriter var1, StackTraceElement[] var2, String var3, String var4, Set<Throwable> var5) {
      assert Thread.holdsLock(var1.lock());

      if (var5.contains(this)) {
         var1.println("\t[CIRCULAR REFERENCE:" + this + "]");
      } else {
         var5.add(this);
         StackTraceElement[] var6 = this.getOurStackTrace();
         int var7 = var6.length - 1;

         for(int var8 = var2.length - 1; var7 >= 0 && var8 >= 0 && var6[var7].equals(var2[var8]); --var8) {
            --var7;
         }

         int var9 = var6.length - 1 - var7;
         var1.println(var4 + var3 + this);

         for(int var10 = 0; var10 <= var7; ++var10) {
            var1.println(var4 + "\tat " + var6[var10]);
         }

         if (var9 != 0) {
            var1.println(var4 + "\t... " + var9 + " more");
         }

         Throwable[] var14 = this.getSuppressed();
         int var11 = var14.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            Throwable var13 = var14[var12];
            var13.printEnclosedStackTrace(var1, var6, "Suppressed: ", var4 + "\t", var5);
         }

         Throwable var15 = this.getCause();
         if (var15 != null) {
            var15.printEnclosedStackTrace(var1, var6, "Caused by: ", var4, var5);
         }
      }

   }

   public void printStackTrace(PrintWriter var1) {
      this.printStackTrace((Throwable.PrintStreamOrWriter)(new Throwable.WrappedPrintWriter(var1)));
   }

   public synchronized Throwable fillInStackTrace() {
      if (this.stackTrace != null || this.backtrace != null) {
         this.fillInStackTrace(0);
         this.stackTrace = UNASSIGNED_STACK;
      }

      return this;
   }

   private native Throwable fillInStackTrace(int var1);

   public StackTraceElement[] getStackTrace() {
      return (StackTraceElement[])this.getOurStackTrace().clone();
   }

   private synchronized StackTraceElement[] getOurStackTrace() {
      if (this.stackTrace != UNASSIGNED_STACK && (this.stackTrace != null || this.backtrace == null)) {
         if (this.stackTrace == null) {
            return UNASSIGNED_STACK;
         }
      } else {
         int var1 = this.getStackTraceDepth();
         this.stackTrace = new StackTraceElement[var1];

         for(int var2 = 0; var2 < var1; ++var2) {
            this.stackTrace[var2] = this.getStackTraceElement(var2);
         }
      }

      return this.stackTrace;
   }

   public void setStackTrace(StackTraceElement[] var1) {
      StackTraceElement[] var2 = (StackTraceElement[])var1.clone();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3] == null) {
            throw new NullPointerException("stackTrace[" + var3 + "]");
         }
      }

      synchronized(this) {
         if (this.stackTrace != null || this.backtrace != null) {
            this.stackTrace = var2;
         }
      }
   }

   native int getStackTraceDepth();

   native StackTraceElement getStackTraceElement(int var1);

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.suppressedExceptions != null) {
         Object var2 = null;
         if (this.suppressedExceptions.isEmpty()) {
            var2 = SUPPRESSED_SENTINEL;
         } else {
            var2 = new ArrayList(1);
            Iterator var3 = this.suppressedExceptions.iterator();

            while(var3.hasNext()) {
               Throwable var4 = (Throwable)var3.next();
               if (var4 == null) {
                  throw new NullPointerException("Cannot suppress a null exception.");
               }

               if (var4 == this) {
                  throw new IllegalArgumentException("Self-suppression not permitted");
               }

               ((List)var2).add(var4);
            }
         }

         this.suppressedExceptions = (List)var2;
      }

      if (this.stackTrace != null) {
         if (this.stackTrace.length == 0) {
            this.stackTrace = (StackTraceElement[])UNASSIGNED_STACK.clone();
         } else if (this.stackTrace.length == 1 && Throwable.SentinelHolder.STACK_TRACE_ELEMENT_SENTINEL.equals(this.stackTrace[0])) {
            this.stackTrace = null;
         } else {
            StackTraceElement[] var6 = this.stackTrace;
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               StackTraceElement var5 = var6[var8];
               if (var5 == null) {
                  throw new NullPointerException("null StackTraceElement in serial stream. ");
               }
            }
         }
      } else {
         this.stackTrace = (StackTraceElement[])UNASSIGNED_STACK.clone();
      }

   }

   private synchronized void writeObject(ObjectOutputStream var1) throws IOException {
      this.getOurStackTrace();
      StackTraceElement[] var2 = this.stackTrace;

      try {
         if (this.stackTrace == null) {
            this.stackTrace = Throwable.SentinelHolder.STACK_TRACE_SENTINEL;
         }

         var1.defaultWriteObject();
      } finally {
         this.stackTrace = var2;
      }

   }

   public final synchronized void addSuppressed(Throwable var1) {
      if (var1 == this) {
         throw new IllegalArgumentException("Self-suppression not permitted", var1);
      } else if (var1 == null) {
         throw new NullPointerException("Cannot suppress a null exception.");
      } else if (this.suppressedExceptions != null) {
         if (this.suppressedExceptions == SUPPRESSED_SENTINEL) {
            this.suppressedExceptions = new ArrayList(1);
         }

         this.suppressedExceptions.add(var1);
      }
   }

   public final synchronized Throwable[] getSuppressed() {
      return this.suppressedExceptions != SUPPRESSED_SENTINEL && this.suppressedExceptions != null ? (Throwable[])this.suppressedExceptions.toArray(EMPTY_THROWABLE_ARRAY) : EMPTY_THROWABLE_ARRAY;
   }

   private static class WrappedPrintWriter extends Throwable.PrintStreamOrWriter {
      private final PrintWriter printWriter;

      WrappedPrintWriter(PrintWriter var1) {
         super(null);
         this.printWriter = var1;
      }

      Object lock() {
         return this.printWriter;
      }

      void println(Object var1) {
         this.printWriter.println(var1);
      }
   }

   private static class WrappedPrintStream extends Throwable.PrintStreamOrWriter {
      private final PrintStream printStream;

      WrappedPrintStream(PrintStream var1) {
         super(null);
         this.printStream = var1;
      }

      Object lock() {
         return this.printStream;
      }

      void println(Object var1) {
         this.printStream.println(var1);
      }
   }

   private abstract static class PrintStreamOrWriter {
      private PrintStreamOrWriter() {
      }

      abstract Object lock();

      abstract void println(Object var1);

      // $FF: synthetic method
      PrintStreamOrWriter(Object var1) {
         this();
      }
   }

   private static class SentinelHolder {
      public static final StackTraceElement STACK_TRACE_ELEMENT_SENTINEL = new StackTraceElement("", "", (String)null, Integer.MIN_VALUE);
      public static final StackTraceElement[] STACK_TRACE_SENTINEL;

      static {
         STACK_TRACE_SENTINEL = new StackTraceElement[]{STACK_TRACE_ELEMENT_SENTINEL};
      }
   }
}
