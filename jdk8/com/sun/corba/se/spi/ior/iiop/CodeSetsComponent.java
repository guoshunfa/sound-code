package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.spi.ior.TaggedComponent;

public interface CodeSetsComponent extends TaggedComponent {
   CodeSetComponentInfo getCodeSetComponentInfo();
}
