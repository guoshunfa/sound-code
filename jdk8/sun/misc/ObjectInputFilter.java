package sun.misc;

import java.io.ObjectInputStream;
import java.io.SerializablePermission;
import java.security.AccessController;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import sun.util.logging.PlatformLogger;

@FunctionalInterface
public interface ObjectInputFilter {
   ObjectInputFilter.Status checkInput(ObjectInputFilter.FilterInfo var1);

   public static final class Config {
      private static final Object serialFilterLock = new Object();
      private static final PlatformLogger configLog;
      private static final String SERIAL_FILTER_PROPNAME = "jdk.serialFilter";
      private static final ObjectInputFilter configuredFilter = (ObjectInputFilter)AccessController.doPrivileged(() -> {
         String var0 = System.getProperty("jdk.serialFilter");
         if (var0 == null) {
            var0 = Security.getProperty("jdk.serialFilter");
         }

         if (var0 != null) {
            PlatformLogger var1 = PlatformLogger.getLogger("java.io.serialization");
            var1.info("Creating serialization filter from {0}", var0);

            try {
               return createFilter(var0);
            } catch (RuntimeException var3) {
               var1.warning("Error configuring filter: {0}", (Throwable)var3);
            }
         }

         return null;
      });
      private static ObjectInputFilter serialFilter;

      private Config() {
      }

      static void filterLog(PlatformLogger.Level var0, String var1, Object... var2) {
         if (configLog != null) {
            if (PlatformLogger.Level.INFO.equals(var0)) {
               configLog.info(var1, var2);
            } else if (PlatformLogger.Level.WARNING.equals(var0)) {
               configLog.warning(var1, var2);
            } else {
               configLog.severe(var1, var2);
            }
         }

      }

      public static ObjectInputFilter getObjectInputFilter(ObjectInputStream var0) {
         Objects.requireNonNull(var0, (String)"inputStream");
         return SharedSecrets.getJavaOISAccess().getObjectInputFilter(var0);
      }

      public static void setObjectInputFilter(ObjectInputStream var0, ObjectInputFilter var1) {
         Objects.requireNonNull(var0, (String)"inputStream");
         SharedSecrets.getJavaOISAccess().setObjectInputFilter(var0, var1);
      }

      public static ObjectInputFilter getSerialFilter() {
         synchronized(serialFilterLock) {
            return serialFilter;
         }
      }

      public static void setSerialFilter(ObjectInputFilter var0) {
         Objects.requireNonNull(var0, (String)"filter");
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkPermission(new SerializablePermission("serialFilter"));
         }

         synchronized(serialFilterLock) {
            if (serialFilter != null) {
               throw new IllegalStateException("Serial filter can only be set once");
            } else {
               serialFilter = var0;
            }
         }
      }

      public static ObjectInputFilter createFilter(String var0) {
         Objects.requireNonNull(var0, (String)"pattern");
         return ObjectInputFilter.Config.Global.createFilter(var0, true);
      }

      public static ObjectInputFilter createFilter2(String var0) {
         Objects.requireNonNull(var0, (String)"pattern");
         return ObjectInputFilter.Config.Global.createFilter(var0, false);
      }

      static {
         configLog = configuredFilter != null ? PlatformLogger.getLogger("java.io.serialization") : null;
         serialFilter = configuredFilter;
      }

      static final class Global implements ObjectInputFilter {
         private final String pattern;
         private final List<Function<Class<?>, ObjectInputFilter.Status>> filters;
         private long maxStreamBytes;
         private long maxDepth;
         private long maxReferences;
         private long maxArrayLength;
         private final boolean checkComponentType;

         static ObjectInputFilter createFilter(String var0, boolean var1) {
            ObjectInputFilter.Config.Global var2 = new ObjectInputFilter.Config.Global(var0, var1);
            return var2.isEmpty() ? null : var2;
         }

         private Global(String var1, boolean var2) {
            this.pattern = var1;
            this.checkComponentType = var2;
            this.maxArrayLength = Long.MAX_VALUE;
            this.maxDepth = Long.MAX_VALUE;
            this.maxReferences = Long.MAX_VALUE;
            this.maxStreamBytes = Long.MAX_VALUE;
            String[] var3 = var1.split(";");
            this.filters = new ArrayList(var3.length);

            for(int var4 = 0; var4 < var3.length; ++var4) {
               String var5 = var3[var4];
               int var6 = var5.length();
               if (var6 != 0 && !this.parseLimit(var5)) {
                  boolean var7 = var5.charAt(0) == '!';
                  if (var5.indexOf(47) >= 0) {
                     throw new IllegalArgumentException("invalid character \"/\" in: \"" + var1 + "\"");
                  }

                  String var8;
                  if (var5.endsWith("*")) {
                     if (var5.endsWith(".*")) {
                        var8 = var5.substring(var7 ? 1 : 0, var6 - 1);
                        if (var8.length() < 2) {
                           throw new IllegalArgumentException("package missing in: \"" + var1 + "\"");
                        }

                        if (var7) {
                           this.filters.add((var1x) -> {
                              return matchesPackage(var1x, var8) ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.UNDECIDED;
                           });
                        } else {
                           this.filters.add((var1x) -> {
                              return matchesPackage(var1x, var8) ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.UNDECIDED;
                           });
                        }
                     } else if (var5.endsWith(".**")) {
                        var8 = var5.substring(var7 ? 1 : 0, var6 - 2);
                        if (var8.length() < 2) {
                           throw new IllegalArgumentException("package missing in: \"" + var1 + "\"");
                        }

                        if (var7) {
                           this.filters.add((var1x) -> {
                              return var1x.getName().startsWith(var8) ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.UNDECIDED;
                           });
                        } else {
                           this.filters.add((var1x) -> {
                              return var1x.getName().startsWith(var8) ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.UNDECIDED;
                           });
                        }
                     } else {
                        var8 = var5.substring(var7 ? 1 : 0, var6 - 1);
                        if (var7) {
                           this.filters.add((var1x) -> {
                              return var1x.getName().startsWith(var8) ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.UNDECIDED;
                           });
                        } else {
                           this.filters.add((var1x) -> {
                              return var1x.getName().startsWith(var8) ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.UNDECIDED;
                           });
                        }
                     }
                  } else {
                     var8 = var5.substring(var7 ? 1 : 0);
                     if (var8.isEmpty()) {
                        throw new IllegalArgumentException("class or package missing in: \"" + var1 + "\"");
                     }

                     if (var7) {
                        this.filters.add((var1x) -> {
                           return var1x.getName().equals(var8) ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.UNDECIDED;
                        });
                     } else {
                        this.filters.add((var1x) -> {
                           return var1x.getName().equals(var8) ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.UNDECIDED;
                        });
                     }
                  }
               }
            }

         }

         private boolean isEmpty() {
            return this.filters.isEmpty() && this.maxArrayLength == Long.MAX_VALUE && this.maxDepth == Long.MAX_VALUE && this.maxReferences == Long.MAX_VALUE && this.maxStreamBytes == Long.MAX_VALUE;
         }

         private boolean parseLimit(String var1) {
            int var2 = var1.indexOf(61);
            if (var2 < 0) {
               return false;
            } else {
               String var3 = var1.substring(var2 + 1);
               if (var1.startsWith("maxdepth=")) {
                  this.maxDepth = parseValue(var3);
               } else if (var1.startsWith("maxarray=")) {
                  this.maxArrayLength = parseValue(var3);
               } else if (var1.startsWith("maxrefs=")) {
                  this.maxReferences = parseValue(var3);
               } else {
                  if (!var1.startsWith("maxbytes=")) {
                     throw new IllegalArgumentException("unknown limit: " + var1.substring(0, var2));
                  }

                  this.maxStreamBytes = parseValue(var3);
               }

               return true;
            }
         }

         private static long parseValue(String var0) throws IllegalArgumentException {
            long var1 = Long.parseLong(var0);
            if (var1 < 0L) {
               throw new IllegalArgumentException("negative limit: " + var0);
            } else {
               return var1;
            }
         }

         public ObjectInputFilter.Status checkInput(ObjectInputFilter.FilterInfo var1) {
            if (var1.references() >= 0L && var1.depth() >= 0L && var1.streamBytes() >= 0L && var1.references() <= this.maxReferences && var1.depth() <= this.maxDepth && var1.streamBytes() <= this.maxStreamBytes) {
               Class var2 = var1.serialClass();
               if (var2 == null) {
                  return ObjectInputFilter.Status.UNDECIDED;
               } else {
                  if (var2.isArray()) {
                     if (var1.arrayLength() >= 0L && var1.arrayLength() > this.maxArrayLength) {
                        return ObjectInputFilter.Status.REJECTED;
                     }

                     if (!this.checkComponentType) {
                        return ObjectInputFilter.Status.UNDECIDED;
                     }

                     do {
                        var2 = var2.getComponentType();
                     } while(var2.isArray());
                  }

                  if (var2.isPrimitive()) {
                     return ObjectInputFilter.Status.UNDECIDED;
                  } else {
                     Optional var4 = this.filters.stream().map((var1x) -> {
                        return (ObjectInputFilter.Status)var1x.apply(var2);
                     }).filter((var0) -> {
                        return var0 != ObjectInputFilter.Status.UNDECIDED;
                     }).findFirst();
                     return (ObjectInputFilter.Status)var4.orElse(ObjectInputFilter.Status.UNDECIDED);
                  }
               }
            } else {
               return ObjectInputFilter.Status.REJECTED;
            }
         }

         private static boolean matchesPackage(Class<?> var0, String var1) {
            String var2 = var0.getName();
            return var2.startsWith(var1) && var2.lastIndexOf(46) == var1.length() - 1;
         }

         public String toString() {
            return this.pattern;
         }
      }
   }

   public static enum Status {
      UNDECIDED,
      ALLOWED,
      REJECTED;
   }

   public interface FilterInfo {
      Class<?> serialClass();

      long arrayLength();

      long depth();

      long references();

      long streamBytes();
   }
}
