package java.util.logging;

public class MemoryHandler extends Handler {
   private static final int DEFAULT_SIZE = 1000;
   private volatile Level pushLevel;
   private int size;
   private Handler target;
   private LogRecord[] buffer;
   int start;
   int count;

   private void configure() {
      LogManager var1 = LogManager.getLogManager();
      String var2 = this.getClass().getName();
      this.pushLevel = var1.getLevelProperty(var2 + ".push", Level.SEVERE);
      this.size = var1.getIntProperty(var2 + ".size", 1000);
      if (this.size <= 0) {
         this.size = 1000;
      }

      this.setLevel(var1.getLevelProperty(var2 + ".level", Level.ALL));
      this.setFilter(var1.getFilterProperty(var2 + ".filter", (Filter)null));
      this.setFormatter(var1.getFormatterProperty(var2 + ".formatter", new SimpleFormatter()));
   }

   public MemoryHandler() {
      this.sealed = false;
      this.configure();
      this.sealed = true;
      LogManager var1 = LogManager.getLogManager();
      String var2 = this.getClass().getName();
      String var3 = var1.getProperty(var2 + ".target");
      if (var3 == null) {
         throw new RuntimeException("The handler " + var2 + " does not specify a target");
      } else {
         try {
            Class var4 = ClassLoader.getSystemClassLoader().loadClass(var3);
            this.target = (Handler)var4.newInstance();
         } catch (InstantiationException | IllegalAccessException | ClassNotFoundException var6) {
            throw new RuntimeException("MemoryHandler can't load handler target \"" + var3 + "\"", var6);
         }

         this.init();
      }
   }

   private void init() {
      this.buffer = new LogRecord[this.size];
      this.start = 0;
      this.count = 0;
   }

   public MemoryHandler(Handler var1, int var2, Level var3) {
      if (var1 != null && var3 != null) {
         if (var2 <= 0) {
            throw new IllegalArgumentException();
         } else {
            this.sealed = false;
            this.configure();
            this.sealed = true;
            this.target = var1;
            this.pushLevel = var3;
            this.size = var2;
            this.init();
         }
      } else {
         throw new NullPointerException();
      }
   }

   public synchronized void publish(LogRecord var1) {
      if (this.isLoggable(var1)) {
         int var2 = (this.start + this.count) % this.buffer.length;
         this.buffer[var2] = var1;
         if (this.count < this.buffer.length) {
            ++this.count;
         } else {
            ++this.start;
            this.start %= this.buffer.length;
         }

         if (var1.getLevel().intValue() >= this.pushLevel.intValue()) {
            this.push();
         }

      }
   }

   public synchronized void push() {
      for(int var1 = 0; var1 < this.count; ++var1) {
         int var2 = (this.start + var1) % this.buffer.length;
         LogRecord var3 = this.buffer[var2];
         this.target.publish(var3);
      }

      this.start = 0;
      this.count = 0;
   }

   public void flush() {
      this.target.flush();
   }

   public void close() throws SecurityException {
      this.target.close();
      this.setLevel(Level.OFF);
   }

   public synchronized void setPushLevel(Level var1) throws SecurityException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.checkPermission();
         this.pushLevel = var1;
      }
   }

   public Level getPushLevel() {
      return this.pushLevel;
   }

   public boolean isLoggable(LogRecord var1) {
      return super.isLoggable(var1);
   }
}
