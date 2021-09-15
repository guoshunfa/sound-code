package java.awt.datatransfer;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OptionalDataException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import sun.awt.datatransfer.DataTransferer;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public class DataFlavor implements Externalizable, Cloneable {
   private static final long serialVersionUID = 8367026044764648243L;
   private static final Class<InputStream> ioInputStreamClass = InputStream.class;
   public static final DataFlavor stringFlavor = createConstant(String.class, "Unicode String");
   public static final DataFlavor imageFlavor = createConstant("image/x-java-image; class=java.awt.Image", "Image");
   /** @deprecated */
   @Deprecated
   public static final DataFlavor plainTextFlavor = createConstant("text/plain; charset=unicode; class=java.io.InputStream", "Plain Text");
   public static final String javaSerializedObjectMimeType = "application/x-java-serialized-object";
   public static final DataFlavor javaFileListFlavor = createConstant((String)"application/x-java-file-list;class=java.util.List", (String)null);
   public static final String javaJVMLocalObjectMimeType = "application/x-java-jvm-local-objectref";
   public static final String javaRemoteObjectMimeType = "application/x-java-remote-object";
   public static DataFlavor selectionHtmlFlavor = initHtmlDataFlavor("selection");
   public static DataFlavor fragmentHtmlFlavor = initHtmlDataFlavor("fragment");
   public static DataFlavor allHtmlFlavor = initHtmlDataFlavor("all");
   private static Comparator<DataFlavor> textFlavorComparator;
   transient int atom;
   MimeType mimeType;
   private String humanPresentableName;
   private Class<?> representationClass;

   protected static final Class<?> tryToLoadClass(String var0, ClassLoader var1) throws ClassNotFoundException {
      ReflectUtil.checkPackageAccess(var0);

      try {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            var2.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
         }

         ClassLoader var3 = ClassLoader.getSystemClassLoader();

         try {
            return Class.forName(var0, true, var3);
         } catch (ClassNotFoundException var7) {
            var3 = Thread.currentThread().getContextClassLoader();
            if (var3 != null) {
               try {
                  return Class.forName(var0, true, var3);
               } catch (ClassNotFoundException var6) {
               }
            }
         }
      } catch (SecurityException var8) {
      }

      return Class.forName(var0, true, var1);
   }

   private static DataFlavor createConstant(Class<?> var0, String var1) {
      try {
         return new DataFlavor(var0, var1);
      } catch (Exception var3) {
         return null;
      }
   }

   private static DataFlavor createConstant(String var0, String var1) {
      try {
         return new DataFlavor(var0, var1);
      } catch (Exception var3) {
         return null;
      }
   }

   private static DataFlavor initHtmlDataFlavor(String var0) {
      try {
         return new DataFlavor("text/html; class=java.lang.String;document=" + var0 + ";charset=Unicode");
      } catch (Exception var2) {
         return null;
      }
   }

   public DataFlavor() {
   }

   private DataFlavor(String var1, String var2, MimeTypeParameterList var3, Class<?> var4, String var5) {
      if (var1 == null) {
         throw new NullPointerException("primaryType");
      } else if (var2 == null) {
         throw new NullPointerException("subType");
      } else if (var4 == null) {
         throw new NullPointerException("representationClass");
      } else {
         if (var3 == null) {
            var3 = new MimeTypeParameterList();
         }

         var3.set("class", var4.getName());
         if (var5 == null) {
            var5 = var3.get("humanPresentableName");
            if (var5 == null) {
               var5 = var1 + "/" + var2;
            }
         }

         try {
            this.mimeType = new MimeType(var1, var2, var3);
         } catch (MimeTypeParseException var7) {
            throw new IllegalArgumentException("MimeType Parse Exception: " + var7.getMessage());
         }

         this.representationClass = var4;
         this.humanPresentableName = var5;
         this.mimeType.removeParameter("humanPresentableName");
      }
   }

   public DataFlavor(Class<?> var1, String var2) {
      this("application", "x-java-serialized-object", (MimeTypeParameterList)null, var1, var2);
      if (var1 == null) {
         throw new NullPointerException("representationClass");
      }
   }

   public DataFlavor(String var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("mimeType");
      } else {
         try {
            this.initialize(var1, var2, this.getClass().getClassLoader());
         } catch (MimeTypeParseException var4) {
            throw new IllegalArgumentException("failed to parse:" + var1);
         } catch (ClassNotFoundException var5) {
            throw new IllegalArgumentException("can't find specified class: " + var5.getMessage());
         }
      }
   }

   public DataFlavor(String var1, String var2, ClassLoader var3) throws ClassNotFoundException {
      if (var1 == null) {
         throw new NullPointerException("mimeType");
      } else {
         try {
            this.initialize(var1, var2, var3);
         } catch (MimeTypeParseException var5) {
            throw new IllegalArgumentException("failed to parse:" + var1);
         }
      }
   }

   public DataFlavor(String var1) throws ClassNotFoundException {
      if (var1 == null) {
         throw new NullPointerException("mimeType");
      } else {
         try {
            this.initialize(var1, (String)null, this.getClass().getClassLoader());
         } catch (MimeTypeParseException var3) {
            throw new IllegalArgumentException("failed to parse:" + var1);
         }
      }
   }

   private void initialize(String var1, String var2, ClassLoader var3) throws MimeTypeParseException, ClassNotFoundException {
      if (var1 == null) {
         throw new NullPointerException("mimeType");
      } else {
         this.mimeType = new MimeType(var1);
         String var4 = this.getParameter("class");
         if (var4 == null) {
            if ("application/x-java-serialized-object".equals(this.mimeType.getBaseType())) {
               throw new IllegalArgumentException("no representation class specified for:" + var1);
            }

            this.representationClass = InputStream.class;
         } else {
            this.representationClass = tryToLoadClass(var4, var3);
         }

         this.mimeType.setParameter("class", this.representationClass.getName());
         if (var2 == null) {
            var2 = this.mimeType.getParameter("humanPresentableName");
            if (var2 == null) {
               var2 = this.mimeType.getPrimaryType() + "/" + this.mimeType.getSubType();
            }
         }

         this.humanPresentableName = var2;
         this.mimeType.removeParameter("humanPresentableName");
      }
   }

   public String toString() {
      String var1 = this.getClass().getName();
      var1 = var1 + "[" + this.paramString() + "]";
      return var1;
   }

   private String paramString() {
      String var1 = "";
      var1 = var1 + "mimetype=";
      if (this.mimeType == null) {
         var1 = var1 + "null";
      } else {
         var1 = var1 + this.mimeType.getBaseType();
      }

      var1 = var1 + ";representationclass=";
      if (this.representationClass == null) {
         var1 = var1 + "null";
      } else {
         var1 = var1 + this.representationClass.getName();
      }

      if (DataTransferer.isFlavorCharsetTextType(this) && (this.isRepresentationClassInputStream() || this.isRepresentationClassByteBuffer() || byte[].class.equals(this.representationClass))) {
         var1 = var1 + ";charset=" + DataTransferer.getTextCharset(this);
      }

      return var1;
   }

   public static final DataFlavor getTextPlainUnicodeFlavor() {
      String var0 = null;
      DataTransferer var1 = DataTransferer.getInstance();
      if (var1 != null) {
         var0 = var1.getDefaultUnicodeEncoding();
      }

      return new DataFlavor("text/plain;charset=" + var0 + ";class=java.io.InputStream", "Plain Text");
   }

   public static final DataFlavor selectBestTextFlavor(DataFlavor[] var0) {
      if (var0 != null && var0.length != 0) {
         if (textFlavorComparator == null) {
            textFlavorComparator = new DataFlavor.TextFlavorComparator();
         }

         DataFlavor var1 = (DataFlavor)Collections.max(Arrays.asList(var0), textFlavorComparator);
         return !var1.isFlavorTextType() ? null : var1;
      } else {
         return null;
      }
   }

   public Reader getReaderForText(Transferable var1) throws UnsupportedFlavorException, IOException {
      Object var2 = var1.getTransferData(this);
      if (var2 == null) {
         throw new IllegalArgumentException("getTransferData() returned null");
      } else if (var2 instanceof Reader) {
         return (Reader)var2;
      } else if (var2 instanceof String) {
         return new StringReader((String)var2);
      } else if (var2 instanceof CharBuffer) {
         CharBuffer var8 = (CharBuffer)var2;
         int var9 = var8.remaining();
         char[] var10 = new char[var9];
         var8.get(var10, 0, var9);
         return new CharArrayReader(var10);
      } else if (var2 instanceof char[]) {
         return new CharArrayReader((char[])((char[])var2));
      } else {
         Object var3 = null;
         if (var2 instanceof InputStream) {
            var3 = (InputStream)var2;
         } else if (var2 instanceof ByteBuffer) {
            ByteBuffer var4 = (ByteBuffer)var2;
            int var5 = var4.remaining();
            byte[] var6 = new byte[var5];
            var4.get(var6, 0, var5);
            var3 = new ByteArrayInputStream(var6);
         } else if (var2 instanceof byte[]) {
            var3 = new ByteArrayInputStream((byte[])((byte[])var2));
         }

         if (var3 == null) {
            throw new IllegalArgumentException("transfer data is not Reader, String, CharBuffer, char array, InputStream, ByteBuffer, or byte array");
         } else {
            String var7 = this.getParameter("charset");
            return var7 == null ? new InputStreamReader((InputStream)var3) : new InputStreamReader((InputStream)var3, var7);
         }
      }
   }

   public String getMimeType() {
      return this.mimeType != null ? this.mimeType.toString() : null;
   }

   public Class<?> getRepresentationClass() {
      return this.representationClass;
   }

   public String getHumanPresentableName() {
      return this.humanPresentableName;
   }

   public String getPrimaryType() {
      return this.mimeType != null ? this.mimeType.getPrimaryType() : null;
   }

   public String getSubType() {
      return this.mimeType != null ? this.mimeType.getSubType() : null;
   }

   public String getParameter(String var1) {
      if (var1.equals("humanPresentableName")) {
         return this.humanPresentableName;
      } else {
         return this.mimeType != null ? this.mimeType.getParameter(var1) : null;
      }
   }

   public void setHumanPresentableName(String var1) {
      this.humanPresentableName = var1;
   }

   public boolean equals(Object var1) {
      return var1 instanceof DataFlavor && this.equals((DataFlavor)var1);
   }

   public boolean equals(DataFlavor var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (!Objects.equals(this.getRepresentationClass(), var1.getRepresentationClass())) {
         return false;
      } else {
         if (this.mimeType == null) {
            if (var1.mimeType != null) {
               return false;
            }
         } else {
            if (!this.mimeType.match(var1.mimeType)) {
               return false;
            }

            if ("text".equals(this.getPrimaryType())) {
               String var2;
               String var3;
               if (DataTransferer.doesSubtypeSupportCharset(this) && this.representationClass != null && !this.isStandardTextRepresentationClass()) {
                  var2 = DataTransferer.canonicalName(this.getParameter("charset"));
                  var3 = DataTransferer.canonicalName(var1.getParameter("charset"));
                  if (!Objects.equals(var2, var3)) {
                     return false;
                  }
               }

               if ("html".equals(this.getSubType())) {
                  var2 = this.getParameter("document");
                  var3 = var1.getParameter("document");
                  if (!Objects.equals(var2, var3)) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean equals(String var1) {
      return var1 != null && this.mimeType != null ? this.isMimeTypeEqual(var1) : false;
   }

   public int hashCode() {
      int var1 = 0;
      if (this.representationClass != null) {
         var1 += this.representationClass.hashCode();
      }

      if (this.mimeType != null) {
         String var2 = this.mimeType.getPrimaryType();
         if (var2 != null) {
            var1 += var2.hashCode();
         }

         if ("text".equals(var2)) {
            String var3;
            if (DataTransferer.doesSubtypeSupportCharset(this) && this.representationClass != null && !this.isStandardTextRepresentationClass()) {
               var3 = DataTransferer.canonicalName(this.getParameter("charset"));
               if (var3 != null) {
                  var1 += var3.hashCode();
               }
            }

            if ("html".equals(this.getSubType())) {
               var3 = this.getParameter("document");
               if (var3 != null) {
                  var1 += var3.hashCode();
               }
            }
         }
      }

      return var1;
   }

   public boolean match(DataFlavor var1) {
      return this.equals(var1);
   }

   public boolean isMimeTypeEqual(String var1) {
      if (var1 == null) {
         throw new NullPointerException("mimeType");
      } else if (this.mimeType == null) {
         return false;
      } else {
         try {
            return this.mimeType.match(new MimeType(var1));
         } catch (MimeTypeParseException var3) {
            return false;
         }
      }
   }

   public final boolean isMimeTypeEqual(DataFlavor var1) {
      return this.isMimeTypeEqual(var1.mimeType);
   }

   private boolean isMimeTypeEqual(MimeType var1) {
      if (this.mimeType == null) {
         return var1 == null;
      } else {
         return this.mimeType.match(var1);
      }
   }

   private boolean isStandardTextRepresentationClass() {
      return this.isRepresentationClassReader() || String.class.equals(this.representationClass) || this.isRepresentationClassCharBuffer() || char[].class.equals(this.representationClass);
   }

   public boolean isMimeTypeSerializedObject() {
      return this.isMimeTypeEqual("application/x-java-serialized-object");
   }

   public final Class<?> getDefaultRepresentationClass() {
      return ioInputStreamClass;
   }

   public final String getDefaultRepresentationClassAsString() {
      return this.getDefaultRepresentationClass().getName();
   }

   public boolean isRepresentationClassInputStream() {
      return ioInputStreamClass.isAssignableFrom(this.representationClass);
   }

   public boolean isRepresentationClassReader() {
      return Reader.class.isAssignableFrom(this.representationClass);
   }

   public boolean isRepresentationClassCharBuffer() {
      return CharBuffer.class.isAssignableFrom(this.representationClass);
   }

   public boolean isRepresentationClassByteBuffer() {
      return ByteBuffer.class.isAssignableFrom(this.representationClass);
   }

   public boolean isRepresentationClassSerializable() {
      return Serializable.class.isAssignableFrom(this.representationClass);
   }

   public boolean isRepresentationClassRemote() {
      return DataTransferer.isRemote(this.representationClass);
   }

   public boolean isFlavorSerializedObjectType() {
      return this.isRepresentationClassSerializable() && this.isMimeTypeEqual("application/x-java-serialized-object");
   }

   public boolean isFlavorRemoteObjectType() {
      return this.isRepresentationClassRemote() && this.isRepresentationClassSerializable() && this.isMimeTypeEqual("application/x-java-remote-object");
   }

   public boolean isFlavorJavaFileListType() {
      if (this.mimeType != null && this.representationClass != null) {
         return List.class.isAssignableFrom(this.representationClass) && this.mimeType.match(javaFileListFlavor.mimeType);
      } else {
         return false;
      }
   }

   public boolean isFlavorTextType() {
      return DataTransferer.isFlavorCharsetTextType(this) || DataTransferer.isFlavorNoncharsetTextType(this);
   }

   public synchronized void writeExternal(ObjectOutput var1) throws IOException {
      if (this.mimeType != null) {
         this.mimeType.setParameter("humanPresentableName", this.humanPresentableName);
         var1.writeObject(this.mimeType);
         this.mimeType.removeParameter("humanPresentableName");
      } else {
         var1.writeObject((Object)null);
      }

      var1.writeObject(this.representationClass);
   }

   public synchronized void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      String var2 = null;
      this.mimeType = (MimeType)var1.readObject();
      if (this.mimeType != null) {
         this.humanPresentableName = this.mimeType.getParameter("humanPresentableName");
         this.mimeType.removeParameter("humanPresentableName");
         var2 = this.mimeType.getParameter("class");
         if (var2 == null) {
            throw new IOException("no class parameter specified in: " + this.mimeType);
         }
      }

      try {
         this.representationClass = (Class)var1.readObject();
      } catch (OptionalDataException var4) {
         if (!var4.eof || var4.length != 0) {
            throw var4;
         }

         if (var2 != null) {
            this.representationClass = tryToLoadClass(var2, this.getClass().getClassLoader());
         }
      }

   }

   public Object clone() throws CloneNotSupportedException {
      Object var1 = super.clone();
      if (this.mimeType != null) {
         ((DataFlavor)var1).mimeType = (MimeType)this.mimeType.clone();
      }

      return var1;
   }

   /** @deprecated */
   @Deprecated
   protected String normalizeMimeTypeParameter(String var1, String var2) {
      return var2;
   }

   /** @deprecated */
   @Deprecated
   protected String normalizeMimeType(String var1) {
      return var1;
   }

   static class TextFlavorComparator extends DataTransferer.DataFlavorComparator {
      public int compare(Object var1, Object var2) {
         DataFlavor var3 = (DataFlavor)var1;
         DataFlavor var4 = (DataFlavor)var2;
         if (var3.isFlavorTextType()) {
            return var4.isFlavorTextType() ? super.compare(var1, var2) : 1;
         } else {
            return var4.isFlavorTextType() ? -1 : 0;
         }
      }
   }
}
