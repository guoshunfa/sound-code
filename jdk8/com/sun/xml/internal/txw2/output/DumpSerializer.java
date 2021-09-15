package com.sun.xml.internal.txw2.output;

import java.io.PrintStream;

public class DumpSerializer implements XmlSerializer {
   private final PrintStream out;

   public DumpSerializer(PrintStream out) {
      this.out = out;
   }

   public void beginStartTag(String uri, String localName, String prefix) {
      this.out.println('<' + prefix + ':' + localName);
   }

   public void writeAttribute(String uri, String localName, String prefix, StringBuilder value) {
      this.out.println('@' + prefix + ':' + localName + '=' + value);
   }

   public void writeXmlns(String prefix, String uri) {
      this.out.println("xmlns:" + prefix + '=' + uri);
   }

   public void endStartTag(String uri, String localName, String prefix) {
      this.out.println('>');
   }

   public void endTag() {
      this.out.println("</  >");
   }

   public void text(StringBuilder text) {
      this.out.println((Object)text);
   }

   public void cdata(StringBuilder text) {
      this.out.println("<![CDATA[");
      this.out.println((Object)text);
      this.out.println("]]>");
   }

   public void comment(StringBuilder comment) {
      this.out.println("<!--");
      this.out.println((Object)comment);
      this.out.println("-->");
   }

   public void startDocument() {
      this.out.println("<?xml?>");
   }

   public void endDocument() {
      this.out.println("done");
   }

   public void flush() {
      this.out.println("flush");
   }
}
