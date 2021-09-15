package sun.java2d.xr;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.font.XRTextRenderer;
import sun.java2d.SunGraphics2D;
import sun.java2d.jules.TrapezoidList;
import sun.java2d.loops.XORComposite;

public class XRCompositeManager {
   private static boolean enableGradCache = true;
   private static XRCompositeManager instance;
   private static final int SOLID = 0;
   private static final int TEXTURE = 1;
   private static final int GRADIENT = 2;
   int srcType;
   XRSolidSrcPict solidSrc32;
   XRSurfaceData texture;
   XRSurfaceData gradient;
   int alphaMask = 0;
   XRColor solidColor = new XRColor();
   float extraAlpha = 1.0F;
   byte compRule = 3;
   XRColor alphaColor = new XRColor();
   XRSurfaceData solidSrcPict;
   int alphaMaskPict;
   int gradCachePixmap;
   int gradCachePicture;
   boolean xorEnabled = false;
   int validatedPixel = 0;
   Composite validatedComp;
   Paint validatedPaint;
   float validatedExtraAlpha = 1.0F;
   XRBackend con = new XRBackendNative();
   MaskTileManager maskBuffer;
   XRTextRenderer textRenderer;
   XRMaskImage maskImage;

   public static synchronized XRCompositeManager getInstance(XRSurfaceData var0) {
      if (instance == null) {
         instance = new XRCompositeManager(var0);
      }

      return instance;
   }

   private XRCompositeManager(XRSurfaceData var1) {
      String var2 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty("sun.java2d.xrgradcache");
         }
      });
      enableGradCache = var2 == null || !var2.equalsIgnoreCase("false") && !var2.equalsIgnoreCase("f");
      XRPaints.register(this);
      this.initResources(var1);
      this.maskBuffer = new MaskTileManager(this, var1.getXid());
      this.textRenderer = new XRTextRenderer(this);
      this.maskImage = new XRMaskImage(this, var1.getXid());
   }

   public void initResources(XRSurfaceData var1) {
      int var2 = var1.getXid();
      this.solidSrc32 = new XRSolidSrcPict(this.con, var2);
      this.setForeground(0);
      int var3 = this.con.createPixmap(var2, 8, 1, 1);
      this.alphaMaskPict = this.con.createPicture(var3, 2);
      this.con.setPictureRepeat(this.alphaMaskPict, 1);
      this.con.renderRectangle(this.alphaMaskPict, (byte)0, XRColor.NO_ALPHA, 0, 0, 1, 1);
      if (enableGradCache) {
         this.gradCachePixmap = this.con.createPixmap(var2, 32, 256, 256);
         this.gradCachePicture = this.con.createPicture(this.gradCachePixmap, 0);
      }

   }

   public void setForeground(int var1) {
      this.solidColor.setColorValues(var1, true);
   }

   public void setGradientPaint(XRSurfaceData var1) {
      if (this.gradient != null) {
         this.con.freePicture(this.gradient.picture);
      }

      this.gradient = var1;
      this.srcType = 2;
   }

   public void setTexturePaint(XRSurfaceData var1) {
      this.texture = var1;
      this.srcType = 1;
   }

   public void XRResetPaint() {
      this.srcType = 0;
   }

   public void validateCompositeState(Composite var1, AffineTransform var2, Paint var3, SunGraphics2D var4) {
      boolean var5 = var3 != this.validatedPaint || var3 == null;
      if (var1 != this.validatedComp) {
         if (var1 != null) {
            this.setComposite((Composite)var1);
         } else {
            var1 = AlphaComposite.getInstance(3);
            this.setComposite((Composite)var1);
         }

         var5 = true;
         this.validatedComp = (Composite)var1;
      }

      if (var4 != null && (this.validatedPixel != var4.pixel || var5)) {
         this.validatedPixel = var4.pixel;
         this.setForeground(this.validatedPixel);
      }

      if (var5) {
         if (var3 != null && var4 != null && var4.paintState >= 2) {
            XRPaints.setPaint(var4, var3);
         } else {
            this.XRResetPaint();
         }

         this.validatedPaint = var3;
      }

      if (this.srcType != 0) {
         AffineTransform var6 = (AffineTransform)var2.clone();

         try {
            var6.invert();
         } catch (NoninvertibleTransformException var8) {
            var6.setToIdentity();
         }

         this.getCurrentSource().validateAsSource(var6, -1, XRUtils.ATransOpToXRQuality(var4.interpolationType));
      }

   }

   private void setComposite(Composite var1) {
      if (var1 instanceof AlphaComposite) {
         AlphaComposite var2 = (AlphaComposite)var1;
         this.validatedExtraAlpha = var2.getAlpha();
         this.compRule = XRUtils.j2dAlphaCompToXR(var2.getRule());
         this.extraAlpha = this.validatedExtraAlpha;
         if (this.extraAlpha == 1.0F) {
            this.alphaMask = 0;
            this.alphaColor.alpha = XRColor.FULL_ALPHA.alpha;
         } else {
            this.alphaColor.alpha = XRColor.byteToXRColorValue((int)(this.extraAlpha * 255.0F));
            this.alphaMask = this.alphaMaskPict;
            this.con.renderRectangle(this.alphaMaskPict, (byte)1, this.alphaColor, 0, 0, 1, 1);
         }

         this.xorEnabled = false;
      } else {
         if (!(var1 instanceof XORComposite)) {
            throw new InternalError("Composite accaleration not implemented for: " + var1.getClass().getName());
         }

         this.xorEnabled = true;
      }

   }

   public boolean maskRequired() {
      return !this.xorEnabled && (this.srcType != 0 || this.srcType == 0 && this.solidColor.alpha != 65535 || this.extraAlpha != 1.0F);
   }

   public void XRComposite(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11) {
      int var12 = var1 == 0 ? this.getCurrentSource().picture : var1;
      int var13 = var4;
      int var14 = var5;
      if (enableGradCache && this.gradient != null && var12 == this.gradient.picture) {
         this.con.renderComposite((byte)1, this.gradient.picture, 0, this.gradCachePicture, var4, var5, 0, 0, 0, 0, var10, var11);
         var13 = 0;
         var14 = 0;
         var12 = this.gradCachePicture;
      }

      this.con.renderComposite(this.compRule, var12, var2, var3, var13, var14, var6, var7, var8, var9, var10, var11);
   }

   public void XRCompositeTraps(int var1, int var2, int var3, TrapezoidList var4) {
      boolean var5 = false;
      boolean var6 = false;
      int var7;
      int var8;
      if (var4.getP1YLeft(0) < var4.getP2YLeft(0)) {
         var7 = var4.getP1XLeft(0);
         var8 = var4.getP1YLeft(0);
      } else {
         var7 = var4.getP2XLeft(0);
         var8 = var4.getP2YLeft(0);
      }

      var7 = (int)Math.floor(XRUtils.XFixedToDouble(var7));
      var8 = (int)Math.floor(XRUtils.XFixedToDouble(var8));
      this.con.renderCompositeTrapezoids(this.compRule, this.getCurrentSource().picture, 2, var1, var7, var8, var4);
   }

   public void XRRenderRectangles(XRSurfaceData var1, GrowableRectArray var2) {
      if (this.xorEnabled) {
         this.con.GCRectangles(var1.getXid(), var1.getGC(), var2);
      } else if (var2.getSize() == 1) {
         this.con.renderRectangle(var1.getPicture(), this.compRule, this.solidColor, var2.getX(0), var2.getY(0), var2.getWidth(0), var2.getHeight(0));
      } else {
         this.con.renderRectangles(var1.getPicture(), this.compRule, this.solidColor, var2);
      }

   }

   public void XRCompositeRectangles(XRSurfaceData var1, GrowableRectArray var2) {
      int var3 = this.getCurrentSource().picture;

      for(int var4 = 0; var4 < var2.getSize(); ++var4) {
         int var5 = var2.getX(var4);
         int var6 = var2.getY(var4);
         int var7 = var2.getWidth(var4);
         int var8 = var2.getHeight(var4);
         this.con.renderComposite(this.compRule, var3, 0, var1.picture, var5, var6, 0, 0, var5, var6, var7, var8);
      }

   }

   protected XRSurfaceData getCurrentSource() {
      switch(this.srcType) {
      case 0:
         return this.solidSrc32.prepareSrcPict(this.validatedPixel);
      case 1:
         return this.texture;
      case 2:
         return this.gradient;
      default:
         return null;
      }
   }

   public void compositeBlit(XRSurfaceData var1, XRSurfaceData var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      this.con.renderComposite(this.compRule, var1.picture, this.alphaMask, var2.picture, var3, var4, 0, 0, var5, var6, var7, var8);
   }

   public void compositeText(XRSurfaceData var1, int var2, int var3, int var4, int var5, GrowableEltArray var6) {
      byte var7 = this.compRule != 1 ? this.compRule : 3;
      this.con.XRenderCompositeText(var7, this.getCurrentSource().picture, var1.picture, var5, var2, var3, 0, 0, var4, var6);
   }

   public XRColor getMaskColor() {
      return !this.isTexturePaintActive() ? XRColor.FULL_ALPHA : this.getAlphaColor();
   }

   public int getExtraAlphaMask() {
      return this.alphaMask;
   }

   public boolean isTexturePaintActive() {
      return this.srcType == 1;
   }

   public boolean isSolidPaintActive() {
      return this.srcType == 0;
   }

   public XRColor getAlphaColor() {
      return this.alphaColor;
   }

   public XRBackend getBackend() {
      return this.con;
   }

   public float getExtraAlpha() {
      return this.validatedExtraAlpha;
   }

   public byte getCompRule() {
      return this.compRule;
   }

   public XRTextRenderer getTextRenderer() {
      return this.textRenderer;
   }

   public MaskTileManager getMaskBuffer() {
      return this.maskBuffer;
   }

   public XRMaskImage getMaskImage() {
      return this.maskImage;
   }
}
