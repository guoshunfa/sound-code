package com.sun.xml.internal.bind.v2.bytecode;

import com.sun.xml.internal.bind.Util;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ClassTailor {
   private static final Logger logger = Util.getClassLogger();

   private ClassTailor() {
   }

   public static String toVMClassName(Class c) {
      assert !c.isPrimitive();

      return c.isArray() ? toVMTypeName(c) : c.getName().replace('.', '/');
   }

   public static String toVMTypeName(Class c) {
      if (c.isArray()) {
         return '[' + toVMTypeName(c.getComponentType());
      } else if (c.isPrimitive()) {
         if (c == Boolean.TYPE) {
            return "Z";
         } else if (c == Character.TYPE) {
            return "C";
         } else if (c == Byte.TYPE) {
            return "B";
         } else if (c == Double.TYPE) {
            return "D";
         } else if (c == Float.TYPE) {
            return "F";
         } else if (c == Integer.TYPE) {
            return "I";
         } else if (c == Long.TYPE) {
            return "J";
         } else if (c == Short.TYPE) {
            return "S";
         } else {
            throw new IllegalArgumentException(c.getName());
         }
      } else {
         return 'L' + c.getName().replace('.', '/') + ';';
      }
   }

   public static byte[] tailor(Class templateClass, String newClassName, String... replacements) {
      String vmname = toVMClassName(templateClass);
      return tailor(SecureLoader.getClassClassLoader(templateClass).getResourceAsStream(vmname + ".class"), vmname, newClassName, replacements);
   }

   public static byte[] tailor(InputStream image, String templateClassName, String newClassName, String... replacements) {
      DataInputStream in = new DataInputStream(image);

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
         DataOutputStream out = new DataOutputStream(baos);
         long l = in.readLong();
         out.writeLong(l);
         short count = in.readShort();
         out.writeShort(count);

         for(int i = 0; i < count; ++i) {
            byte tag = in.readByte();
            out.writeByte(tag);
            switch(tag) {
            case 0:
               break;
            case 1:
               String value = in.readUTF();
               if (value.equals(templateClassName)) {
                  value = newClassName;
               } else {
                  for(int j = 0; j < replacements.length; j += 2) {
                     if (value.equals(replacements[j])) {
                        value = replacements[j + 1];
                        break;
                     }
                  }
               }

               out.writeUTF(value);
               break;
            case 2:
            default:
               throw new IllegalArgumentException("Unknown constant type " + tag);
            case 3:
            case 4:
               out.writeInt(in.readInt());
               break;
            case 5:
            case 6:
               ++i;
               out.writeLong(in.readLong());
               break;
            case 7:
            case 8:
               out.writeShort(in.readShort());
               break;
            case 9:
            case 10:
            case 11:
            case 12:
               out.writeInt(in.readInt());
            }
         }

         byte[] buf = new byte[512];

         int len;
         while((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
         }

         in.close();
         out.close();
         return baos.toByteArray();
      } catch (IOException var14) {
         logger.log(Level.WARNING, (String)"failed to tailor", (Throwable)var14);
         return null;
      }
   }
}
