package jdk.internal.org.xml.sax;

public interface Locator {
   String getPublicId();

   String getSystemId();

   int getLineNumber();

   int getColumnNumber();
}
