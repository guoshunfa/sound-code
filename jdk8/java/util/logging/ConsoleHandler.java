package java.util.logging;

public class ConsoleHandler extends StreamHandler {
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

   public ConsoleHandler() {
      this.sealed = false;
      this.configure();
      this.setOutputStream(System.err);
      this.sealed = true;
   }

   public void publish(LogRecord var1) {
      super.publish(var1);
      this.flush();
   }

   public void close() {
      this.flush();
   }
}
