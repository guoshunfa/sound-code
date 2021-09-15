package javax.xml.bind.annotation.adapters;

import javax.xml.bind.DatatypeConverter;

public final class HexBinaryAdapter extends XmlAdapter<String, byte[]> {
   public byte[] unmarshal(String s) {
      return s == null ? null : DatatypeConverter.parseHexBinary(s);
   }

   public String marshal(byte[] bytes) {
      return bytes == null ? null : DatatypeConverter.printHexBinary(bytes);
   }
}
