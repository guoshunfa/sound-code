package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;

public class ProgressMonitor implements Accessible {
   private ProgressMonitor root;
   private JDialog dialog;
   private JOptionPane pane;
   private JProgressBar myBar;
   private JLabel noteLabel;
   private Component parentComponent;
   private String note;
   private Object[] cancelOption;
   private Object message;
   private long T0;
   private int millisToDecideToPopup;
   private int millisToPopup;
   private int min;
   private int max;
   protected AccessibleContext accessibleContext;
   private AccessibleContext accessibleJOptionPane;

   public ProgressMonitor(Component var1, Object var2, String var3, int var4, int var5) {
      this(var1, var2, var3, var4, var5, (ProgressMonitor)null);
   }

   private ProgressMonitor(Component var1, Object var2, String var3, int var4, int var5, ProgressMonitor var6) {
      this.cancelOption = null;
      this.millisToDecideToPopup = 500;
      this.millisToPopup = 2000;
      this.accessibleContext = null;
      this.accessibleJOptionPane = null;
      this.min = var4;
      this.max = var5;
      this.parentComponent = var1;
      this.cancelOption = new Object[1];
      this.cancelOption[0] = UIManager.getString("OptionPane.cancelButtonText");
      this.message = var2;
      this.note = var3;
      if (var6 != null) {
         this.root = var6.root != null ? var6.root : var6;
         this.T0 = this.root.T0;
         this.dialog = this.root.dialog;
      } else {
         this.T0 = System.currentTimeMillis();
      }

   }

   public void setProgress(int var1) {
      if (var1 >= this.max) {
         this.close();
      } else if (this.myBar != null) {
         this.myBar.setValue(var1);
      } else {
         long var2 = System.currentTimeMillis();
         long var4 = (long)((int)(var2 - this.T0));
         if (var4 >= (long)this.millisToDecideToPopup) {
            int var6;
            if (var1 > this.min) {
               var6 = (int)(var4 * (long)(this.max - this.min) / (long)(var1 - this.min));
            } else {
               var6 = this.millisToPopup;
            }

            if (var6 >= this.millisToPopup) {
               this.myBar = new JProgressBar();
               this.myBar.setMinimum(this.min);
               this.myBar.setMaximum(this.max);
               this.myBar.setValue(var1);
               if (this.note != null) {
                  this.noteLabel = new JLabel(this.note);
               }

               this.pane = new ProgressMonitor.ProgressOptionPane(new Object[]{this.message, this.noteLabel, this.myBar});
               this.dialog = this.pane.createDialog(this.parentComponent, UIManager.getString("ProgressMonitor.progressText"));
               this.dialog.show();
            }
         }
      }

   }

   public void close() {
      if (this.dialog != null) {
         this.dialog.setVisible(false);
         this.dialog.dispose();
         this.dialog = null;
         this.pane = null;
         this.myBar = null;
      }

   }

   public int getMinimum() {
      return this.min;
   }

   public void setMinimum(int var1) {
      if (this.myBar != null) {
         this.myBar.setMinimum(var1);
      }

      this.min = var1;
   }

   public int getMaximum() {
      return this.max;
   }

   public void setMaximum(int var1) {
      if (this.myBar != null) {
         this.myBar.setMaximum(var1);
      }

      this.max = var1;
   }

   public boolean isCanceled() {
      if (this.pane == null) {
         return false;
      } else {
         Object var1 = this.pane.getValue();
         return var1 != null && this.cancelOption.length == 1 && var1.equals(this.cancelOption[0]);
      }
   }

   public void setMillisToDecideToPopup(int var1) {
      this.millisToDecideToPopup = var1;
   }

   public int getMillisToDecideToPopup() {
      return this.millisToDecideToPopup;
   }

   public void setMillisToPopup(int var1) {
      this.millisToPopup = var1;
   }

   public int getMillisToPopup() {
      return this.millisToPopup;
   }

   public void setNote(String var1) {
      this.note = var1;
      if (this.noteLabel != null) {
         this.noteLabel.setText(var1);
      }

   }

   public String getNote() {
      return this.note;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new ProgressMonitor.AccessibleProgressMonitor();
      }

      if (this.pane != null && this.accessibleJOptionPane == null && this.accessibleContext instanceof ProgressMonitor.AccessibleProgressMonitor) {
         ((ProgressMonitor.AccessibleProgressMonitor)this.accessibleContext).optionPaneCreated();
      }

      return this.accessibleContext;
   }

   protected class AccessibleProgressMonitor extends AccessibleContext implements AccessibleText, ChangeListener, PropertyChangeListener {
      private Object oldModelValue;

      private void optionPaneCreated() {
         ProgressMonitor.this.accessibleJOptionPane = ((ProgressMonitor.ProgressOptionPane)ProgressMonitor.this.pane).getAccessibleJOptionPane();
         if (ProgressMonitor.this.myBar != null) {
            ProgressMonitor.this.myBar.addChangeListener(this);
         }

         if (ProgressMonitor.this.noteLabel != null) {
            ProgressMonitor.this.noteLabel.addPropertyChangeListener(this);
         }

      }

      public void stateChanged(ChangeEvent var1) {
         if (var1 != null) {
            if (ProgressMonitor.this.myBar != null) {
               Integer var2 = ProgressMonitor.this.myBar.getValue();
               this.firePropertyChange("AccessibleValue", this.oldModelValue, var2);
               this.oldModelValue = var2;
            }

         }
      }

      public void propertyChange(PropertyChangeEvent var1) {
         if (var1.getSource() == ProgressMonitor.this.noteLabel && var1.getPropertyName() == "text") {
            this.firePropertyChange("AccessibleText", (Object)null, 0);
         }

      }

      public String getAccessibleName() {
         if (this.accessibleName != null) {
            return this.accessibleName;
         } else {
            return ProgressMonitor.this.accessibleJOptionPane != null ? ProgressMonitor.this.accessibleJOptionPane.getAccessibleName() : null;
         }
      }

      public String getAccessibleDescription() {
         if (this.accessibleDescription != null) {
            return this.accessibleDescription;
         } else {
            return ProgressMonitor.this.accessibleJOptionPane != null ? ProgressMonitor.this.accessibleJOptionPane.getAccessibleDescription() : null;
         }
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.PROGRESS_MONITOR;
      }

      public AccessibleStateSet getAccessibleStateSet() {
         return ProgressMonitor.this.accessibleJOptionPane != null ? ProgressMonitor.this.accessibleJOptionPane.getAccessibleStateSet() : null;
      }

      public Accessible getAccessibleParent() {
         return ProgressMonitor.this.dialog;
      }

      private AccessibleContext getParentAccessibleContext() {
         return ProgressMonitor.this.dialog != null ? ProgressMonitor.this.dialog.getAccessibleContext() : null;
      }

      public int getAccessibleIndexInParent() {
         return ProgressMonitor.this.accessibleJOptionPane != null ? ProgressMonitor.this.accessibleJOptionPane.getAccessibleIndexInParent() : -1;
      }

      public int getAccessibleChildrenCount() {
         AccessibleContext var1 = this.getPanelAccessibleContext();
         return var1 != null ? var1.getAccessibleChildrenCount() : 0;
      }

      public Accessible getAccessibleChild(int var1) {
         AccessibleContext var2 = this.getPanelAccessibleContext();
         return var2 != null ? var2.getAccessibleChild(var1) : null;
      }

      private AccessibleContext getPanelAccessibleContext() {
         if (ProgressMonitor.this.myBar != null) {
            Container var1 = ProgressMonitor.this.myBar.getParent();
            if (var1 instanceof Accessible) {
               return var1.getAccessibleContext();
            }
         }

         return null;
      }

      public Locale getLocale() throws IllegalComponentStateException {
         return ProgressMonitor.this.accessibleJOptionPane != null ? ProgressMonitor.this.accessibleJOptionPane.getLocale() : null;
      }

      public AccessibleComponent getAccessibleComponent() {
         return ProgressMonitor.this.accessibleJOptionPane != null ? ProgressMonitor.this.accessibleJOptionPane.getAccessibleComponent() : null;
      }

      public AccessibleValue getAccessibleValue() {
         return ProgressMonitor.this.myBar != null ? ProgressMonitor.this.myBar.getAccessibleContext().getAccessibleValue() : null;
      }

      public AccessibleText getAccessibleText() {
         return this.getNoteLabelAccessibleText() != null ? this : null;
      }

      private AccessibleText getNoteLabelAccessibleText() {
         return ProgressMonitor.this.noteLabel != null ? ProgressMonitor.this.noteLabel.getAccessibleContext().getAccessibleText() : null;
      }

      public int getIndexAtPoint(Point var1) {
         AccessibleText var2 = this.getNoteLabelAccessibleText();
         if (var2 != null && this.sameWindowAncestor(ProgressMonitor.this.pane, ProgressMonitor.this.noteLabel)) {
            Point var3 = SwingUtilities.convertPoint(ProgressMonitor.this.pane, var1, ProgressMonitor.this.noteLabel);
            if (var3 != null) {
               return var2.getIndexAtPoint(var3);
            }
         }

         return -1;
      }

      public Rectangle getCharacterBounds(int var1) {
         AccessibleText var2 = this.getNoteLabelAccessibleText();
         if (var2 != null && this.sameWindowAncestor(ProgressMonitor.this.pane, ProgressMonitor.this.noteLabel)) {
            Rectangle var3 = var2.getCharacterBounds(var1);
            if (var3 != null) {
               return SwingUtilities.convertRectangle(ProgressMonitor.this.noteLabel, var3, ProgressMonitor.this.pane);
            }
         }

         return null;
      }

      private boolean sameWindowAncestor(Component var1, Component var2) {
         if (var1 != null && var2 != null) {
            return SwingUtilities.getWindowAncestor(var1) == SwingUtilities.getWindowAncestor(var2);
         } else {
            return false;
         }
      }

      public int getCharCount() {
         AccessibleText var1 = this.getNoteLabelAccessibleText();
         return var1 != null ? var1.getCharCount() : -1;
      }

      public int getCaretPosition() {
         AccessibleText var1 = this.getNoteLabelAccessibleText();
         return var1 != null ? var1.getCaretPosition() : -1;
      }

      public String getAtIndex(int var1, int var2) {
         AccessibleText var3 = this.getNoteLabelAccessibleText();
         return var3 != null ? var3.getAtIndex(var1, var2) : null;
      }

      public String getAfterIndex(int var1, int var2) {
         AccessibleText var3 = this.getNoteLabelAccessibleText();
         return var3 != null ? var3.getAfterIndex(var1, var2) : null;
      }

      public String getBeforeIndex(int var1, int var2) {
         AccessibleText var3 = this.getNoteLabelAccessibleText();
         return var3 != null ? var3.getBeforeIndex(var1, var2) : null;
      }

      public AttributeSet getCharacterAttribute(int var1) {
         AccessibleText var2 = this.getNoteLabelAccessibleText();
         return var2 != null ? var2.getCharacterAttribute(var1) : null;
      }

      public int getSelectionStart() {
         AccessibleText var1 = this.getNoteLabelAccessibleText();
         return var1 != null ? var1.getSelectionStart() : -1;
      }

      public int getSelectionEnd() {
         AccessibleText var1 = this.getNoteLabelAccessibleText();
         return var1 != null ? var1.getSelectionEnd() : -1;
      }

      public String getSelectedText() {
         AccessibleText var1 = this.getNoteLabelAccessibleText();
         return var1 != null ? var1.getSelectedText() : null;
      }
   }

   private class ProgressOptionPane extends JOptionPane {
      ProgressOptionPane(Object var2) {
         super(var2, 1, -1, (Icon)null, ProgressMonitor.this.cancelOption, (Object)null);
      }

      public int getMaxCharactersPerLineCount() {
         return 60;
      }

      public JDialog createDialog(Component var1, String var2) {
         Window var4 = JOptionPane.getWindowForComponent(var1);
         final JDialog var3;
         if (var4 instanceof Frame) {
            var3 = new JDialog((Frame)var4, var2, false);
         } else {
            var3 = new JDialog((Dialog)var4, var2, false);
         }

         if (var4 instanceof SwingUtilities.SharedOwnerFrame) {
            WindowListener var5 = SwingUtilities.getSharedOwnerFrameShutdownListener();
            var3.addWindowListener(var5);
         }

         Container var6 = var3.getContentPane();
         var6.setLayout(new BorderLayout());
         var6.add((Component)this, (Object)"Center");
         var3.pack();
         var3.setLocationRelativeTo(var1);
         var3.addWindowListener(new WindowAdapter() {
            boolean gotFocus = false;

            public void windowClosing(WindowEvent var1) {
               ProgressOptionPane.this.setValue(ProgressMonitor.this.cancelOption[0]);
            }

            public void windowActivated(WindowEvent var1) {
               if (!this.gotFocus) {
                  ProgressOptionPane.this.selectInitialValue();
                  this.gotFocus = true;
               }

            }
         });
         this.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent var1) {
               if (var3.isVisible() && var1.getSource() == ProgressOptionPane.this && (var1.getPropertyName().equals("value") || var1.getPropertyName().equals("inputValue"))) {
                  var3.setVisible(false);
                  var3.dispose();
               }

            }
         });
         return var3;
      }

      public AccessibleContext getAccessibleContext() {
         return ProgressMonitor.this.getAccessibleContext();
      }

      private AccessibleContext getAccessibleJOptionPane() {
         return super.getAccessibleContext();
      }
   }
}
