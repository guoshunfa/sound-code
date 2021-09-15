package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.util.AttributesImpl;

public final class AttributesExImpl extends AttributesImpl implements AttributesEx {
   public CharSequence getData(int idx) {
      return this.getValue(idx);
   }

   public CharSequence getData(String nsUri, String localName) {
      return this.getValue(nsUri, localName);
   }
}
