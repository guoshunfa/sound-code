package javax.swing.text.html.parser;

import javax.swing.text.html.HTML;

public class TagElement {
   Element elem;
   HTML.Tag htmlTag;
   boolean insertedByErrorRecovery;

   public TagElement(Element var1) {
      this(var1, false);
   }

   public TagElement(Element var1, boolean var2) {
      this.elem = var1;
      this.htmlTag = HTML.getTag(var1.getName());
      if (this.htmlTag == null) {
         this.htmlTag = new HTML.UnknownTag(var1.getName());
      }

      this.insertedByErrorRecovery = var2;
   }

   public boolean breaksFlow() {
      return this.htmlTag.breaksFlow();
   }

   public boolean isPreformatted() {
      return this.htmlTag.isPreformatted();
   }

   public Element getElement() {
      return this.elem;
   }

   public HTML.Tag getHTMLTag() {
      return this.htmlTag;
   }

   public boolean fictional() {
      return this.insertedByErrorRecovery;
   }
}
