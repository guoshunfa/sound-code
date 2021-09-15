package com.sun.beans.decoder;

public abstract class ElementHandler {
   private DocumentHandler owner;
   private ElementHandler parent;
   private String id;

   public final DocumentHandler getOwner() {
      return this.owner;
   }

   final void setOwner(DocumentHandler var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Every element should have owner");
      } else {
         this.owner = var1;
      }
   }

   public final ElementHandler getParent() {
      return this.parent;
   }

   final void setParent(ElementHandler var1) {
      this.parent = var1;
   }

   protected final Object getVariable(String var1) {
      if (var1.equals(this.id)) {
         ValueObject var2 = this.getValueObject();
         if (var2.isVoid()) {
            throw new IllegalStateException("The element does not return value");
         } else {
            return var2.getValue();
         }
      } else {
         return this.parent != null ? this.parent.getVariable(var1) : this.owner.getVariable(var1);
      }
   }

   protected Object getContextBean() {
      if (this.parent != null) {
         ValueObject var2 = this.parent.getValueObject();
         if (!var2.isVoid()) {
            return var2.getValue();
         } else {
            throw new IllegalStateException("The outer element does not return value");
         }
      } else {
         Object var1 = this.owner.getOwner();
         if (var1 != null) {
            return var1;
         } else {
            throw new IllegalStateException("The topmost element does not have context");
         }
      }
   }

   public void addAttribute(String var1, String var2) {
      if (var1.equals("id")) {
         this.id = var2;
      } else {
         throw new IllegalArgumentException("Unsupported attribute: " + var1);
      }
   }

   public void startElement() {
   }

   public void endElement() {
      ValueObject var1 = this.getValueObject();
      if (!var1.isVoid()) {
         if (this.id != null) {
            this.owner.setVariable(this.id, var1.getValue());
         }

         if (this.isArgument()) {
            if (this.parent != null) {
               this.parent.addArgument(var1.getValue());
            } else {
               this.owner.addObject(var1.getValue());
            }
         }
      }

   }

   public void addCharacter(char var1) {
      if (var1 != ' ' && var1 != '\n' && var1 != '\t' && var1 != '\r') {
         throw new IllegalStateException("Illegal character with code " + var1);
      }
   }

   protected void addArgument(Object var1) {
      throw new IllegalStateException("Could not add argument to simple element");
   }

   protected boolean isArgument() {
      return this.id == null;
   }

   protected abstract ValueObject getValueObject();
}
