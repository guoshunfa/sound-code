package sun.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class PrintingStatus {
   private final PrinterJob job;
   private final Component parent;
   private JDialog abortDialog;
   private JButton abortButton;
   private JLabel statusLabel;
   private MessageFormat statusFormat;
   private final AtomicBoolean isAborted = new AtomicBoolean(false);
   private final Action abortAction = new AbstractAction() {
      public void actionPerformed(ActionEvent var1) {
         if (!PrintingStatus.this.isAborted.get()) {
            PrintingStatus.this.isAborted.set(true);
            PrintingStatus.this.abortButton.setEnabled(false);
            PrintingStatus.this.abortDialog.setTitle(UIManager.getString("PrintingDialog.titleAbortingText"));
            PrintingStatus.this.statusLabel.setText(UIManager.getString("PrintingDialog.contentAbortingText"));
            PrintingStatus.this.job.cancel();
         }

      }
   };
   private final WindowAdapter closeListener = new WindowAdapter() {
      public void windowClosing(WindowEvent var1) {
         PrintingStatus.this.abortAction.actionPerformed((ActionEvent)null);
      }
   };

   public static PrintingStatus createPrintingStatus(Component var0, PrinterJob var1) {
      return new PrintingStatus(var0, var1);
   }

   protected PrintingStatus(Component var1, PrinterJob var2) {
      this.job = var2;
      this.parent = var1;
   }

   private void init() {
      String var1 = UIManager.getString("PrintingDialog.titleProgressText");
      String var2 = UIManager.getString("PrintingDialog.contentInitialText");
      this.statusFormat = new MessageFormat(UIManager.getString("PrintingDialog.contentProgressText"));
      String var3 = UIManager.getString("PrintingDialog.abortButtonText");
      String var4 = UIManager.getString("PrintingDialog.abortButtonToolTipText");
      int var5 = getInt("PrintingDialog.abortButtonMnemonic", -1);
      int var6 = getInt("PrintingDialog.abortButtonDisplayedMnemonicIndex", -1);
      this.abortButton = new JButton(var3);
      this.abortButton.addActionListener(this.abortAction);
      this.abortButton.setToolTipText(var4);
      if (var5 != -1) {
         this.abortButton.setMnemonic(var5);
      }

      if (var6 != -1) {
         this.abortButton.setDisplayedMnemonicIndex(var6);
      }

      this.statusLabel = new JLabel(var2);
      JOptionPane var7 = new JOptionPane(this.statusLabel, 1, -1, (Icon)null, new Object[]{this.abortButton}, this.abortButton);
      var7.getActionMap().put("close", this.abortAction);
      if (this.parent != null && this.parent.getParent() instanceof JViewport) {
         this.abortDialog = var7.createDialog(this.parent.getParent(), var1);
      } else {
         this.abortDialog = var7.createDialog(this.parent, var1);
      }

      this.abortDialog.setDefaultCloseOperation(0);
      this.abortDialog.addWindowListener(this.closeListener);
   }

   public void showModal(final boolean var1) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.showModalOnEDT(var1);
      } else {
         try {
            SwingUtilities.invokeAndWait(new Runnable() {
               public void run() {
                  PrintingStatus.this.showModalOnEDT(var1);
               }
            });
         } catch (InterruptedException var4) {
            throw new RuntimeException(var4);
         } catch (InvocationTargetException var5) {
            Throwable var3 = var5.getCause();
            if (var3 instanceof RuntimeException) {
               throw (RuntimeException)var3;
            }

            if (var3 instanceof Error) {
               throw (Error)var3;
            }

            throw new RuntimeException(var3);
         }
      }

   }

   private void showModalOnEDT(boolean var1) {
      assert SwingUtilities.isEventDispatchThread();

      this.init();
      this.abortDialog.setModal(var1);
      this.abortDialog.setVisible(true);
   }

   public void dispose() {
      if (SwingUtilities.isEventDispatchThread()) {
         this.disposeOnEDT();
      } else {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               PrintingStatus.this.disposeOnEDT();
            }
         });
      }

   }

   private void disposeOnEDT() {
      assert SwingUtilities.isEventDispatchThread();

      if (this.abortDialog != null) {
         this.abortDialog.removeWindowListener(this.closeListener);
         this.abortDialog.dispose();
         this.abortDialog = null;
      }

   }

   public boolean isAborted() {
      return this.isAborted.get();
   }

   public Printable createNotificationPrintable(Printable var1) {
      return new PrintingStatus.NotificationPrintable(var1);
   }

   static int getInt(Object var0, int var1) {
      Object var2 = UIManager.get(var0);
      if (var2 instanceof Integer) {
         return (Integer)var2;
      } else {
         if (var2 instanceof String) {
            try {
               return Integer.parseInt((String)var2);
            } catch (NumberFormatException var4) {
            }
         }

         return var1;
      }
   }

   private class NotificationPrintable implements Printable {
      private final Printable printDelegatee;

      public NotificationPrintable(Printable var2) {
         if (var2 == null) {
            throw new NullPointerException("Printable is null");
         } else {
            this.printDelegatee = var2;
         }
      }

      public int print(Graphics var1, PageFormat var2, final int var3) throws PrinterException {
         int var4 = this.printDelegatee.print(var1, var2, var3);
         if (var4 != 1 && !PrintingStatus.this.isAborted()) {
            if (SwingUtilities.isEventDispatchThread()) {
               this.updateStatusOnEDT(var3);
            } else {
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     NotificationPrintable.this.updateStatusOnEDT(var3);
                  }
               });
            }
         }

         return var4;
      }

      private void updateStatusOnEDT(int var1) {
         assert SwingUtilities.isEventDispatchThread();

         Object[] var2 = new Object[]{new Integer(var1 + 1)};
         PrintingStatus.this.statusLabel.setText(PrintingStatus.this.statusFormat.format(var2));
      }
   }
}
