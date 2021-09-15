package com.sun.corba.se.impl.naming.namingutil;

import java.util.List;

public interface INSURL {
   boolean getRIRFlag();

   List getEndpointInfo();

   String getKeyString();

   String getStringifiedName();

   boolean isCorbanameURL();

   void dPrint();
}
