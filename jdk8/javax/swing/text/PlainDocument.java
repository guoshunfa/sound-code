package javax.swing.text;

import java.util.Vector;

public class PlainDocument extends AbstractDocument {
   public static final String tabSizeAttribute = "tabSize";
   public static final String lineLimitAttribute = "lineLimit";
   private AbstractDocument.AbstractElement defaultRoot;
   private Vector<Element> added;
   private Vector<Element> removed;
   private transient Segment s;

   public PlainDocument() {
      this(new GapContent());
   }

   public PlainDocument(AbstractDocument.Content var1) {
      super(var1);
      this.added = new Vector();
      this.removed = new Vector();
      this.putProperty("tabSize", 8);
      this.defaultRoot = this.createDefaultRoot();
   }

   public void insertString(int var1, String var2, AttributeSet var3) throws BadLocationException {
      Object var4 = this.getProperty("filterNewlines");
      if (var4 instanceof Boolean && var4.equals(Boolean.TRUE) && var2 != null && var2.indexOf(10) >= 0) {
         StringBuilder var5 = new StringBuilder(var2);
         int var6 = var5.length();

         for(int var7 = 0; var7 < var6; ++var7) {
            if (var5.charAt(var7) == '\n') {
               var5.setCharAt(var7, ' ');
            }
         }

         var2 = var5.toString();
      }

      super.insertString(var1, var2, var3);
   }

   public Element getDefaultRootElement() {
      return this.defaultRoot;
   }

   protected AbstractDocument.AbstractElement createDefaultRoot() {
      AbstractDocument.BranchElement var1 = (AbstractDocument.BranchElement)this.createBranchElement((Element)null, (AttributeSet)null);
      Element var2 = this.createLeafElement(var1, (AttributeSet)null, 0, 1);
      Element[] var3 = new Element[]{var2};
      var1.replace(0, 0, var3);
      return var1;
   }

   public Element getParagraphElement(int var1) {
      Element var2 = this.getDefaultRootElement();
      return var2.getElement(var2.getElementIndex(var1));
   }

   protected void insertUpdate(AbstractDocument.DefaultDocumentEvent var1, AttributeSet var2) {
      this.removed.removeAllElements();
      this.added.removeAllElements();
      AbstractDocument.BranchElement var3 = (AbstractDocument.BranchElement)this.getDefaultRootElement();
      int var4 = var1.getOffset();
      int var5 = var1.getLength();
      if (var4 > 0) {
         --var4;
         ++var5;
      }

      int var6 = var3.getElementIndex(var4);
      Element var7 = var3.getElement(var6);
      int var8 = var7.getStartOffset();
      int var9 = var7.getEndOffset();
      int var10 = var8;

      try {
         if (this.s == null) {
            this.s = new Segment();
         }

         this.getContent().getChars(var4, var5, this.s);
         boolean var11 = false;
         int var12 = 0;

         while(true) {
            if (var12 >= var5) {
               if (var11) {
                  this.removed.addElement(var7);
                  if (var4 + var5 == var9 && var10 != var9 && var6 + 1 < var3.getElementCount()) {
                     Element var16 = var3.getElement(var6 + 1);
                     this.removed.addElement(var16);
                     var9 = var16.getEndOffset();
                  }

                  if (var10 < var9) {
                     this.added.addElement(this.createLeafElement(var3, (AttributeSet)null, var10, var9));
                  }

                  Element[] var17 = new Element[this.added.size()];
                  this.added.copyInto(var17);
                  Element[] var18 = new Element[this.removed.size()];
                  this.removed.copyInto(var18);
                  AbstractDocument.ElementEdit var19 = new AbstractDocument.ElementEdit(var3, var6, var18, var17);
                  var1.addEdit(var19);
                  var3.replace(var6, var18.length, var17);
               }

               if (Utilities.isComposedTextAttributeDefined(var2)) {
                  this.insertComposedTextUpdate(var1, var2);
               }
               break;
            }

            char var13 = this.s.array[this.s.offset + var12];
            if (var13 == '\n') {
               int var14 = var4 + var12 + 1;
               this.added.addElement(this.createLeafElement(var3, (AttributeSet)null, var10, var14));
               var10 = var14;
               var11 = true;
            }

            ++var12;
         }
      } catch (BadLocationException var15) {
         throw new Error("Internal error: " + var15.toString());
      }

      super.insertUpdate(var1, var2);
   }

   protected void removeUpdate(AbstractDocument.DefaultDocumentEvent var1) {
      this.removed.removeAllElements();
      AbstractDocument.BranchElement var2 = (AbstractDocument.BranchElement)this.getDefaultRootElement();
      int var3 = var1.getOffset();
      int var4 = var1.getLength();
      int var5 = var2.getElementIndex(var3);
      int var6 = var2.getElementIndex(var3 + var4);
      Element[] var9;
      Element[] var10;
      AbstractDocument.ElementEdit var11;
      if (var5 != var6) {
         int var7;
         for(var7 = var5; var7 <= var6; ++var7) {
            this.removed.addElement(var2.getElement(var7));
         }

         var7 = var2.getElement(var5).getStartOffset();
         int var8 = var2.getElement(var6).getEndOffset();
         var9 = new Element[]{this.createLeafElement(var2, (AttributeSet)null, var7, var8)};
         var10 = new Element[this.removed.size()];
         this.removed.copyInto(var10);
         var11 = new AbstractDocument.ElementEdit(var2, var5, var10, var9);
         var1.addEdit(var11);
         var2.replace(var5, var10.length, var9);
      } else {
         Element var12 = var2.getElement(var5);
         if (!var12.isLeaf()) {
            Element var13 = var12.getElement(var12.getElementIndex(var3));
            if (Utilities.isComposedTextElement(var13)) {
               var9 = new Element[]{this.createLeafElement(var2, (AttributeSet)null, var12.getStartOffset(), var12.getEndOffset())};
               var10 = new Element[]{var12};
               var11 = new AbstractDocument.ElementEdit(var2, var5, var10, var9);
               var1.addEdit(var11);
               var2.replace(var5, 1, var9);
            }
         }
      }

      super.removeUpdate(var1);
   }

   private void insertComposedTextUpdate(AbstractDocument.DefaultDocumentEvent var1, AttributeSet var2) {
      this.added.removeAllElements();
      AbstractDocument.BranchElement var3 = (AbstractDocument.BranchElement)this.getDefaultRootElement();
      int var4 = var1.getOffset();
      int var5 = var1.getLength();
      int var6 = var3.getElementIndex(var4);
      Element var7 = var3.getElement(var6);
      int var8 = var7.getStartOffset();
      int var9 = var7.getEndOffset();
      AbstractDocument.BranchElement[] var10 = new AbstractDocument.BranchElement[]{(AbstractDocument.BranchElement)this.createBranchElement(var3, (AttributeSet)null)};
      Element[] var11 = new Element[]{var7};
      if (var8 != var4) {
         this.added.addElement(this.createLeafElement(var10[0], (AttributeSet)null, var8, var4));
      }

      this.added.addElement(this.createLeafElement(var10[0], var2, var4, var4 + var5));
      if (var9 != var4 + var5) {
         this.added.addElement(this.createLeafElement(var10[0], (AttributeSet)null, var4 + var5, var9));
      }

      Element[] var12 = new Element[this.added.size()];
      this.added.copyInto(var12);
      AbstractDocument.ElementEdit var13 = new AbstractDocument.ElementEdit(var3, var6, var11, var10);
      var1.addEdit(var13);
      var10[0].replace(0, 0, var12);
      var3.replace(var6, 1, var10);
   }
}
