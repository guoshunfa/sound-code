package sun.java2d.opengl;

import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.hw.ContextCapabilities;

public class OGLContext extends BufferedContext {
   private final OGLGraphicsConfig config;

   OGLContext(RenderQueue var1, OGLGraphicsConfig var2) {
      super(var1);
      this.config = var2;
   }

   static void setScratchSurface(OGLGraphicsConfig var0) {
      setScratchSurface(var0.getNativeConfigInfo());
   }

   static void setScratchSurface(long var0) {
      currentContext = null;
      OGLRenderQueue var2 = OGLRenderQueue.getInstance();
      RenderBuffer var3 = var2.getBuffer();
      var2.ensureCapacityAndAlignment(12, 4);
      var3.putInt(71);
      var3.putLong(var0);
   }

   static void invalidateCurrentContext() {
      if (currentContext != null) {
         currentContext.invalidateContext();
         currentContext = null;
      }

      OGLRenderQueue var0 = OGLRenderQueue.getInstance();
      var0.ensureCapacity(4);
      var0.getBuffer().putInt(75);
      var0.flushNow();
   }

   public RenderQueue getRenderQueue() {
      return OGLRenderQueue.getInstance();
   }

   static final native String getOGLIdString();

   public void saveState() {
      this.invalidateContext();
      invalidateCurrentContext();
      setScratchSurface(this.config);
      this.rq.ensureCapacity(4);
      this.buf.putInt(78);
      this.rq.flushNow();
   }

   public void restoreState() {
      this.invalidateContext();
      invalidateCurrentContext();
      setScratchSurface(this.config);
      this.rq.ensureCapacity(4);
      this.buf.putInt(79);
      this.rq.flushNow();
   }

   static class OGLContextCaps extends ContextCapabilities {
      static final int CAPS_EXT_FBOBJECT = 12;
      static final int CAPS_STORED_ALPHA = 2;
      static final int CAPS_DOUBLEBUFFERED = 65536;
      static final int CAPS_EXT_LCD_SHADER = 131072;
      static final int CAPS_EXT_BIOP_SHADER = 262144;
      static final int CAPS_EXT_GRAD_SHADER = 524288;
      static final int CAPS_EXT_TEXRECT = 1048576;
      static final int CAPS_EXT_TEXBARRIER = 2097152;

      OGLContextCaps(int var1, String var2) {
         super(var1, var2);
      }

      public String toString() {
         StringBuffer var1 = new StringBuffer(super.toString());
         if ((this.caps & 12) != 0) {
            var1.append("CAPS_EXT_FBOBJECT|");
         }

         if ((this.caps & 2) != 0) {
            var1.append("CAPS_STORED_ALPHA|");
         }

         if ((this.caps & 65536) != 0) {
            var1.append("CAPS_DOUBLEBUFFERED|");
         }

         if ((this.caps & 131072) != 0) {
            var1.append("CAPS_EXT_LCD_SHADER|");
         }

         if ((this.caps & 262144) != 0) {
            var1.append("CAPS_BIOP_SHADER|");
         }

         if ((this.caps & 524288) != 0) {
            var1.append("CAPS_EXT_GRAD_SHADER|");
         }

         if ((this.caps & 1048576) != 0) {
            var1.append("CAPS_EXT_TEXRECT|");
         }

         if ((this.caps & 2097152) != 0) {
            var1.append("CAPS_EXT_TEXBARRIER|");
         }

         return var1.toString();
      }
   }
}
