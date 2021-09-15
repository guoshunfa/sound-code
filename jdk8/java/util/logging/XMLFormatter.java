package java.util.logging;

import java.nio.charset.Charset;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

public class XMLFormatter extends Formatter {
   private LogManager manager = LogManager.getLogManager();

   private void a2(StringBuilder var1, int var2) {
      if (var2 < 10) {
         var1.append('0');
      }

      var1.append(var2);
   }

   private void appendISO8601(StringBuilder var1, long var2) {
      GregorianCalendar var4 = new GregorianCalendar();
      var4.setTimeInMillis(var2);
      var1.append(var4.get(1));
      var1.append('-');
      this.a2(var1, var4.get(2) + 1);
      var1.append('-');
      this.a2(var1, var4.get(5));
      var1.append('T');
      this.a2(var1, var4.get(11));
      var1.append(':');
      this.a2(var1, var4.get(12));
      var1.append(':');
      this.a2(var1, var4.get(13));
   }

   private void escape(StringBuilder var1, String var2) {
      if (var2 == null) {
         var2 = "<null>";
      }

      for(int var3 = 0; var3 < var2.length(); ++var3) {
         char var4 = var2.charAt(var3);
         if (var4 == '<') {
            var1.append("&lt;");
         } else if (var4 == '>') {
            var1.append("&gt;");
         } else if (var4 == '&') {
            var1.append("&amp;");
         } else {
            var1.append(var4);
         }
      }

   }

   public String format(LogRecord var1) {
      StringBuilder var2 = new StringBuilder(500);
      var2.append("<record>\n");
      var2.append("  <date>");
      this.appendISO8601(var2, var1.getMillis());
      var2.append("</date>\n");
      var2.append("  <millis>");
      var2.append(var1.getMillis());
      var2.append("</millis>\n");
      var2.append("  <sequence>");
      var2.append(var1.getSequenceNumber());
      var2.append("</sequence>\n");
      String var3 = var1.getLoggerName();
      if (var3 != null) {
         var2.append("  <logger>");
         this.escape(var2, var3);
         var2.append("</logger>\n");
      }

      var2.append("  <level>");
      this.escape(var2, var1.getLevel().toString());
      var2.append("</level>\n");
      if (var1.getSourceClassName() != null) {
         var2.append("  <class>");
         this.escape(var2, var1.getSourceClassName());
         var2.append("</class>\n");
      }

      if (var1.getSourceMethodName() != null) {
         var2.append("  <method>");
         this.escape(var2, var1.getSourceMethodName());
         var2.append("</method>\n");
      }

      var2.append("  <thread>");
      var2.append(var1.getThreadID());
      var2.append("</thread>\n");
      if (var1.getMessage() != null) {
         String var4 = this.formatMessage(var1);
         var2.append("  <message>");
         this.escape(var2, var4);
         var2.append("</message>");
         var2.append("\n");
      }

      ResourceBundle var12 = var1.getResourceBundle();

      try {
         if (var12 != null && var12.getString(var1.getMessage()) != null) {
            var2.append("  <key>");
            this.escape(var2, var1.getMessage());
            var2.append("</key>\n");
            var2.append("  <catalog>");
            this.escape(var2, var1.getResourceBundleName());
            var2.append("</catalog>\n");
         }
      } catch (Exception var11) {
      }

      Object[] var5 = var1.getParameters();
      if (var5 != null && var5.length != 0 && var1.getMessage().indexOf("{") == -1) {
         for(int var6 = 0; var6 < var5.length; ++var6) {
            var2.append("  <param>");

            try {
               this.escape(var2, var5[var6].toString());
            } catch (Exception var10) {
               var2.append("???");
            }

            var2.append("</param>\n");
         }
      }

      if (var1.getThrown() != null) {
         Throwable var13 = var1.getThrown();
         var2.append("  <exception>\n");
         var2.append("    <message>");
         this.escape(var2, var13.toString());
         var2.append("</message>\n");
         StackTraceElement[] var7 = var13.getStackTrace();

         for(int var8 = 0; var8 < var7.length; ++var8) {
            StackTraceElement var9 = var7[var8];
            var2.append("    <frame>\n");
            var2.append("      <class>");
            this.escape(var2, var9.getClassName());
            var2.append("</class>\n");
            var2.append("      <method>");
            this.escape(var2, var9.getMethodName());
            var2.append("</method>\n");
            if (var9.getLineNumber() >= 0) {
               var2.append("      <line>");
               var2.append(var9.getLineNumber());
               var2.append("</line>\n");
            }

            var2.append("    </frame>\n");
         }

         var2.append("  </exception>\n");
      }

      var2.append("</record>\n");
      return var2.toString();
   }

   public String getHead(Handler var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("<?xml version=\"1.0\"");
      String var3;
      if (var1 != null) {
         var3 = var1.getEncoding();
      } else {
         var3 = null;
      }

      if (var3 == null) {
         var3 = Charset.defaultCharset().name();
      }

      try {
         Charset var4 = Charset.forName(var3);
         var3 = var4.name();
      } catch (Exception var5) {
      }

      var2.append(" encoding=\"");
      var2.append(var3);
      var2.append("\"");
      var2.append(" standalone=\"no\"?>\n");
      var2.append("<!DOCTYPE log SYSTEM \"logger.dtd\">\n");
      var2.append("<log>\n");
      return var2.toString();
   }

   public String getTail(Handler var1) {
      return "</log>\n";
   }
}
