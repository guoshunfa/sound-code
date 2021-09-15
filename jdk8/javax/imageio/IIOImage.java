package javax.imageio;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.List;
import javax.imageio.metadata.IIOMetadata;

public class IIOImage {
   protected RenderedImage image;
   protected Raster raster;
   protected List<? extends BufferedImage> thumbnails = null;
   protected IIOMetadata metadata;

   public IIOImage(RenderedImage var1, List<? extends BufferedImage> var2, IIOMetadata var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("image == null!");
      } else {
         this.image = var1;
         this.raster = null;
         this.thumbnails = var2;
         this.metadata = var3;
      }
   }

   public IIOImage(Raster var1, List<? extends BufferedImage> var2, IIOMetadata var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("raster == null!");
      } else {
         this.raster = var1;
         this.image = null;
         this.thumbnails = var2;
         this.metadata = var3;
      }
   }

   public RenderedImage getRenderedImage() {
      synchronized(this) {
         return this.image;
      }
   }

   public void setRenderedImage(RenderedImage var1) {
      synchronized(this) {
         if (var1 == null) {
            throw new IllegalArgumentException("image == null!");
         } else {
            this.image = var1;
            this.raster = null;
         }
      }
   }

   public boolean hasRaster() {
      synchronized(this) {
         return this.raster != null;
      }
   }

   public Raster getRaster() {
      synchronized(this) {
         return this.raster;
      }
   }

   public void setRaster(Raster var1) {
      synchronized(this) {
         if (var1 == null) {
            throw new IllegalArgumentException("raster == null!");
         } else {
            this.raster = var1;
            this.image = null;
         }
      }
   }

   public int getNumThumbnails() {
      return this.thumbnails == null ? 0 : this.thumbnails.size();
   }

   public BufferedImage getThumbnail(int var1) {
      if (this.thumbnails == null) {
         throw new IndexOutOfBoundsException("No thumbnails available!");
      } else {
         return (BufferedImage)this.thumbnails.get(var1);
      }
   }

   public List<? extends BufferedImage> getThumbnails() {
      return this.thumbnails;
   }

   public void setThumbnails(List<? extends BufferedImage> var1) {
      this.thumbnails = var1;
   }

   public IIOMetadata getMetadata() {
      return this.metadata;
   }

   public void setMetadata(IIOMetadata var1) {
      this.metadata = var1;
   }
}
