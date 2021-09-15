package sun.print;

import java.io.OutputStream;
import java.util.Locale;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.ServiceUIFactory;
import javax.print.StreamPrintService;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.ColorSupported;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import javax.print.event.PrintServiceAttributeListener;

public class PSStreamPrintService extends StreamPrintService implements SunPrinterJobService {
   private static final Class[] suppAttrCats = new Class[]{Chromaticity.class, Copies.class, Fidelity.class, JobName.class, Media.class, MediaPrintableArea.class, OrientationRequested.class, PageRanges.class, RequestingUserName.class, SheetCollate.class, Sides.class};
   private static int MAXCOPIES = 1000;
   private static final MediaSizeName[] mediaSizes;

   public PSStreamPrintService(OutputStream var1) {
      super(var1);
   }

   public String getOutputFormat() {
      return "application/postscript";
   }

   public DocFlavor[] getSupportedDocFlavors() {
      return PSStreamPrinterFactory.getFlavors();
   }

   public DocPrintJob createPrintJob() {
      return new PSStreamPrintJob(this);
   }

   public boolean usesClass(Class var1) {
      return var1 == PSPrinterJob.class;
   }

   public String getName() {
      return "Postscript output";
   }

   public void addPrintServiceAttributeListener(PrintServiceAttributeListener var1) {
   }

   public void removePrintServiceAttributeListener(PrintServiceAttributeListener var1) {
   }

   public <T extends PrintServiceAttribute> T getAttribute(Class<T> var1) {
      if (var1 == null) {
         throw new NullPointerException("category");
      } else if (!PrintServiceAttribute.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException("Not a PrintServiceAttribute");
      } else {
         return var1 == ColorSupported.class ? ColorSupported.SUPPORTED : null;
      }
   }

   public PrintServiceAttributeSet getAttributes() {
      HashPrintServiceAttributeSet var1 = new HashPrintServiceAttributeSet();
      var1.add(ColorSupported.SUPPORTED);
      return AttributeSetUtilities.unmodifiableView((PrintServiceAttributeSet)var1);
   }

   public boolean isDocFlavorSupported(DocFlavor var1) {
      DocFlavor[] var2 = this.getSupportedDocFlavors();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var1.equals(var2[var3])) {
            return true;
         }
      }

      return false;
   }

   public Class<?>[] getSupportedAttributeCategories() {
      Class[] var1 = new Class[suppAttrCats.length];
      System.arraycopy(suppAttrCats, 0, var1, 0, var1.length);
      return var1;
   }

   public boolean isAttributeCategorySupported(Class<? extends Attribute> var1) {
      if (var1 == null) {
         throw new NullPointerException("null category");
      } else if (!Attribute.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException(var1 + " is not an Attribute");
      } else {
         for(int var2 = 0; var2 < suppAttrCats.length; ++var2) {
            if (var1 == suppAttrCats[var2]) {
               return true;
            }
         }

         return false;
      }
   }

   public Object getDefaultAttributeValue(Class<? extends Attribute> var1) {
      if (var1 == null) {
         throw new NullPointerException("null category");
      } else if (!Attribute.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException(var1 + " is not an Attribute");
      } else if (!this.isAttributeCategorySupported(var1)) {
         return null;
      } else if (var1 == Copies.class) {
         return new Copies(1);
      } else if (var1 == Chromaticity.class) {
         return Chromaticity.COLOR;
      } else if (var1 == Fidelity.class) {
         return Fidelity.FIDELITY_FALSE;
      } else {
         String var2;
         if (var1 == Media.class) {
            var2 = Locale.getDefault().getCountry();
            return var2 == null || !var2.equals("") && !var2.equals(Locale.US.getCountry()) && !var2.equals(Locale.CANADA.getCountry()) ? MediaSizeName.ISO_A4 : MediaSizeName.NA_LETTER;
         } else if (var1 != MediaPrintableArea.class) {
            if (var1 == OrientationRequested.class) {
               return OrientationRequested.PORTRAIT;
            } else if (var1 == PageRanges.class) {
               return new PageRanges(1, Integer.MAX_VALUE);
            } else if (var1 == SheetCollate.class) {
               return SheetCollate.UNCOLLATED;
            } else {
               return var1 == Sides.class ? Sides.ONE_SIDED : null;
            }
         } else {
            var2 = Locale.getDefault().getCountry();
            float var5 = 0.5F;
            float var3;
            float var4;
            if (var2 == null || !var2.equals("") && !var2.equals(Locale.US.getCountry()) && !var2.equals(Locale.CANADA.getCountry())) {
               var3 = MediaSize.ISO.A4.getX(25400) - 2.0F * var5;
               var4 = MediaSize.ISO.A4.getY(25400) - 2.0F * var5;
            } else {
               var3 = MediaSize.NA.LETTER.getX(25400) - 2.0F * var5;
               var4 = MediaSize.NA.LETTER.getY(25400) - 2.0F * var5;
            }

            return new MediaPrintableArea(var5, var5, var3, var4, 25400);
         }
      }
   }

   public Object getSupportedAttributeValues(Class<? extends Attribute> var1, DocFlavor var2, AttributeSet var3) {
      if (var1 == null) {
         throw new NullPointerException("null category");
      } else if (!Attribute.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException(var1 + " does not implement Attribute");
      } else if (var2 != null && !this.isDocFlavorSupported(var2)) {
         throw new IllegalArgumentException(var2 + " is an unsupported flavor");
      } else if (!this.isAttributeCategorySupported(var1)) {
         return null;
      } else if (var1 == Chromaticity.class) {
         Chromaticity[] var18 = new Chromaticity[]{Chromaticity.COLOR};
         return var18;
      } else if (var1 == JobName.class) {
         return new JobName("", (Locale)null);
      } else if (var1 == RequestingUserName.class) {
         return new RequestingUserName("", (Locale)null);
      } else if (var1 == OrientationRequested.class) {
         if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
            return null;
         } else {
            OrientationRequested[] var17 = new OrientationRequested[]{OrientationRequested.PORTRAIT, OrientationRequested.LANDSCAPE, OrientationRequested.REVERSE_LANDSCAPE};
            return var17;
         }
      } else if (var1 != Copies.class && var1 != CopiesSupported.class) {
         if (var1 == Media.class) {
            Media[] var16 = new Media[mediaSizes.length];
            System.arraycopy(mediaSizes, 0, var16, 0, mediaSizes.length);
            return var16;
         } else if (var1 == Fidelity.class) {
            Fidelity[] var14 = new Fidelity[]{Fidelity.FIDELITY_FALSE, Fidelity.FIDELITY_TRUE};
            return var14;
         } else if (var1 == MediaPrintableArea.class) {
            if (var3 == null) {
               return null;
            } else {
               MediaSize var12 = (MediaSize)var3.get(MediaSize.class);
               if (var12 == null) {
                  Media var5 = (Media)var3.get(Media.class);
                  if (var5 != null && var5 instanceof MediaSizeName) {
                     MediaSizeName var6 = (MediaSizeName)var5;
                     var12 = MediaSize.getMediaSizeForName(var6);
                  }
               }

               if (var12 == null) {
                  return null;
               } else {
                  MediaPrintableArea[] var13 = new MediaPrintableArea[1];
                  float var15 = var12.getX(25400);
                  float var7 = var12.getY(25400);
                  float var8 = 0.5F;
                  float var9 = 0.5F;
                  if (var15 < 5.0F) {
                     var8 = var15 / 10.0F;
                  }

                  if (var7 < 5.0F) {
                     var9 = var7 / 10.0F;
                  }

                  var13[0] = new MediaPrintableArea(var8, var9, var15 - 2.0F * var8, var7 - 2.0F * var9, 25400);
                  return var13;
               }
            }
         } else if (var1 == PageRanges.class) {
            if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
               return null;
            } else {
               PageRanges[] var11 = new PageRanges[]{new PageRanges(1, Integer.MAX_VALUE)};
               return var11;
            }
         } else if (var1 == SheetCollate.class) {
            SheetCollate[] var10;
            if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
               var10 = new SheetCollate[]{SheetCollate.UNCOLLATED};
               return var10;
            } else {
               var10 = new SheetCollate[]{SheetCollate.UNCOLLATED, SheetCollate.COLLATED};
               return var10;
            }
         } else if (var1 == Sides.class) {
            if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
               return null;
            } else {
               Sides[] var4 = new Sides[]{Sides.ONE_SIDED, Sides.TWO_SIDED_LONG_EDGE, Sides.TWO_SIDED_SHORT_EDGE};
               return var4;
            }
         } else {
            return null;
         }
      } else {
         return new CopiesSupported(1, MAXCOPIES);
      }
   }

   private boolean isSupportedCopies(Copies var1) {
      int var2 = var1.getValue();
      return var2 > 0 && var2 < MAXCOPIES;
   }

   private boolean isSupportedMedia(MediaSizeName var1) {
      for(int var2 = 0; var2 < mediaSizes.length; ++var2) {
         if (var1.equals(mediaSizes[var2])) {
            return true;
         }
      }

      return false;
   }

   public boolean isAttributeValueSupported(Attribute var1, DocFlavor var2, AttributeSet var3) {
      if (var1 == null) {
         throw new NullPointerException("null attribute");
      } else if (var2 != null && !this.isDocFlavorSupported(var2)) {
         throw new IllegalArgumentException(var2 + " is an unsupported flavor");
      } else {
         Class var4 = var1.getCategory();
         if (!this.isAttributeCategorySupported(var4)) {
            return false;
         } else if (var1.getCategory() == Chromaticity.class) {
            return var1 == Chromaticity.COLOR;
         } else if (var1.getCategory() == Copies.class) {
            return this.isSupportedCopies((Copies)var1);
         } else if (var1.getCategory() == Media.class && var1 instanceof MediaSizeName) {
            return this.isSupportedMedia((MediaSizeName)var1);
         } else {
            if (var1.getCategory() == OrientationRequested.class) {
               if (var1 == OrientationRequested.REVERSE_PORTRAIT || var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                  return false;
               }
            } else if (var1.getCategory() == PageRanges.class) {
               if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                  return false;
               }
            } else if (var1.getCategory() == SheetCollate.class) {
               if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                  return false;
               }
            } else if (var1.getCategory() == Sides.class && var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
               return false;
            }

            return true;
         }
      }
   }

   public AttributeSet getUnsupportedAttributes(DocFlavor var1, AttributeSet var2) {
      if (var1 != null && !this.isDocFlavorSupported(var1)) {
         throw new IllegalArgumentException("flavor " + var1 + "is not supported");
      } else if (var2 == null) {
         return null;
      } else {
         HashAttributeSet var4 = new HashAttributeSet();
         Attribute[] var5 = var2.toArray();

         for(int var6 = 0; var6 < var5.length; ++var6) {
            try {
               Attribute var3 = var5[var6];
               if (!this.isAttributeCategorySupported(var3.getCategory())) {
                  var4.add(var3);
               } else if (!this.isAttributeValueSupported(var3, var1, var2)) {
                  var4.add(var3);
               }
            } catch (ClassCastException var8) {
            }
         }

         if (var4.isEmpty()) {
            return null;
         } else {
            return var4;
         }
      }
   }

   public ServiceUIFactory getServiceUIFactory() {
      return null;
   }

   public String toString() {
      return "PSStreamPrintService: " + this.getName();
   }

   public boolean equals(Object var1) {
      return var1 == this || var1 instanceof PSStreamPrintService && ((PSStreamPrintService)var1).getName().equals(this.getName());
   }

   public int hashCode() {
      return this.getClass().hashCode() + this.getName().hashCode();
   }

   static {
      mediaSizes = new MediaSizeName[]{MediaSizeName.NA_LETTER, MediaSizeName.TABLOID, MediaSizeName.LEDGER, MediaSizeName.NA_LEGAL, MediaSizeName.EXECUTIVE, MediaSizeName.ISO_A3, MediaSizeName.ISO_A4, MediaSizeName.ISO_A5, MediaSizeName.ISO_B4, MediaSizeName.ISO_B5};
   }
}
