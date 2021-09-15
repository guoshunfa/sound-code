package org.w3c.dom.html;

import org.w3c.dom.DOMException;

public interface HTMLTableRowElement extends HTMLElement {
   int getRowIndex();

   int getSectionRowIndex();

   HTMLCollection getCells();

   String getAlign();

   void setAlign(String var1);

   String getBgColor();

   void setBgColor(String var1);

   String getCh();

   void setCh(String var1);

   String getChOff();

   void setChOff(String var1);

   String getVAlign();

   void setVAlign(String var1);

   HTMLElement insertCell(int var1) throws DOMException;

   void deleteCell(int var1) throws DOMException;
}
