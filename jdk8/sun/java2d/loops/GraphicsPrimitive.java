package sun.java2d.loops;

import java.awt.AlphaComposite;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;
import sun.security.action.GetPropertyAction;

public abstract class GraphicsPrimitive {
   private String methodSignature;
   private int uniqueID;
   private static int unusedPrimID = 1;
   private SurfaceType sourceType;
   private CompositeType compositeType;
   private SurfaceType destType;
   private long pNativePrim;
   static HashMap traceMap;
   public static int traceflags;
   public static String tracefile;
   public static PrintStream traceout;
   public static final int TRACELOG = 1;
   public static final int TRACETIMESTAMP = 2;
   public static final int TRACECOUNTS = 4;
   private String cachedname;

   public static final synchronized int makePrimTypeID() {
      if (unusedPrimID > 255) {
         throw new InternalError("primitive id overflow");
      } else {
         return unusedPrimID++;
      }
   }

   public static final synchronized int makeUniqueID(int var0, SurfaceType var1, CompositeType var2, SurfaceType var3) {
      return var0 << 24 | var3.getUniqueID() << 16 | var2.getUniqueID() << 8 | var1.getUniqueID();
   }

   protected GraphicsPrimitive(String var1, int var2, SurfaceType var3, CompositeType var4, SurfaceType var5) {
      this.methodSignature = var1;
      this.sourceType = var3;
      this.compositeType = var4;
      this.destType = var5;
      if (var3 != null && var4 != null && var5 != null) {
         this.uniqueID = makeUniqueID(var2, var3, var4, var5);
      } else {
         this.uniqueID = var2 << 24;
      }

   }

   protected GraphicsPrimitive(long var1, String var3, int var4, SurfaceType var5, CompositeType var6, SurfaceType var7) {
      this.pNativePrim = var1;
      this.methodSignature = var3;
      this.sourceType = var5;
      this.compositeType = var6;
      this.destType = var7;
      if (var5 != null && var6 != null && var7 != null) {
         this.uniqueID = makeUniqueID(var4, var5, var6, var7);
      } else {
         this.uniqueID = var4 << 24;
      }

   }

   public final int getUniqueID() {
      return this.uniqueID;
   }

   public final String getSignature() {
      return this.methodSignature;
   }

   public final int getPrimTypeID() {
      return this.uniqueID >>> 24;
   }

   public final long getNativePrim() {
      return this.pNativePrim;
   }

   public final SurfaceType getSourceType() {
      return this.sourceType;
   }

   public final CompositeType getCompositeType() {
      return this.compositeType;
   }

   public final SurfaceType getDestType() {
      return this.destType;
   }

   public final boolean satisfies(String var1, SurfaceType var2, CompositeType var3, SurfaceType var4) {
      if (var1 != this.methodSignature) {
         return false;
      } else {
         while(var2 != null) {
            if (var2.equals(this.sourceType)) {
               while(var3 != null) {
                  if (var3.equals(this.compositeType)) {
                     while(var4 != null) {
                        if (var4.equals(this.destType)) {
                           return true;
                        }

                        var4 = var4.getSuperType();
                     }

                     return false;
                  }

                  var3 = var3.getSuperType();
               }

               return false;
            }

            var2 = var2.getSuperType();
         }

         return false;
      }
   }

   final boolean satisfiesSameAs(GraphicsPrimitive var1) {
      return this.methodSignature == var1.methodSignature && this.sourceType.equals(var1.sourceType) && this.compositeType.equals(var1.compositeType) && this.destType.equals(var1.destType);
   }

   public abstract GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3);

   public abstract GraphicsPrimitive traceWrap();

   public static boolean tracingEnabled() {
      return traceflags != 0;
   }

   private static PrintStream getTraceOutputFile() {
      if (traceout == null) {
         if (tracefile != null) {
            FileOutputStream var0 = (FileOutputStream)AccessController.doPrivileged(new PrivilegedAction<FileOutputStream>() {
               public FileOutputStream run() {
                  try {
                     return new FileOutputStream(GraphicsPrimitive.tracefile);
                  } catch (FileNotFoundException var2) {
                     return null;
                  }
               }
            });
            if (var0 != null) {
               traceout = new PrintStream(var0);
            } else {
               traceout = System.err;
            }
         } else {
            traceout = System.err;
         }
      }

      return traceout;
   }

   public static synchronized void tracePrimitive(Object var0) {
      if ((traceflags & 4) != 0) {
         if (traceMap == null) {
            traceMap = new HashMap();
            GraphicsPrimitive.TraceReporter.setShutdownHook();
         }

         Object var1 = traceMap.get(var0);
         if (var1 == null) {
            var1 = new int[1];
            traceMap.put(var0, var1);
         }

         int var10002 = ((int[])((int[])var1))[0]++;
      }

      if ((traceflags & 1) != 0) {
         PrintStream var2 = getTraceOutputFile();
         if ((traceflags & 2) != 0) {
            var2.print(System.currentTimeMillis() + ": ");
         }

         var2.println(var0);
      }

   }

   protected void setupGeneralBinaryOp(GraphicsPrimitive.GeneralBinaryOp var1) {
      int var2 = var1.getPrimTypeID();
      String var3 = var1.getSignature();
      SurfaceType var4 = var1.getSourceType();
      CompositeType var5 = var1.getCompositeType();
      SurfaceType var6 = var1.getDestType();
      Blit var7 = createConverter(var4, SurfaceType.IntArgb);
      GraphicsPrimitive var10 = GraphicsPrimitiveMgr.locatePrim(var2, SurfaceType.IntArgb, var5, var6);
      Blit var8;
      Blit var9;
      if (var10 != null) {
         var8 = null;
         var9 = null;
      } else {
         var10 = getGeneralOp(var2, var5);
         if (var10 == null) {
            throw new InternalError("Cannot construct general op for " + var3 + " " + var5);
         }

         var8 = createConverter(var6, SurfaceType.IntArgb);
         var9 = createConverter(SurfaceType.IntArgb, var6);
      }

      var1.setPrimitives(var7, var8, var10, var9);
   }

   protected void setupGeneralUnaryOp(GraphicsPrimitive.GeneralUnaryOp var1) {
      int var2 = var1.getPrimTypeID();
      String var3 = var1.getSignature();
      CompositeType var4 = var1.getCompositeType();
      SurfaceType var5 = var1.getDestType();
      Blit var6 = createConverter(var5, SurfaceType.IntArgb);
      GraphicsPrimitive var7 = getGeneralOp(var2, var4);
      Blit var8 = createConverter(SurfaceType.IntArgb, var5);
      if (var6 != null && var7 != null && var8 != null) {
         var1.setPrimitives(var6, var7, var8);
      } else {
         throw new InternalError("Cannot construct binary op for " + var4 + " " + var5);
      }
   }

   protected static Blit createConverter(SurfaceType var0, SurfaceType var1) {
      if (var0.equals(var1)) {
         return null;
      } else {
         Blit var2 = Blit.getFromCache(var0, CompositeType.SrcNoEa, var1);
         if (var2 == null) {
            throw new InternalError("Cannot construct converter for " + var0 + "=>" + var1);
         } else {
            return var2;
         }
      }
   }

   protected static SurfaceData convertFrom(Blit var0, SurfaceData var1, int var2, int var3, int var4, int var5, SurfaceData var6) {
      return convertFrom(var0, var1, var2, var3, var4, var5, var6, 2);
   }

   protected static SurfaceData convertFrom(Blit var0, SurfaceData var1, int var2, int var3, int var4, int var5, SurfaceData var6, int var7) {
      if (var6 != null) {
         Rectangle var8 = var6.getBounds();
         if (var4 > var8.width || var5 > var8.height) {
            var6 = null;
         }
      }

      if (var6 == null) {
         BufferedImage var9 = new BufferedImage(var4, var5, var7);
         var6 = BufImgSurfaceData.createData(var9);
      }

      var0.Blit(var1, var6, AlphaComposite.Src, (Region)null, var2, var3, 0, 0, var4, var5);
      return var6;
   }

   protected static void convertTo(Blit var0, SurfaceData var1, SurfaceData var2, Region var3, int var4, int var5, int var6, int var7) {
      if (var0 != null) {
         var0.Blit(var1, var2, AlphaComposite.Src, var3, 0, 0, var4, var5, var6, var7);
      }

   }

   protected static GraphicsPrimitive getGeneralOp(int var0, CompositeType var1) {
      return GraphicsPrimitiveMgr.locatePrim(var0, SurfaceType.IntArgb, var1, SurfaceType.IntArgb);
   }

   public static String simplename(Field[] var0, Object var1) {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         Field var3 = var0[var2];

         try {
            if (var1 == var3.get((Object)null)) {
               return var3.getName();
            }
         } catch (Exception var5) {
         }
      }

      return "\"" + var1.toString() + "\"";
   }

   public static String simplename(SurfaceType var0) {
      return simplename(SurfaceType.class.getDeclaredFields(), var0);
   }

   public static String simplename(CompositeType var0) {
      return simplename(CompositeType.class.getDeclaredFields(), var0);
   }

   public String toString() {
      if (this.cachedname == null) {
         String var1 = this.methodSignature;
         int var2 = var1.indexOf(40);
         if (var2 >= 0) {
            var1 = var1.substring(0, var2);
         }

         this.cachedname = this.getClass().getName() + "::" + var1 + "(" + simplename(this.sourceType) + ", " + simplename(this.compositeType) + ", " + simplename(this.destType) + ")";
      }

      return this.cachedname;
   }

   static {
      GetPropertyAction var0 = new GetPropertyAction("sun.java2d.trace");
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)var0);
      if (var1 != null) {
         boolean var2 = false;
         int var3 = 0;
         StringTokenizer var4 = new StringTokenizer(var1, ",");

         while(var4.hasMoreTokens()) {
            String var5 = var4.nextToken();
            if (var5.equalsIgnoreCase("count")) {
               var3 |= 4;
            } else if (var5.equalsIgnoreCase("log")) {
               var3 |= 1;
            } else if (var5.equalsIgnoreCase("timestamp")) {
               var3 |= 2;
            } else if (var5.equalsIgnoreCase("verbose")) {
               var2 = true;
            } else if (var5.regionMatches(true, 0, "out:", 0, 4)) {
               tracefile = var5.substring(4);
            } else {
               if (!var5.equalsIgnoreCase("help")) {
                  System.err.println("unrecognized token: " + var5);
               }

               System.err.println("usage: -Dsun.java2d.trace=[log[,timestamp]],[count],[out:<filename>],[help],[verbose]");
            }
         }

         if (var2) {
            System.err.print("GraphicsPrimitive logging ");
            if ((var3 & 1) != 0) {
               System.err.println("enabled");
               System.err.print("GraphicsPrimitive timetamps ");
               if ((var3 & 2) != 0) {
                  System.err.println("enabled");
               } else {
                  System.err.println("disabled");
               }
            } else {
               System.err.println("[and timestamps] disabled");
            }

            System.err.print("GraphicsPrimitive invocation counts ");
            if ((var3 & 4) != 0) {
               System.err.println("enabled");
            } else {
               System.err.println("disabled");
            }

            System.err.print("GraphicsPrimitive trace output to ");
            if (tracefile == null) {
               System.err.println("System.err");
            } else {
               System.err.println("file '" + tracefile + "'");
            }
         }

         traceflags = var3;
      }

   }

   public static class TraceReporter extends Thread {
      public static void setShutdownHook() {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               GraphicsPrimitive.TraceReporter var1 = new GraphicsPrimitive.TraceReporter();
               var1.setContextClassLoader((ClassLoader)null);
               Runtime.getRuntime().addShutdownHook(var1);
               return null;
            }
         });
      }

      public void run() {
         PrintStream var1 = GraphicsPrimitive.getTraceOutputFile();
         Iterator var2 = GraphicsPrimitive.traceMap.entrySet().iterator();
         long var3 = 0L;

         int var5;
         int[] var8;
         for(var5 = 0; var2.hasNext(); var3 += (long)var8[0]) {
            Map.Entry var6 = (Map.Entry)var2.next();
            Object var7 = var6.getKey();
            var8 = (int[])((int[])var6.getValue());
            if (var8[0] == 1) {
               var1.print("1 call to ");
            } else {
               var1.print(var8[0] + " calls to ");
            }

            var1.println(var7);
            ++var5;
         }

         if (var5 == 0) {
            var1.println("No graphics primitives executed");
         } else if (var5 > 1) {
            var1.println(var3 + " total calls to " + var5 + " different primitives");
         }

      }
   }

   protected interface GeneralUnaryOp {
      void setPrimitives(Blit var1, GraphicsPrimitive var2, Blit var3);

      CompositeType getCompositeType();

      SurfaceType getDestType();

      String getSignature();

      int getPrimTypeID();
   }

   protected interface GeneralBinaryOp {
      void setPrimitives(Blit var1, Blit var2, GraphicsPrimitive var3, Blit var4);

      SurfaceType getSourceType();

      CompositeType getCompositeType();

      SurfaceType getDestType();

      String getSignature();

      int getPrimTypeID();
   }
}
