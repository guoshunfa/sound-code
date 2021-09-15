package javax.imageio.metadata;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.w3c.dom.Node;

public abstract class IIOMetadata {
   protected boolean standardFormatSupported;
   protected String nativeMetadataFormatName = null;
   protected String nativeMetadataFormatClassName = null;
   protected String[] extraMetadataFormatNames = null;
   protected String[] extraMetadataFormatClassNames = null;
   protected IIOMetadataController defaultController = null;
   protected IIOMetadataController controller = null;

   protected IIOMetadata() {
   }

   protected IIOMetadata(boolean var1, String var2, String var3, String[] var4, String[] var5) {
      this.standardFormatSupported = var1;
      this.nativeMetadataFormatName = var2;
      this.nativeMetadataFormatClassName = var3;
      if (var4 != null) {
         if (var4.length == 0) {
            throw new IllegalArgumentException("extraMetadataFormatNames.length == 0!");
         }

         if (var5 == null) {
            throw new IllegalArgumentException("extraMetadataFormatNames != null && extraMetadataFormatClassNames == null!");
         }

         if (var5.length != var4.length) {
            throw new IllegalArgumentException("extraMetadataFormatClassNames.length != extraMetadataFormatNames.length!");
         }

         this.extraMetadataFormatNames = (String[])((String[])var4.clone());
         this.extraMetadataFormatClassNames = (String[])((String[])var5.clone());
      } else if (var5 != null) {
         throw new IllegalArgumentException("extraMetadataFormatNames == null && extraMetadataFormatClassNames != null!");
      }

   }

   public boolean isStandardMetadataFormatSupported() {
      return this.standardFormatSupported;
   }

   public abstract boolean isReadOnly();

   public String getNativeMetadataFormatName() {
      return this.nativeMetadataFormatName;
   }

   public String[] getExtraMetadataFormatNames() {
      return this.extraMetadataFormatNames == null ? null : (String[])((String[])this.extraMetadataFormatNames.clone());
   }

   public String[] getMetadataFormatNames() {
      String var1 = this.getNativeMetadataFormatName();
      String var2 = this.isStandardMetadataFormatSupported() ? "javax_imageio_1.0" : null;
      String[] var3 = this.getExtraMetadataFormatNames();
      int var4 = 0;
      if (var1 != null) {
         ++var4;
      }

      if (var2 != null) {
         ++var4;
      }

      if (var3 != null) {
         var4 += var3.length;
      }

      if (var4 == 0) {
         return null;
      } else {
         String[] var5 = new String[var4];
         int var6 = 0;
         if (var1 != null) {
            var5[var6++] = var1;
         }

         if (var2 != null) {
            var5[var6++] = var2;
         }

         if (var3 != null) {
            for(int var7 = 0; var7 < var3.length; ++var7) {
               var5[var6++] = var3[var7];
            }
         }

         return var5;
      }
   }

   public IIOMetadataFormat getMetadataFormat(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("formatName == null!");
      } else if (this.standardFormatSupported && var1.equals("javax_imageio_1.0")) {
         return IIOMetadataFormatImpl.getStandardFormatInstance();
      } else {
         String var2 = null;
         if (var1.equals(this.nativeMetadataFormatName)) {
            var2 = this.nativeMetadataFormatClassName;
         } else if (this.extraMetadataFormatNames != null) {
            for(int var3 = 0; var3 < this.extraMetadataFormatNames.length; ++var3) {
               if (var1.equals(this.extraMetadataFormatNames[var3])) {
                  var2 = this.extraMetadataFormatClassNames[var3];
                  break;
               }
            }
         }

         if (var2 == null) {
            throw new IllegalArgumentException("Unsupported format name");
         } else {
            try {
               Class var11 = null;
               ClassLoader var5 = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
                  public Object run() {
                     return IIOMetadata.this.getClass().getClassLoader();
                  }
               });

               try {
                  var11 = Class.forName(var2, true, var5);
               } catch (ClassNotFoundException var9) {
                  var5 = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
                     public Object run() {
                        return Thread.currentThread().getContextClassLoader();
                     }
                  });

                  try {
                     var11 = Class.forName(var2, true, var5);
                  } catch (ClassNotFoundException var8) {
                     var11 = Class.forName(var2, true, ClassLoader.getSystemClassLoader());
                  }
               }

               Method var6 = var11.getMethod("getInstance");
               return (IIOMetadataFormat)var6.invoke((Object)null);
            } catch (Exception var10) {
               IllegalStateException var4 = new IllegalStateException("Can't obtain format");
               var4.initCause(var10);
               throw var4;
            }
         }
      }
   }

   public abstract Node getAsTree(String var1);

   public abstract void mergeTree(String var1, Node var2) throws IIOInvalidTreeException;

   protected IIOMetadataNode getStandardChromaNode() {
      return null;
   }

   protected IIOMetadataNode getStandardCompressionNode() {
      return null;
   }

   protected IIOMetadataNode getStandardDataNode() {
      return null;
   }

   protected IIOMetadataNode getStandardDimensionNode() {
      return null;
   }

   protected IIOMetadataNode getStandardDocumentNode() {
      return null;
   }

   protected IIOMetadataNode getStandardTextNode() {
      return null;
   }

   protected IIOMetadataNode getStandardTileNode() {
      return null;
   }

   protected IIOMetadataNode getStandardTransparencyNode() {
      return null;
   }

   private void append(IIOMetadataNode var1, IIOMetadataNode var2) {
      if (var2 != null) {
         var1.appendChild(var2);
      }

   }

   protected final IIOMetadataNode getStandardTree() {
      IIOMetadataNode var1 = new IIOMetadataNode("javax_imageio_1.0");
      this.append(var1, this.getStandardChromaNode());
      this.append(var1, this.getStandardCompressionNode());
      this.append(var1, this.getStandardDataNode());
      this.append(var1, this.getStandardDimensionNode());
      this.append(var1, this.getStandardDocumentNode());
      this.append(var1, this.getStandardTextNode());
      this.append(var1, this.getStandardTileNode());
      this.append(var1, this.getStandardTransparencyNode());
      return var1;
   }

   public void setFromTree(String var1, Node var2) throws IIOInvalidTreeException {
      this.reset();
      this.mergeTree(var1, var2);
   }

   public abstract void reset();

   public void setController(IIOMetadataController var1) {
      this.controller = var1;
   }

   public IIOMetadataController getController() {
      return this.controller;
   }

   public IIOMetadataController getDefaultController() {
      return this.defaultController;
   }

   public boolean hasController() {
      return this.getController() != null;
   }

   public boolean activateController() {
      if (!this.hasController()) {
         throw new IllegalStateException("hasController() == false!");
      } else {
         return this.getController().activate(this);
      }
   }
}
