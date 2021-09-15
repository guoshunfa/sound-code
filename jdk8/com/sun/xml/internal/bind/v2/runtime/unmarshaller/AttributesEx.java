package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.Attributes;

public interface AttributesEx extends Attributes {
   CharSequence getData(int var1);

   CharSequence getData(String var1, String var2);
}
