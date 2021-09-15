package javax.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.beans.ConstructorProperties;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import sun.awt.AppContext;

public class ImageIcon implements Icon, Serializable, Accessible {
   private transient String filename;
   private transient URL location;
   transient Image image;
   transient int loadStatus;
   ImageObserver imageObserver;
   String description;
   /** @deprecated */
   @Deprecated
   protected static final Component component = (Component)AccessController.doPrivileged(new PrivilegedAction<Component>() {
      public Component run() {
         try {
            Component var1 = ImageIcon.createNoPermsComponent();
            Field var2 = Component.class.getDeclaredField("appContext");
            var2.setAccessible(true);
            var2.set(var1, (Object)null);
            return var1;
         } catch (Throwable var3) {
            var3.printStackTrace();
            return null;
         }
      }
   });
   /** @deprecated */
   @Deprecated
   protected static final MediaTracker tracker;
   private static int mediaTrackerID;
   private static final Object TRACKER_KEY;
   int width;
   int height;
   private ImageIcon.AccessibleImageIcon accessibleContext;

   private static Component createNoPermsComponent() {
      return (Component)AccessController.doPrivileged(new PrivilegedAction<Component>() {
         public Component run() {
            return new Component() {
            };
         }
      }, new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain((CodeSource)null, (PermissionCollection)null)}));
   }

   public ImageIcon(String var1, String var2) {
      this.loadStatus = 0;
      this.description = null;
      this.width = -1;
      this.height = -1;
      this.accessibleContext = null;
      this.image = Toolkit.getDefaultToolkit().getImage(var1);
      if (this.image != null) {
         this.filename = var1;
         this.description = var2;
         this.loadImage(this.image);
      }
   }

   @ConstructorProperties({"description"})
   public ImageIcon(String var1) {
      this(var1, var1);
   }

   public ImageIcon(URL var1, String var2) {
      this.loadStatus = 0;
      this.description = null;
      this.width = -1;
      this.height = -1;
      this.accessibleContext = null;
      this.image = Toolkit.getDefaultToolkit().getImage(var1);
      if (this.image != null) {
         this.location = var1;
         this.description = var2;
         this.loadImage(this.image);
      }
   }

   public ImageIcon(URL var1) {
      this(var1, var1.toExternalForm());
   }

   public ImageIcon(Image var1, String var2) {
      this(var1);
      this.description = var2;
   }

   public ImageIcon(Image var1) {
      this.loadStatus = 0;
      this.description = null;
      this.width = -1;
      this.height = -1;
      this.accessibleContext = null;
      this.image = var1;
      Object var2 = var1.getProperty("comment", this.imageObserver);
      if (var2 instanceof String) {
         this.description = (String)var2;
      }

      this.loadImage(var1);
   }

   public ImageIcon(byte[] var1, String var2) {
      this.loadStatus = 0;
      this.description = null;
      this.width = -1;
      this.height = -1;
      this.accessibleContext = null;
      this.image = Toolkit.getDefaultToolkit().createImage(var1);
      if (this.image != null) {
         this.description = var2;
         this.loadImage(this.image);
      }
   }

   public ImageIcon(byte[] var1) {
      this.loadStatus = 0;
      this.description = null;
      this.width = -1;
      this.height = -1;
      this.accessibleContext = null;
      this.image = Toolkit.getDefaultToolkit().createImage(var1);
      if (this.image != null) {
         Object var2 = this.image.getProperty("comment", this.imageObserver);
         if (var2 instanceof String) {
            this.description = (String)var2;
         }

         this.loadImage(this.image);
      }
   }

   public ImageIcon() {
      this.loadStatus = 0;
      this.description = null;
      this.width = -1;
      this.height = -1;
      this.accessibleContext = null;
   }

   protected void loadImage(Image var1) {
      MediaTracker var2 = this.getTracker();
      synchronized(var2) {
         int var4 = this.getNextID();
         var2.addImage(var1, var4);

         try {
            var2.waitForID(var4, 0L);
         } catch (InterruptedException var7) {
            System.out.println("INTERRUPTED while loading Image");
         }

         this.loadStatus = var2.statusID(var4, false);
         var2.removeImage(var1, var4);
         this.width = var1.getWidth(this.imageObserver);
         this.height = var1.getHeight(this.imageObserver);
      }
   }

   private int getNextID() {
      synchronized(this.getTracker()) {
         return ++mediaTrackerID;
      }
   }

   private MediaTracker getTracker() {
      AppContext var2 = AppContext.getAppContext();
      Object var1;
      synchronized(var2) {
         var1 = var2.get(TRACKER_KEY);
         if (var1 == null) {
            Component var4 = new Component() {
            };
            var1 = new MediaTracker(var4);
            var2.put(TRACKER_KEY, var1);
         }
      }

      return (MediaTracker)var1;
   }

   public int getImageLoadStatus() {
      return this.loadStatus;
   }

   @Transient
   public Image getImage() {
      return this.image;
   }

   public void setImage(Image var1) {
      this.image = var1;
      this.loadImage(var1);
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String var1) {
      this.description = var1;
   }

   public synchronized void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      if (this.imageObserver == null) {
         var2.drawImage(this.image, var3, var4, var1);
      } else {
         var2.drawImage(this.image, var3, var4, this.imageObserver);
      }

   }

   public int getIconWidth() {
      return this.width;
   }

   public int getIconHeight() {
      return this.height;
   }

   public void setImageObserver(ImageObserver var1) {
      this.imageObserver = var1;
   }

   @Transient
   public ImageObserver getImageObserver() {
      return this.imageObserver;
   }

   public String toString() {
      return this.description != null ? this.description : super.toString();
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      int var2 = var1.readInt();
      int var3 = var1.readInt();
      int[] var4 = (int[])((int[])var1.readObject());
      if (var4 != null) {
         Toolkit var5 = Toolkit.getDefaultToolkit();
         ColorModel var6 = ColorModel.getRGBdefault();
         this.image = var5.createImage((ImageProducer)(new MemoryImageSource(var2, var3, var6, var4, 0, var2)));
         this.loadImage(this.image);
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      int var2 = this.getIconWidth();
      int var3 = this.getIconHeight();
      int[] var4 = this.image != null ? new int[var2 * var3] : null;
      if (this.image != null) {
         try {
            PixelGrabber var5 = new PixelGrabber(this.image, 0, 0, var2, var3, var4, 0, var2);
            var5.grabPixels();
            if ((var5.getStatus() & 128) != 0) {
               throw new IOException("failed to load image contents");
            }
         } catch (InterruptedException var6) {
            throw new IOException("image load interrupted");
         }
      }

      var1.writeInt(var2);
      var1.writeInt(var3);
      var1.writeObject(var4);
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new ImageIcon.AccessibleImageIcon();
      }

      return this.accessibleContext;
   }

   static {
      tracker = new MediaTracker(component);
      TRACKER_KEY = new StringBuilder("TRACKER_KEY");
   }

   protected class AccessibleImageIcon extends AccessibleContext implements AccessibleIcon, Serializable {
      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.ICON;
      }

      public AccessibleStateSet getAccessibleStateSet() {
         return null;
      }

      public Accessible getAccessibleParent() {
         return null;
      }

      public int getAccessibleIndexInParent() {
         return -1;
      }

      public int getAccessibleChildrenCount() {
         return 0;
      }

      public Accessible getAccessibleChild(int var1) {
         return null;
      }

      public Locale getLocale() throws IllegalComponentStateException {
         return null;
      }

      public String getAccessibleIconDescription() {
         return ImageIcon.this.getDescription();
      }

      public void setAccessibleIconDescription(String var1) {
         ImageIcon.this.setDescription(var1);
      }

      public int getAccessibleIconHeight() {
         return ImageIcon.this.height;
      }

      public int getAccessibleIconWidth() {
         return ImageIcon.this.width;
      }

      private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
         var1.defaultReadObject();
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         var1.defaultWriteObject();
      }
   }
}
