package javax.swing.text.html.parser;

import java.util.BitSet;

final class TagStack implements DTDConstants {
   TagElement tag;
   Element elem;
   ContentModelState state;
   TagStack next;
   BitSet inclusions;
   BitSet exclusions;
   boolean net;
   boolean pre;

   TagStack(TagElement var1, TagStack var2) {
      this.tag = var1;
      this.elem = var1.getElement();
      this.next = var2;
      Element var3 = var1.getElement();
      if (var3.getContent() != null) {
         this.state = new ContentModelState(var3.getContent());
      }

      if (var2 != null) {
         this.inclusions = var2.inclusions;
         this.exclusions = var2.exclusions;
         this.pre = var2.pre;
      }

      if (var1.isPreformatted()) {
         this.pre = true;
      }

      if (var3.inclusions != null) {
         if (this.inclusions != null) {
            this.inclusions = (BitSet)this.inclusions.clone();
            this.inclusions.or(var3.inclusions);
         } else {
            this.inclusions = var3.inclusions;
         }
      }

      if (var3.exclusions != null) {
         if (this.exclusions != null) {
            this.exclusions = (BitSet)this.exclusions.clone();
            this.exclusions.or(var3.exclusions);
         } else {
            this.exclusions = var3.exclusions;
         }
      }

   }

   public Element first() {
      return this.state != null ? this.state.first() : null;
   }

   public ContentModel contentModel() {
      return this.state == null ? null : this.state.getModel();
   }

   boolean excluded(int var1) {
      return this.exclusions != null && this.exclusions.get(this.elem.getIndex());
   }

   boolean advance(Element var1) {
      if (this.exclusions != null && this.exclusions.get(var1.getIndex())) {
         return false;
      } else {
         if (this.state != null) {
            ContentModelState var2 = this.state.advance(var1);
            if (var2 != null) {
               this.state = var2;
               return true;
            }
         } else if (this.elem.getType() == 19) {
            return true;
         }

         return this.inclusions != null && this.inclusions.get(var1.getIndex());
      }
   }

   boolean terminate() {
      return this.state == null || this.state.terminate();
   }

   public String toString() {
      return this.next == null ? "<" + this.tag.getElement().getName() + ">" : this.next + " <" + this.tag.getElement().getName() + ">";
   }
}
