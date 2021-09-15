package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.XMLString;

abstract class Comparator {
   abstract boolean compareStrings(XMLString var1, XMLString var2);

   abstract boolean compareNumbers(double var1, double var3);
}
