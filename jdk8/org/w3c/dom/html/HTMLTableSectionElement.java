package org.w3c.dom.html;

import org.w3c.dom.DOMException;

public interface HTMLTableSectionElement extends HTMLElement {
   String getAlign();

   void setAlign(String var1);

   String getCh();

   void setCh(String var1);

   String getChOff();

   void setChOff(String var1);

   String getVAlign();

   void setVAlign(String var1);

   HTMLCollection getRows();

   HTMLElement insertRow(int var1) throws DOMException;

   void deleteRow(int var1) throws DOMException;
}
