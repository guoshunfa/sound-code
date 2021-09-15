package org.w3c.dom.html;

public interface HTMLOptionElement extends HTMLElement {
   HTMLFormElement getForm();

   boolean getDefaultSelected();

   void setDefaultSelected(boolean var1);

   String getText();

   int getIndex();

   boolean getDisabled();

   void setDisabled(boolean var1);

   String getLabel();

   void setLabel(String var1);

   boolean getSelected();

   void setSelected(boolean var1);

   String getValue();

   void setValue(String var1);
}
