package java.util.logging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

public class LogRecord implements Serializable {
   private static final AtomicLong globalSequenceNumber = new AtomicLong(0L);
   private static final int MIN_SEQUENTIAL_THREAD_ID = 1073741823;
   private static final AtomicInteger nextThreadId = new AtomicInteger(1073741823);
   private static final ThreadLocal<Integer> threadIds = new ThreadLocal();
   private Level level;
   private long sequenceNumber;
   private String sourceClassName;
   private String sourceMethodName;
   private String message;
   private int threadID;
   private long millis;
   private Throwable thrown;
   private String loggerName;
   private String resourceBundleName;
   private transient boolean needToInferCaller;
   private transient Object[] parameters;
   private transient ResourceBundle resourceBundle;
   private static final long serialVersionUID = 5372048053134512534L;

   private int defaultThreadID() {
      long var1 = Thread.currentThread().getId();
      if (var1 < 1073741823L) {
         return (int)var1;
      } else {
         Integer var3 = (Integer)threadIds.get();
         if (var3 == null) {
            var3 = nextThreadId.getAndIncrement();
            threadIds.set(var3);
         }

         return var3;
      }
   }

   public LogRecord(Level var1, String var2) {
      var1.getClass();
      this.level = var1;
      this.message = var2;
      this.sequenceNumber = globalSequenceNumber.getAndIncrement();
      this.threadID = this.defaultThreadID();
      this.millis = System.currentTimeMillis();
      this.needToInferCaller = true;
   }

   public String getLoggerName() {
      return this.loggerName;
   }

   public void setLoggerName(String var1) {
      this.loggerName = var1;
   }

   public ResourceBundle getResourceBundle() {
      return this.resourceBundle;
   }

   public void setResourceBundle(ResourceBundle var1) {
      this.resourceBundle = var1;
   }

   public String getResourceBundleName() {
      return this.resourceBundleName;
   }

   public void setResourceBundleName(String var1) {
      this.resourceBundleName = var1;
   }

   public Level getLevel() {
      return this.level;
   }

   public void setLevel(Level var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.level = var1;
      }
   }

   public long getSequenceNumber() {
      return this.sequenceNumber;
   }

   public void setSequenceNumber(long var1) {
      this.sequenceNumber = var1;
   }

   public String getSourceClassName() {
      if (this.needToInferCaller) {
         this.inferCaller();
      }

      return this.sourceClassName;
   }

   public void setSourceClassName(String var1) {
      this.sourceClassName = var1;
      this.needToInferCaller = false;
   }

   public String getSourceMethodName() {
      if (this.needToInferCaller) {
         this.inferCaller();
      }

      return this.sourceMethodName;
   }

   public void setSourceMethodName(String var1) {
      this.sourceMethodName = var1;
      this.needToInferCaller = false;
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }

   public Object[] getParameters() {
      return this.parameters;
   }

   public void setParameters(Object[] var1) {
      this.parameters = var1;
   }

   public int getThreadID() {
      return this.threadID;
   }

   public void setThreadID(int var1) {
      this.threadID = var1;
   }

   public long getMillis() {
      return this.millis;
   }

   public void setMillis(long var1) {
      this.millis = var1;
   }

   public Throwable getThrown() {
      return this.thrown;
   }

   public void setThrown(Throwable var1) {
      this.thrown = var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeByte(1);
      var1.writeByte(0);
      if (this.parameters == null) {
         var1.writeInt(-1);
      } else {
         var1.writeInt(this.parameters.length);

         for(int var2 = 0; var2 < this.parameters.length; ++var2) {
            if (this.parameters[var2] == null) {
               var1.writeObject((Object)null);
            } else {
               var1.writeObject(this.parameters[var2].toString());
            }
         }

      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      byte var2 = var1.readByte();
      byte var3 = var1.readByte();
      if (var2 != 1) {
         throw new IOException("LogRecord: bad version: " + var2 + "." + var3);
      } else {
         int var4 = var1.readInt();
         if (var4 < -1) {
            throw new NegativeArraySizeException();
         } else {
            if (var4 == -1) {
               this.parameters = null;
            } else if (var4 < 255) {
               this.parameters = new Object[var4];

               for(int var5 = 0; var5 < this.parameters.length; ++var5) {
                  this.parameters[var5] = var1.readObject();
               }
            } else {
               ArrayList var8 = new ArrayList(Math.min(var4, 1024));

               for(int var6 = 0; var6 < var4; ++var6) {
                  var8.add(var1.readObject());
               }

               this.parameters = var8.toArray(new Object[var8.size()]);
            }

            if (this.resourceBundleName != null) {
               try {
                  ResourceBundle var9 = ResourceBundle.getBundle(this.resourceBundleName, Locale.getDefault(), ClassLoader.getSystemClassLoader());
                  this.resourceBundle = var9;
               } catch (MissingResourceException var7) {
                  this.resourceBundle = null;
               }
            }

            this.needToInferCaller = false;
         }
      }
   }

   private void inferCaller() {
      this.needToInferCaller = false;
      JavaLangAccess var1 = SharedSecrets.getJavaLangAccess();
      Throwable var2 = new Throwable();
      int var3 = var1.getStackTraceDepth(var2);
      boolean var4 = true;

      for(int var5 = 0; var5 < var3; ++var5) {
         StackTraceElement var6 = var1.getStackTraceElement(var2, var5);
         String var7 = var6.getClassName();
         boolean var8 = this.isLoggerImplFrame(var7);
         if (var4) {
            if (var8) {
               var4 = false;
            }
         } else if (!var8 && !var7.startsWith("java.lang.reflect.") && !var7.startsWith("sun.reflect.")) {
            this.setSourceClassName(var7);
            this.setSourceMethodName(var6.getMethodName());
            return;
         }
      }

   }

   private boolean isLoggerImplFrame(String var1) {
      return var1.equals("java.util.logging.Logger") || var1.startsWith("java.util.logging.LoggingProxyImpl") || var1.startsWith("sun.util.logging.");
   }
}
