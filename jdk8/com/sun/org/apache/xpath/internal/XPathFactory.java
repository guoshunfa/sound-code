package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import javax.xml.transform.SourceLocator;

public interface XPathFactory {
   XPath create(String var1, SourceLocator var2, PrefixResolver var3, int var4);
}
