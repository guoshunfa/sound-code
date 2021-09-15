package com.sun.org.apache.xml.internal.dtm;

public interface DTMWSFilter {
   short NOTSTRIP = 1;
   short STRIP = 2;
   short INHERIT = 3;

   short getShouldStripSpace(int var1, DTM var2);
}
