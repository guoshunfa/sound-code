package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.PGPData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMPGPData extends DOMStructure implements PGPData {
   private final byte[] keyId;
   private final byte[] keyPacket;
   private final List<XMLStructure> externalElements;

   public DOMPGPData(byte[] var1, List<? extends XMLStructure> var2) {
      if (var1 == null) {
         throw new NullPointerException("keyPacket cannot be null");
      } else {
         if (var2 != null && !var2.isEmpty()) {
            this.externalElements = Collections.unmodifiableList(new ArrayList(var2));
            int var3 = 0;

            for(int var4 = this.externalElements.size(); var3 < var4; ++var3) {
               if (!(this.externalElements.get(var3) instanceof XMLStructure)) {
                  throw new ClassCastException("other[" + var3 + "] is not a valid PGPData type");
               }
            }
         } else {
            this.externalElements = Collections.emptyList();
         }

         this.keyPacket = (byte[])((byte[])var1.clone());
         this.checkKeyPacket(var1);
         this.keyId = null;
      }
   }

   public DOMPGPData(byte[] var1, byte[] var2, List<? extends XMLStructure> var3) {
      if (var1 == null) {
         throw new NullPointerException("keyId cannot be null");
      } else if (var1.length != 8) {
         throw new IllegalArgumentException("keyId must be 8 bytes long");
      } else {
         if (var3 != null && !var3.isEmpty()) {
            this.externalElements = Collections.unmodifiableList(new ArrayList(var3));
            int var4 = 0;

            for(int var5 = this.externalElements.size(); var4 < var5; ++var4) {
               if (!(this.externalElements.get(var4) instanceof XMLStructure)) {
                  throw new ClassCastException("other[" + var4 + "] is not a valid PGPData type");
               }
            }
         } else {
            this.externalElements = Collections.emptyList();
         }

         this.keyId = (byte[])((byte[])var1.clone());
         this.keyPacket = var2 == null ? null : (byte[])((byte[])var2.clone());
         if (var2 != null) {
            this.checkKeyPacket(var2);
         }

      }
   }

   public DOMPGPData(Element var1) throws MarshalException {
      byte[] var2 = null;
      byte[] var3 = null;
      NodeList var4 = var1.getChildNodes();
      int var5 = var4.getLength();
      ArrayList var6 = new ArrayList(var5);

      for(int var7 = 0; var7 < var5; ++var7) {
         Node var8 = var4.item(var7);
         if (var8.getNodeType() == 1) {
            Element var9 = (Element)var8;
            String var10 = var9.getLocalName();

            try {
               if (var10.equals("PGPKeyID")) {
                  var2 = Base64.decode(var9);
               } else if (var10.equals("PGPKeyPacket")) {
                  var3 = Base64.decode(var9);
               } else {
                  var6.add(new javax.xml.crypto.dom.DOMStructure(var9));
               }
            } catch (Base64DecodingException var12) {
               throw new MarshalException(var12);
            }
         }
      }

      this.keyId = var2;
      this.keyPacket = var3;
      this.externalElements = Collections.unmodifiableList(var6);
   }

   public byte[] getKeyId() {
      return this.keyId == null ? null : (byte[])((byte[])this.keyId.clone());
   }

   public byte[] getKeyPacket() {
      return this.keyPacket == null ? null : (byte[])((byte[])this.keyPacket.clone());
   }

   public List getExternalElements() {
      return this.externalElements;
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      Document var4 = DOMUtils.getOwnerDocument(var1);
      Element var5 = DOMUtils.createElement(var4, "PGPData", "http://www.w3.org/2000/09/xmldsig#", var2);
      Element var6;
      if (this.keyId != null) {
         var6 = DOMUtils.createElement(var4, "PGPKeyID", "http://www.w3.org/2000/09/xmldsig#", var2);
         var6.appendChild(var4.createTextNode(Base64.encode(this.keyId)));
         var5.appendChild(var6);
      }

      if (this.keyPacket != null) {
         var6 = DOMUtils.createElement(var4, "PGPKeyPacket", "http://www.w3.org/2000/09/xmldsig#", var2);
         var6.appendChild(var4.createTextNode(Base64.encode(this.keyPacket)));
         var5.appendChild(var6);
      }

      Iterator var8 = this.externalElements.iterator();

      while(var8.hasNext()) {
         XMLStructure var7 = (XMLStructure)var8.next();
         DOMUtils.appendChild(var5, ((javax.xml.crypto.dom.DOMStructure)var7).getNode());
      }

      var1.appendChild(var5);
   }

   private void checkKeyPacket(byte[] var1) {
      if (var1.length < 3) {
         throw new IllegalArgumentException("keypacket must be at least 3 bytes long");
      } else {
         byte var2 = var1[0];
         if ((var2 & 128) != 128) {
            throw new IllegalArgumentException("keypacket tag is invalid: bit 7 is not set");
         } else if ((var2 & 64) != 64) {
            throw new IllegalArgumentException("old keypacket tag format is unsupported");
         } else if ((var2 & 6) != 6 && (var2 & 14) != 14 && (var2 & 5) != 5 && (var2 & 7) != 7) {
            throw new IllegalArgumentException("keypacket tag is invalid: must be 6, 14, 5, or 7");
         }
      }
   }
}
