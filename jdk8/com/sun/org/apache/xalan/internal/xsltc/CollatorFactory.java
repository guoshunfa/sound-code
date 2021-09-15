package com.sun.org.apache.xalan.internal.xsltc;

import java.text.Collator;
import java.util.Locale;

public interface CollatorFactory {
   Collator getCollator(String var1, String var2);

   Collator getCollator(Locale var1);
}
