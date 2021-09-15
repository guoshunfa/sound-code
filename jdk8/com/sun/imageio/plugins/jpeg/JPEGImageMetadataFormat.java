package com.sun.imageio.plugins.jpeg;

import java.awt.color.ICC_Profile;
import java.util.ArrayList;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;

public class JPEGImageMetadataFormat extends JPEGMetadataFormat {
   private static JPEGImageMetadataFormat theInstance = null;

   private JPEGImageMetadataFormat() {
      super("javax_imageio_jpeg_image_1.0", 1);
      this.addElement("JPEGvariety", "javax_imageio_jpeg_image_1.0", 3);
      this.addElement("markerSequence", "javax_imageio_jpeg_image_1.0", 4);
      this.addElement("app0JFIF", "JPEGvariety", 2);
      this.addStreamElements("markerSequence");
      this.addElement("app14Adobe", "markerSequence", 0);
      this.addElement("sof", "markerSequence", 1, 4);
      this.addElement("sos", "markerSequence", 1, 4);
      this.addElement("JFXX", "app0JFIF", 1, Integer.MAX_VALUE);
      this.addElement("app0JFXX", "JFXX", 3);
      this.addElement("app2ICC", "app0JFIF", 0);
      this.addAttribute("app0JFIF", "majorVersion", 2, false, "1", "0", "255", true, true);
      this.addAttribute("app0JFIF", "minorVersion", 2, false, "2", "0", "255", true, true);
      ArrayList var1 = new ArrayList();
      var1.add("0");
      var1.add("1");
      var1.add("2");
      this.addAttribute("app0JFIF", "resUnits", 2, false, "0", var1);
      this.addAttribute("app0JFIF", "Xdensity", 2, false, "1", "1", "65535", true, true);
      this.addAttribute("app0JFIF", "Ydensity", 2, false, "1", "1", "65535", true, true);
      this.addAttribute("app0JFIF", "thumbWidth", 2, false, "0", "0", "255", true, true);
      this.addAttribute("app0JFIF", "thumbHeight", 2, false, "0", "0", "255", true, true);
      this.addElement("JFIFthumbJPEG", "app0JFXX", 2);
      this.addElement("JFIFthumbPalette", "app0JFXX", 0);
      this.addElement("JFIFthumbRGB", "app0JFXX", 0);
      ArrayList var2 = new ArrayList();
      var2.add("16");
      var2.add("17");
      var2.add("19");
      this.addAttribute("app0JFXX", "extensionCode", 2, false, (String)null, var2);
      this.addChildElement("markerSequence", "JFIFthumbJPEG");
      this.addAttribute("JFIFthumbPalette", "thumbWidth", 2, false, (String)null, "0", "255", true, true);
      this.addAttribute("JFIFthumbPalette", "thumbHeight", 2, false, (String)null, "0", "255", true, true);
      this.addAttribute("JFIFthumbRGB", "thumbWidth", 2, false, (String)null, "0", "255", true, true);
      this.addAttribute("JFIFthumbRGB", "thumbHeight", 2, false, (String)null, "0", "255", true, true);
      this.addObjectValue("app2ICC", ICC_Profile.class, false, (Object)null);
      this.addAttribute("app14Adobe", "version", 2, false, "100", "100", "255", true, true);
      this.addAttribute("app14Adobe", "flags0", 2, false, "0", "0", "65535", true, true);
      this.addAttribute("app14Adobe", "flags1", 2, false, "0", "0", "65535", true, true);
      ArrayList var3 = new ArrayList();
      var3.add("0");
      var3.add("1");
      var3.add("2");
      this.addAttribute("app14Adobe", "transform", 2, true, (String)null, var3);
      this.addElement("componentSpec", "sof", 0);
      ArrayList var4 = new ArrayList();
      var4.add("0");
      var4.add("1");
      var4.add("2");
      this.addAttribute("sof", "process", 2, false, (String)null, var4);
      this.addAttribute("sof", "samplePrecision", 2, false, "8");
      this.addAttribute("sof", "numLines", 2, false, (String)null, "0", "65535", true, true);
      this.addAttribute("sof", "samplesPerLine", 2, false, (String)null, "0", "65535", true, true);
      ArrayList var5 = new ArrayList();
      var5.add("1");
      var5.add("2");
      var5.add("3");
      var5.add("4");
      this.addAttribute("sof", "numFrameComponents", 2, false, (String)null, var5);
      this.addAttribute("componentSpec", "componentId", 2, true, (String)null, "0", "255", true, true);
      this.addAttribute("componentSpec", "HsamplingFactor", 2, true, (String)null, "1", "255", true, true);
      this.addAttribute("componentSpec", "VsamplingFactor", 2, true, (String)null, "1", "255", true, true);
      ArrayList var6 = new ArrayList();
      var6.add("0");
      var6.add("1");
      var6.add("2");
      var6.add("3");
      this.addAttribute("componentSpec", "QtableSelector", 2, true, (String)null, var6);
      this.addElement("scanComponentSpec", "sos", 0);
      this.addAttribute("sos", "numScanComponents", 2, true, (String)null, var5);
      this.addAttribute("sos", "startSpectralSelection", 2, false, "0", "0", "63", true, true);
      this.addAttribute("sos", "endSpectralSelection", 2, false, "63", "0", "63", true, true);
      this.addAttribute("sos", "approxHigh", 2, false, "0", "0", "15", true, true);
      this.addAttribute("sos", "approxLow", 2, false, "0", "0", "15", true, true);
      this.addAttribute("scanComponentSpec", "componentSelector", 2, true, (String)null, "0", "255", true, true);
      this.addAttribute("scanComponentSpec", "dcHuffTable", 2, true, (String)null, var6);
      this.addAttribute("scanComponentSpec", "acHuffTable", 2, true, (String)null, var6);
   }

   public boolean canNodeAppear(String var1, ImageTypeSpecifier var2) {
      if (!var1.equals(this.getRootName()) && !var1.equals("JPEGvariety") && !this.isInSubtree(var1, "markerSequence")) {
         return this.isInSubtree(var1, "app0JFIF") && JPEG.isJFIFcompliant(var2, true);
      } else {
         return true;
      }
   }

   public static synchronized IIOMetadataFormat getInstance() {
      if (theInstance == null) {
         theInstance = new JPEGImageMetadataFormat();
      }

      return theInstance;
   }
}
