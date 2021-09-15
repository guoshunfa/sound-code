package com.sun.xml.internal.ws.fault;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

class ReasonType {
   @XmlElements({@XmlElement(
   name = "Text",
   namespace = "http://www.w3.org/2003/05/soap-envelope",
   type = TextType.class
)})
   private final List<TextType> text = new ArrayList();

   ReasonType() {
   }

   ReasonType(String txt) {
      this.text.add(new TextType(txt));
   }

   List<TextType> texts() {
      return this.text;
   }
}
