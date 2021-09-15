package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractSerializer implements Serializer {
   protected Canonicalizer canon;

   public void setCanonicalizer(Canonicalizer var1) {
      this.canon = var1;
   }

   public String serialize(Element var1) throws Exception {
      return this.canonSerialize(var1);
   }

   public byte[] serializeToByteArray(Element var1) throws Exception {
      return this.canonSerializeToByteArray(var1);
   }

   public String serialize(NodeList var1) throws Exception {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      this.canon.setWriter(var2);
      this.canon.notReset();

      for(int var3 = 0; var3 < var1.getLength(); ++var3) {
         this.canon.canonicalizeSubtree(var1.item(var3));
      }

      String var4 = var2.toString("UTF-8");
      var2.reset();
      return var4;
   }

   public byte[] serializeToByteArray(NodeList var1) throws Exception {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      this.canon.setWriter(var2);
      this.canon.notReset();

      for(int var3 = 0; var3 < var1.getLength(); ++var3) {
         this.canon.canonicalizeSubtree(var1.item(var3));
      }

      return var2.toByteArray();
   }

   public String canonSerialize(Node var1) throws Exception {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      this.canon.setWriter(var2);
      this.canon.notReset();
      this.canon.canonicalizeSubtree(var1);
      String var3 = var2.toString("UTF-8");
      var2.reset();
      return var3;
   }

   public byte[] canonSerializeToByteArray(Node var1) throws Exception {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      this.canon.setWriter(var2);
      this.canon.notReset();
      this.canon.canonicalizeSubtree(var1);
      return var2.toByteArray();
   }

   public abstract Node deserialize(String var1, Node var2) throws XMLEncryptionException;

   public abstract Node deserialize(byte[] var1, Node var2) throws XMLEncryptionException;

   protected static byte[] createContext(byte[] var0, Node var1) throws XMLEncryptionException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();

      try {
         OutputStreamWriter var3 = new OutputStreamWriter(var2, "UTF-8");
         var3.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dummy");
         HashMap var4 = new HashMap();

         for(Node var5 = var1; var5 != null; var5 = var5.getParentNode()) {
            NamedNodeMap var6 = var5.getAttributes();
            if (var6 != null) {
               for(int var7 = 0; var7 < var6.getLength(); ++var7) {
                  Node var8 = var6.item(var7);
                  String var9 = var8.getNodeName();
                  if ((var9.equals("xmlns") || var9.startsWith("xmlns:")) && !var4.containsKey(var8.getNodeName())) {
                     var3.write(" ");
                     var3.write(var9);
                     var3.write("=\"");
                     var3.write(var8.getNodeValue());
                     var3.write("\"");
                     var4.put(var9, var8.getNodeValue());
                  }
               }
            }
         }

         var3.write(">");
         var3.flush();
         var2.write(var0);
         var3.write("</dummy>");
         var3.close();
         return var2.toByteArray();
      } catch (UnsupportedEncodingException var10) {
         throw new XMLEncryptionException("empty", var10);
      } catch (IOException var11) {
         throw new XMLEncryptionException("empty", var11);
      }
   }

   protected static String createContext(String var0, Node var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dummy");
      HashMap var3 = new HashMap();

      for(Node var4 = var1; var4 != null; var4 = var4.getParentNode()) {
         NamedNodeMap var5 = var4.getAttributes();
         if (var5 != null) {
            for(int var6 = 0; var6 < var5.getLength(); ++var6) {
               Node var7 = var5.item(var6);
               String var8 = var7.getNodeName();
               if ((var8.equals("xmlns") || var8.startsWith("xmlns:")) && !var3.containsKey(var7.getNodeName())) {
                  var2.append(" " + var8 + "=\"" + var7.getNodeValue() + "\"");
                  var3.put(var8, var7.getNodeValue());
               }
            }
         }
      }

      var2.append(">" + var0 + "</dummy>");
      return var2.toString();
   }
}
