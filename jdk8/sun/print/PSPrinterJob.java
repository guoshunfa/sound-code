package sun.print;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterIOException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderMalfunctionError;
import java.nio.file.Files;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import javax.print.PrintService;
import javax.print.StreamPrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.DialogTypeSelection;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.Sides;
import sun.awt.CharsetString;
import sun.awt.FontConfiguration;
import sun.awt.PlatformFont;
import sun.awt.SunToolkit;
import sun.font.FontUtilities;

public class PSPrinterJob extends RasterPrinterJob {
   protected static final int FILL_EVEN_ODD = 1;
   protected static final int FILL_WINDING = 2;
   private static final int MAX_PSSTR = 65535;
   private static final int RED_MASK = 16711680;
   private static final int GREEN_MASK = 65280;
   private static final int BLUE_MASK = 255;
   private static final int RED_SHIFT = 16;
   private static final int GREEN_SHIFT = 8;
   private static final int BLUE_SHIFT = 0;
   private static final int LOWNIBBLE_MASK = 15;
   private static final int HINIBBLE_MASK = 240;
   private static final int HINIBBLE_SHIFT = 4;
   private static final byte[] hexDigits = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};
   private static final int PS_XRES = 300;
   private static final int PS_YRES = 300;
   private static final String ADOBE_PS_STR = "%!PS-Adobe-3.0";
   private static final String EOF_COMMENT = "%%EOF";
   private static final String PAGE_COMMENT = "%%Page: ";
   private static final String READIMAGEPROC = "/imStr 0 def /imageSrc {currentfile /ASCII85Decode filter /RunLengthDecode filter  imStr readstring pop } def";
   private static final String COPIES = "/#copies exch def";
   private static final String PAGE_SAVE = "/pgSave save def";
   private static final String PAGE_RESTORE = "pgSave restore";
   private static final String SHOWPAGE = "showpage";
   private static final String IMAGE_SAVE = "/imSave save def";
   private static final String IMAGE_STR = " string /imStr exch def";
   private static final String IMAGE_RESTORE = "imSave restore";
   private static final String COORD_PREP = " 0 exch translate 1 -1 scale[72 300 div 0 0 72 300 div 0 0]concat";
   private static final String SetFontName = "F";
   private static final String DrawStringName = "S";
   private static final String EVEN_ODD_FILL_STR = "EF";
   private static final String WINDING_FILL_STR = "WF";
   private static final String EVEN_ODD_CLIP_STR = "EC";
   private static final String WINDING_CLIP_STR = "WC";
   private static final String MOVETO_STR = " M";
   private static final String LINETO_STR = " L";
   private static final String CURVETO_STR = " C";
   private static final String GRESTORE_STR = "R";
   private static final String GSAVE_STR = "G";
   private static final String NEWPATH_STR = "N";
   private static final String CLOSEPATH_STR = "P";
   private static final String SETRGBCOLOR_STR = " SC";
   private static final String SETGRAY_STR = " SG";
   private int mDestType;
   private String mDestination = "lp";
   private boolean mNoJobSheet = false;
   private String mOptions;
   private Font mLastFont;
   private Color mLastColor;
   private Shape mLastClip;
   private AffineTransform mLastTransform;
   private PSPrinterJob.EPSPrinter epsPrinter = null;
   FontMetrics mCurMetrics;
   PrintStream mPSStream;
   File spoolFile;
   private String mFillOpStr = "WF";
   private String mClipOpStr = "WC";
   ArrayList mGStateStack = new ArrayList();
   private float mPenX;
   private float mPenY;
   private float mStartPathX;
   private float mStartPathY;
   private static Properties mFontProps = null;
   private static boolean isMac;

   private static Properties initProps() {
      String var0 = System.getProperty("java.home");
      if (var0 != null) {
         String var1 = SunToolkit.getStartupLocale().getLanguage();

         try {
            File var2 = new File(var0 + File.separator + "lib" + File.separator + "psfontj2d.properties." + var1);
            if (!var2.canRead()) {
               var2 = new File(var0 + File.separator + "lib" + File.separator + "psfont.properties." + var1);
               if (!var2.canRead()) {
                  var2 = new File(var0 + File.separator + "lib" + File.separator + "psfontj2d.properties");
                  if (!var2.canRead()) {
                     var2 = new File(var0 + File.separator + "lib" + File.separator + "psfont.properties");
                     if (!var2.canRead()) {
                        return (Properties)null;
                     }
                  }
               }
            }

            BufferedInputStream var3 = new BufferedInputStream(new FileInputStream(var2.getPath()));
            Properties var4 = new Properties();
            var4.load((InputStream)var3);
            var3.close();
            return var4;
         } catch (Exception var5) {
            return (Properties)null;
         }
      } else {
         return (Properties)null;
      }
   }

   public boolean printDialog() throws HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         if (this.attributes == null) {
            this.attributes = new HashPrintRequestAttributeSet();
         }

         this.attributes.add(new Copies(this.getCopies()));
         this.attributes.add(new JobName(this.getJobName(), (Locale)null));
         boolean var1 = false;
         DialogTypeSelection var2 = (DialogTypeSelection)this.attributes.get(DialogTypeSelection.class);
         if (var2 == DialogTypeSelection.NATIVE) {
            this.attributes.remove(DialogTypeSelection.class);
            var1 = this.printDialog(this.attributes);
            this.attributes.add(DialogTypeSelection.NATIVE);
         } else {
            var1 = this.printDialog(this.attributes);
         }

         if (var1) {
            JobName var3 = (JobName)this.attributes.get(JobName.class);
            if (var3 != null) {
               this.setJobName(var3.getValue());
            }

            Copies var4 = (Copies)this.attributes.get(Copies.class);
            if (var4 != null) {
               this.setCopies(var4.getValue());
            }

            Destination var5 = (Destination)this.attributes.get(Destination.class);
            if (var5 != null) {
               try {
                  this.mDestType = 1;
                  this.mDestination = (new File(var5.getURI())).getPath();
               } catch (Exception var8) {
                  this.mDestination = "out.ps";
               }
            } else {
               this.mDestType = 0;
               PrintService var6 = this.getPrintService();
               if (var6 != null) {
                  this.mDestination = var6.getName();
                  if (isMac) {
                     PrintServiceAttributeSet var7 = var6.getAttributes();
                     if (var7 != null) {
                        this.mDestination = var7.get(PrinterName.class).toString();
                     }
                  }
               }
            }
         }

         return var1;
      }
   }

   protected void startDoc() throws PrinterException {
      if (this.epsPrinter == null) {
         Object var1;
         if (this.getPrintService() instanceof PSStreamPrintService) {
            StreamPrintService var2 = (StreamPrintService)this.getPrintService();
            this.mDestType = 2;
            if (var2.isDisposed()) {
               throw new PrinterException("service is disposed");
            }

            var1 = var2.getOutputStream();
            if (var1 == null) {
               throw new PrinterException("Null output stream");
            }
         } else {
            this.mNoJobSheet = super.noJobSheet;
            if (super.destinationAttr != null) {
               this.mDestType = 1;
               this.mDestination = super.destinationAttr;
            }

            if (this.mDestType == 1) {
               try {
                  this.spoolFile = new File(this.mDestination);
                  var1 = new FileOutputStream(this.spoolFile);
               } catch (IOException var9) {
                  throw new PrinterIOException(var9);
               }
            } else {
               PSPrinterJob.PrinterOpener var10 = new PSPrinterJob.PrinterOpener();
               AccessController.doPrivileged((PrivilegedAction)var10);
               if (var10.pex != null) {
                  throw var10.pex;
               }

               var1 = var10.result;
            }
         }

         this.mPSStream = new PrintStream(new BufferedOutputStream((OutputStream)var1));
         this.mPSStream.println("%!PS-Adobe-3.0");
      }

      this.mPSStream.println("%%BeginProlog");
      this.mPSStream.println("/imStr 0 def /imageSrc {currentfile /ASCII85Decode filter /RunLengthDecode filter  imStr readstring pop } def");
      this.mPSStream.println("/BD {bind def} bind def");
      this.mPSStream.println("/D {def} BD");
      this.mPSStream.println("/C {curveto} BD");
      this.mPSStream.println("/L {lineto} BD");
      this.mPSStream.println("/M {moveto} BD");
      this.mPSStream.println("/R {grestore} BD");
      this.mPSStream.println("/G {gsave} BD");
      this.mPSStream.println("/N {newpath} BD");
      this.mPSStream.println("/P {closepath} BD");
      this.mPSStream.println("/EC {eoclip} BD");
      this.mPSStream.println("/WC {clip} BD");
      this.mPSStream.println("/EF {eofill} BD");
      this.mPSStream.println("/WF {fill} BD");
      this.mPSStream.println("/SG {setgray} BD");
      this.mPSStream.println("/SC {setrgbcolor} BD");
      this.mPSStream.println("/ISOF {");
      this.mPSStream.println("     dup findfont dup length 1 add dict begin {");
      this.mPSStream.println("             1 index /FID eq {pop pop} {D} ifelse");
      this.mPSStream.println("     } forall /Encoding ISOLatin1Encoding D");
      this.mPSStream.println("     currentdict end definefont");
      this.mPSStream.println("} BD");
      this.mPSStream.println("/NZ {dup 1 lt {pop 1} if} BD");
      this.mPSStream.println("/S {");
      this.mPSStream.println("     moveto 1 index stringwidth pop NZ sub");
      this.mPSStream.println("     1 index length 1 sub NZ div 0");
      this.mPSStream.println("     3 2 roll ashow newpath} BD");
      this.mPSStream.println("/FL [");
      if (mFontProps == null) {
         this.mPSStream.println(" /Helvetica ISOF");
         this.mPSStream.println(" /Helvetica-Bold ISOF");
         this.mPSStream.println(" /Helvetica-Oblique ISOF");
         this.mPSStream.println(" /Helvetica-BoldOblique ISOF");
         this.mPSStream.println(" /Times-Roman ISOF");
         this.mPSStream.println(" /Times-Bold ISOF");
         this.mPSStream.println(" /Times-Italic ISOF");
         this.mPSStream.println(" /Times-BoldItalic ISOF");
         this.mPSStream.println(" /Courier ISOF");
         this.mPSStream.println(" /Courier-Bold ISOF");
         this.mPSStream.println(" /Courier-Oblique ISOF");
         this.mPSStream.println(" /Courier-BoldOblique ISOF");
      } else {
         int var11 = Integer.parseInt(mFontProps.getProperty("font.num", "9"));

         for(int var3 = 0; var3 < var11; ++var3) {
            this.mPSStream.println("    /" + mFontProps.getProperty("font." + String.valueOf(var3), "Courier ISOF"));
         }
      }

      this.mPSStream.println("] D");
      this.mPSStream.println("/F {");
      this.mPSStream.println("     FL exch get exch scalefont");
      this.mPSStream.println("     [1 0 0 -1 0 0] makefont setfont} BD");
      this.mPSStream.println("%%EndProlog");
      this.mPSStream.println("%%BeginSetup");
      if (this.epsPrinter == null) {
         PageFormat var12 = this.getPageable().getPageFormat(0);
         double var13 = var12.getPaper().getHeight();
         double var5 = var12.getPaper().getWidth();
         this.mPSStream.print("<< /PageSize [" + var5 + " " + var13 + "]");
         final PrintService var7 = this.getPrintService();
         Boolean var8 = (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               try {
                  Class var1 = Class.forName("sun.print.IPPPrintService");
                  if (var1.isInstance(var7)) {
                     Method var2 = var1.getMethod("isPostscript", (Class[])null);
                     return (Boolean)var2.invoke(var7, (Object[])null);
                  }
               } catch (Throwable var3) {
               }

               return Boolean.TRUE;
            }
         });
         if (var8) {
            this.mPSStream.print(" /DeferredMediaSelection true");
         }

         this.mPSStream.print(" /ImagingBBox null /ManualFeed false");
         this.mPSStream.print(this.isCollated() ? " /Collate true" : "");
         this.mPSStream.print(" /NumCopies " + this.getCopiesInt());
         if (this.sidesAttr != Sides.ONE_SIDED) {
            if (this.sidesAttr == Sides.TWO_SIDED_LONG_EDGE) {
               this.mPSStream.print(" /Duplex true ");
            } else if (this.sidesAttr == Sides.TWO_SIDED_SHORT_EDGE) {
               this.mPSStream.print(" /Duplex true /Tumble true ");
            }
         }

         this.mPSStream.println(" >> setpagedevice ");
      }

      this.mPSStream.println("%%EndSetup");
   }

   protected void abortDoc() {
      if (this.mPSStream != null && this.mDestType != 2) {
         this.mPSStream.close();
      }

      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            if (PSPrinterJob.this.spoolFile != null && PSPrinterJob.this.spoolFile.exists()) {
               PSPrinterJob.this.spoolFile.delete();
            }

            return null;
         }
      });
   }

   protected void endDoc() throws PrinterException {
      if (this.mPSStream != null) {
         this.mPSStream.println("%%EOF");
         this.mPSStream.flush();
         if (this.mDestType != 2) {
            this.mPSStream.close();
         }
      }

      if (this.mDestType == 0) {
         PrintService var1 = this.getPrintService();
         if (var1 != null) {
            this.mDestination = var1.getName();
            if (isMac) {
               PrintServiceAttributeSet var2 = var1.getAttributes();
               if (var2 != null) {
                  this.mDestination = var2.get(PrinterName.class).toString();
               }
            }
         }

         PSPrinterJob.PrinterSpooler var3 = new PSPrinterJob.PrinterSpooler();
         AccessController.doPrivileged((PrivilegedAction)var3);
         if (var3.pex != null) {
            throw var3.pex;
         }
      }

   }

   protected void startPage(PageFormat var1, Printable var2, int var3, boolean var4) throws PrinterException {
      double var5 = var1.getPaper().getHeight();
      double var7 = var1.getPaper().getWidth();
      int var9 = var3 + 1;
      this.mGStateStack = new ArrayList();
      this.mGStateStack.add(new PSPrinterJob.GState());
      this.mPSStream.println("%%Page: " + var9 + " " + var9);
      if (var3 > 0 && var4) {
         this.mPSStream.print("<< /PageSize [" + var7 + " " + var5 + "]");
         final PrintService var10 = this.getPrintService();
         Boolean var11 = (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               try {
                  Class var1 = Class.forName("sun.print.IPPPrintService");
                  if (var1.isInstance(var10)) {
                     Method var2 = var1.getMethod("isPostscript", (Class[])null);
                     return (Boolean)var2.invoke(var10, (Object[])null);
                  }
               } catch (Throwable var3) {
               }

               return Boolean.TRUE;
            }
         });
         if (var11) {
            this.mPSStream.print(" /DeferredMediaSelection true");
         }

         this.mPSStream.println(" >> setpagedevice");
      }

      this.mPSStream.println("/pgSave save def");
      this.mPSStream.println(var5 + " 0 exch translate 1 -1 scale[72 300 div 0 0 72 300 div 0 0]concat");
   }

   protected void endPage(PageFormat var1, Printable var2, int var3) throws PrinterException {
      this.mPSStream.println("pgSave restore");
      this.mPSStream.println("showpage");
   }

   protected void drawImageBGR(byte[] var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, int var11) {
      this.setTransform(new AffineTransform());
      this.prepDrawing();
      int var12 = (int)var8;
      int var13 = (int)var9;
      this.mPSStream.println("/imSave save def");

      int var14;
      for(var14 = 3 * var12; var14 > 65535; var14 /= 2) {
      }

      this.mPSStream.println(var14 + " string /imStr exch def");
      this.mPSStream.println("[" + var4 + " 0 0 " + var5 + " " + var2 + " " + var3 + "]concat");
      this.mPSStream.println(var12 + " " + var13 + " " + 8 + "[" + var12 + " 0 0 " + var13 + " 0 " + 0 + "]/imageSrc load false 3 colorimage");
      boolean var15 = false;
      byte[] var16 = new byte[var12 * 3];

      try {
         int var21 = (int)var7 * var10;

         for(int var17 = 0; var17 < var13; ++var17) {
            var21 += (int)var6;
            var21 = swapBGRtoRGB(var1, var21, var16);
            byte[] var18 = this.rlEncode(var16);
            byte[] var19 = this.ascii85Encode(var18);
            this.mPSStream.write((byte[])var19);
            this.mPSStream.println("");
         }
      } catch (IOException var20) {
      }

      this.mPSStream.println("imSave restore");
   }

   protected void printBand(byte[] var1, int var2, int var3, int var4, int var5) throws PrinterException {
      this.mPSStream.println("/imSave save def");

      int var6;
      for(var6 = 3 * var4; var6 > 65535; var6 /= 2) {
      }

      this.mPSStream.println(var6 + " string /imStr exch def");
      this.mPSStream.println("[" + var4 + " 0 0 " + var5 + " " + var2 + " " + var3 + "]concat");
      this.mPSStream.println(var4 + " " + var5 + " " + 8 + "[" + var4 + " 0 0 " + -var5 + " 0 " + var5 + "]/imageSrc load false 3 colorimage");
      int var7 = 0;
      byte[] var8 = new byte[var4 * 3];

      try {
         for(int var9 = 0; var9 < var5; ++var9) {
            var7 = swapBGRtoRGB(var1, var7, var8);
            byte[] var10 = this.rlEncode(var8);
            byte[] var11 = this.ascii85Encode(var10);
            this.mPSStream.write((byte[])var11);
            this.mPSStream.println("");
         }
      } catch (IOException var12) {
         throw new PrinterIOException(var12);
      }

      this.mPSStream.println("imSave restore");
   }

   protected Graphics2D createPathGraphics(PeekGraphics var1, PrinterJob var2, Printable var3, PageFormat var4, int var5) {
      PeekMetrics var7 = var1.getMetrics();
      PSPathGraphics var6;
      if (forcePDL || !forceRaster && !var7.hasNonSolidColors() && !var7.hasCompositing()) {
         BufferedImage var8 = new BufferedImage(8, 8, 1);
         Graphics2D var9 = var8.createGraphics();
         boolean var10 = !var1.getAWTDrawingOnly();
         var6 = new PSPathGraphics(var9, var2, var3, var4, var5, var10);
      } else {
         var6 = null;
      }

      return var6;
   }

   protected void selectClipPath() {
      this.mPSStream.println(this.mClipOpStr);
   }

   protected void setClip(Shape var1) {
      this.mLastClip = var1;
   }

   protected void setTransform(AffineTransform var1) {
      this.mLastTransform = var1;
   }

   protected boolean setFont(Font var1) {
      this.mLastFont = var1;
      return true;
   }

   private int[] getPSFontIndexArray(Font var1, CharsetString[] var2) {
      int[] var3 = null;
      if (mFontProps != null) {
         var3 = new int[var2.length];
      }

      for(int var4 = 0; var4 < var2.length && var3 != null; ++var4) {
         CharsetString var5 = var2[var4];
         CharsetEncoder var6 = var5.fontDescriptor.encoder;
         String var7 = var5.fontDescriptor.getFontCharsetName();
         if ("Symbol".equals(var7)) {
            var7 = "symbol";
         } else if (!"WingDings".equals(var7) && !"X11Dingbats".equals(var7)) {
            var7 = this.makeCharsetName(var7, var5.charsetChars);
         } else {
            var7 = "dingbats";
         }

         int var8 = var1.getStyle() | FontUtilities.getFont2D(var1).getStyle();
         String var9 = FontConfiguration.getStyleString(var8);
         String var10 = var1.getFamily().toLowerCase(Locale.ENGLISH);
         var10 = var10.replace(' ', '_');
         String var11 = mFontProps.getProperty(var10, "");
         String var12 = mFontProps.getProperty(var11 + "." + var7 + "." + var9, (String)null);
         if (var12 != null) {
            try {
               var3[var4] = Integer.parseInt(mFontProps.getProperty(var12));
            } catch (NumberFormatException var14) {
               var3 = null;
            }
         } else {
            var3 = null;
         }
      }

      return var3;
   }

   private static String escapeParens(String var0) {
      if (var0.indexOf(40) == -1 && var0.indexOf(41) == -1) {
         return var0;
      } else {
         int var1 = 0;

         int var2;
         for(var2 = 0; (var2 = var0.indexOf(40, var2)) != -1; ++var2) {
            ++var1;
         }

         for(var2 = 0; (var2 = var0.indexOf(41, var2)) != -1; ++var2) {
            ++var1;
         }

         char[] var3 = var0.toCharArray();
         char[] var4 = new char[var3.length + var1];
         var2 = 0;

         for(int var5 = 0; var5 < var3.length; ++var5) {
            if (var3[var5] == '(' || var3[var5] == ')') {
               var4[var2++] = '\\';
            }

            var4[var2++] = var3[var5];
         }

         return new String(var4);
      }
   }

   protected int platformFontCount(Font var1, String var2) {
      if (mFontProps == null) {
         return 0;
      } else {
         CharsetString[] var3 = ((PlatformFont)((PlatformFont)var1.getPeer())).makeMultiCharsetString(var2, false);
         if (var3 == null) {
            return 0;
         } else {
            int[] var4 = this.getPSFontIndexArray(var1, var3);
            return var4 == null ? 0 : var4.length;
         }
      }
   }

   protected boolean textOut(Graphics var1, String var2, float var3, float var4, Font var5, FontRenderContext var6, float var7) {
      boolean var8 = true;
      if (mFontProps == null) {
         return false;
      } else {
         this.prepDrawing();
         var2 = this.removeControlChars(var2);
         if (var2.length() == 0) {
            return true;
         } else {
            CharsetString[] var9 = ((PlatformFont)((PlatformFont)var5.getPeer())).makeMultiCharsetString(var2, false);
            if (var9 == null) {
               return false;
            } else {
               int[] var10 = this.getPSFontIndexArray(var5, var9);
               if (var10 != null) {
                  for(int var11 = 0; var11 < var9.length; ++var11) {
                     CharsetString var12 = var9[var11];
                     CharsetEncoder var13 = var12.fontDescriptor.encoder;
                     StringBuffer var14 = new StringBuffer();
                     byte[] var15 = new byte[var12.length * 2];
                     boolean var16 = false;

                     int var24;
                     try {
                        ByteBuffer var17 = ByteBuffer.wrap(var15);
                        var13.encode(CharBuffer.wrap(var12.charsetChars, var12.offset, var12.length), var17, true);
                        var17.flip();
                        var24 = var17.limit();
                     } catch (IllegalStateException var22) {
                        continue;
                     } catch (CoderMalfunctionError var23) {
                        continue;
                     }

                     float var25;
                     if (var9.length == 1 && var7 != 0.0F) {
                        var25 = var7;
                     } else {
                        Rectangle2D var18 = var5.getStringBounds(var12.charsetChars, var12.offset, var12.offset + var12.length, var6);
                        var25 = (float)var18.getWidth();
                     }

                     if (var25 == 0.0F) {
                        return var8;
                     }

                     var14.append('<');

                     for(int var26 = 0; var26 < var24; ++var26) {
                        byte var19 = var15[var26];
                        String var20 = Integer.toHexString(var19);
                        int var21 = var20.length();
                        if (var21 > 2) {
                           var20 = var20.substring(var21 - 2, var21);
                        } else if (var21 == 1) {
                           var20 = "0" + var20;
                        } else if (var21 == 0) {
                           var20 = "00";
                        }

                        var14.append(var20);
                     }

                     var14.append('>');
                     this.getGState().emitPSFont(var10[var11], var5.getSize2D());
                     this.mPSStream.println(var14.toString() + " " + var25 + " " + var3 + " " + var4 + " " + "S");
                     var3 += var25;
                  }
               } else {
                  var8 = false;
               }

               return var8;
            }
         }
      }
   }

   protected void setFillMode(int var1) {
      switch(var1) {
      case 1:
         this.mFillOpStr = "EF";
         this.mClipOpStr = "EC";
         break;
      case 2:
         this.mFillOpStr = "WF";
         this.mClipOpStr = "WC";
         break;
      default:
         throw new IllegalArgumentException();
      }

   }

   protected void setColor(Color var1) {
      this.mLastColor = var1;
   }

   protected void fillPath() {
      this.mPSStream.println(this.mFillOpStr);
   }

   protected void beginPath() {
      this.prepDrawing();
      this.mPSStream.println("N");
      this.mPenX = 0.0F;
      this.mPenY = 0.0F;
   }

   protected void closeSubpath() {
      this.mPSStream.println("P");
      this.mPenX = this.mStartPathX;
      this.mPenY = this.mStartPathY;
   }

   protected void moveTo(float var1, float var2) {
      this.mPSStream.println(this.trunc(var1) + " " + this.trunc(var2) + " M");
      this.mStartPathX = var1;
      this.mStartPathY = var2;
      this.mPenX = var1;
      this.mPenY = var2;
   }

   protected void lineTo(float var1, float var2) {
      this.mPSStream.println(this.trunc(var1) + " " + this.trunc(var2) + " L");
      this.mPenX = var1;
      this.mPenY = var2;
   }

   protected void bezierTo(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.mPSStream.println(this.trunc(var1) + " " + this.trunc(var2) + " " + this.trunc(var3) + " " + this.trunc(var4) + " " + this.trunc(var5) + " " + this.trunc(var6) + " C");
      this.mPenX = var5;
      this.mPenY = var6;
   }

   String trunc(float var1) {
      float var2 = Math.abs(var1);
      if (var2 >= 1.0F && var2 <= 1000.0F) {
         var1 = (float)Math.round(var1 * 1000.0F) / 1000.0F;
      }

      return Float.toString(var1);
   }

   protected float getPenX() {
      return this.mPenX;
   }

   protected float getPenY() {
      return this.mPenY;
   }

   protected double getXRes() {
      return 300.0D;
   }

   protected double getYRes() {
      return 300.0D;
   }

   protected double getPhysicalPrintableX(Paper var1) {
      return 0.0D;
   }

   protected double getPhysicalPrintableY(Paper var1) {
      return 0.0D;
   }

   protected double getPhysicalPrintableWidth(Paper var1) {
      return var1.getImageableWidth();
   }

   protected double getPhysicalPrintableHeight(Paper var1) {
      return var1.getImageableHeight();
   }

   protected double getPhysicalPageWidth(Paper var1) {
      return var1.getWidth();
   }

   protected double getPhysicalPageHeight(Paper var1) {
      return var1.getHeight();
   }

   protected int getNoncollatedCopies() {
      return 1;
   }

   protected int getCollatedCopies() {
      return 1;
   }

   private String[] printExecCmd(String var1, String var2, boolean var3, String var4, int var5, String var6) {
      boolean var7 = true;
      boolean var8 = true;
      boolean var9 = true;
      boolean var10 = true;
      boolean var11 = true;
      boolean var12 = false;
      int var14 = 2;
      byte var15 = 0;
      if (var1 != null && !var1.equals("") && !var1.equals("lp")) {
         var12 |= var7;
         ++var14;
      }

      if (var2 != null && !var2.equals("")) {
         var12 |= var8;
         ++var14;
      }

      if (var4 != null && !var4.equals("")) {
         var12 |= var9;
         ++var14;
      }

      if (var5 > 1) {
         var12 |= var10;
         ++var14;
      }

      if (var3) {
         var12 |= var11;
         ++var14;
      }

      String var16 = System.getProperty("os.name");
      String[] var13;
      int var17;
      if (!var16.equals("Linux") && !var16.contains("OS X")) {
         ++var14;
         var13 = new String[var14];
         var17 = var15 + 1;
         var13[var15] = "/usr/bin/lp";
         var13[var17++] = "-c";
         if (var12 & var7) {
            var13[var17++] = "-d" + var1;
         }

         if (var12 & var9) {
            var13[var17++] = "-t" + var4;
         }

         if (var12 & var10) {
            var13[var17++] = "-n" + var5;
         }

         if (var12 & var11) {
            var13[var17++] = "-o nobanner";
         }

         if (var12 & var8) {
            var13[var17++] = "-o" + var2;
         }
      } else {
         var13 = new String[var14];
         var17 = var15 + 1;
         var13[var15] = "/usr/bin/lpr";
         if (var12 & var7) {
            var13[var17++] = "-P" + var1;
         }

         if (var12 & var9) {
            var13[var17++] = "-J" + var4;
         }

         if (var12 & var10) {
            var13[var17++] = "-#" + var5;
         }

         if (var12 & var11) {
            var13[var17++] = "-h";
         }

         if (var12 & var8) {
            var13[var17++] = new String(var2);
         }
      }

      var13[var17++] = var6;
      return var13;
   }

   private static int swapBGRtoRGB(byte[] var0, int var1, byte[] var2) {
      for(int var3 = 0; var1 < var0.length - 2 && var3 < var2.length - 2; var1 += 3) {
         var2[var3++] = var0[var1 + 2];
         var2[var3++] = var0[var1 + 1];
         var2[var3++] = var0[var1 + 0];
      }

      return var1;
   }

   private String makeCharsetName(String var1, char[] var2) {
      if (!var1.equals("Cp1252") && !var1.equals("ISO8859_1")) {
         int var3;
         if (var1.equals("UTF8")) {
            for(var3 = 0; var3 < var2.length; ++var3) {
               if (var2[var3] > 255) {
                  return var1.toLowerCase();
               }
            }

            return "latin1";
         } else if (var1.startsWith("ISO8859")) {
            for(var3 = 0; var3 < var2.length; ++var3) {
               if (var2[var3] > 127) {
                  return var1.toLowerCase();
               }
            }

            return "latin1";
         } else {
            return var1.toLowerCase();
         }
      } else {
         return "latin1";
      }
   }

   private void prepDrawing() {
      while(!this.isOuterGState() && (!this.getGState().canSetClip(this.mLastClip) || !this.getGState().mTransform.equals(this.mLastTransform))) {
         this.grestore();
      }

      this.getGState().emitPSColor(this.mLastColor);
      if (this.isOuterGState()) {
         this.gsave();
         this.getGState().emitTransform(this.mLastTransform);
         this.getGState().emitPSClip(this.mLastClip);
      }

   }

   private PSPrinterJob.GState getGState() {
      int var1 = this.mGStateStack.size();
      return (PSPrinterJob.GState)this.mGStateStack.get(var1 - 1);
   }

   private void gsave() {
      PSPrinterJob.GState var1 = this.getGState();
      this.mGStateStack.add(new PSPrinterJob.GState(var1));
      this.mPSStream.println("G");
   }

   private void grestore() {
      int var1 = this.mGStateStack.size();
      this.mGStateStack.remove(var1 - 1);
      this.mPSStream.println("R");
   }

   private boolean isOuterGState() {
      return this.mGStateStack.size() == 1;
   }

   void convertToPSPath(PathIterator var1) {
      float[] var2 = new float[6];
      byte var4;
      if (var1.getWindingRule() == 0) {
         var4 = 1;
      } else {
         var4 = 2;
      }

      this.beginPath();
      this.setFillMode(var4);

      for(; !var1.isDone(); var1.next()) {
         int var3 = var1.currentSegment(var2);
         switch(var3) {
         case 0:
            this.moveTo(var2[0], var2[1]);
            break;
         case 1:
            this.lineTo(var2[0], var2[1]);
            break;
         case 2:
            float var5 = this.getPenX();
            float var6 = this.getPenY();
            float var7 = var5 + (var2[0] - var5) * 2.0F / 3.0F;
            float var8 = var6 + (var2[1] - var6) * 2.0F / 3.0F;
            float var9 = var2[2] - (var2[2] - var2[0]) * 2.0F / 3.0F;
            float var10 = var2[3] - (var2[3] - var2[1]) * 2.0F / 3.0F;
            this.bezierTo(var7, var8, var9, var10, var2[2], var2[3]);
            break;
         case 3:
            this.bezierTo(var2[0], var2[1], var2[2], var2[3], var2[4], var2[5]);
            break;
         case 4:
            this.closeSubpath();
         }
      }

   }

   protected void deviceFill(PathIterator var1, Color var2, AffineTransform var3, Shape var4) {
      this.setTransform(var3);
      this.setClip(var4);
      this.setColor(var2);
      this.convertToPSPath(var1);
      this.mPSStream.println("G");
      this.selectClipPath();
      this.fillPath();
      this.mPSStream.println("R N");
   }

   private byte[] rlEncode(byte[] var1) {
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      byte[] var6 = new byte[var1.length * 2 + 2];

      while(true) {
         while(var2 < var1.length) {
            if (var5 == 0) {
               var4 = var2++;
               var5 = 1;
            }

            while(var5 < 128 && var2 < var1.length && var1[var2] == var1[var4]) {
               ++var5;
               ++var2;
            }

            if (var5 > 1) {
               var6[var3++] = (byte)(257 - var5);
               var6[var3++] = var1[var4];
               var5 = 0;
            } else {
               while(var5 < 128 && var2 < var1.length && var1[var2] != var1[var2 - 1]) {
                  ++var5;
                  ++var2;
               }

               var6[var3++] = (byte)(var5 - 1);

               for(int var7 = var4; var7 < var4 + var5; ++var7) {
                  var6[var3++] = var1[var7];
               }

               var5 = 0;
            }
         }

         var6[var3++] = -128;
         byte[] var8 = new byte[var3];
         System.arraycopy(var6, 0, var8, 0, var3);
         return var8;
      }
   }

   private byte[] ascii85Encode(byte[] var1) {
      byte[] var2 = new byte[(var1.length + 4) * 5 / 4 + 2];
      long var3 = 85L;
      long var5 = var3 * var3;
      long var7 = var3 * var5;
      long var9 = var3 * var7;
      byte var11 = 33;
      int var12 = 0;
      int var13 = 0;

      long var14;
      long var16;
      while(var12 + 3 < var1.length) {
         var14 = ((long)(var1[var12++] & 255) << 24) + ((long)(var1[var12++] & 255) << 16) + ((long)(var1[var12++] & 255) << 8) + (long)(var1[var12++] & 255);
         if (var14 == 0L) {
            var2[var13++] = 122;
         } else {
            var2[var13++] = (byte)((int)(var14 / var9 + (long)var11));
            var16 = var14 % var9;
            var2[var13++] = (byte)((int)(var16 / var7 + (long)var11));
            var16 %= var7;
            var2[var13++] = (byte)((int)(var16 / var5 + (long)var11));
            var16 %= var5;
            var2[var13++] = (byte)((int)(var16 / var3 + (long)var11));
            var16 %= var3;
            var2[var13++] = (byte)((int)(var16 + (long)var11));
         }
      }

      if (var12 < var1.length) {
         int var18 = var1.length - var12;

         for(var14 = 0L; var12 < var1.length; var14 = (var14 << 8) + (long)(var1[var12++] & 255)) {
         }

         for(int var19 = 4 - var18; var19-- > 0; var14 <<= 8) {
         }

         byte[] var20 = new byte[5];
         var20[0] = (byte)((int)(var14 / var9 + (long)var11));
         var16 = var14 % var9;
         var20[1] = (byte)((int)(var16 / var7 + (long)var11));
         var16 %= var7;
         var20[2] = (byte)((int)(var16 / var5 + (long)var11));
         var16 %= var5;
         var20[3] = (byte)((int)(var16 / var3 + (long)var11));
         var16 %= var3;
         var20[4] = (byte)((int)(var16 + (long)var11));

         for(int var21 = 0; var21 < var18 + 1; ++var21) {
            var2[var13++] = var20[var21];
         }
      }

      var2[var13++] = 126;
      var2[var13++] = 62;
      byte[] var23 = new byte[var13];
      System.arraycopy(var2, 0, var23, 0, var13);
      return var23;
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            PSPrinterJob.mFontProps = PSPrinterJob.initProps();
            String var1 = System.getProperty("os.name");
            PSPrinterJob.isMac = var1.startsWith("Mac");
            return null;
         }
      });
   }

   public static class EPSPrinter implements Pageable {
      private PageFormat pf;
      private PSPrinterJob job;
      private int llx;
      private int lly;
      private int urx;
      private int ury;
      private Printable printable;
      private PrintStream stream;
      private String epsTitle;

      public EPSPrinter(Printable var1, String var2, PrintStream var3, int var4, int var5, int var6, int var7) {
         this.printable = var1;
         this.epsTitle = var2;
         this.stream = var3;
         this.llx = var4;
         this.lly = var5;
         this.urx = this.llx + var6;
         this.ury = this.lly + var7;
         Paper var8 = new Paper();
         var8.setSize((double)var6, (double)var7);
         var8.setImageableArea(0.0D, 0.0D, (double)var6, (double)var7);
         this.pf = new PageFormat();
         this.pf.setPaper(var8);
      }

      public void print() throws PrinterException {
         this.stream.println("%!PS-Adobe-3.0 EPSF-3.0");
         this.stream.println("%%BoundingBox: " + this.llx + " " + this.lly + " " + this.urx + " " + this.ury);
         this.stream.println("%%Title: " + this.epsTitle);
         this.stream.println("%%Creator: Java Printing");
         this.stream.println("%%CreationDate: " + new Date());
         this.stream.println("%%EndComments");
         this.stream.println("/pluginSave save def");
         this.stream.println("mark");
         this.job = new PSPrinterJob();
         this.job.epsPrinter = this;
         this.job.mPSStream = this.stream;
         this.job.mDestType = 2;
         this.job.startDoc();

         try {
            this.job.printPage(this, 0);
         } catch (Throwable var5) {
            if (var5 instanceof PrinterException) {
               throw (PrinterException)var5;
            }

            throw new PrinterException(var5.toString());
         } finally {
            this.stream.println("cleartomark");
            this.stream.println("pluginSave restore");
            this.job.endDoc();
         }

         this.stream.flush();
      }

      public int getNumberOfPages() {
         return 1;
      }

      public PageFormat getPageFormat(int var1) {
         if (var1 > 0) {
            throw new IndexOutOfBoundsException("pgIndex");
         } else {
            return this.pf;
         }
      }

      public Printable getPrintable(int var1) {
         if (var1 > 0) {
            throw new IndexOutOfBoundsException("pgIndex");
         } else {
            return this.printable;
         }
      }
   }

   public static class PluginPrinter implements Printable {
      private PSPrinterJob.EPSPrinter epsPrinter;
      private Component applet;
      private PrintStream stream;
      private String epsTitle;
      private int bx;
      private int by;
      private int bw;
      private int bh;
      private int width;
      private int height;

      public PluginPrinter(Component var1, PrintStream var2, int var3, int var4, int var5, int var6) {
         this.applet = var1;
         this.epsTitle = "Java Plugin Applet";
         this.stream = var2;
         this.bx = var3;
         this.by = var4;
         this.bw = var5;
         this.bh = var6;
         this.width = var1.size().width;
         this.height = var1.size().height;
         this.epsPrinter = new PSPrinterJob.EPSPrinter(this, this.epsTitle, var2, 0, 0, this.width, this.height);
      }

      public void printPluginPSHeader() {
         this.stream.println("%%BeginDocument: JavaPluginApplet");
      }

      public void printPluginApplet() {
         try {
            this.epsPrinter.print();
         } catch (PrinterException var2) {
         }

      }

      public void printPluginPSTrailer() {
         this.stream.println("%%EndDocument: JavaPluginApplet");
         this.stream.flush();
      }

      public void printAll() {
         this.printPluginPSHeader();
         this.printPluginApplet();
         this.printPluginPSTrailer();
      }

      public int print(Graphics var1, PageFormat var2, int var3) {
         if (var3 > 0) {
            return 1;
         } else {
            this.applet.printAll(var1);
            return 0;
         }
      }
   }

   private class GState {
      Color mColor;
      Shape mClip;
      Font mFont;
      AffineTransform mTransform;

      GState() {
         this.mColor = Color.black;
         this.mClip = null;
         this.mFont = null;
         this.mTransform = new AffineTransform();
      }

      GState(PSPrinterJob.GState var2) {
         this.mColor = var2.mColor;
         this.mClip = var2.mClip;
         this.mFont = var2.mFont;
         this.mTransform = var2.mTransform;
      }

      boolean canSetClip(Shape var1) {
         return this.mClip == null || this.mClip.equals(var1);
      }

      void emitPSClip(Shape var1) {
         if (var1 != null && (this.mClip == null || !this.mClip.equals(var1))) {
            String var2 = PSPrinterJob.this.mFillOpStr;
            String var3 = PSPrinterJob.this.mClipOpStr;
            PSPrinterJob.this.convertToPSPath(var1.getPathIterator(new AffineTransform()));
            PSPrinterJob.this.selectClipPath();
            this.mClip = var1;
            PSPrinterJob.this.mClipOpStr = var2;
            PSPrinterJob.this.mFillOpStr = var2;
         }

      }

      void emitTransform(AffineTransform var1) {
         if (var1 != null && !var1.equals(this.mTransform)) {
            double[] var2 = new double[6];
            var1.getMatrix(var2);
            PSPrinterJob.this.mPSStream.println("[" + (float)var2[0] + " " + (float)var2[1] + " " + (float)var2[2] + " " + (float)var2[3] + " " + (float)var2[4] + " " + (float)var2[5] + "] concat");
            this.mTransform = var1;
         }

      }

      void emitPSColor(Color var1) {
         if (var1 != null && !var1.equals(this.mColor)) {
            float[] var2 = var1.getRGBColorComponents((float[])null);
            if (var2[0] == var2[1] && var2[1] == var2[2]) {
               PSPrinterJob.this.mPSStream.println(var2[0] + " SG");
            } else {
               PSPrinterJob.this.mPSStream.println(var2[0] + " " + var2[1] + " " + var2[2] + " " + " SC");
            }

            this.mColor = var1;
         }

      }

      void emitPSFont(int var1, float var2) {
         PSPrinterJob.this.mPSStream.println(var2 + " " + var1 + " " + "F");
      }
   }

   private class PrinterSpooler implements PrivilegedAction {
      PrinterException pex;

      private PrinterSpooler() {
      }

      private void handleProcessFailure(Process var1, String[] var2, int var3) throws IOException {
         StringWriter var4 = new StringWriter();
         Throwable var5 = null;

         try {
            PrintWriter var6 = new PrintWriter(var4);
            Throwable var7 = null;

            try {
               var6.append("error=").append(Integer.toString(var3));
               var6.append(" running:");
               String[] var8 = var2;
               int var9 = var2.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  String var11 = var8[var10];
                  var6.append(" '").append(var11).append("'");
               }

               try {
                  InputStream var157 = var1.getErrorStream();
                  Throwable var158 = null;
                  boolean var102 = false;

                  try {
                     var102 = true;
                     InputStreamReader var159 = new InputStreamReader(var157);
                     Throwable var160 = null;

                     try {
                        BufferedReader var12 = new BufferedReader(var159);
                        Throwable var13 = null;

                        try {
                           while(var12.ready()) {
                              var6.println();
                              var6.append("\t\t").append(var12.readLine());
                           }
                        } catch (Throwable var146) {
                           var13 = var146;
                           throw var146;
                        } finally {
                           if (var12 != null) {
                              if (var13 != null) {
                                 try {
                                    var12.close();
                                 } catch (Throwable var144) {
                                    var13.addSuppressed(var144);
                                 }
                              } else {
                                 var12.close();
                              }
                           }

                        }
                     } catch (Throwable var148) {
                        var160 = var148;
                        throw var148;
                     } finally {
                        if (var159 != null) {
                           if (var160 != null) {
                              try {
                                 var159.close();
                              } catch (Throwable var143) {
                                 var160.addSuppressed(var143);
                              }
                           } else {
                              var159.close();
                           }
                        }

                     }

                     var102 = false;
                  } catch (Throwable var150) {
                     var158 = var150;
                     throw var150;
                  } finally {
                     if (var102) {
                        if (var157 != null) {
                           if (var158 != null) {
                              try {
                                 var157.close();
                              } catch (Throwable var142) {
                                 var158.addSuppressed(var142);
                              }
                           } else {
                              var157.close();
                           }
                        }

                     }
                  }

                  if (var157 != null) {
                     if (var158 != null) {
                        try {
                           var157.close();
                        } catch (Throwable var145) {
                           var158.addSuppressed(var145);
                        }
                     } else {
                        var157.close();
                     }
                  }
               } finally {
                  var6.flush();
                  throw new IOException(var4.toString());
               }
            } catch (Throwable var153) {
               var7 = var153;
               throw var153;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var141) {
                        var7.addSuppressed(var141);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         } catch (Throwable var155) {
            var5 = var155;
            throw var155;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var140) {
                     var5.addSuppressed(var140);
                  }
               } else {
                  var4.close();
               }
            }

         }
      }

      public Object run() {
         if (PSPrinterJob.this.spoolFile != null && PSPrinterJob.this.spoolFile.exists()) {
            try {
               String var1 = PSPrinterJob.this.spoolFile.getAbsolutePath();
               String[] var2 = PSPrinterJob.this.printExecCmd(PSPrinterJob.this.mDestination, PSPrinterJob.this.mOptions, PSPrinterJob.this.mNoJobSheet, PSPrinterJob.this.getJobNameInt(), 1, var1);
               Process var3 = Runtime.getRuntime().exec(var2);
               var3.waitFor();
               int var4 = var3.exitValue();
               if (0 != var4) {
                  this.handleProcessFailure(var3, var2, var4);
               }
            } catch (IOException var9) {
               this.pex = new PrinterIOException(var9);
            } catch (InterruptedException var10) {
               this.pex = new PrinterException(var10.toString());
            } finally {
               PSPrinterJob.this.spoolFile.delete();
            }

            return null;
         } else {
            this.pex = new PrinterException("No spool file");
            return null;
         }
      }

      // $FF: synthetic method
      PrinterSpooler(Object var2) {
         this();
      }
   }

   private class PrinterOpener implements PrivilegedAction {
      PrinterException pex;
      OutputStream result;

      private PrinterOpener() {
      }

      public Object run() {
         try {
            PSPrinterJob.this.spoolFile = Files.createTempFile("javaprint", ".ps").toFile();
            PSPrinterJob.this.spoolFile.deleteOnExit();
            this.result = new FileOutputStream(PSPrinterJob.this.spoolFile);
            return this.result;
         } catch (IOException var2) {
            this.pex = new PrinterIOException(var2);
            return null;
         }
      }

      // $FF: synthetic method
      PrinterOpener(Object var2) {
         this();
      }
   }
}
