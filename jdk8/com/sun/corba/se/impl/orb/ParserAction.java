package com.sun.corba.se.impl.orb;

import java.util.Properties;

public interface ParserAction {
   String getPropertyName();

   boolean isPrefix();

   String getFieldName();

   Object apply(Properties var1);
}
