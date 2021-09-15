package sun.swing.text;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.BorderFactory;
import javax.swing.CellRendererPane;
import javax.swing.JEditorPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import sun.font.FontDesignMetrics;
import sun.swing.text.html.FrameEditorPaneTag;

public class TextComponentPrintable implements CountingPrintable {
   private static final int LIST_SIZE = 1000;
   private boolean isLayouted = false;
   private final JTextComponent textComponentToPrint;
   private final AtomicReference<FontRenderContext> frc = new AtomicReference((Object)null);
   private final JTextComponent printShell;
   private final MessageFormat headerFormat;
   private final MessageFormat footerFormat;
   private static final float HEADER_FONT_SIZE = 18.0F;
   private static final float FOOTER_FONT_SIZE = 12.0F;
   private final Font headerFont;
   private final Font footerFont;
   private final List<TextComponentPrintable.IntegerSegment> rowsMetrics;
   private final List<TextComponentPrintable.IntegerSegment> pagesMetrics;
   private boolean needReadLock = false;

   public static Printable getPrintable(JTextComponent var0, MessageFormat var1, MessageFormat var2) {
      if (var0 instanceof JEditorPane && isFrameSetDocument(var0.getDocument())) {
         List var3 = getFrames((JEditorPane)var0);
         ArrayList var4 = new ArrayList();
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            JEditorPane var6 = (JEditorPane)var5.next();
            var4.add((CountingPrintable)getPrintable(var6, var1, var2));
         }

         return new CompoundPrintable(var4);
      } else {
         return new TextComponentPrintable(var0, var1, var2);
      }
   }

   private static boolean isFrameSetDocument(Document var0) {
      boolean var1 = false;
      if (var0 instanceof HTMLDocument) {
         HTMLDocument var2 = (HTMLDocument)var0;
         if (var2.getIterator(HTML.Tag.FRAME).isValid()) {
            var1 = true;
         }
      }

      return var1;
   }

   private static List<JEditorPane> getFrames(JEditorPane var0) {
      ArrayList var1 = new ArrayList();
      getFrames(var0, var1);
      if (var1.size() == 0) {
         createFrames(var0);
         getFrames(var0, var1);
      }

      return var1;
   }

   private static void getFrames(Container var0, List<JEditorPane> var1) {
      Component[] var2 = var0.getComponents();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Component var5 = var2[var4];
         if (var5 instanceof FrameEditorPaneTag && var5 instanceof JEditorPane) {
            var1.add((JEditorPane)var5);
         } else if (var5 instanceof Container) {
            getFrames((Container)var5, var1);
         }
      }

   }

   private static void createFrames(final JEditorPane var0) {
      Runnable var1 = new Runnable() {
         public void run() {
            CellRendererPane var3 = new CellRendererPane();
            var3.add(var0);
            var3.setSize(500, 500);
         }
      };
      if (SwingUtilities.isEventDispatchThread()) {
         var1.run();
      } else {
         try {
            SwingUtilities.invokeAndWait(var1);
         } catch (Exception var3) {
            if (var3 instanceof RuntimeException) {
               throw (RuntimeException)var3;
            }

            throw new RuntimeException(var3);
         }
      }

   }

   private TextComponentPrintable(JTextComponent var1, MessageFormat var2, MessageFormat var3) {
      this.textComponentToPrint = var1;
      this.headerFormat = var2;
      this.footerFormat = var3;
      this.headerFont = var1.getFont().deriveFont(1, 18.0F);
      this.footerFont = var1.getFont().deriveFont(0, 12.0F);
      this.pagesMetrics = Collections.synchronizedList(new ArrayList());
      this.rowsMetrics = new ArrayList(1000);
      this.printShell = this.createPrintShell(var1);
   }

   private JTextComponent createPrintShell(final JTextComponent var1) {
      if (SwingUtilities.isEventDispatchThread()) {
         return this.createPrintShellOnEDT(var1);
      } else {
         FutureTask var2 = new FutureTask(new Callable<JTextComponent>() {
            public JTextComponent call() throws Exception {
               return TextComponentPrintable.this.createPrintShellOnEDT(var1);
            }
         });
         SwingUtilities.invokeLater(var2);

         try {
            return (JTextComponent)var2.get();
         } catch (InterruptedException var5) {
            throw new RuntimeException(var5);
         } catch (ExecutionException var6) {
            Throwable var4 = var6.getCause();
            if (var4 instanceof Error) {
               throw (Error)var4;
            } else if (var4 instanceof RuntimeException) {
               throw (RuntimeException)var4;
            } else {
               throw new AssertionError(var4);
            }
         }
      }
   }

   private JTextComponent createPrintShellOnEDT(final JTextComponent var1) {
      assert SwingUtilities.isEventDispatchThread();

      Object var2 = null;
      if (var1 instanceof JPasswordField) {
         var2 = new JPasswordField() {
            {
               this.setEchoChar(((JPasswordField)var1).getEchoChar());
               this.setHorizontalAlignment(((JTextField)var1).getHorizontalAlignment());
            }

            public FontMetrics getFontMetrics(Font var1x) {
               return (FontMetrics)(TextComponentPrintable.this.frc.get() == null ? super.getFontMetrics(var1x) : FontDesignMetrics.getMetrics(var1x, (FontRenderContext)TextComponentPrintable.this.frc.get()));
            }
         };
      } else if (var1 instanceof JTextField) {
         var2 = new JTextField() {
            {
               this.setHorizontalAlignment(((JTextField)var1).getHorizontalAlignment());
            }

            public FontMetrics getFontMetrics(Font var1x) {
               return (FontMetrics)(TextComponentPrintable.this.frc.get() == null ? super.getFontMetrics(var1x) : FontDesignMetrics.getMetrics(var1x, (FontRenderContext)TextComponentPrintable.this.frc.get()));
            }
         };
      } else if (var1 instanceof JTextArea) {
         var2 = new JTextArea() {
            {
               JTextArea var3 = (JTextArea)var1;
               this.setLineWrap(var3.getLineWrap());
               this.setWrapStyleWord(var3.getWrapStyleWord());
               this.setTabSize(var3.getTabSize());
            }

            public FontMetrics getFontMetrics(Font var1x) {
               return (FontMetrics)(TextComponentPrintable.this.frc.get() == null ? super.getFontMetrics(var1x) : FontDesignMetrics.getMetrics(var1x, (FontRenderContext)TextComponentPrintable.this.frc.get()));
            }
         };
      } else if (var1 instanceof JTextPane) {
         var2 = new JTextPane() {
            public FontMetrics getFontMetrics(Font var1x) {
               return (FontMetrics)(TextComponentPrintable.this.frc.get() == null ? super.getFontMetrics(var1x) : FontDesignMetrics.getMetrics(var1x, (FontRenderContext)TextComponentPrintable.this.frc.get()));
            }

            public EditorKit getEditorKit() {
               return this.getDocument() == var1.getDocument() ? ((JTextPane)var1).getEditorKit() : super.getEditorKit();
            }
         };
      } else if (var1 instanceof JEditorPane) {
         var2 = new JEditorPane() {
            public FontMetrics getFontMetrics(Font var1x) {
               return (FontMetrics)(TextComponentPrintable.this.frc.get() == null ? super.getFontMetrics(var1x) : FontDesignMetrics.getMetrics(var1x, (FontRenderContext)TextComponentPrintable.this.frc.get()));
            }

            public EditorKit getEditorKit() {
               return this.getDocument() == var1.getDocument() ? ((JEditorPane)var1).getEditorKit() : super.getEditorKit();
            }
         };
      }

      ((JTextComponent)var2).setBorder((Border)null);
      ((JTextComponent)var2).setOpaque(var1.isOpaque());
      ((JTextComponent)var2).setEditable(var1.isEditable());
      ((JTextComponent)var2).setEnabled(var1.isEnabled());
      ((JTextComponent)var2).setFont(var1.getFont());
      ((JTextComponent)var2).setBackground(var1.getBackground());
      ((JTextComponent)var2).setForeground(var1.getForeground());
      ((JTextComponent)var2).setComponentOrientation(var1.getComponentOrientation());
      if (var2 instanceof JEditorPane) {
         ((JTextComponent)var2).putClientProperty("JEditorPane.honorDisplayProperties", var1.getClientProperty("JEditorPane.honorDisplayProperties"));
         ((JTextComponent)var2).putClientProperty("JEditorPane.w3cLengthUnits", var1.getClientProperty("JEditorPane.w3cLengthUnits"));
         ((JTextComponent)var2).putClientProperty("charset", var1.getClientProperty("charset"));
      }

      ((JTextComponent)var2).setDocument(var1.getDocument());
      return (JTextComponent)var2;
   }

   public int getNumberOfPages() {
      return this.pagesMetrics.size();
   }

   public int print(final Graphics var1, final PageFormat var2, final int var3) throws PrinterException {
      if (!this.isLayouted) {
         if (var1 instanceof Graphics2D) {
            this.frc.set(((Graphics2D)var1).getFontRenderContext());
         }

         this.layout((int)Math.floor(var2.getImageableWidth()));
         this.calculateRowsMetrics();
      }

      int var4;
      if (!SwingUtilities.isEventDispatchThread()) {
         Callable var5 = new Callable<Integer>() {
            public Integer call() throws Exception {
               return TextComponentPrintable.this.printOnEDT(var1, var2, var3);
            }
         };
         FutureTask var6 = new FutureTask(var5);
         SwingUtilities.invokeLater(var6);

         try {
            var4 = (Integer)var6.get();
         } catch (InterruptedException var9) {
            throw new RuntimeException(var9);
         } catch (ExecutionException var10) {
            Throwable var8 = var10.getCause();
            if (var8 instanceof PrinterException) {
               throw (PrinterException)var8;
            }

            if (var8 instanceof RuntimeException) {
               throw (RuntimeException)var8;
            }

            if (var8 instanceof Error) {
               throw (Error)var8;
            }

            throw new RuntimeException(var8);
         }
      } else {
         var4 = this.printOnEDT(var1, var2, var3);
      }

      return var4;
   }

   private int printOnEDT(Graphics var1, PageFormat var2, int var3) throws PrinterException {
      assert SwingUtilities.isEventDispatchThread();

      Object var4 = BorderFactory.createEmptyBorder();
      if (this.headerFormat != null || this.footerFormat != null) {
         Object[] var5 = new Object[]{var3 + 1};
         if (this.headerFormat != null) {
            var4 = new TitledBorder((Border)var4, this.headerFormat.format(var5), 2, 1, this.headerFont, this.printShell.getForeground());
         }

         if (this.footerFormat != null) {
            var4 = new TitledBorder((Border)var4, this.footerFormat.format(var5), 2, 6, this.footerFont, this.printShell.getForeground());
         }
      }

      Insets var9 = ((Border)var4).getBorderInsets(this.printShell);
      this.updatePagesMetrics(var3, (int)Math.floor(var2.getImageableHeight()) - var9.top - var9.bottom);
      if (this.pagesMetrics.size() <= var3) {
         return 1;
      } else {
         Graphics2D var6 = (Graphics2D)var1.create();
         var6.translate(var2.getImageableX(), var2.getImageableY());
         ((Border)var4).paintBorder(this.printShell, var6, 0, 0, (int)Math.floor(var2.getImageableWidth()), (int)Math.floor(var2.getImageableHeight()));
         var6.translate(0, var9.top);
         Rectangle var7 = new Rectangle(0, 0, (int)var2.getWidth(), ((TextComponentPrintable.IntegerSegment)this.pagesMetrics.get(var3)).end - ((TextComponentPrintable.IntegerSegment)this.pagesMetrics.get(var3)).start + 1);
         var6.clip(var7);
         int var8 = 0;
         if (ComponentOrientation.RIGHT_TO_LEFT == this.printShell.getComponentOrientation()) {
            var8 = (int)var2.getImageableWidth() - this.printShell.getWidth();
         }

         var6.translate(var8, -((TextComponentPrintable.IntegerSegment)this.pagesMetrics.get(var3)).start);
         this.printShell.print(var6);
         var6.dispose();
         return 0;
      }
   }

   private void releaseReadLock() {
      assert !SwingUtilities.isEventDispatchThread();

      Document var1 = this.textComponentToPrint.getDocument();
      if (var1 instanceof AbstractDocument) {
         try {
            ((AbstractDocument)var1).readUnlock();
            this.needReadLock = true;
         } catch (Error var3) {
         }
      }

   }

   private void acquireReadLock() {
      assert !SwingUtilities.isEventDispatchThread();

      if (this.needReadLock) {
         try {
            SwingUtilities.invokeAndWait(new Runnable() {
               public void run() {
               }
            });
         } catch (InterruptedException var2) {
         } catch (InvocationTargetException var3) {
         }

         Document var1 = this.textComponentToPrint.getDocument();
         ((AbstractDocument)var1).readLock();
         this.needReadLock = false;
      }

   }

   private void layout(final int var1) {
      if (!SwingUtilities.isEventDispatchThread()) {
         Callable var2 = new Callable<Object>() {
            public Object call() throws Exception {
               TextComponentPrintable.this.layoutOnEDT(var1);
               return null;
            }
         };
         FutureTask var3 = new FutureTask(var2);
         this.releaseReadLock();
         SwingUtilities.invokeLater(var3);

         try {
            var3.get();
         } catch (InterruptedException var10) {
            throw new RuntimeException(var10);
         } catch (ExecutionException var11) {
            Throwable var5 = var11.getCause();
            if (var5 instanceof RuntimeException) {
               throw (RuntimeException)var5;
            }

            if (var5 instanceof Error) {
               throw (Error)var5;
            }

            throw new RuntimeException(var5);
         } finally {
            this.acquireReadLock();
         }
      } else {
         this.layoutOnEDT(var1);
      }

      this.isLayouted = true;
   }

   private void layoutOnEDT(int var1) {
      assert SwingUtilities.isEventDispatchThread();

      CellRendererPane var3 = new CellRendererPane();
      JViewport var4 = new JViewport();
      var4.setBorder((Border)null);
      Dimension var5 = new Dimension(var1, 2147482647);
      if (this.printShell instanceof JTextField) {
         var5 = new Dimension(var5.width, this.printShell.getPreferredSize().height);
      }

      this.printShell.setSize(var5);
      var4.setComponentOrientation(this.printShell.getComponentOrientation());
      var4.setSize(var5);
      var4.add(this.printShell);
      var3.add(var4);
   }

   private void updatePagesMetrics(int var1, int var2) {
      label39:
      while(true) {
         if (var1 >= this.pagesMetrics.size() && !this.rowsMetrics.isEmpty()) {
            int var3 = this.pagesMetrics.size() - 1;
            int var4 = var3 >= 0 ? ((TextComponentPrintable.IntegerSegment)this.pagesMetrics.get(var3)).end + 1 : 0;

            int var5;
            for(var5 = 0; var5 < this.rowsMetrics.size() && ((TextComponentPrintable.IntegerSegment)this.rowsMetrics.get(var5)).end - var4 + 1 <= var2; ++var5) {
            }

            if (var5 == 0) {
               this.pagesMetrics.add(new TextComponentPrintable.IntegerSegment(var4, var4 + var2 - 1));
               continue;
            }

            --var5;
            this.pagesMetrics.add(new TextComponentPrintable.IntegerSegment(var4, ((TextComponentPrintable.IntegerSegment)this.rowsMetrics.get(var5)).end));
            int var6 = 0;

            while(true) {
               if (var6 > var5) {
                  continue label39;
               }

               this.rowsMetrics.remove(0);
               ++var6;
            }
         }

         return;
      }
   }

   private void calculateRowsMetrics() {
      int var1 = this.printShell.getDocument().getLength();
      ArrayList var2 = new ArrayList(1000);
      int var4 = 0;
      int var5 = -1;

      for(int var6 = -1; var4 < var1; ++var4) {
         try {
            Rectangle var3 = this.printShell.modelToView(var4);
            if (var3 != null) {
               int var7 = (int)var3.getY();
               int var8 = (int)var3.getHeight();
               if (var8 != 0 && (var7 != var5 || var8 != var6)) {
                  var5 = var7;
                  var6 = var8;
                  var2.add(new TextComponentPrintable.IntegerSegment(var7, var7 + var8 - 1));
               }
            }
         } catch (BadLocationException var9) {
            assert false;
         }
      }

      Collections.sort(var2);
      var4 = Integer.MIN_VALUE;
      var5 = Integer.MIN_VALUE;
      Iterator var10 = var2.iterator();

      while(var10.hasNext()) {
         TextComponentPrintable.IntegerSegment var11 = (TextComponentPrintable.IntegerSegment)var10.next();
         if (var5 < var11.start) {
            if (var5 != Integer.MIN_VALUE) {
               this.rowsMetrics.add(new TextComponentPrintable.IntegerSegment(var4, var5));
            }

            var4 = var11.start;
            var5 = var11.end;
         } else {
            var5 = var11.end;
         }
      }

      if (var5 != Integer.MIN_VALUE) {
         this.rowsMetrics.add(new TextComponentPrintable.IntegerSegment(var4, var5));
      }

   }

   private static class IntegerSegment implements Comparable<TextComponentPrintable.IntegerSegment> {
      final int start;
      final int end;

      IntegerSegment(int var1, int var2) {
         this.start = var1;
         this.end = var2;
      }

      public int compareTo(TextComponentPrintable.IntegerSegment var1) {
         int var2 = this.start - var1.start;
         return var2 != 0 ? var2 : this.end - var1.end;
      }

      public boolean equals(Object var1) {
         if (var1 instanceof TextComponentPrintable.IntegerSegment) {
            return this.compareTo((TextComponentPrintable.IntegerSegment)var1) == 0;
         } else {
            return false;
         }
      }

      public int hashCode() {
         byte var1 = 17;
         int var2 = 37 * var1 + this.start;
         var2 = 37 * var2 + this.end;
         return var2;
      }

      public String toString() {
         return "IntegerSegment [" + this.start + ", " + this.end + "]";
      }
   }
}
