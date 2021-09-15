package java.util.logging;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class StreamHandler extends Handler {
   private OutputStream output;
   private boolean doneHeader;
   private volatile Writer writer;

   private void configure() {
      LogManager var1 = LogManager.getLogManager();
      String var2 = this.getClass().getName();
      this.setLevel(var1.getLevelProperty(var2 + ".level", Level.INFO));
      this.setFilter(var1.getFilterProperty(var2 + ".filter", (Filter)null));
      this.setFormatter(var1.getFormatterProperty(var2 + ".formatter", new SimpleFormatter()));

      try {
         this.setEncoding(var1.getStringProperty(var2 + ".encoding", (String)null));
      } catch (Exception var6) {
         try {
            this.setEncoding((String)null);
         } catch (Exception var5) {
         }
      }

   }

   public StreamHandler() {
      this.sealed = false;
      this.configure();
      this.sealed = true;
   }

   public StreamHandler(OutputStream var1, Formatter var2) {
      this.sealed = false;
      this.configure();
      this.setFormatter(var2);
      this.setOutputStream(var1);
      this.sealed = true;
   }

   protected synchronized void setOutputStream(OutputStream var1) throws SecurityException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.flushAndClose();
         this.output = var1;
         this.doneHeader = false;
         String var2 = this.getEncoding();
         if (var2 == null) {
            this.writer = new OutputStreamWriter(this.output);
         } else {
            try {
               this.writer = new OutputStreamWriter(this.output, var2);
            } catch (UnsupportedEncodingException var4) {
               throw new Error("Unexpected exception " + var4);
            }
         }

      }
   }

   public synchronized void setEncoding(String var1) throws SecurityException, UnsupportedEncodingException {
      super.setEncoding(var1);
      if (this.output != null) {
         this.flush();
         if (var1 == null) {
            this.writer = new OutputStreamWriter(this.output);
         } else {
            this.writer = new OutputStreamWriter(this.output, var1);
         }

      }
   }

   public synchronized void publish(LogRecord var1) {
      if (this.isLoggable(var1)) {
         String var2;
         try {
            var2 = this.getFormatter().format(var1);
         } catch (Exception var5) {
            this.reportError((String)null, var5, 5);
            return;
         }

         try {
            if (!this.doneHeader) {
               this.writer.write(this.getFormatter().getHead(this));
               this.doneHeader = true;
            }

            this.writer.write(var2);
         } catch (Exception var4) {
            this.reportError((String)null, var4, 1);
         }

      }
   }

   public boolean isLoggable(LogRecord var1) {
      return this.writer != null && var1 != null ? super.isLoggable(var1) : false;
   }

   public synchronized void flush() {
      if (this.writer != null) {
         try {
            this.writer.flush();
         } catch (Exception var2) {
            this.reportError((String)null, var2, 2);
         }
      }

   }

   private synchronized void flushAndClose() throws SecurityException {
      this.checkPermission();
      if (this.writer != null) {
         try {
            if (!this.doneHeader) {
               this.writer.write(this.getFormatter().getHead(this));
               this.doneHeader = true;
            }

            this.writer.write(this.getFormatter().getTail(this));
            this.writer.flush();
            this.writer.close();
         } catch (Exception var2) {
            this.reportError((String)null, var2, 3);
         }

         this.writer = null;
         this.output = null;
      }

   }

   public synchronized void close() throws SecurityException {
      this.flushAndClose();
   }
}
