package sun.print;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobPriority;
import javax.print.attribute.standard.JobSheets;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterInfo;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterMakeAndModel;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.NumberFormatter;

public class ServiceDialog extends JDialog implements ActionListener {
   public static final int WAITING = 0;
   public static final int APPROVE = 1;
   public static final int CANCEL = 2;
   private static final String strBundle = "sun.print.resources.serviceui";
   private static final Insets panelInsets = new Insets(6, 6, 6, 6);
   private static final Insets compInsets = new Insets(3, 6, 3, 6);
   private static ResourceBundle messageRB;
   private JTabbedPane tpTabs;
   private JButton btnCancel;
   private JButton btnApprove;
   private PrintService[] services;
   private int defaultServiceIndex;
   private PrintRequestAttributeSet asOriginal;
   private HashPrintRequestAttributeSet asCurrent;
   private PrintService psCurrent;
   private DocFlavor docFlavor;
   private int status;
   private ServiceDialog.ValidatingFileChooser jfc;
   private ServiceDialog.GeneralPanel pnlGeneral;
   private ServiceDialog.PageSetupPanel pnlPageSetup;
   private ServiceDialog.AppearancePanel pnlAppearance;
   private boolean isAWT = false;
   static Class _keyEventClazz;

   public ServiceDialog(GraphicsConfiguration var1, int var2, int var3, PrintService[] var4, int var5, DocFlavor var6, PrintRequestAttributeSet var7, Dialog var8) {
      super(var8, getMsg("dialog.printtitle"), true, var1);
      this.initPrintDialog(var2, var3, var4, var5, var6, var7);
   }

   public ServiceDialog(GraphicsConfiguration var1, int var2, int var3, PrintService[] var4, int var5, DocFlavor var6, PrintRequestAttributeSet var7, Frame var8) {
      super(var8, getMsg("dialog.printtitle"), true, var1);
      this.initPrintDialog(var2, var3, var4, var5, var6, var7);
   }

   void initPrintDialog(int var1, int var2, PrintService[] var3, int var4, DocFlavor var5, PrintRequestAttributeSet var6) {
      this.services = var3;
      this.defaultServiceIndex = var4;
      this.asOriginal = var6;
      this.asCurrent = new HashPrintRequestAttributeSet(var6);
      this.psCurrent = var3[var4];
      this.docFlavor = var5;
      SunPageSelection var7 = (SunPageSelection)var6.get(SunPageSelection.class);
      if (var7 != null) {
         this.isAWT = true;
      }

      if (var6.get(DialogOnTop.class) != null) {
         this.setAlwaysOnTop(true);
      }

      Container var8 = this.getContentPane();
      var8.setLayout(new BorderLayout());
      this.tpTabs = new JTabbedPane();
      this.tpTabs.setBorder(new EmptyBorder(5, 5, 5, 5));
      String var9 = getMsg("tab.general");
      int var10 = getVKMnemonic("tab.general");
      this.pnlGeneral = new ServiceDialog.GeneralPanel();
      this.tpTabs.add((String)var9, (Component)this.pnlGeneral);
      this.tpTabs.setMnemonicAt(0, var10);
      String var11 = getMsg("tab.pagesetup");
      int var12 = getVKMnemonic("tab.pagesetup");
      this.pnlPageSetup = new ServiceDialog.PageSetupPanel();
      this.tpTabs.add((String)var11, (Component)this.pnlPageSetup);
      this.tpTabs.setMnemonicAt(1, var12);
      String var13 = getMsg("tab.appearance");
      int var14 = getVKMnemonic("tab.appearance");
      this.pnlAppearance = new ServiceDialog.AppearancePanel();
      this.tpTabs.add((String)var13, (Component)this.pnlAppearance);
      this.tpTabs.setMnemonicAt(2, var14);
      var8.add((Component)this.tpTabs, (Object)"Center");
      this.updatePanels();
      JPanel var15 = new JPanel(new FlowLayout(4));
      this.btnApprove = createExitButton("button.print", this);
      var15.add(this.btnApprove);
      this.getRootPane().setDefaultButton(this.btnApprove);
      this.btnCancel = createExitButton("button.cancel", this);
      this.handleEscKey(this.btnCancel);
      var15.add(this.btnCancel);
      var8.add((Component)var15, (Object)"South");
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent var1) {
            ServiceDialog.this.dispose(2);
         }
      });
      this.getAccessibleContext().setAccessibleDescription(getMsg("dialog.printtitle"));
      this.setResizable(false);
      this.setLocation(var1, var2);
      this.pack();
   }

   public ServiceDialog(GraphicsConfiguration var1, int var2, int var3, PrintService var4, DocFlavor var5, PrintRequestAttributeSet var6, Dialog var7) {
      super(var7, getMsg("dialog.pstitle"), true, var1);
      this.initPageDialog(var2, var3, var4, var5, var6);
   }

   public ServiceDialog(GraphicsConfiguration var1, int var2, int var3, PrintService var4, DocFlavor var5, PrintRequestAttributeSet var6, Frame var7) {
      super(var7, getMsg("dialog.pstitle"), true, var1);
      this.initPageDialog(var2, var3, var4, var5, var6);
   }

   void initPageDialog(int var1, int var2, PrintService var3, DocFlavor var4, PrintRequestAttributeSet var5) {
      this.psCurrent = var3;
      this.docFlavor = var4;
      this.asOriginal = var5;
      this.asCurrent = new HashPrintRequestAttributeSet(var5);
      if (var5.get(DialogOnTop.class) != null) {
         this.setAlwaysOnTop(true);
      }

      Container var6 = this.getContentPane();
      var6.setLayout(new BorderLayout());
      this.pnlPageSetup = new ServiceDialog.PageSetupPanel();
      var6.add((Component)this.pnlPageSetup, (Object)"Center");
      this.pnlPageSetup.updateInfo();
      JPanel var7 = new JPanel(new FlowLayout(4));
      this.btnApprove = createExitButton("button.ok", this);
      var7.add(this.btnApprove);
      this.getRootPane().setDefaultButton(this.btnApprove);
      this.btnCancel = createExitButton("button.cancel", this);
      this.handleEscKey(this.btnCancel);
      var7.add(this.btnCancel);
      var6.add((Component)var7, (Object)"South");
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent var1) {
            ServiceDialog.this.dispose(2);
         }
      });
      this.getAccessibleContext().setAccessibleDescription(getMsg("dialog.pstitle"));
      this.setResizable(false);
      this.setLocation(var1, var2);
      this.pack();
   }

   private void handleEscKey(JButton var1) {
      AbstractAction var2 = new AbstractAction() {
         public void actionPerformed(ActionEvent var1) {
            ServiceDialog.this.dispose(2);
         }
      };
      KeyStroke var3 = KeyStroke.getKeyStroke(27, 0);
      InputMap var4 = var1.getInputMap(2);
      ActionMap var5 = var1.getActionMap();
      if (var4 != null && var5 != null) {
         var4.put(var3, "cancel");
         var5.put("cancel", var2);
      }

   }

   public int getStatus() {
      return this.status;
   }

   public PrintRequestAttributeSet getAttributes() {
      return (PrintRequestAttributeSet)(this.status == 1 ? this.asCurrent : this.asOriginal);
   }

   public PrintService getPrintService() {
      return this.status == 1 ? this.psCurrent : null;
   }

   public void dispose(int var1) {
      this.status = var1;
      super.dispose();
   }

   public void actionPerformed(ActionEvent var1) {
      Object var2 = var1.getSource();
      boolean var3 = false;
      if (var2 == this.btnApprove) {
         var3 = true;
         if (this.pnlGeneral != null) {
            if (this.pnlGeneral.isPrintToFileRequested()) {
               var3 = this.showFileChooser();
            } else {
               this.asCurrent.remove(Destination.class);
            }
         }
      }

      this.dispose(var3 ? 1 : 2);
   }

   private boolean showFileChooser() {
      Class var1 = Destination.class;
      Destination var2 = (Destination)this.asCurrent.get(var1);
      if (var2 == null) {
         var2 = (Destination)this.asOriginal.get(var1);
         if (var2 == null) {
            var2 = (Destination)this.psCurrent.getDefaultAttributeValue(var1);
            if (var2 == null) {
               try {
                  var2 = new Destination(new URI("file:out.prn"));
               } catch (URISyntaxException var9) {
               }
            }
         }
      }

      File var3;
      if (var2 != null) {
         try {
            var3 = new File(var2.getURI());
         } catch (Exception var8) {
            var3 = new File("out.prn");
         }
      } else {
         var3 = new File("out.prn");
      }

      ServiceDialog.ValidatingFileChooser var4 = new ServiceDialog.ValidatingFileChooser();
      var4.setApproveButtonText(getMsg("button.ok"));
      var4.setDialogTitle(getMsg("dialog.printtofile"));
      var4.setDialogType(1);
      var4.setSelectedFile(var3);
      int var5 = var4.showDialog(this, (String)null);
      if (var5 == 0) {
         var3 = var4.getSelectedFile();

         try {
            this.asCurrent.add(new Destination(var3.toURI()));
         } catch (Exception var7) {
            this.asCurrent.remove(var1);
         }
      } else {
         this.asCurrent.remove(var1);
      }

      return var5 == 0;
   }

   private void updatePanels() {
      this.pnlGeneral.updateInfo();
      this.pnlPageSetup.updateInfo();
      this.pnlAppearance.updateInfo();
   }

   public static void initResource() {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            try {
               ServiceDialog.messageRB = ResourceBundle.getBundle("sun.print.resources.serviceui");
               return null;
            } catch (MissingResourceException var2) {
               throw new Error("Fatal: Resource for ServiceUI is missing");
            }
         }
      });
   }

   public static String getMsg(String var0) {
      try {
         return removeMnemonics(messageRB.getString(var0));
      } catch (MissingResourceException var2) {
         throw new Error("Fatal: Resource for ServiceUI is broken; there is no " + var0 + " key in resource");
      }
   }

   private static String removeMnemonics(String var0) {
      int var1 = var0.indexOf(38);
      int var2 = var0.length();
      if (var1 >= 0 && var1 != var2 - 1) {
         int var3 = var0.indexOf(38, var1 + 1);
         if (var3 == var1 + 1) {
            return var3 + 1 == var2 ? var0.substring(0, var1 + 1) : var0.substring(0, var1 + 1) + removeMnemonics(var0.substring(var3 + 1));
         } else {
            return var1 == 0 ? removeMnemonics(var0.substring(1)) : var0.substring(0, var1) + removeMnemonics(var0.substring(var1 + 1));
         }
      } else {
         return var0;
      }
   }

   private static char getMnemonic(String var0) {
      String var1 = messageRB.getString(var0).replace("&&", "");
      int var2 = var1.indexOf(38);
      if (0 <= var2 && var2 < var1.length() - 1) {
         char var3 = var1.charAt(var2 + 1);
         return Character.toUpperCase(var3);
      } else {
         return '\u0000';
      }
   }

   private static int getVKMnemonic(String var0) {
      String var1 = String.valueOf(getMnemonic(var0));
      if (var1 != null && var1.length() == 1) {
         String var2 = "VK_" + var1.toUpperCase();

         try {
            if (_keyEventClazz == null) {
               _keyEventClazz = Class.forName("java.awt.event.KeyEvent", true, ServiceDialog.class.getClassLoader());
            }

            Field var3 = _keyEventClazz.getDeclaredField(var2);
            int var4 = var3.getInt((Object)null);
            return var4;
         } catch (Exception var5) {
            return 0;
         }
      } else {
         return 0;
      }
   }

   private static URL getImageResource(final String var0) {
      URL var1 = (URL)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            URL var1 = ServiceDialog.class.getResource("resources/" + var0);
            return var1;
         }
      });
      if (var1 == null) {
         throw new Error("Fatal: Resource for ServiceUI is broken; there is no " + var0 + " key in resource");
      } else {
         return var1;
      }
   }

   private static JButton createButton(String var0, ActionListener var1) {
      JButton var2 = new JButton(getMsg(var0));
      var2.setMnemonic(getMnemonic(var0));
      var2.addActionListener(var1);
      return var2;
   }

   private static JButton createExitButton(String var0, ActionListener var1) {
      String var2 = getMsg(var0);
      JButton var3 = new JButton(var2);
      var3.addActionListener(var1);
      var3.getAccessibleContext().setAccessibleDescription(var2);
      return var3;
   }

   private static JCheckBox createCheckBox(String var0, ActionListener var1) {
      JCheckBox var2 = new JCheckBox(getMsg(var0));
      var2.setMnemonic(getMnemonic(var0));
      var2.addActionListener(var1);
      return var2;
   }

   private static JRadioButton createRadioButton(String var0, ActionListener var1) {
      JRadioButton var2 = new JRadioButton(getMsg(var0));
      var2.setMnemonic(getMnemonic(var0));
      var2.addActionListener(var1);
      return var2;
   }

   public static void showNoPrintService(GraphicsConfiguration var0) {
      Frame var1 = new Frame(var0);
      JOptionPane.showMessageDialog(var1, getMsg("dialog.noprintermsg"));
      var1.dispose();
   }

   private static void addToGB(Component var0, Container var1, GridBagLayout var2, GridBagConstraints var3) {
      var2.setConstraints(var0, var3);
      var1.add(var0);
   }

   private static void addToBG(AbstractButton var0, Container var1, ButtonGroup var2) {
      var2.add(var0);
      var1.add(var0);
   }

   static {
      initResource();
      _keyEventClazz = null;
   }

   private class ValidatingFileChooser extends JFileChooser {
      private ValidatingFileChooser() {
      }

      public void approveSelection() {
         File var1 = this.getSelectedFile();

         boolean var2;
         try {
            var2 = var1.exists();
         } catch (SecurityException var6) {
            var2 = false;
         }

         if (var2) {
            int var3 = JOptionPane.showConfirmDialog(this, ServiceDialog.getMsg("dialog.overwrite"), ServiceDialog.getMsg("dialog.owtitle"), 0);
            if (var3 != 0) {
               return;
            }
         }

         try {
            if (var1.createNewFile()) {
               var1.delete();
            }
         } catch (IOException var4) {
            JOptionPane.showMessageDialog(this, ServiceDialog.getMsg("dialog.writeerror") + " " + var1, ServiceDialog.getMsg("dialog.owtitle"), 2);
            return;
         } catch (SecurityException var5) {
         }

         File var7 = var1.getParentFile();
         if ((!var1.exists() || var1.isFile() && var1.canWrite()) && (var7 == null || var7.exists() && (!var7.exists() || var7.canWrite()))) {
            super.approveSelection();
         } else {
            JOptionPane.showMessageDialog(this, ServiceDialog.getMsg("dialog.writeerror") + " " + var1, ServiceDialog.getMsg("dialog.owtitle"), 2);
         }
      }

      // $FF: synthetic method
      ValidatingFileChooser(Object var2) {
         this();
      }
   }

   private class IconRadioButton extends JPanel {
      private JRadioButton rb;
      private JLabel lbl;

      public IconRadioButton(String var2, String var3, boolean var4, ButtonGroup var5, ActionListener var6) {
         super(new FlowLayout(3));
         final URL var7 = ServiceDialog.getImageResource(var3);
         Icon var8 = (Icon)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               ImageIcon var1 = new ImageIcon(var7);
               return var1;
            }
         });
         this.lbl = new JLabel(var8);
         this.add(this.lbl);
         this.rb = ServiceDialog.createRadioButton(var2, var6);
         this.rb.setSelected(var4);
         ServiceDialog.addToBG(this.rb, this, var5);
      }

      public void addActionListener(ActionListener var1) {
         this.rb.addActionListener(var1);
      }

      public boolean isSameAs(Object var1) {
         return this.rb == var1;
      }

      public void setEnabled(boolean var1) {
         this.rb.setEnabled(var1);
         this.lbl.setEnabled(var1);
      }

      public boolean isSelected() {
         return this.rb.isSelected();
      }

      public void setSelected(boolean var1) {
         this.rb.setSelected(var1);
      }
   }

   private class JobAttributesPanel extends JPanel implements ActionListener, ChangeListener, FocusListener {
      private final String strTitle = ServiceDialog.getMsg("border.jobattributes");
      private JLabel lblPriority;
      private JLabel lblJobName;
      private JLabel lblUserName;
      private JSpinner spinPriority;
      private SpinnerNumberModel snModel;
      private JCheckBox cbJobSheets;
      private JTextField tfJobName;
      private JTextField tfUserName;

      public JobAttributesPanel() {
         GridBagLayout var2 = new GridBagLayout();
         GridBagConstraints var3 = new GridBagConstraints();
         this.setLayout(var2);
         this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
         var3.fill = 0;
         var3.insets = ServiceDialog.compInsets;
         var3.weighty = 1.0D;
         this.cbJobSheets = ServiceDialog.createCheckBox("checkbox.jobsheets", this);
         var3.anchor = 21;
         ServiceDialog.addToGB(this.cbJobSheets, this, var2, var3);
         JPanel var4 = new JPanel();
         this.lblPriority = new JLabel(ServiceDialog.getMsg("label.priority"), 11);
         this.lblPriority.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.priority"));
         var4.add(this.lblPriority);
         this.snModel = new SpinnerNumberModel(1, 1, 100, 1);
         this.spinPriority = new JSpinner(this.snModel);
         this.lblPriority.setLabelFor(this.spinPriority);
         ((JSpinner.NumberEditor)this.spinPriority.getEditor()).getTextField().setColumns(3);
         this.spinPriority.addChangeListener(this);
         var4.add(this.spinPriority);
         var3.anchor = 22;
         var3.gridwidth = 0;
         var4.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.priority"));
         ServiceDialog.addToGB(var4, this, var2, var3);
         var3.fill = 2;
         var3.anchor = 10;
         var3.weightx = 0.0D;
         var3.gridwidth = 1;
         char var5 = ServiceDialog.getMnemonic("label.jobname");
         this.lblJobName = new JLabel(ServiceDialog.getMsg("label.jobname"), 11);
         this.lblJobName.setDisplayedMnemonic(var5);
         ServiceDialog.addToGB(this.lblJobName, this, var2, var3);
         var3.weightx = 1.0D;
         var3.gridwidth = 0;
         this.tfJobName = new JTextField();
         this.lblJobName.setLabelFor(this.tfJobName);
         this.tfJobName.addFocusListener(this);
         this.tfJobName.setFocusAccelerator(var5);
         this.tfJobName.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.jobname"));
         ServiceDialog.addToGB(this.tfJobName, this, var2, var3);
         var3.weightx = 0.0D;
         var3.gridwidth = 1;
         char var6 = ServiceDialog.getMnemonic("label.username");
         this.lblUserName = new JLabel(ServiceDialog.getMsg("label.username"), 11);
         this.lblUserName.setDisplayedMnemonic(var6);
         ServiceDialog.addToGB(this.lblUserName, this, var2, var3);
         var3.gridwidth = 0;
         this.tfUserName = new JTextField();
         this.lblUserName.setLabelFor(this.tfUserName);
         this.tfUserName.addFocusListener(this);
         this.tfUserName.setFocusAccelerator(var6);
         this.tfUserName.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.username"));
         ServiceDialog.addToGB(this.tfUserName, this, var2, var3);
      }

      public void actionPerformed(ActionEvent var1) {
         if (this.cbJobSheets.isSelected()) {
            ServiceDialog.this.asCurrent.add(JobSheets.STANDARD);
         } else {
            ServiceDialog.this.asCurrent.add(JobSheets.NONE);
         }

      }

      public void stateChanged(ChangeEvent var1) {
         ServiceDialog.this.asCurrent.add(new JobPriority(this.snModel.getNumber().intValue()));
      }

      public void focusLost(FocusEvent var1) {
         Object var2 = var1.getSource();
         if (var2 == this.tfJobName) {
            ServiceDialog.this.asCurrent.add(new JobName(this.tfJobName.getText(), Locale.getDefault()));
         } else if (var2 == this.tfUserName) {
            ServiceDialog.this.asCurrent.add(new RequestingUserName(this.tfUserName.getText(), Locale.getDefault()));
         }

      }

      public void focusGained(FocusEvent var1) {
      }

      public void updateInfo() {
         Class var1 = JobSheets.class;
         Class var2 = JobPriority.class;
         Class var3 = JobName.class;
         Class var4 = RequestingUserName.class;
         boolean var5 = false;
         boolean var6 = false;
         boolean var7 = false;
         boolean var8 = false;
         if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(var1)) {
            var5 = true;
         }

         JobSheets var9 = (JobSheets)ServiceDialog.this.asCurrent.get(var1);
         if (var9 == null) {
            var9 = (JobSheets)ServiceDialog.this.psCurrent.getDefaultAttributeValue(var1);
            if (var9 == null) {
               var9 = JobSheets.NONE;
            }
         }

         this.cbJobSheets.setSelected(var9 != JobSheets.NONE);
         this.cbJobSheets.setEnabled(var5);
         if (!ServiceDialog.this.isAWT && ServiceDialog.this.psCurrent.isAttributeCategorySupported(var2)) {
            var6 = true;
         }

         JobPriority var10 = (JobPriority)ServiceDialog.this.asCurrent.get(var2);
         if (var10 == null) {
            var10 = (JobPriority)ServiceDialog.this.psCurrent.getDefaultAttributeValue(var2);
            if (var10 == null) {
               var10 = new JobPriority(1);
            }
         }

         int var11 = var10.getValue();
         if (var11 < 1 || var11 > 100) {
            var11 = 1;
         }

         this.snModel.setValue(new Integer(var11));
         this.lblPriority.setEnabled(var6);
         this.spinPriority.setEnabled(var6);
         if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(var3)) {
            var7 = true;
         }

         JobName var12 = (JobName)ServiceDialog.this.asCurrent.get(var3);
         if (var12 == null) {
            var12 = (JobName)ServiceDialog.this.psCurrent.getDefaultAttributeValue(var3);
            if (var12 == null) {
               var12 = new JobName("", Locale.getDefault());
            }
         }

         this.tfJobName.setText(var12.getValue());
         this.tfJobName.setEnabled(var7);
         this.lblJobName.setEnabled(var7);
         if (!ServiceDialog.this.isAWT && ServiceDialog.this.psCurrent.isAttributeCategorySupported(var4)) {
            var8 = true;
         }

         RequestingUserName var13 = (RequestingUserName)ServiceDialog.this.asCurrent.get(var4);
         if (var13 == null) {
            var13 = (RequestingUserName)ServiceDialog.this.psCurrent.getDefaultAttributeValue(var4);
            if (var13 == null) {
               var13 = new RequestingUserName("", Locale.getDefault());
            }
         }

         this.tfUserName.setText(var13.getValue());
         this.tfUserName.setEnabled(var8);
         this.lblUserName.setEnabled(var8);
      }
   }

   private class SidesPanel extends JPanel implements ActionListener {
      private final String strTitle = ServiceDialog.getMsg("border.sides");
      private ServiceDialog.IconRadioButton rbOneSide;
      private ServiceDialog.IconRadioButton rbTumble;
      private ServiceDialog.IconRadioButton rbDuplex;

      public SidesPanel() {
         GridBagLayout var2 = new GridBagLayout();
         GridBagConstraints var3 = new GridBagConstraints();
         this.setLayout(var2);
         this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
         var3.fill = 1;
         var3.insets = ServiceDialog.compInsets;
         var3.weighty = 1.0D;
         var3.gridwidth = 0;
         ButtonGroup var4 = new ButtonGroup();
         this.rbOneSide = ServiceDialog.this.new IconRadioButton("radiobutton.oneside", "oneside.png", true, var4, this);
         this.rbOneSide.addActionListener(this);
         ServiceDialog.addToGB(this.rbOneSide, this, var2, var3);
         this.rbTumble = ServiceDialog.this.new IconRadioButton("radiobutton.tumble", "tumble.png", false, var4, this);
         this.rbTumble.addActionListener(this);
         ServiceDialog.addToGB(this.rbTumble, this, var2, var3);
         this.rbDuplex = ServiceDialog.this.new IconRadioButton("radiobutton.duplex", "duplex.png", false, var4, this);
         this.rbDuplex.addActionListener(this);
         var3.gridwidth = 0;
         ServiceDialog.addToGB(this.rbDuplex, this, var2, var3);
      }

      public void actionPerformed(ActionEvent var1) {
         Object var2 = var1.getSource();
         if (this.rbOneSide.isSameAs(var2)) {
            ServiceDialog.this.asCurrent.add(Sides.ONE_SIDED);
         } else if (this.rbTumble.isSameAs(var2)) {
            ServiceDialog.this.asCurrent.add(Sides.TUMBLE);
         } else if (this.rbDuplex.isSameAs(var2)) {
            ServiceDialog.this.asCurrent.add(Sides.DUPLEX);
         }

      }

      public void updateInfo() {
         Class var1 = Sides.class;
         boolean var2 = false;
         boolean var3 = false;
         boolean var4 = false;
         if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(var1)) {
            Object var5 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(var1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
            if (var5 instanceof Sides[]) {
               Sides[] var6 = (Sides[])((Sides[])var5);

               for(int var7 = 0; var7 < var6.length; ++var7) {
                  Sides var8 = var6[var7];
                  if (var8 == Sides.ONE_SIDED) {
                     var2 = true;
                  } else if (var8 == Sides.TUMBLE) {
                     var3 = true;
                  } else if (var8 == Sides.DUPLEX) {
                     var4 = true;
                  }
               }
            }
         }

         this.rbOneSide.setEnabled(var2);
         this.rbTumble.setEnabled(var3);
         this.rbDuplex.setEnabled(var4);
         Sides var9 = (Sides)ServiceDialog.this.asCurrent.get(var1);
         if (var9 == null) {
            var9 = (Sides)ServiceDialog.this.psCurrent.getDefaultAttributeValue(var1);
            if (var9 == null) {
               var9 = Sides.ONE_SIDED;
            }
         }

         if (var9 == Sides.ONE_SIDED) {
            this.rbOneSide.setSelected(true);
         } else if (var9 == Sides.TUMBLE) {
            this.rbTumble.setSelected(true);
         } else {
            this.rbDuplex.setSelected(true);
         }

      }
   }

   private class QualityPanel extends JPanel implements ActionListener {
      private final String strTitle = ServiceDialog.getMsg("border.quality");
      private JRadioButton rbDraft;
      private JRadioButton rbNormal;
      private JRadioButton rbHigh;

      public QualityPanel() {
         GridBagLayout var2 = new GridBagLayout();
         GridBagConstraints var3 = new GridBagConstraints();
         this.setLayout(var2);
         this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
         var3.fill = 1;
         var3.gridwidth = 0;
         var3.weighty = 1.0D;
         ButtonGroup var4 = new ButtonGroup();
         this.rbDraft = ServiceDialog.createRadioButton("radiobutton.draftq", this);
         var4.add(this.rbDraft);
         ServiceDialog.addToGB(this.rbDraft, this, var2, var3);
         this.rbNormal = ServiceDialog.createRadioButton("radiobutton.normalq", this);
         this.rbNormal.setSelected(true);
         var4.add(this.rbNormal);
         ServiceDialog.addToGB(this.rbNormal, this, var2, var3);
         this.rbHigh = ServiceDialog.createRadioButton("radiobutton.highq", this);
         var4.add(this.rbHigh);
         ServiceDialog.addToGB(this.rbHigh, this, var2, var3);
      }

      public void actionPerformed(ActionEvent var1) {
         Object var2 = var1.getSource();
         if (var2 == this.rbDraft) {
            ServiceDialog.this.asCurrent.add(PrintQuality.DRAFT);
         } else if (var2 == this.rbNormal) {
            ServiceDialog.this.asCurrent.add(PrintQuality.NORMAL);
         } else if (var2 == this.rbHigh) {
            ServiceDialog.this.asCurrent.add(PrintQuality.HIGH);
         }

      }

      public void updateInfo() {
         Class var1 = PrintQuality.class;
         boolean var2 = false;
         boolean var3 = false;
         boolean var4 = false;
         if (ServiceDialog.this.isAWT) {
            var2 = true;
            var3 = true;
            var4 = true;
         } else if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(var1)) {
            Object var5 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(var1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
            if (var5 instanceof PrintQuality[]) {
               PrintQuality[] var6 = (PrintQuality[])((PrintQuality[])var5);

               for(int var7 = 0; var7 < var6.length; ++var7) {
                  PrintQuality var8 = var6[var7];
                  if (var8 == PrintQuality.DRAFT) {
                     var2 = true;
                  } else if (var8 == PrintQuality.NORMAL) {
                     var3 = true;
                  } else if (var8 == PrintQuality.HIGH) {
                     var4 = true;
                  }
               }
            }
         }

         this.rbDraft.setEnabled(var2);
         this.rbNormal.setEnabled(var3);
         this.rbHigh.setEnabled(var4);
         PrintQuality var9 = (PrintQuality)ServiceDialog.this.asCurrent.get(var1);
         if (var9 == null) {
            var9 = (PrintQuality)ServiceDialog.this.psCurrent.getDefaultAttributeValue(var1);
            if (var9 == null) {
               var9 = PrintQuality.NORMAL;
            }
         }

         if (var9 == PrintQuality.DRAFT) {
            this.rbDraft.setSelected(true);
         } else if (var9 == PrintQuality.NORMAL) {
            this.rbNormal.setSelected(true);
         } else {
            this.rbHigh.setSelected(true);
         }

      }
   }

   private class ChromaticityPanel extends JPanel implements ActionListener {
      private final String strTitle = ServiceDialog.getMsg("border.chromaticity");
      private JRadioButton rbMonochrome;
      private JRadioButton rbColor;

      public ChromaticityPanel() {
         GridBagLayout var2 = new GridBagLayout();
         GridBagConstraints var3 = new GridBagConstraints();
         this.setLayout(var2);
         this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
         var3.fill = 1;
         var3.gridwidth = 0;
         var3.weighty = 1.0D;
         ButtonGroup var4 = new ButtonGroup();
         this.rbMonochrome = ServiceDialog.createRadioButton("radiobutton.monochrome", this);
         this.rbMonochrome.setSelected(true);
         var4.add(this.rbMonochrome);
         ServiceDialog.addToGB(this.rbMonochrome, this, var2, var3);
         this.rbColor = ServiceDialog.createRadioButton("radiobutton.color", this);
         var4.add(this.rbColor);
         ServiceDialog.addToGB(this.rbColor, this, var2, var3);
      }

      public void actionPerformed(ActionEvent var1) {
         Object var2 = var1.getSource();
         if (var2 == this.rbMonochrome) {
            ServiceDialog.this.asCurrent.add(Chromaticity.MONOCHROME);
         } else if (var2 == this.rbColor) {
            ServiceDialog.this.asCurrent.add(Chromaticity.COLOR);
         }

      }

      public void updateInfo() {
         Class var1 = Chromaticity.class;
         boolean var2 = false;
         boolean var3 = false;
         if (ServiceDialog.this.isAWT) {
            var2 = true;
            var3 = true;
         } else if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(var1)) {
            Object var4 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(var1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
            if (var4 instanceof Chromaticity[]) {
               Chromaticity[] var5 = (Chromaticity[])((Chromaticity[])var4);

               for(int var6 = 0; var6 < var5.length; ++var6) {
                  Chromaticity var7 = var5[var6];
                  if (var7 == Chromaticity.MONOCHROME) {
                     var2 = true;
                  } else if (var7 == Chromaticity.COLOR) {
                     var3 = true;
                  }
               }
            }
         }

         this.rbMonochrome.setEnabled(var2);
         this.rbColor.setEnabled(var3);
         Chromaticity var8 = (Chromaticity)ServiceDialog.this.asCurrent.get(var1);
         if (var8 == null) {
            var8 = (Chromaticity)ServiceDialog.this.psCurrent.getDefaultAttributeValue(var1);
            if (var8 == null) {
               var8 = Chromaticity.MONOCHROME;
            }
         }

         if (var8 == Chromaticity.MONOCHROME) {
            this.rbMonochrome.setSelected(true);
         } else {
            this.rbColor.setSelected(true);
         }

      }
   }

   private class AppearancePanel extends JPanel {
      private ServiceDialog.ChromaticityPanel pnlChromaticity;
      private ServiceDialog.QualityPanel pnlQuality;
      private ServiceDialog.JobAttributesPanel pnlJobAttributes;
      private ServiceDialog.SidesPanel pnlSides;

      public AppearancePanel() {
         GridBagLayout var2 = new GridBagLayout();
         GridBagConstraints var3 = new GridBagConstraints();
         this.setLayout(var2);
         var3.fill = 1;
         var3.insets = ServiceDialog.panelInsets;
         var3.weightx = 1.0D;
         var3.weighty = 1.0D;
         var3.gridwidth = -1;
         this.pnlChromaticity = ServiceDialog.this.new ChromaticityPanel();
         ServiceDialog.addToGB(this.pnlChromaticity, this, var2, var3);
         var3.gridwidth = 0;
         this.pnlQuality = ServiceDialog.this.new QualityPanel();
         ServiceDialog.addToGB(this.pnlQuality, this, var2, var3);
         var3.gridwidth = 1;
         this.pnlSides = ServiceDialog.this.new SidesPanel();
         ServiceDialog.addToGB(this.pnlSides, this, var2, var3);
         var3.gridwidth = 0;
         this.pnlJobAttributes = ServiceDialog.this.new JobAttributesPanel();
         ServiceDialog.addToGB(this.pnlJobAttributes, this, var2, var3);
      }

      public void updateInfo() {
         this.pnlChromaticity.updateInfo();
         this.pnlQuality.updateInfo();
         this.pnlSides.updateInfo();
         this.pnlJobAttributes.updateInfo();
      }
   }

   private class OrientationPanel extends JPanel implements ActionListener {
      private final String strTitle = ServiceDialog.getMsg("border.orientation");
      private ServiceDialog.IconRadioButton rbPortrait;
      private ServiceDialog.IconRadioButton rbLandscape;
      private ServiceDialog.IconRadioButton rbRevPortrait;
      private ServiceDialog.IconRadioButton rbRevLandscape;
      private ServiceDialog.MarginsPanel pnlMargins = null;

      public OrientationPanel() {
         GridBagLayout var2 = new GridBagLayout();
         GridBagConstraints var3 = new GridBagConstraints();
         this.setLayout(var2);
         this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
         var3.fill = 1;
         var3.insets = ServiceDialog.compInsets;
         var3.weighty = 1.0D;
         var3.gridwidth = 0;
         ButtonGroup var4 = new ButtonGroup();
         this.rbPortrait = ServiceDialog.this.new IconRadioButton("radiobutton.portrait", "orientPortrait.png", true, var4, this);
         this.rbPortrait.addActionListener(this);
         ServiceDialog.addToGB(this.rbPortrait, this, var2, var3);
         this.rbLandscape = ServiceDialog.this.new IconRadioButton("radiobutton.landscape", "orientLandscape.png", false, var4, this);
         this.rbLandscape.addActionListener(this);
         ServiceDialog.addToGB(this.rbLandscape, this, var2, var3);
         this.rbRevPortrait = ServiceDialog.this.new IconRadioButton("radiobutton.revportrait", "orientRevPortrait.png", false, var4, this);
         this.rbRevPortrait.addActionListener(this);
         ServiceDialog.addToGB(this.rbRevPortrait, this, var2, var3);
         this.rbRevLandscape = ServiceDialog.this.new IconRadioButton("radiobutton.revlandscape", "orientRevLandscape.png", false, var4, this);
         this.rbRevLandscape.addActionListener(this);
         ServiceDialog.addToGB(this.rbRevLandscape, this, var2, var3);
      }

      public void actionPerformed(ActionEvent var1) {
         Object var2 = var1.getSource();
         if (this.rbPortrait.isSameAs(var2)) {
            ServiceDialog.this.asCurrent.add(OrientationRequested.PORTRAIT);
         } else if (this.rbLandscape.isSameAs(var2)) {
            ServiceDialog.this.asCurrent.add(OrientationRequested.LANDSCAPE);
         } else if (this.rbRevPortrait.isSameAs(var2)) {
            ServiceDialog.this.asCurrent.add(OrientationRequested.REVERSE_PORTRAIT);
         } else if (this.rbRevLandscape.isSameAs(var2)) {
            ServiceDialog.this.asCurrent.add(OrientationRequested.REVERSE_LANDSCAPE);
         }

         if (this.pnlMargins != null) {
            this.pnlMargins.updateInfo();
         }

      }

      void addOrientationListener(ServiceDialog.MarginsPanel var1) {
         this.pnlMargins = var1;
      }

      public void updateInfo() {
         Class var1 = OrientationRequested.class;
         boolean var2 = false;
         boolean var3 = false;
         boolean var4 = false;
         boolean var5 = false;
         if (ServiceDialog.this.isAWT) {
            var2 = true;
            var3 = true;
         } else if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(var1)) {
            Object var6 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(var1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
            if (var6 instanceof OrientationRequested[]) {
               OrientationRequested[] var7 = (OrientationRequested[])((OrientationRequested[])var6);

               for(int var8 = 0; var8 < var7.length; ++var8) {
                  OrientationRequested var9 = var7[var8];
                  if (var9 == OrientationRequested.PORTRAIT) {
                     var2 = true;
                  } else if (var9 == OrientationRequested.LANDSCAPE) {
                     var3 = true;
                  } else if (var9 == OrientationRequested.REVERSE_PORTRAIT) {
                     var4 = true;
                  } else if (var9 == OrientationRequested.REVERSE_LANDSCAPE) {
                     var5 = true;
                  }
               }
            }
         }

         this.rbPortrait.setEnabled(var2);
         this.rbLandscape.setEnabled(var3);
         this.rbRevPortrait.setEnabled(var4);
         this.rbRevLandscape.setEnabled(var5);
         OrientationRequested var10 = (OrientationRequested)ServiceDialog.this.asCurrent.get(var1);
         if (var10 == null || !ServiceDialog.this.psCurrent.isAttributeValueSupported(var10, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent)) {
            var10 = (OrientationRequested)ServiceDialog.this.psCurrent.getDefaultAttributeValue(var1);
            if (var10 != null && !ServiceDialog.this.psCurrent.isAttributeValueSupported(var10, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent)) {
               var10 = null;
               Object var11 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(var1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
               if (var11 instanceof OrientationRequested[]) {
                  OrientationRequested[] var12 = (OrientationRequested[])((OrientationRequested[])var11);
                  if (var12.length > 1) {
                     var10 = var12[0];
                  }
               }
            }

            if (var10 == null) {
               var10 = OrientationRequested.PORTRAIT;
            }

            ServiceDialog.this.asCurrent.add(var10);
         }

         if (var10 == OrientationRequested.PORTRAIT) {
            this.rbPortrait.setSelected(true);
         } else if (var10 == OrientationRequested.LANDSCAPE) {
            this.rbLandscape.setSelected(true);
         } else if (var10 == OrientationRequested.REVERSE_PORTRAIT) {
            this.rbRevPortrait.setSelected(true);
         } else {
            this.rbRevLandscape.setSelected(true);
         }

      }
   }

   private class MediaPanel extends JPanel implements ItemListener {
      private final String strTitle = ServiceDialog.getMsg("border.media");
      private JLabel lblSize;
      private JLabel lblSource;
      private JComboBox cbSize;
      private JComboBox cbSource;
      private Vector sizes = new Vector();
      private Vector sources = new Vector();
      private ServiceDialog.MarginsPanel pnlMargins = null;

      public MediaPanel() {
         GridBagLayout var2 = new GridBagLayout();
         GridBagConstraints var3 = new GridBagConstraints();
         this.setLayout(var2);
         this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
         this.cbSize = new JComboBox();
         this.cbSource = new JComboBox();
         var3.fill = 1;
         var3.insets = ServiceDialog.compInsets;
         var3.weighty = 1.0D;
         var3.weightx = 0.0D;
         this.lblSize = new JLabel(ServiceDialog.getMsg("label.size"), 11);
         this.lblSize.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.size"));
         this.lblSize.setLabelFor(this.cbSize);
         ServiceDialog.addToGB(this.lblSize, this, var2, var3);
         var3.weightx = 1.0D;
         var3.gridwidth = 0;
         ServiceDialog.addToGB(this.cbSize, this, var2, var3);
         var3.weightx = 0.0D;
         var3.gridwidth = 1;
         this.lblSource = new JLabel(ServiceDialog.getMsg("label.source"), 11);
         this.lblSource.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.source"));
         this.lblSource.setLabelFor(this.cbSource);
         ServiceDialog.addToGB(this.lblSource, this, var2, var3);
         var3.gridwidth = 0;
         ServiceDialog.addToGB(this.cbSource, this, var2, var3);
      }

      private String getMediaName(String var1) {
         try {
            String var2 = var1.replace(' ', '-');
            var2 = var2.replace('#', 'n');
            return ServiceDialog.messageRB.getString(var2);
         } catch (MissingResourceException var3) {
            return var1;
         }
      }

      public void itemStateChanged(ItemEvent var1) {
         Object var2 = var1.getSource();
         if (var1.getStateChange() == 1) {
            int var3;
            int var4;
            if (var2 == this.cbSize) {
               var3 = this.cbSize.getSelectedIndex();
               if (var3 >= 0 && var3 < this.sizes.size()) {
                  if (this.cbSource.getItemCount() > 1 && this.cbSource.getSelectedIndex() >= 1) {
                     var4 = this.cbSource.getSelectedIndex() - 1;
                     MediaTray var9 = (MediaTray)this.sources.get(var4);
                     ServiceDialog.this.asCurrent.add(new SunAlternateMedia(var9));
                  }

                  ServiceDialog.this.asCurrent.add((MediaSizeName)this.sizes.get(var3));
               }
            } else if (var2 == this.cbSource) {
               var3 = this.cbSource.getSelectedIndex();
               if (var3 >= 1 && var3 < this.sources.size() + 1) {
                  ServiceDialog.this.asCurrent.remove(SunAlternateMedia.class);
                  MediaTray var8 = (MediaTray)this.sources.get(var3 - 1);
                  Media var5 = (Media)ServiceDialog.this.asCurrent.get(Media.class);
                  if (var5 != null && !(var5 instanceof MediaTray)) {
                     if (var5 instanceof MediaSizeName) {
                        MediaSizeName var6 = (MediaSizeName)var5;
                        Media var7 = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Media.class);
                        if (var7 instanceof MediaSizeName && var7.equals(var6)) {
                           ServiceDialog.this.asCurrent.add(var8);
                        } else {
                           ServiceDialog.this.asCurrent.add(new SunAlternateMedia(var8));
                        }
                     }
                  } else {
                     ServiceDialog.this.asCurrent.add(var8);
                  }
               } else if (var3 == 0) {
                  ServiceDialog.this.asCurrent.remove(SunAlternateMedia.class);
                  if (this.cbSize.getItemCount() > 0) {
                     var4 = this.cbSize.getSelectedIndex();
                     ServiceDialog.this.asCurrent.add((MediaSizeName)this.sizes.get(var4));
                  }
               }
            }

            if (this.pnlMargins != null) {
               this.pnlMargins.updateInfo();
            }
         }

      }

      public void addMediaListener(ServiceDialog.MarginsPanel var1) {
         this.pnlMargins = var1;
      }

      public void updateInfo() {
         Class var1 = Media.class;
         Class var2 = SunAlternateMedia.class;
         boolean var3 = false;
         this.cbSize.removeItemListener(this);
         this.cbSize.removeAllItems();
         this.cbSource.removeItemListener(this);
         this.cbSource.removeAllItems();
         this.cbSource.addItem(this.getMediaName("auto-select"));
         this.sizes.clear();
         this.sources.clear();
         if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(var1)) {
            var3 = true;
            Object var4 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(var1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
            if (var4 instanceof Media[]) {
               Media[] var5 = (Media[])((Media[])var4);

               for(int var6 = 0; var6 < var5.length; ++var6) {
                  Media var7 = var5[var6];
                  if (var7 instanceof MediaSizeName) {
                     this.sizes.add(var7);
                     this.cbSize.addItem(this.getMediaName(var7.toString()));
                  } else if (var7 instanceof MediaTray) {
                     this.sources.add(var7);
                     this.cbSource.addItem(this.getMediaName(var7.toString()));
                  }
               }
            }
         }

         boolean var10 = var3 && this.sizes.size() > 0;
         this.lblSize.setEnabled(var10);
         this.cbSize.setEnabled(var10);
         if (ServiceDialog.this.isAWT) {
            this.cbSource.setEnabled(false);
            this.lblSource.setEnabled(false);
         } else {
            this.cbSource.setEnabled(var3);
         }

         if (var3) {
            Media var11 = (Media)ServiceDialog.this.asCurrent.get(var1);
            Media var12 = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(var1);
            if (var12 instanceof MediaSizeName) {
               this.cbSize.setSelectedIndex(this.sizes.size() > 0 ? this.sizes.indexOf(var12) : -1);
            }

            if (var11 == null || !ServiceDialog.this.psCurrent.isAttributeValueSupported(var11, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent)) {
               var11 = var12;
               if (var12 == null && this.sizes.size() > 0) {
                  var11 = (Media)this.sizes.get(0);
               }

               if (var11 != null) {
                  ServiceDialog.this.asCurrent.add(var11);
               }
            }

            if (var11 != null) {
               if (var11 instanceof MediaSizeName) {
                  MediaSizeName var13 = (MediaSizeName)var11;
                  this.cbSize.setSelectedIndex(this.sizes.indexOf(var13));
               } else if (var11 instanceof MediaTray) {
                  MediaTray var14 = (MediaTray)var11;
                  this.cbSource.setSelectedIndex(this.sources.indexOf(var14) + 1);
               }
            } else {
               this.cbSize.setSelectedIndex(this.sizes.size() > 0 ? 0 : -1);
               this.cbSource.setSelectedIndex(0);
            }

            SunAlternateMedia var15 = (SunAlternateMedia)ServiceDialog.this.asCurrent.get(var2);
            MediaTray var9;
            if (var15 != null) {
               Media var8 = var15.getMedia();
               if (var8 instanceof MediaTray) {
                  var9 = (MediaTray)var8;
                  this.cbSource.setSelectedIndex(this.sources.indexOf(var9) + 1);
               }
            }

            int var16 = this.cbSize.getSelectedIndex();
            if (var16 >= 0 && var16 < this.sizes.size()) {
               ServiceDialog.this.asCurrent.add((MediaSizeName)this.sizes.get(var16));
            }

            var16 = this.cbSource.getSelectedIndex();
            if (var16 >= 1 && var16 < this.sources.size() + 1) {
               var9 = (MediaTray)this.sources.get(var16 - 1);
               if (var11 instanceof MediaTray) {
                  ServiceDialog.this.asCurrent.add(var9);
               } else {
                  ServiceDialog.this.asCurrent.add(new SunAlternateMedia(var9));
               }
            }
         }

         this.cbSize.addItemListener(this);
         this.cbSource.addItemListener(this);
      }
   }

   private class MarginsPanel extends JPanel implements ActionListener, FocusListener {
      private final String strTitle = ServiceDialog.getMsg("border.margins");
      private JFormattedTextField leftMargin;
      private JFormattedTextField rightMargin;
      private JFormattedTextField topMargin;
      private JFormattedTextField bottomMargin;
      private JLabel lblLeft;
      private JLabel lblRight;
      private JLabel lblTop;
      private JLabel lblBottom;
      private int units = 1000;
      private float lmVal = -1.0F;
      private float rmVal = -1.0F;
      private float tmVal = -1.0F;
      private float bmVal = -1.0F;
      private Float lmObj;
      private Float rmObj;
      private Float tmObj;
      private Float bmObj;

      public MarginsPanel() {
         GridBagLayout var2 = new GridBagLayout();
         GridBagConstraints var3 = new GridBagConstraints();
         var3.fill = 2;
         var3.weightx = 1.0D;
         var3.weighty = 0.0D;
         var3.insets = ServiceDialog.compInsets;
         this.setLayout(var2);
         this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
         String var4 = "label.millimetres";
         String var5 = Locale.getDefault().getCountry();
         if (var5 != null && (var5.equals("") || var5.equals(Locale.US.getCountry()) || var5.equals(Locale.CANADA.getCountry()))) {
            var4 = "label.inches";
            this.units = 25400;
         }

         String var6 = ServiceDialog.getMsg(var4);
         DecimalFormat var7;
         if (this.units == 1000) {
            var7 = new DecimalFormat("###.##");
            var7.setMaximumIntegerDigits(3);
         } else {
            var7 = new DecimalFormat("##.##");
            var7.setMaximumIntegerDigits(2);
         }

         var7.setMinimumFractionDigits(1);
         var7.setMaximumFractionDigits(2);
         var7.setMinimumIntegerDigits(1);
         var7.setParseIntegerOnly(false);
         var7.setDecimalSeparatorAlwaysShown(true);
         NumberFormatter var8 = new NumberFormatter(var7);
         var8.setMinimum(new Float(0.0F));
         var8.setMaximum(new Float(999.0F));
         var8.setAllowsInvalid(true);
         var8.setCommitsOnValidEdit(true);
         this.leftMargin = new JFormattedTextField(var8);
         this.leftMargin.addFocusListener(this);
         this.leftMargin.addActionListener(this);
         this.leftMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.leftmargin"));
         this.rightMargin = new JFormattedTextField(var8);
         this.rightMargin.addFocusListener(this);
         this.rightMargin.addActionListener(this);
         this.rightMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.rightmargin"));
         this.topMargin = new JFormattedTextField(var8);
         this.topMargin.addFocusListener(this);
         this.topMargin.addActionListener(this);
         this.topMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.topmargin"));
         this.topMargin = new JFormattedTextField(var8);
         this.bottomMargin = new JFormattedTextField(var8);
         this.bottomMargin.addFocusListener(this);
         this.bottomMargin.addActionListener(this);
         this.bottomMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.bottommargin"));
         this.topMargin = new JFormattedTextField(var8);
         var3.gridwidth = -1;
         this.lblLeft = new JLabel(ServiceDialog.getMsg("label.leftmargin") + " " + var6, 10);
         this.lblLeft.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.leftmargin"));
         this.lblLeft.setLabelFor(this.leftMargin);
         ServiceDialog.addToGB(this.lblLeft, this, var2, var3);
         var3.gridwidth = 0;
         this.lblRight = new JLabel(ServiceDialog.getMsg("label.rightmargin") + " " + var6, 10);
         this.lblRight.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.rightmargin"));
         this.lblRight.setLabelFor(this.rightMargin);
         ServiceDialog.addToGB(this.lblRight, this, var2, var3);
         var3.gridwidth = -1;
         ServiceDialog.addToGB(this.leftMargin, this, var2, var3);
         var3.gridwidth = 0;
         ServiceDialog.addToGB(this.rightMargin, this, var2, var3);
         ServiceDialog.addToGB(new JPanel(), this, var2, var3);
         var3.gridwidth = -1;
         this.lblTop = new JLabel(ServiceDialog.getMsg("label.topmargin") + " " + var6, 10);
         this.lblTop.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.topmargin"));
         this.lblTop.setLabelFor(this.topMargin);
         ServiceDialog.addToGB(this.lblTop, this, var2, var3);
         var3.gridwidth = 0;
         this.lblBottom = new JLabel(ServiceDialog.getMsg("label.bottommargin") + " " + var6, 10);
         this.lblBottom.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.bottommargin"));
         this.lblBottom.setLabelFor(this.bottomMargin);
         ServiceDialog.addToGB(this.lblBottom, this, var2, var3);
         var3.gridwidth = -1;
         ServiceDialog.addToGB(this.topMargin, this, var2, var3);
         var3.gridwidth = 0;
         ServiceDialog.addToGB(this.bottomMargin, this, var2, var3);
      }

      public void actionPerformed(ActionEvent var1) {
         Object var2 = var1.getSource();
         this.updateMargins(var2);
      }

      public void focusLost(FocusEvent var1) {
         Object var2 = var1.getSource();
         this.updateMargins(var2);
      }

      public void focusGained(FocusEvent var1) {
      }

      public void updateMargins(Object var1) {
         if (var1 instanceof JFormattedTextField) {
            JFormattedTextField var2 = (JFormattedTextField)var1;
            Float var3 = (Float)var2.getValue();
            if (var3 != null) {
               if (var2 != this.leftMargin || !var3.equals(this.lmObj)) {
                  if (var2 != this.rightMargin || !var3.equals(this.rmObj)) {
                     if (var2 != this.topMargin || !var3.equals(this.tmObj)) {
                        if (var2 != this.bottomMargin || !var3.equals(this.bmObj)) {
                           Float var14 = (Float)this.leftMargin.getValue();
                           var3 = (Float)this.rightMargin.getValue();
                           Float var4 = (Float)this.topMargin.getValue();
                           Float var5 = (Float)this.bottomMargin.getValue();
                           float var6 = var14;
                           float var7 = var3;
                           float var8 = var4;
                           float var9 = var5;
                           Class var10 = OrientationRequested.class;
                           OrientationRequested var11 = (OrientationRequested)ServiceDialog.this.asCurrent.get(var10);
                           if (var11 == null) {
                              var11 = (OrientationRequested)ServiceDialog.this.psCurrent.getDefaultAttributeValue(var10);
                           }

                           float var12;
                           if (var11 == OrientationRequested.REVERSE_PORTRAIT) {
                              var12 = var6;
                              var6 = var7;
                              var7 = var12;
                              var12 = var8;
                              var8 = var9;
                              var9 = var12;
                           } else if (var11 == OrientationRequested.LANDSCAPE) {
                              var12 = var6;
                              var6 = var8;
                              var8 = var7;
                              var7 = var9;
                              var9 = var12;
                           } else if (var11 == OrientationRequested.REVERSE_LANDSCAPE) {
                              var12 = var6;
                              var6 = var9;
                              var9 = var7;
                              var7 = var8;
                              var8 = var12;
                           }

                           MediaPrintableArea var13;
                           if ((var13 = this.validateMargins(var6, var7, var8, var9)) != null) {
                              ServiceDialog.this.asCurrent.add(var13);
                              this.lmVal = var6;
                              this.rmVal = var7;
                              this.tmVal = var8;
                              this.bmVal = var9;
                              this.lmObj = var14;
                              this.rmObj = var3;
                              this.tmObj = var4;
                              this.bmObj = var5;
                           } else {
                              if (this.lmObj == null || this.rmObj == null || this.tmObj == null || this.rmObj == null) {
                                 return;
                              }

                              this.leftMargin.setValue(this.lmObj);
                              this.rightMargin.setValue(this.rmObj);
                              this.topMargin.setValue(this.tmObj);
                              this.bottomMargin.setValue(this.bmObj);
                           }

                        }
                     }
                  }
               }
            }
         }
      }

      private MediaPrintableArea validateMargins(float var1, float var2, float var3, float var4) {
         Class var5 = MediaPrintableArea.class;
         MediaPrintableArea var7 = null;
         MediaSize var8 = null;
         Media var9 = (Media)ServiceDialog.this.asCurrent.get(Media.class);
         if (var9 == null || !(var9 instanceof MediaSizeName)) {
            var9 = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Media.class);
         }

         if (var9 != null && var9 instanceof MediaSizeName) {
            MediaSizeName var10 = (MediaSizeName)var9;
            var8 = MediaSize.getMediaSizeForName(var10);
         }

         if (var8 == null) {
            var8 = new MediaSize(8.5F, 11.0F, 25400);
         }

         if (var9 != null) {
            HashPrintRequestAttributeSet var16 = new HashPrintRequestAttributeSet(ServiceDialog.this.asCurrent);
            var16.add(var9);
            Object var11 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(var5, ServiceDialog.this.docFlavor, var16);
            if (var11 instanceof MediaPrintableArea[] && ((MediaPrintableArea[])((MediaPrintableArea[])var11)).length > 0) {
               var7 = ((MediaPrintableArea[])((MediaPrintableArea[])var11))[0];
            }
         }

         if (var7 == null) {
            var7 = new MediaPrintableArea(0.0F, 0.0F, var8.getX(this.units), var8.getY(this.units), this.units);
         }

         float var17 = var8.getX(this.units);
         float var18 = var8.getY(this.units);
         float var14 = var17 - var1 - var2;
         float var15 = var18 - var3 - var4;
         return var14 > 0.0F && var15 > 0.0F && var1 >= 0.0F && var3 >= 0.0F && var1 >= var7.getX(this.units) && var14 <= var7.getWidth(this.units) && var3 >= var7.getY(this.units) && var15 <= var7.getHeight(this.units) ? new MediaPrintableArea(var1, var3, var14, var15, this.units) : null;
      }

      public void updateInfo() {
         if (ServiceDialog.this.isAWT) {
            this.leftMargin.setEnabled(false);
            this.rightMargin.setEnabled(false);
            this.topMargin.setEnabled(false);
            this.bottomMargin.setEnabled(false);
            this.lblLeft.setEnabled(false);
            this.lblRight.setEnabled(false);
            this.lblTop.setEnabled(false);
            this.lblBottom.setEnabled(false);
         } else {
            Class var1 = MediaPrintableArea.class;
            MediaPrintableArea var2 = (MediaPrintableArea)ServiceDialog.this.asCurrent.get(var1);
            MediaPrintableArea var3 = null;
            MediaSize var4 = null;
            Media var5 = (Media)ServiceDialog.this.asCurrent.get(Media.class);
            if (var5 == null || !(var5 instanceof MediaSizeName)) {
               var5 = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Media.class);
            }

            if (var5 != null && var5 instanceof MediaSizeName) {
               MediaSizeName var6 = (MediaSizeName)var5;
               var4 = MediaSize.getMediaSizeForName(var6);
            }

            if (var4 == null) {
               var4 = new MediaSize(8.5F, 11.0F, 25400);
            }

            if (var5 != null) {
               HashPrintRequestAttributeSet var23 = new HashPrintRequestAttributeSet(ServiceDialog.this.asCurrent);
               var23.add(var5);
               Object var7 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(var1, ServiceDialog.this.docFlavor, var23);
               if (var7 instanceof MediaPrintableArea[] && ((MediaPrintableArea[])((MediaPrintableArea[])var7)).length > 0) {
                  var3 = ((MediaPrintableArea[])((MediaPrintableArea[])var7))[0];
               } else if (var7 instanceof MediaPrintableArea) {
                  var3 = (MediaPrintableArea)var7;
               }
            }

            if (var3 == null) {
               var3 = new MediaPrintableArea(0.0F, 0.0F, var4.getX(this.units), var4.getY(this.units), this.units);
            }

            float var24 = var4.getX(25400);
            float var25 = var4.getY(25400);
            float var8 = 5.0F;
            float var9;
            if (var24 > var8) {
               var9 = 1.0F;
            } else {
               var9 = var24 / var8;
            }

            float var10;
            if (var25 > var8) {
               var10 = 1.0F;
            } else {
               var10 = var25 / var8;
            }

            if (var2 == null) {
               var2 = new MediaPrintableArea(var9, var10, var24 - 2.0F * var9, var25 - 2.0F * var10, 25400);
               ServiceDialog.this.asCurrent.add(var2);
            }

            float var11 = var2.getX(this.units);
            float var12 = var2.getY(this.units);
            float var13 = var2.getWidth(this.units);
            float var14 = var2.getHeight(this.units);
            float var15 = var3.getX(this.units);
            float var16 = var3.getY(this.units);
            float var17 = var3.getWidth(this.units);
            float var18 = var3.getHeight(this.units);
            boolean var19 = false;
            var24 = var4.getX(this.units);
            var25 = var4.getY(this.units);
            if (this.lmVal >= 0.0F) {
               var19 = true;
               if (this.lmVal + this.rmVal > var24) {
                  if (var13 > var17) {
                     var13 = var17;
                  }

                  var11 = (var24 - var13) / 2.0F;
               } else {
                  var11 = this.lmVal >= var15 ? this.lmVal : var15;
                  var13 = var24 - var11 - this.rmVal;
               }

               if (this.tmVal + this.bmVal > var25) {
                  if (var14 > var18) {
                     var14 = var18;
                  }

                  var12 = (var25 - var14) / 2.0F;
               } else {
                  var12 = this.tmVal >= var16 ? this.tmVal : var16;
                  var14 = var25 - var12 - this.bmVal;
               }
            }

            if (var11 < var15) {
               var19 = true;
               var11 = var15;
            }

            if (var12 < var16) {
               var19 = true;
               var12 = var16;
            }

            if (var13 > var17) {
               var19 = true;
               var13 = var17;
            }

            if (var14 > var18) {
               var19 = true;
               var14 = var18;
            }

            if (var11 + var13 > var15 + var17 || var13 <= 0.0F) {
               var19 = true;
               var11 = var15;
               var13 = var17;
            }

            if (var12 + var14 > var16 + var18 || var14 <= 0.0F) {
               var19 = true;
               var12 = var16;
               var14 = var18;
            }

            if (var19) {
               var2 = new MediaPrintableArea(var11, var12, var13, var14, this.units);
               ServiceDialog.this.asCurrent.add(var2);
            }

            this.lmVal = var11;
            this.tmVal = var12;
            this.rmVal = var4.getX(this.units) - var11 - var13;
            this.bmVal = var4.getY(this.units) - var12 - var14;
            this.lmObj = new Float(this.lmVal);
            this.rmObj = new Float(this.rmVal);
            this.tmObj = new Float(this.tmVal);
            this.bmObj = new Float(this.bmVal);
            Class var20 = OrientationRequested.class;
            OrientationRequested var21 = (OrientationRequested)ServiceDialog.this.asCurrent.get(var20);
            if (var21 == null) {
               var21 = (OrientationRequested)ServiceDialog.this.psCurrent.getDefaultAttributeValue(var20);
            }

            Float var22;
            if (var21 == OrientationRequested.REVERSE_PORTRAIT) {
               var22 = this.lmObj;
               this.lmObj = this.rmObj;
               this.rmObj = var22;
               var22 = this.tmObj;
               this.tmObj = this.bmObj;
               this.bmObj = var22;
            } else if (var21 == OrientationRequested.LANDSCAPE) {
               var22 = this.lmObj;
               this.lmObj = this.bmObj;
               this.bmObj = this.rmObj;
               this.rmObj = this.tmObj;
               this.tmObj = var22;
            } else if (var21 == OrientationRequested.REVERSE_LANDSCAPE) {
               var22 = this.lmObj;
               this.lmObj = this.tmObj;
               this.tmObj = this.rmObj;
               this.rmObj = this.bmObj;
               this.bmObj = var22;
            }

            this.leftMargin.setValue(this.lmObj);
            this.rightMargin.setValue(this.rmObj);
            this.topMargin.setValue(this.tmObj);
            this.bottomMargin.setValue(this.bmObj);
         }
      }
   }

   private class PageSetupPanel extends JPanel {
      private ServiceDialog.MediaPanel pnlMedia;
      private ServiceDialog.OrientationPanel pnlOrientation;
      private ServiceDialog.MarginsPanel pnlMargins;

      public PageSetupPanel() {
         GridBagLayout var2 = new GridBagLayout();
         GridBagConstraints var3 = new GridBagConstraints();
         this.setLayout(var2);
         var3.fill = 1;
         var3.insets = ServiceDialog.panelInsets;
         var3.weightx = 1.0D;
         var3.weighty = 1.0D;
         var3.gridwidth = 0;
         this.pnlMedia = ServiceDialog.this.new MediaPanel();
         ServiceDialog.addToGB(this.pnlMedia, this, var2, var3);
         this.pnlOrientation = ServiceDialog.this.new OrientationPanel();
         var3.gridwidth = -1;
         ServiceDialog.addToGB(this.pnlOrientation, this, var2, var3);
         this.pnlMargins = ServiceDialog.this.new MarginsPanel();
         this.pnlOrientation.addOrientationListener(this.pnlMargins);
         this.pnlMedia.addMediaListener(this.pnlMargins);
         var3.gridwidth = 0;
         ServiceDialog.addToGB(this.pnlMargins, this, var2, var3);
      }

      public void updateInfo() {
         this.pnlMedia.updateInfo();
         this.pnlOrientation.updateInfo();
         this.pnlMargins.updateInfo();
      }
   }

   private class CopiesPanel extends JPanel implements ActionListener, ChangeListener {
      private final String strTitle = ServiceDialog.getMsg("border.copies");
      private SpinnerNumberModel snModel;
      private JSpinner spinCopies;
      private JLabel lblCopies;
      private JCheckBox cbCollate;
      private boolean scSupported;

      public CopiesPanel() {
         GridBagLayout var2 = new GridBagLayout();
         GridBagConstraints var3 = new GridBagConstraints();
         this.setLayout(var2);
         this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
         var3.fill = 2;
         var3.insets = ServiceDialog.compInsets;
         this.lblCopies = new JLabel(ServiceDialog.getMsg("label.numcopies"), 11);
         this.lblCopies.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.numcopies"));
         this.lblCopies.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.numcopies"));
         ServiceDialog.addToGB(this.lblCopies, this, var2, var3);
         this.snModel = new SpinnerNumberModel(1, 1, 999, 1);
         this.spinCopies = new JSpinner(this.snModel);
         this.lblCopies.setLabelFor(this.spinCopies);
         ((JSpinner.NumberEditor)this.spinCopies.getEditor()).getTextField().setColumns(3);
         this.spinCopies.addChangeListener(this);
         var3.gridwidth = 0;
         ServiceDialog.addToGB(this.spinCopies, this, var2, var3);
         this.cbCollate = ServiceDialog.createCheckBox("checkbox.collate", this);
         this.cbCollate.setEnabled(false);
         ServiceDialog.addToGB(this.cbCollate, this, var2, var3);
      }

      public void actionPerformed(ActionEvent var1) {
         if (this.cbCollate.isSelected()) {
            ServiceDialog.this.asCurrent.add(SheetCollate.COLLATED);
         } else {
            ServiceDialog.this.asCurrent.add(SheetCollate.UNCOLLATED);
         }

      }

      public void stateChanged(ChangeEvent var1) {
         this.updateCollateCB();
         ServiceDialog.this.asCurrent.add(new Copies(this.snModel.getNumber().intValue()));
      }

      private void updateCollateCB() {
         int var1 = this.snModel.getNumber().intValue();
         if (ServiceDialog.this.isAWT) {
            this.cbCollate.setEnabled(true);
         } else {
            this.cbCollate.setEnabled(var1 > 1 && this.scSupported);
         }

      }

      public void updateInfo() {
         Class var1 = Copies.class;
         Class var2 = CopiesSupported.class;
         Class var3 = SheetCollate.class;
         boolean var4 = false;
         this.scSupported = false;
         if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(var1)) {
            var4 = true;
         }

         CopiesSupported var5 = (CopiesSupported)ServiceDialog.this.psCurrent.getSupportedAttributeValues(var1, (DocFlavor)null, (AttributeSet)null);
         if (var5 == null) {
            var5 = new CopiesSupported(1, 999);
         }

         Copies var6 = (Copies)ServiceDialog.this.asCurrent.get(var1);
         if (var6 == null) {
            var6 = (Copies)ServiceDialog.this.psCurrent.getDefaultAttributeValue(var1);
            if (var6 == null) {
               var6 = new Copies(1);
            }
         }

         this.spinCopies.setEnabled(var4);
         this.lblCopies.setEnabled(var4);
         int[][] var7 = var5.getMembers();
         int var8;
         int var9;
         if (var7.length > 0 && var7[0].length > 0) {
            var8 = var7[0][0];
            var9 = var7[0][1];
         } else {
            var8 = 1;
            var9 = Integer.MAX_VALUE;
         }

         this.snModel.setMinimum(new Integer(var8));
         this.snModel.setMaximum(new Integer(var9));
         int var10 = var6.getValue();
         if (var10 < var8 || var10 > var9) {
            var10 = var8;
         }

         this.snModel.setValue(new Integer(var10));
         if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(var3)) {
            this.scSupported = true;
         }

         SheetCollate var11 = (SheetCollate)ServiceDialog.this.asCurrent.get(var3);
         if (var11 == null) {
            var11 = (SheetCollate)ServiceDialog.this.psCurrent.getDefaultAttributeValue(var3);
            if (var11 == null) {
               var11 = SheetCollate.UNCOLLATED;
            }
         }

         this.cbCollate.setSelected(var11 == SheetCollate.COLLATED);
         this.updateCollateCB();
      }
   }

   private class PrintRangePanel extends JPanel implements ActionListener, FocusListener {
      private final String strTitle = ServiceDialog.getMsg("border.printrange");
      private final PageRanges prAll = new PageRanges(1, Integer.MAX_VALUE);
      private JRadioButton rbAll;
      private JRadioButton rbPages;
      private JRadioButton rbSelect;
      private JFormattedTextField tfRangeFrom;
      private JFormattedTextField tfRangeTo;
      private JLabel lblRangeTo;
      private boolean prSupported;

      public PrintRangePanel() {
         GridBagLayout var2 = new GridBagLayout();
         GridBagConstraints var3 = new GridBagConstraints();
         this.setLayout(var2);
         this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
         var3.fill = 1;
         var3.insets = ServiceDialog.compInsets;
         var3.gridwidth = 0;
         ButtonGroup var4 = new ButtonGroup();
         JPanel var5 = new JPanel(new FlowLayout(3));
         this.rbAll = ServiceDialog.createRadioButton("radiobutton.rangeall", this);
         this.rbAll.setSelected(true);
         var4.add(this.rbAll);
         var5.add(this.rbAll);
         ServiceDialog.addToGB(var5, this, var2, var3);
         JPanel var6 = new JPanel(new FlowLayout(3));
         this.rbPages = ServiceDialog.createRadioButton("radiobutton.rangepages", this);
         var4.add(this.rbPages);
         var6.add(this.rbPages);
         DecimalFormat var7 = new DecimalFormat("####0");
         var7.setMinimumFractionDigits(0);
         var7.setMaximumFractionDigits(0);
         var7.setMinimumIntegerDigits(0);
         var7.setMaximumIntegerDigits(5);
         var7.setParseIntegerOnly(true);
         var7.setDecimalSeparatorAlwaysShown(false);
         NumberFormatter var8 = new NumberFormatter(var7);
         var8.setMinimum(new Integer(1));
         var8.setMaximum(new Integer(Integer.MAX_VALUE));
         var8.setAllowsInvalid(true);
         var8.setCommitsOnValidEdit(true);
         this.tfRangeFrom = new JFormattedTextField(var8);
         this.tfRangeFrom.setColumns(4);
         this.tfRangeFrom.setEnabled(false);
         this.tfRangeFrom.addActionListener(this);
         this.tfRangeFrom.addFocusListener(this);
         this.tfRangeFrom.setFocusLostBehavior(3);
         this.tfRangeFrom.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("radiobutton.rangepages"));
         var6.add(this.tfRangeFrom);
         this.lblRangeTo = new JLabel(ServiceDialog.getMsg("label.rangeto"));
         this.lblRangeTo.setEnabled(false);
         var6.add(this.lblRangeTo);

         NumberFormatter var9;
         try {
            var9 = (NumberFormatter)var8.clone();
         } catch (CloneNotSupportedException var11) {
            var9 = new NumberFormatter();
         }

         this.tfRangeTo = new JFormattedTextField(var9);
         this.tfRangeTo.setColumns(4);
         this.tfRangeTo.setEnabled(false);
         this.tfRangeTo.addFocusListener(this);
         this.tfRangeTo.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.rangeto"));
         var6.add(this.tfRangeTo);
         ServiceDialog.addToGB(var6, this, var2, var3);
      }

      public void actionPerformed(ActionEvent var1) {
         Object var2 = var1.getSource();
         SunPageSelection var3 = SunPageSelection.ALL;
         this.setupRangeWidgets();
         if (var2 == this.rbAll) {
            ServiceDialog.this.asCurrent.add(this.prAll);
         } else if (var2 == this.rbSelect) {
            var3 = SunPageSelection.SELECTION;
         } else if (var2 == this.rbPages || var2 == this.tfRangeFrom || var2 == this.tfRangeTo) {
            this.updateRangeAttribute();
            var3 = SunPageSelection.RANGE;
         }

         if (ServiceDialog.this.isAWT) {
            ServiceDialog.this.asCurrent.add(var3);
         }

      }

      public void focusLost(FocusEvent var1) {
         Object var2 = var1.getSource();
         if (var2 == this.tfRangeFrom || var2 == this.tfRangeTo) {
            this.updateRangeAttribute();
         }

      }

      public void focusGained(FocusEvent var1) {
      }

      private void setupRangeWidgets() {
         boolean var1 = this.rbPages.isSelected() && this.prSupported;
         this.tfRangeFrom.setEnabled(var1);
         this.tfRangeTo.setEnabled(var1);
         this.lblRangeTo.setEnabled(var1);
      }

      private void updateRangeAttribute() {
         String var1 = this.tfRangeFrom.getText();
         String var2 = this.tfRangeTo.getText();

         int var3;
         try {
            var3 = Integer.parseInt(var1);
         } catch (NumberFormatException var7) {
            var3 = 1;
         }

         int var4;
         try {
            var4 = Integer.parseInt(var2);
         } catch (NumberFormatException var6) {
            var4 = var3;
         }

         if (var3 < 1) {
            var3 = 1;
            this.tfRangeFrom.setValue(new Integer(1));
         }

         if (var4 < var3) {
            var4 = var3;
            this.tfRangeTo.setValue(new Integer(var3));
         }

         PageRanges var5 = new PageRanges(var3, var4);
         ServiceDialog.this.asCurrent.add(var5);
      }

      public void updateInfo() {
         Class var1 = PageRanges.class;
         this.prSupported = false;
         if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(var1) || ServiceDialog.this.isAWT) {
            this.prSupported = true;
         }

         SunPageSelection var2 = SunPageSelection.ALL;
         int var3 = 1;
         int var4 = 1;
         PageRanges var5 = (PageRanges)ServiceDialog.this.asCurrent.get(var1);
         if (var5 != null && !var5.equals(this.prAll)) {
            var2 = SunPageSelection.RANGE;
            int[][] var6 = var5.getMembers();
            if (var6.length > 0 && var6[0].length > 1) {
               var3 = var6[0][0];
               var4 = var6[0][1];
            }
         }

         if (ServiceDialog.this.isAWT) {
            var2 = (SunPageSelection)ServiceDialog.this.asCurrent.get(SunPageSelection.class);
         }

         if (var2 == SunPageSelection.ALL) {
            this.rbAll.setSelected(true);
         } else if (var2 != SunPageSelection.SELECTION) {
            this.rbPages.setSelected(true);
         }

         this.tfRangeFrom.setValue(new Integer(var3));
         this.tfRangeTo.setValue(new Integer(var4));
         this.rbAll.setEnabled(this.prSupported);
         this.rbPages.setEnabled(this.prSupported);
         this.setupRangeWidgets();
      }
   }

   private class PrintServicePanel extends JPanel implements ActionListener, ItemListener, PopupMenuListener {
      private final String strTitle = ServiceDialog.getMsg("border.printservice");
      private FilePermission printToFilePermission;
      private JButton btnProperties;
      private JCheckBox cbPrintToFile;
      private JComboBox cbName;
      private JLabel lblType;
      private JLabel lblStatus;
      private JLabel lblInfo;
      private ServiceUIFactory uiFactory;
      private boolean changedService = false;
      private boolean filePermission;

      public PrintServicePanel() {
         this.uiFactory = ServiceDialog.this.psCurrent.getServiceUIFactory();
         GridBagLayout var2 = new GridBagLayout();
         GridBagConstraints var3 = new GridBagConstraints();
         this.setLayout(var2);
         this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
         String[] var4 = new String[ServiceDialog.this.services.length];

         for(int var5 = 0; var5 < var4.length; ++var5) {
            var4[var5] = ServiceDialog.this.services[var5].getName();
         }

         this.cbName = new JComboBox(var4);
         this.cbName.setSelectedIndex(ServiceDialog.this.defaultServiceIndex);
         this.cbName.addItemListener(this);
         this.cbName.addPopupMenuListener(this);
         var3.fill = 1;
         var3.insets = ServiceDialog.compInsets;
         var3.weightx = 0.0D;
         JLabel var6 = new JLabel(ServiceDialog.getMsg("label.psname"), 11);
         var6.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.psname"));
         var6.setLabelFor(this.cbName);
         ServiceDialog.addToGB(var6, this, var2, var3);
         var3.weightx = 1.0D;
         var3.gridwidth = -1;
         ServiceDialog.addToGB(this.cbName, this, var2, var3);
         var3.weightx = 0.0D;
         var3.gridwidth = 0;
         this.btnProperties = ServiceDialog.createButton("button.properties", this);
         ServiceDialog.addToGB(this.btnProperties, this, var2, var3);
         var3.weighty = 1.0D;
         this.lblStatus = this.addLabel(ServiceDialog.getMsg("label.status"), var2, var3);
         this.lblStatus.setLabelFor((Component)null);
         this.lblType = this.addLabel(ServiceDialog.getMsg("label.pstype"), var2, var3);
         this.lblType.setLabelFor((Component)null);
         var3.gridwidth = 1;
         ServiceDialog.addToGB(new JLabel(ServiceDialog.getMsg("label.info"), 11), this, var2, var3);
         var3.gridwidth = -1;
         this.lblInfo = new JLabel();
         this.lblInfo.setLabelFor((Component)null);
         ServiceDialog.addToGB(this.lblInfo, this, var2, var3);
         var3.gridwidth = 0;
         this.cbPrintToFile = ServiceDialog.createCheckBox("checkbox.printtofile", this);
         ServiceDialog.addToGB(this.cbPrintToFile, this, var2, var3);
         this.filePermission = this.allowedToPrintToFile();
      }

      public boolean isPrintToFileSelected() {
         return this.cbPrintToFile.isSelected();
      }

      private JLabel addLabel(String var1, GridBagLayout var2, GridBagConstraints var3) {
         var3.gridwidth = 1;
         ServiceDialog.addToGB(new JLabel(var1, 11), this, var2, var3);
         var3.gridwidth = 0;
         JLabel var4 = new JLabel();
         ServiceDialog.addToGB(var4, this, var2, var3);
         return var4;
      }

      public void actionPerformed(ActionEvent var1) {
         Object var2 = var1.getSource();
         if (var2 == this.btnProperties && this.uiFactory != null) {
            JDialog var3 = (JDialog)this.uiFactory.getUI(3, "javax.swing.JDialog");
            if (var3 != null) {
               var3.show();
            } else {
               DocumentPropertiesUI var4 = null;

               try {
                  var4 = (DocumentPropertiesUI)this.uiFactory.getUI(199, DocumentPropertiesUI.DOCPROPERTIESCLASSNAME);
               } catch (Exception var8) {
               }

               if (var4 != null) {
                  PrinterJobWrapper var5 = (PrinterJobWrapper)ServiceDialog.this.asCurrent.get(PrinterJobWrapper.class);
                  if (var5 == null) {
                     return;
                  }

                  PrinterJob var6 = var5.getPrinterJob();
                  if (var6 == null) {
                     return;
                  }

                  PrintRequestAttributeSet var7 = var4.showDocumentProperties(var6, ServiceDialog.this, ServiceDialog.this.psCurrent, ServiceDialog.this.asCurrent);
                  if (var7 != null) {
                     ServiceDialog.this.asCurrent.addAll(var7);
                     ServiceDialog.this.updatePanels();
                  }
               }
            }
         }

      }

      public void itemStateChanged(ItemEvent var1) {
         if (var1.getStateChange() == 1) {
            int var2 = this.cbName.getSelectedIndex();
            if (var2 >= 0 && var2 < ServiceDialog.this.services.length && !ServiceDialog.this.services[var2].equals(ServiceDialog.this.psCurrent)) {
               ServiceDialog.this.psCurrent = ServiceDialog.this.services[var2];
               this.uiFactory = ServiceDialog.this.psCurrent.getServiceUIFactory();
               this.changedService = true;
               Destination var3 = (Destination)ServiceDialog.this.asOriginal.get(Destination.class);
               if ((var3 != null || this.isPrintToFileSelected()) && ServiceDialog.this.psCurrent.isAttributeCategorySupported(Destination.class)) {
                  if (var3 != null) {
                     ServiceDialog.this.asCurrent.add(var3);
                  } else {
                     var3 = (Destination)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Destination.class);
                     if (var3 == null) {
                        try {
                           var3 = new Destination(new URI("file:out.prn"));
                        } catch (URISyntaxException var5) {
                        }
                     }

                     if (var3 != null) {
                        ServiceDialog.this.asCurrent.add(var3);
                     }
                  }
               } else {
                  ServiceDialog.this.asCurrent.remove(Destination.class);
               }
            }
         }

      }

      public void popupMenuWillBecomeVisible(PopupMenuEvent var1) {
         this.changedService = false;
      }

      public void popupMenuWillBecomeInvisible(PopupMenuEvent var1) {
         if (this.changedService) {
            this.changedService = false;
            ServiceDialog.this.updatePanels();
         }

      }

      public void popupMenuCanceled(PopupMenuEvent var1) {
      }

      private boolean allowedToPrintToFile() {
         try {
            this.throwPrintToFile();
            return true;
         } catch (SecurityException var2) {
            return false;
         }
      }

      private void throwPrintToFile() {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            if (this.printToFilePermission == null) {
               this.printToFilePermission = new FilePermission("<<ALL FILES>>", "read,write");
            }

            var1.checkPermission(this.printToFilePermission);
         }

      }

      public void updateInfo() {
         Class var1 = Destination.class;
         boolean var2 = false;
         boolean var3 = false;
         boolean var4 = this.filePermission ? this.allowedToPrintToFile() : false;
         if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(var1)) {
            var2 = true;
         }

         Destination var5 = (Destination)ServiceDialog.this.asCurrent.get(var1);
         if (var5 != null) {
            var3 = true;
         }

         this.cbPrintToFile.setEnabled(var2 && var4);
         this.cbPrintToFile.setSelected(var3 && var4 && var2);
         PrintServiceAttribute var6 = ServiceDialog.this.psCurrent.getAttribute(PrinterMakeAndModel.class);
         if (var6 != null) {
            this.lblType.setText(var6.toString());
         }

         PrintServiceAttribute var7 = ServiceDialog.this.psCurrent.getAttribute(PrinterIsAcceptingJobs.class);
         if (var7 != null) {
            this.lblStatus.setText(ServiceDialog.getMsg(var7.toString()));
         }

         PrintServiceAttribute var8 = ServiceDialog.this.psCurrent.getAttribute(PrinterInfo.class);
         if (var8 != null) {
            this.lblInfo.setText(var8.toString());
         }

         this.btnProperties.setEnabled(this.uiFactory != null);
      }
   }

   private class GeneralPanel extends JPanel {
      private ServiceDialog.PrintServicePanel pnlPrintService;
      private ServiceDialog.PrintRangePanel pnlPrintRange;
      private ServiceDialog.CopiesPanel pnlCopies;

      public GeneralPanel() {
         GridBagLayout var2 = new GridBagLayout();
         GridBagConstraints var3 = new GridBagConstraints();
         this.setLayout(var2);
         var3.fill = 1;
         var3.insets = ServiceDialog.panelInsets;
         var3.weightx = 1.0D;
         var3.weighty = 1.0D;
         var3.gridwidth = 0;
         this.pnlPrintService = ServiceDialog.this.new PrintServicePanel();
         ServiceDialog.addToGB(this.pnlPrintService, this, var2, var3);
         var3.gridwidth = -1;
         this.pnlPrintRange = ServiceDialog.this.new PrintRangePanel();
         ServiceDialog.addToGB(this.pnlPrintRange, this, var2, var3);
         var3.gridwidth = 0;
         this.pnlCopies = ServiceDialog.this.new CopiesPanel();
         ServiceDialog.addToGB(this.pnlCopies, this, var2, var3);
      }

      public boolean isPrintToFileRequested() {
         return this.pnlPrintService.isPrintToFileSelected();
      }

      public void updateInfo() {
         this.pnlPrintService.updateInfo();
         this.pnlPrintRange.updateInfo();
         this.pnlCopies.updateInfo();
      }
   }
}
