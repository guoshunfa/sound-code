package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import java.io.IOException;

public abstract class XMLEntityReader implements XMLLocator {
   public abstract void setEncoding(String var1) throws IOException;

   public abstract String getEncoding();

   public abstract int getCharacterOffset();

   public abstract void setVersion(String var1);

   public abstract String getVersion();

   public abstract boolean isExternal();

   public abstract int peekChar() throws IOException;

   public abstract int scanChar() throws IOException;

   public abstract String scanNmtoken() throws IOException;

   public abstract String scanName() throws IOException;

   public abstract boolean scanQName(QName var1) throws IOException;

   public abstract int scanContent(XMLString var1) throws IOException;

   public abstract int scanLiteral(int var1, XMLString var2) throws IOException;

   public abstract boolean scanData(String var1, XMLStringBuffer var2) throws IOException;

   public abstract boolean skipChar(int var1) throws IOException;

   public abstract boolean skipSpaces() throws IOException;

   public abstract boolean skipString(String var1) throws IOException;

   public abstract void registerListener(XMLBufferListener var1);
}
