package apple.laf;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public final class JRSUIControl {
   private static final int INCOHERENT = 2;
   private static final int NOT_INIT = 1;
   private static final int SUCCESS = 0;
   private static final int NULL_PTR = -1;
   private static final int NULL_CG_REF = -2;
   private static int nativeJRSInitialized = 1;
   private static final int NIO_BUFFER_SIZE = 128;
   private static final ThreadLocal<JRSUIControl.ThreadLocalByteBuffer> threadLocal = new ThreadLocal();
   private final HashMap<JRSUIConstants.Key, JRSUIConstants.DoubleValue> nativeMap;
   private final HashMap<JRSUIConstants.Key, JRSUIConstants.DoubleValue> changes;
   private long cfDictionaryPtr;
   private long priorEncodedProperties;
   private long currentEncodedProperties;
   private final boolean flipped;

   private static native int initNativeJRSUI();

   private static native long getPtrOfBuffer(ByteBuffer var0);

   private static native long getCFDictionary(boolean var0);

   private static native void disposeCFDictionary(long var0);

   private static native int syncChanges(long var0, long var2);

   private static native int paintToCGContext(long var0, long var2, long var4, long var6, double var8, double var10, double var12, double var14);

   private static native int paintChangesToCGContext(long var0, long var2, long var4, long var6, double var8, double var10, double var12, double var14, long var16);

   private static native int paintImage(int[] var0, int var1, int var2, long var3, long var5, long var7, double var9, double var11, double var13, double var15);

   private static native int paintChangesImage(int[] var0, int var1, int var2, long var3, long var5, long var7, double var9, double var11, double var13, double var15, long var17);

   private static native int getNativeHitPart(long var0, long var2, long var4, double var6, double var8, double var10, double var12, double var14, double var16);

   private static native void getNativePartBounds(double[] var0, long var1, long var3, long var5, double var7, double var9, double var11, double var13, int var15);

   private static native double getNativeScrollBarOffsetChange(long var0, long var2, long var4, double var6, double var8, double var10, double var12, int var14, int var15, int var16);

   public static void initJRSUI() {
      if (nativeJRSInitialized != 0) {
         nativeJRSInitialized = initNativeJRSUI();
         if (nativeJRSInitialized != 0) {
            throw new RuntimeException("JRSUI could not be initialized (" + nativeJRSInitialized + ").");
         }
      }
   }

   private static JRSUIControl.ThreadLocalByteBuffer getThreadLocalBuffer() {
      JRSUIControl.ThreadLocalByteBuffer var0 = (JRSUIControl.ThreadLocalByteBuffer)threadLocal.get();
      if (var0 != null) {
         return var0;
      } else {
         var0 = new JRSUIControl.ThreadLocalByteBuffer();
         threadLocal.set(var0);
         return var0;
      }
   }

   public JRSUIControl(boolean var1) {
      this.flipped = var1;
      this.cfDictionaryPtr = getCFDictionary(var1);
      if (this.cfDictionaryPtr == 0L) {
         throw new RuntimeException("Unable to create native representation");
      } else {
         this.nativeMap = new HashMap();
         this.changes = new HashMap();
      }
   }

   JRSUIControl(JRSUIControl var1) {
      this.flipped = var1.flipped;
      this.cfDictionaryPtr = getCFDictionary(this.flipped);
      if (this.cfDictionaryPtr == 0L) {
         throw new RuntimeException("Unable to create native representation");
      } else {
         this.nativeMap = new HashMap();
         this.changes = new HashMap(var1.nativeMap);
         this.changes.putAll(var1.changes);
      }
   }

   protected final synchronized void finalize() throws Throwable {
      if (this.cfDictionaryPtr != 0L) {
         disposeCFDictionary(this.cfDictionaryPtr);
         this.cfDictionaryPtr = 0L;
      }
   }

   private JRSUIControl.BufferState loadBufferWithChanges(JRSUIControl.ThreadLocalByteBuffer var1) {
      ByteBuffer var2 = var1.buffer;
      var2.rewind();
      Iterator var3 = (new HashSet(this.changes.keySet())).iterator();

      while(var3.hasNext()) {
         JRSUIConstants.Key var4 = (JRSUIConstants.Key)var3.next();
         int var5 = var2.position();
         JRSUIConstants.DoubleValue var6 = (JRSUIConstants.DoubleValue)this.changes.get(var4);

         try {
            var2.putLong(var4.getConstantPtr());
            var2.put(var6.getTypeCode());
            var6.putValueInBuffer(var2);
         } catch (BufferOverflowException var8) {
            return this.handleBufferOverflow(var2, var5);
         } catch (RuntimeException var9) {
            System.err.println((Object)this);
            throw var9;
         }

         if (var2.position() >= 120) {
            return this.handleBufferOverflow(var2, var5);
         }

         this.changes.remove(var4);
         this.nativeMap.put(var4, var6);
      }

      var2.putLong(0L);
      return JRSUIControl.BufferState.ALL_CHANGES_IN_BUFFER;
   }

   private JRSUIControl.BufferState handleBufferOverflow(ByteBuffer var1, int var2) {
      if (var2 == 0) {
         var1.putLong(0, 0L);
         return JRSUIControl.BufferState.CHANGE_WONT_FIT_IN_BUFFER;
      } else {
         var1.putLong(var2, 0L);
         return JRSUIControl.BufferState.SOME_CHANGES_IN_BUFFER;
      }
   }

   private synchronized void set(JRSUIConstants.Key var1, JRSUIConstants.DoubleValue var2) {
      JRSUIConstants.DoubleValue var3 = (JRSUIConstants.DoubleValue)this.nativeMap.get(var1);
      if (var3 != null && var3.equals(var2)) {
         this.changes.remove(var1);
      } else {
         this.changes.put(var1, var2);
      }
   }

   public void set(JRSUIState var1) {
      var1.apply(this);
   }

   void setEncodedState(long var1) {
      this.currentEncodedProperties = var1;
   }

   void set(JRSUIConstants.Key var1, double var2) {
      this.set(var1, new JRSUIConstants.DoubleValue(var2));
   }

   public void paint(int[] var1, int var2, int var3, double var4, double var6, double var8, double var10) {
      this.paintImage(var1, var2, var3, var4, var6, var8, var10);
      this.priorEncodedProperties = this.currentEncodedProperties;
   }

   private synchronized int paintImage(int[] var1, int var2, int var3, double var4, double var6, double var8, double var10) {
      if (this.changes.isEmpty()) {
         return paintImage(var1, var2, var3, this.cfDictionaryPtr, this.priorEncodedProperties, this.currentEncodedProperties, var4, var6, var8, var10);
      } else {
         JRSUIControl.ThreadLocalByteBuffer var12 = getThreadLocalBuffer();
         JRSUIControl.BufferState var13 = this.loadBufferWithChanges(var12);
         if (var13 == JRSUIControl.BufferState.ALL_CHANGES_IN_BUFFER) {
            return paintChangesImage(var1, var2, var3, this.cfDictionaryPtr, this.priorEncodedProperties, this.currentEncodedProperties, var4, var6, var8, var10, var12.ptr);
         } else {
            while(var13 == JRSUIControl.BufferState.SOME_CHANGES_IN_BUFFER) {
               int var14 = syncChanges(this.cfDictionaryPtr, var12.ptr);
               if (var14 != 0) {
                  throw new RuntimeException("JRSUI failed to sync changes into the native buffer: " + this);
               }

               var13 = this.loadBufferWithChanges(var12);
            }

            if (var13 == JRSUIControl.BufferState.CHANGE_WONT_FIT_IN_BUFFER) {
               throw new RuntimeException("JRSUI failed to sync changes to the native buffer, because some change was too big: " + this);
            } else {
               return paintChangesImage(var1, var2, var3, this.cfDictionaryPtr, this.priorEncodedProperties, this.currentEncodedProperties, var4, var6, var8, var10, var12.ptr);
            }
         }
      }
   }

   public void paint(long var1, double var3, double var5, double var7, double var9) {
      this.paintToCGContext(var1, var3, var5, var7, var9);
      this.priorEncodedProperties = this.currentEncodedProperties;
   }

   private synchronized int paintToCGContext(long var1, double var3, double var5, double var7, double var9) {
      if (this.changes.isEmpty()) {
         return paintToCGContext(var1, this.cfDictionaryPtr, this.priorEncodedProperties, this.currentEncodedProperties, var3, var5, var7, var9);
      } else {
         JRSUIControl.ThreadLocalByteBuffer var11 = getThreadLocalBuffer();
         JRSUIControl.BufferState var12 = this.loadBufferWithChanges(var11);
         if (var12 == JRSUIControl.BufferState.ALL_CHANGES_IN_BUFFER) {
            return paintChangesToCGContext(var1, this.cfDictionaryPtr, this.priorEncodedProperties, this.currentEncodedProperties, var3, var5, var7, var9, var11.ptr);
         } else {
            while(var12 == JRSUIControl.BufferState.SOME_CHANGES_IN_BUFFER) {
               int var13 = syncChanges(this.cfDictionaryPtr, var11.ptr);
               if (var13 != 0) {
                  throw new RuntimeException("JRSUI failed to sync changes into the native buffer: " + this);
               }

               var12 = this.loadBufferWithChanges(var11);
            }

            if (var12 == JRSUIControl.BufferState.CHANGE_WONT_FIT_IN_BUFFER) {
               throw new RuntimeException("JRSUI failed to sync changes to the native buffer, because some change was too big: " + this);
            } else {
               return paintChangesToCGContext(var1, this.cfDictionaryPtr, this.priorEncodedProperties, this.currentEncodedProperties, var3, var5, var7, var9, var11.ptr);
            }
         }
      }
   }

   JRSUIConstants.Hit getHitForPoint(double var1, double var3, double var5, double var7, double var9, double var11) {
      this.sync();
      JRSUIConstants.Hit var13 = JRSUIConstants.getHit(getNativeHitPart(this.cfDictionaryPtr, this.priorEncodedProperties, this.currentEncodedProperties, var1, var3, var5, var7, var9, 2.0D * var3 + var7 - var11));
      this.priorEncodedProperties = this.currentEncodedProperties;
      return var13;
   }

   void getPartBounds(double[] var1, double var2, double var4, double var6, double var8, int var10) {
      if (var1 == null) {
         throw new NullPointerException("Cannot load null rect");
      } else if (var1.length != 4) {
         throw new IllegalArgumentException("Rect must have four elements");
      } else {
         this.sync();
         getNativePartBounds(var1, this.cfDictionaryPtr, this.priorEncodedProperties, this.currentEncodedProperties, var2, var4, var6, var8, var10);
         this.priorEncodedProperties = this.currentEncodedProperties;
      }
   }

   double getScrollBarOffsetChange(double var1, double var3, double var5, double var7, int var9, int var10, int var11) {
      this.sync();
      double var12 = getNativeScrollBarOffsetChange(this.cfDictionaryPtr, this.priorEncodedProperties, this.currentEncodedProperties, var1, var3, var5, var7, var9, var10, var11);
      this.priorEncodedProperties = this.currentEncodedProperties;
      return var12;
   }

   private void sync() {
      if (!this.changes.isEmpty()) {
         JRSUIControl.ThreadLocalByteBuffer var1 = getThreadLocalBuffer();
         JRSUIControl.BufferState var2 = this.loadBufferWithChanges(var1);
         int var3;
         if (var2 == JRSUIControl.BufferState.ALL_CHANGES_IN_BUFFER) {
            var3 = syncChanges(this.cfDictionaryPtr, var1.ptr);
            if (var3 != 0) {
               throw new RuntimeException("JRSUI failed to sync changes into the native buffer: " + this);
            }
         } else {
            while(var2 == JRSUIControl.BufferState.SOME_CHANGES_IN_BUFFER) {
               var3 = syncChanges(this.cfDictionaryPtr, var1.ptr);
               if (var3 != 0) {
                  throw new RuntimeException("JRSUI failed to sync changes into the native buffer: " + this);
               }

               var2 = this.loadBufferWithChanges(var1);
            }

            if (var2 == JRSUIControl.BufferState.CHANGE_WONT_FIT_IN_BUFFER) {
               throw new RuntimeException("JRSUI failed to sync changes to the native buffer, because some change was too big: " + this);
            }
         }
      }
   }

   public int hashCode() {
      int var1 = (int)(this.currentEncodedProperties ^ this.currentEncodedProperties >>> 32);
      var1 ^= this.nativeMap.hashCode();
      var1 ^= this.changes.hashCode();
      return var1;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof JRSUIControl)) {
         return false;
      } else {
         JRSUIControl var2 = (JRSUIControl)var1;
         if (this.currentEncodedProperties != var2.currentEncodedProperties) {
            return false;
         } else if (!this.nativeMap.equals(var2.nativeMap)) {
            return false;
         } else {
            return this.changes.equals(var2.changes);
         }
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("JRSUIControl[inNative:");
      var1.append(Arrays.toString(this.nativeMap.entrySet().toArray()));
      var1.append(", changes:");
      var1.append(Arrays.toString(this.changes.entrySet().toArray()));
      var1.append("]");
      return var1.toString();
   }

   static enum BufferState {
      NO_CHANGE,
      ALL_CHANGES_IN_BUFFER,
      SOME_CHANGES_IN_BUFFER,
      CHANGE_WONT_FIT_IN_BUFFER;
   }

   private static class ThreadLocalByteBuffer {
      final ByteBuffer buffer = ByteBuffer.allocateDirect(128);
      final long ptr;

      public ThreadLocalByteBuffer() {
         this.buffer.order(ByteOrder.nativeOrder());
         this.ptr = JRSUIControl.getPtrOfBuffer(this.buffer);
      }
   }
}
