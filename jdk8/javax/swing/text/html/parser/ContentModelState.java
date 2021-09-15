package javax.swing.text.html.parser;

class ContentModelState {
   ContentModel model;
   long value;
   ContentModelState next;

   public ContentModelState(ContentModel var1) {
      this(var1, (ContentModelState)null, 0L);
   }

   ContentModelState(Object var1, ContentModelState var2) {
      this(var1, var2, 0L);
   }

   ContentModelState(Object var1, ContentModelState var2, long var3) {
      this.model = (ContentModel)var1;
      this.next = var2;
      this.value = var3;
   }

   public ContentModel getModel() {
      ContentModel var1 = this.model;

      for(int var2 = 0; (long)var2 < this.value; ++var2) {
         if (var1.next == null) {
            return null;
         }

         var1 = var1.next;
      }

      return var1;
   }

   public boolean terminate() {
      ContentModel var1;
      int var2;
      switch(this.model.type) {
      case 38:
         var1 = (ContentModel)this.model.content;

         for(var2 = 0; var1 != null; var1 = var1.next) {
            if ((this.value & 1L << var2) == 0L && !var1.empty()) {
               return false;
            }

            ++var2;
         }

         return this.next == null || this.next.terminate();
      case 43:
         if (this.value == 0L && !this.model.empty()) {
            return false;
         }
      case 42:
      case 63:
         return this.next == null || this.next.terminate();
      case 44:
         var1 = (ContentModel)this.model.content;

         for(var2 = 0; (long)var2 < this.value; var1 = var1.next) {
            ++var2;
         }

         while(var1 != null && var1.empty()) {
            var1 = var1.next;
         }

         if (var1 != null) {
            return false;
         }

         return this.next == null || this.next.terminate();
      case 124:
         for(var1 = (ContentModel)this.model.content; var1 != null; var1 = var1.next) {
            if (var1.empty()) {
               return this.next == null || this.next.terminate();
            }
         }

         return false;
      default:
         return false;
      }
   }

   public Element first() {
      switch(this.model.type) {
      case 38:
      case 42:
      case 63:
      case 124:
         return null;
      case 43:
         return this.model.first();
      case 44:
         ContentModel var1 = (ContentModel)this.model.content;

         for(int var2 = 0; (long)var2 < this.value; var1 = var1.next) {
            ++var2;
         }

         return var1.first();
      default:
         return this.model.first();
      }
   }

   public ContentModelState advance(Object var1) {
      ContentModel var2;
      switch(this.model.type) {
      case 38:
         var2 = (ContentModel)this.model.content;
         boolean var5 = true;

         for(int var4 = 0; var2 != null; var2 = var2.next) {
            if ((this.value & 1L << var4) == 0L) {
               if (var2.first(var1)) {
                  return (new ContentModelState(var2, new ContentModelState(this.model, this.next, this.value | 1L << var4))).advance(var1);
               }

               if (!var2.empty()) {
                  var5 = false;
               }
            }

            ++var4;
         }

         if (var5) {
            if (this.next != null) {
               return this.next.advance(var1);
            }

            return null;
         }
         break;
      case 42:
         if (this.model.first(var1)) {
            return (new ContentModelState(this.model.content, this)).advance(var1);
         } else {
            return this.next != null ? this.next.advance(var1) : null;
         }
      case 43:
         if (this.model.first(var1)) {
            return (new ContentModelState(this.model.content, new ContentModelState(this.model, this.next, this.value + 1L))).advance(var1);
         }

         if (this.value != 0L) {
            if (this.next != null) {
               return this.next.advance(var1);
            }

            return null;
         }
         break;
      case 44:
         var2 = (ContentModel)this.model.content;

         for(int var3 = 0; (long)var3 < this.value; var2 = var2.next) {
            ++var3;
         }

         if (var2.first(var1) || var2.empty()) {
            if (var2.next == null) {
               return (new ContentModelState(var2, this.next)).advance(var1);
            }

            return (new ContentModelState(var2, new ContentModelState(this.model, this.next, this.value + 1L))).advance(var1);
         }
         break;
      case 63:
         if (this.model.first(var1)) {
            return (new ContentModelState(this.model.content, this.next)).advance(var1);
         }

         if (this.next != null) {
            return this.next.advance(var1);
         }

         return null;
      case 124:
         for(var2 = (ContentModel)this.model.content; var2 != null; var2 = var2.next) {
            if (var2.first(var1)) {
               return (new ContentModelState(var2, this.next)).advance(var1);
            }
         }

         return null;
      default:
         if (this.model.content == var1) {
            if (this.next == null && var1 instanceof Element && ((Element)var1).content != null) {
               return new ContentModelState(((Element)var1).content);
            }

            return this.next;
         }
      }

      return null;
   }
}
