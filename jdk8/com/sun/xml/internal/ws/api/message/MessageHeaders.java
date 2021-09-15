package com.sun.xml.internal.ws.api.message;

import com.sun.xml.internal.ws.api.WSBinding;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;

public interface MessageHeaders {
   void understood(Header var1);

   void understood(QName var1);

   void understood(String var1, String var2);

   Header get(String var1, String var2, boolean var3);

   Header get(QName var1, boolean var2);

   Iterator<Header> getHeaders(String var1, String var2, boolean var3);

   Iterator<Header> getHeaders(String var1, boolean var2);

   Iterator<Header> getHeaders(QName var1, boolean var2);

   Iterator<Header> getHeaders();

   boolean hasHeaders();

   boolean add(Header var1);

   Header remove(QName var1);

   Header remove(String var1, String var2);

   void replace(Header var1, Header var2);

   boolean addOrReplace(Header var1);

   Set<QName> getUnderstoodHeaders();

   Set<QName> getNotUnderstoodHeaders(Set<String> var1, Set<QName> var2, WSBinding var3);

   boolean isUnderstood(Header var1);

   boolean isUnderstood(QName var1);

   boolean isUnderstood(String var1, String var2);

   List<Header> asList();
}
