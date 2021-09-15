package sun.rmi.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Date;
import sun.rmi.runtime.NewThreadAction;
import sun.security.action.GetPropertyAction;

class PipeWriter implements Runnable {
   private ByteArrayOutputStream bufOut;
   private int cLast;
   private byte[] currSep;
   private PrintWriter out;
   private InputStream in;
   private String pipeString;
   private String execString;
   private static String lineSeparator = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("line.separator")));
   private static int lineSeparatorLength;
   private static int numExecs = 0;

   private PipeWriter(InputStream var1, OutputStream var2, String var3, int var4) {
      this.in = var1;
      this.out = new PrintWriter(var2);
      this.bufOut = new ByteArrayOutputStream();
      this.currSep = new byte[lineSeparatorLength];
      this.execString = ":ExecGroup-" + Integer.toString(var4) + ':' + var3 + ':';
   }

   public void run() {
      byte[] var1 = new byte[256];

      try {
         int var2;
         while((var2 = this.in.read(var1)) != -1) {
            this.write(var1, 0, var2);
         }

         String var3 = this.bufOut.toString();
         this.bufOut.reset();
         if (var3.length() > 0) {
            this.out.println(this.createAnnotation() + var3);
            this.out.flush();
         }
      } catch (IOException var4) {
      }

   }

   private void write(byte[] var1, int var2, int var3) throws IOException {
      if (var3 < 0) {
         throw new ArrayIndexOutOfBoundsException(var3);
      } else {
         for(int var4 = 0; var4 < var3; ++var4) {
            this.write(var1[var2 + var4]);
         }

      }
   }

   private void write(byte var1) throws IOException {
      boolean var2 = false;

      int var3;
      for(var3 = 1; var3 < this.currSep.length; ++var3) {
         this.currSep[var3 - 1] = this.currSep[var3];
      }

      this.currSep[var3 - 1] = var1;
      this.bufOut.write(var1);
      if (this.cLast >= lineSeparatorLength - 1 && lineSeparator.equals(new String(this.currSep))) {
         this.cLast = 0;
         this.out.print(this.createAnnotation() + this.bufOut.toString());
         this.out.flush();
         this.bufOut.reset();
         if (this.out.checkError()) {
            throw new IOException("PipeWriter: IO Exception when writing to output stream.");
         }
      } else {
         ++this.cLast;
      }

   }

   private String createAnnotation() {
      return (new Date()).toString() + this.execString;
   }

   static void plugTogetherPair(InputStream var0, OutputStream var1, InputStream var2, OutputStream var3) {
      Thread var4 = null;
      Thread var5 = null;
      int var6 = getNumExec();
      var4 = (Thread)AccessController.doPrivileged((PrivilegedAction)(new NewThreadAction(new PipeWriter(var0, var1, "out", var6), "out", true)));
      var5 = (Thread)AccessController.doPrivileged((PrivilegedAction)(new NewThreadAction(new PipeWriter(var2, var3, "err", var6), "err", true)));
      var4.start();
      var5.start();
   }

   private static synchronized int getNumExec() {
      return numExecs++;
   }

   static {
      lineSeparatorLength = lineSeparator.length();
   }
}
