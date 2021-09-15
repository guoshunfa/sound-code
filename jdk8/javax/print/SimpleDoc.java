package javax.print;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.DocAttributeSet;
import sun.reflect.misc.ReflectUtil;

public final class SimpleDoc implements Doc {
   private DocFlavor flavor;
   private DocAttributeSet attributes;
   private Object printData;
   private Reader reader;
   private InputStream inStream;

   public SimpleDoc(Object var1, DocFlavor var2, DocAttributeSet var3) {
      if (var2 != null && var1 != null) {
         Class var4 = null;

         try {
            String var5 = var2.getRepresentationClassName();
            ReflectUtil.checkPackageAccess(var5);
            var4 = Class.forName(var5, false, Thread.currentThread().getContextClassLoader());
         } catch (Throwable var6) {
            throw new IllegalArgumentException("unknown representation class");
         }

         if (!var4.isInstance(var1)) {
            throw new IllegalArgumentException("data is not of declared type");
         } else {
            this.flavor = var2;
            if (var3 != null) {
               this.attributes = AttributeSetUtilities.unmodifiableView(var3);
            }

            this.printData = var1;
         }
      } else {
         throw new IllegalArgumentException("null argument(s)");
      }
   }

   public DocFlavor getDocFlavor() {
      return this.flavor;
   }

   public DocAttributeSet getAttributes() {
      return this.attributes;
   }

   public Object getPrintData() throws IOException {
      return this.printData;
   }

   public Reader getReaderForText() throws IOException {
      if (this.printData instanceof Reader) {
         return (Reader)this.printData;
      } else {
         synchronized(this) {
            if (this.reader != null) {
               return this.reader;
            }

            if (this.printData instanceof char[]) {
               this.reader = new CharArrayReader((char[])((char[])this.printData));
            } else if (this.printData instanceof String) {
               this.reader = new StringReader((String)this.printData);
            }
         }

         return this.reader;
      }
   }

   public InputStream getStreamForBytes() throws IOException {
      if (this.printData instanceof InputStream) {
         return (InputStream)this.printData;
      } else {
         synchronized(this) {
            if (this.inStream != null) {
               return this.inStream;
            }

            if (this.printData instanceof byte[]) {
               this.inStream = new ByteArrayInputStream((byte[])((byte[])this.printData));
            }
         }

         return this.inStream;
      }
   }
}
