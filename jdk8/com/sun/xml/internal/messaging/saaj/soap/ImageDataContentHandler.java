package com.sun.xml.internal.messaging.saaj.soap;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageDataContentHandler extends Component implements DataContentHandler {
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
   private DataFlavor[] flavor;

   public ImageDataContentHandler() {
      String[] mimeTypes = ImageIO.getReaderMIMETypes();
      this.flavor = new DataFlavor[mimeTypes.length];

      for(int i = 0; i < mimeTypes.length; ++i) {
         this.flavor[i] = new ActivationDataFlavor(Image.class, mimeTypes[i], "Image");
      }

   }

   public DataFlavor[] getTransferDataFlavors() {
      return (DataFlavor[])((DataFlavor[])Arrays.copyOf((Object[])this.flavor, this.flavor.length));
   }

   public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
      for(int i = 0; i < this.flavor.length; ++i) {
         if (this.flavor[i].equals(df)) {
            return this.getContent(ds);
         }
      }

      return null;
   }

   public Object getContent(DataSource ds) throws IOException {
      return ImageIO.read((InputStream)(new BufferedInputStream(ds.getInputStream())));
   }

   public void writeTo(Object obj, String type, OutputStream os) throws IOException {
      try {
         BufferedImage bufImage = null;
         if (obj instanceof BufferedImage) {
            bufImage = (BufferedImage)obj;
         } else {
            if (!(obj instanceof Image)) {
               log.log(Level.SEVERE, (String)"SAAJ0520.soap.invalid.obj.type", (Object[])(new String[]{obj.getClass().toString()}));
               throw new IOException("ImageDataContentHandler requires Image object, was given object of type " + obj.getClass().toString());
            }

            bufImage = this.render((Image)obj);
         }

         ImageWriter writer = null;
         Iterator i = ImageIO.getImageWritersByMIMEType(type);
         if (i.hasNext()) {
            writer = (ImageWriter)i.next();
         }

         if (writer != null) {
            ImageOutputStream stream = null;
            stream = ImageIO.createImageOutputStream(os);
            writer.setOutput(stream);
            writer.write((RenderedImage)bufImage);
            writer.dispose();
            stream.close();
         } else {
            log.log(Level.SEVERE, (String)"SAAJ0526.soap.unsupported.mime.type", (Object[])(new String[]{type}));
            throw new IOException("Unsupported mime type:" + type);
         }
      } catch (Exception var8) {
         log.severe("SAAJ0525.soap.cannot.encode.img");
         throw new IOException("Unable to encode the image to a stream " + var8.getMessage());
      }
   }

   private BufferedImage render(Image img) throws InterruptedException {
      MediaTracker tracker = new MediaTracker(this);
      tracker.addImage(img, 0);
      tracker.waitForAll();
      BufferedImage bufImage = new BufferedImage(img.getWidth((ImageObserver)null), img.getHeight((ImageObserver)null), 1);
      Graphics g = bufImage.createGraphics();
      g.drawImage(img, 0, 0, (ImageObserver)null);
      g.dispose();
      return bufImage;
   }
}
