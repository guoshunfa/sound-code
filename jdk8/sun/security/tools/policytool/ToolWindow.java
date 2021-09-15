package sun.security.tools.policytool;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import sun.security.action.GetPropertyAction;

class ToolWindow extends JFrame {
   private static final long serialVersionUID = 5682568601210376777L;
   static final KeyStroke escKey = KeyStroke.getKeyStroke(27, 0);
   public static final Insets TOP_PADDING = new Insets(25, 0, 0, 0);
   public static final Insets BOTTOM_PADDING = new Insets(0, 0, 25, 0);
   public static final Insets LITE_BOTTOM_PADDING = new Insets(0, 0, 10, 0);
   public static final Insets LR_PADDING = new Insets(0, 10, 0, 10);
   public static final Insets TOP_BOTTOM_PADDING = new Insets(15, 0, 15, 0);
   public static final Insets L_TOP_BOTTOM_PADDING = new Insets(5, 10, 15, 0);
   public static final Insets LR_TOP_BOTTOM_PADDING = new Insets(15, 4, 15, 4);
   public static final Insets LR_BOTTOM_PADDING = new Insets(0, 10, 5, 10);
   public static final Insets L_BOTTOM_PADDING = new Insets(0, 10, 5, 0);
   public static final Insets R_BOTTOM_PADDING = new Insets(0, 0, 25, 5);
   public static final Insets R_PADDING = new Insets(0, 0, 0, 5);
   public static final String NEW_POLICY_FILE = "New";
   public static final String OPEN_POLICY_FILE = "Open";
   public static final String SAVE_POLICY_FILE = "Save";
   public static final String SAVE_AS_POLICY_FILE = "Save.As";
   public static final String VIEW_WARNINGS = "View.Warning.Log";
   public static final String QUIT = "Exit";
   public static final String ADD_POLICY_ENTRY = "Add.Policy.Entry";
   public static final String EDIT_POLICY_ENTRY = "Edit.Policy.Entry";
   public static final String REMOVE_POLICY_ENTRY = "Remove.Policy.Entry";
   public static final String EDIT_KEYSTORE = "Edit";
   public static final String ADD_PUBKEY_ALIAS = "Add.Public.Key.Alias";
   public static final String REMOVE_PUBKEY_ALIAS = "Remove.Public.Key.Alias";
   public static final int MW_FILENAME_LABEL = 0;
   public static final int MW_FILENAME_TEXTFIELD = 1;
   public static final int MW_PANEL = 2;
   public static final int MW_ADD_BUTTON = 0;
   public static final int MW_EDIT_BUTTON = 1;
   public static final int MW_REMOVE_BUTTON = 2;
   public static final int MW_POLICY_LIST = 3;
   static final int TEXTFIELD_HEIGHT;
   private PolicyTool tool;
   private int shortCutModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

   ToolWindow(PolicyTool var1) {
      this.tool = var1;
   }

   public Component getComponent(int var1) {
      Component var2 = this.getContentPane().getComponent(var1);
      if (var2 instanceof JScrollPane) {
         var2 = ((JScrollPane)var2).getViewport().getView();
      }

      return var2;
   }

   private void initWindow() {
      this.setDefaultCloseOperation(0);
      JMenuBar var1 = new JMenuBar();
      JMenu var2 = new JMenu();
      configureButton(var2, "File");
      FileMenuListener var3 = new FileMenuListener(this.tool, this);
      this.addMenuItem(var2, "New", var3, "N");
      this.addMenuItem(var2, "Open", var3, "O");
      this.addMenuItem(var2, "Save", var3, "S");
      this.addMenuItem(var2, "Save.As", var3, (String)null);
      this.addMenuItem(var2, "View.Warning.Log", var3, (String)null);
      this.addMenuItem(var2, "Exit", var3, (String)null);
      var1.add(var2);
      var2 = new JMenu();
      configureButton(var2, "KeyStore");
      MainWindowListener var15 = new MainWindowListener(this.tool, this);
      this.addMenuItem(var2, "Edit", var15, (String)null);
      var1.add(var2);
      this.setJMenuBar(var1);
      ((JPanel)this.getContentPane()).setBorder(new EmptyBorder(6, 6, 6, 6));
      JLabel var4 = new JLabel(PolicyTool.getMessage("Policy.File."));
      this.addNewComponent(this, var4, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, LR_TOP_BOTTOM_PADDING);
      JTextField var5 = new JTextField(50);
      var5.setPreferredSize(new Dimension(var5.getPreferredSize().width, TEXTFIELD_HEIGHT));
      var5.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Policy.File."));
      var5.setEditable(false);
      this.addNewComponent(this, var5, 1, 1, 0, 1, 1, 0.0D, 0.0D, 1, LR_TOP_BOTTOM_PADDING);
      JPanel var6 = new JPanel();
      var6.setLayout(new GridBagLayout());
      JButton var7 = new JButton();
      configureButton(var7, "Add.Policy.Entry");
      var7.addActionListener(new MainWindowListener(this.tool, this));
      this.addNewComponent(var6, var7, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, LR_PADDING);
      var7 = new JButton();
      configureButton(var7, "Edit.Policy.Entry");
      var7.addActionListener(new MainWindowListener(this.tool, this));
      this.addNewComponent(var6, var7, 1, 1, 0, 1, 1, 0.0D, 0.0D, 1, LR_PADDING);
      var7 = new JButton();
      configureButton(var7, "Remove.Policy.Entry");
      var7.addActionListener(new MainWindowListener(this.tool, this));
      this.addNewComponent(var6, var7, 2, 2, 0, 1, 1, 0.0D, 0.0D, 1, LR_PADDING);
      this.addNewComponent(this, var6, 2, 0, 2, 2, 1, 0.0D, 0.0D, 1, BOTTOM_PADDING);
      String var8 = this.tool.getPolicyFileName();
      if (var8 == null) {
         String var9 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.home")));
         var8 = var9 + File.separatorChar + ".java.policy";
      }

      JList var10;
      try {
         this.tool.openPolicy(var8);
         DefaultListModel var16 = new DefaultListModel();
         var10 = new JList(var16);
         var10.setVisibleRowCount(15);
         var10.setSelectionMode(0);
         var10.addMouseListener(new PolicyListListener(this.tool, this));
         PolicyEntry[] var17 = this.tool.getEntry();
         if (var17 != null) {
            for(int var18 = 0; var18 < var17.length; ++var18) {
               var16.addElement(var17[var18].headerToString());
            }
         }

         JTextField var19 = (JTextField)this.getComponent(1);
         var19.setText(var8);
         this.initPolicyList(var10);
      } catch (FileNotFoundException var13) {
         var10 = new JList(new DefaultListModel());
         var10.setVisibleRowCount(15);
         var10.setSelectionMode(0);
         var10.addMouseListener(new PolicyListListener(this.tool, this));
         this.initPolicyList(var10);
         this.tool.setPolicyFileName((String)null);
         this.tool.modified = false;
         this.tool.warnings.addElement(var13.toString());
      } catch (Exception var14) {
         var10 = new JList(new DefaultListModel());
         var10.setVisibleRowCount(15);
         var10.setSelectionMode(0);
         var10.addMouseListener(new PolicyListListener(this.tool, this));
         this.initPolicyList(var10);
         this.tool.setPolicyFileName((String)null);
         this.tool.modified = false;
         MessageFormat var11 = new MessageFormat(PolicyTool.getMessage("Could.not.open.policy.file.policyFile.e.toString."));
         Object[] var12 = new Object[]{var8, var14.toString()};
         this.displayErrorDialog((Window)null, (String)var11.format(var12));
      }

   }

   private void addMenuItem(JMenu var1, String var2, ActionListener var3, String var4) {
      JMenuItem var5 = new JMenuItem();
      configureButton(var5, var2);
      if (PolicyTool.rb.containsKey(var2 + ".accelerator")) {
         var4 = PolicyTool.getMessage(var2 + ".accelerator");
      }

      if (var4 != null && !var4.isEmpty()) {
         KeyStroke var6;
         if (var4.length() == 1) {
            var6 = KeyStroke.getKeyStroke(KeyEvent.getExtendedKeyCodeForChar(var4.charAt(0)), this.shortCutModifier);
         } else {
            var6 = KeyStroke.getKeyStroke(var4);
         }

         var5.setAccelerator(var6);
      }

      var5.addActionListener(var3);
      var1.add(var5);
   }

   static void configureButton(AbstractButton var0, String var1) {
      var0.setText(PolicyTool.getMessage(var1));
      var0.setActionCommand(var1);
      int var2 = PolicyTool.getMnemonicInt(var1);
      if (var2 > 0) {
         var0.setMnemonic(var2);
         var0.setDisplayedMnemonicIndex(PolicyTool.getDisplayedMnemonicIndex(var1));
      }

   }

   static void configureLabelFor(JLabel var0, JComponent var1, String var2) {
      var0.setText(PolicyTool.getMessage(var2));
      var0.setLabelFor(var1);
      int var3 = PolicyTool.getMnemonicInt(var2);
      if (var3 > 0) {
         var0.setDisplayedMnemonic(var3);
         var0.setDisplayedMnemonicIndex(PolicyTool.getDisplayedMnemonicIndex(var2));
      }

   }

   void addNewComponent(Container var1, JComponent var2, int var3, int var4, int var5, int var6, int var7, double var8, double var10, int var12, Insets var13) {
      if (var1 instanceof JFrame) {
         var1 = ((JFrame)var1).getContentPane();
      } else if (var1 instanceof JDialog) {
         var1 = ((JDialog)var1).getContentPane();
      }

      var1.add(var2, var3);
      GridBagLayout var14 = (GridBagLayout)var1.getLayout();
      GridBagConstraints var15 = new GridBagConstraints();
      var15.gridx = var4;
      var15.gridy = var5;
      var15.gridwidth = var6;
      var15.gridheight = var7;
      var15.weightx = var8;
      var15.weighty = var10;
      var15.fill = var12;
      if (var13 != null) {
         var15.insets = var13;
      }

      var14.setConstraints(var2, var15);
   }

   void addNewComponent(Container var1, JComponent var2, int var3, int var4, int var5, int var6, int var7, double var8, double var10, int var12) {
      this.addNewComponent(var1, var2, var3, var4, var5, var6, var7, var8, var10, var12, (Insets)null);
   }

   void initPolicyList(JList var1) {
      JScrollPane var2 = new JScrollPane(var1);
      this.addNewComponent(this, var2, 3, 0, 3, 2, 1, 1.0D, 1.0D, 1);
   }

   void replacePolicyList(JList var1) {
      JList var2 = (JList)this.getComponent(3);
      var2.setModel(var1.getModel());
   }

   void displayToolWindow(String[] var1) {
      this.setTitle(PolicyTool.getMessage("Policy.Tool"));
      this.setResizable(true);
      this.addWindowListener(new ToolWindowListener(this.tool, this));
      this.getContentPane().setLayout(new GridBagLayout());
      this.initWindow();
      this.pack();
      this.setLocationRelativeTo((Component)null);
      this.setVisible(true);
      if (this.tool.newWarning) {
         this.displayStatusDialog(this, PolicyTool.getMessage("Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information."));
      }

   }

   void displayErrorDialog(Window var1, String var2) {
      ToolDialog var3 = new ToolDialog(PolicyTool.getMessage("Error"), this.tool, this, true);
      if (var1 == null) {
         this.getLocationOnScreen();
      } else {
         var1.getLocationOnScreen();
      }

      var3.setLayout(new GridBagLayout());
      JLabel var5 = new JLabel(var2);
      this.addNewComponent(var3, var5, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1);
      JButton var6 = new JButton(PolicyTool.getMessage("OK"));
      ErrorOKButtonListener var7 = new ErrorOKButtonListener(var3);
      var6.addActionListener(var7);
      this.addNewComponent(var3, var6, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3);
      var3.getRootPane().setDefaultButton(var6);
      var3.getRootPane().registerKeyboardAction(var7, escKey, 2);
      var3.pack();
      var3.setLocationRelativeTo(var1);
      var3.setVisible(true);
   }

   void displayErrorDialog(Window var1, Throwable var2) {
      if (!(var2 instanceof NoDisplayException)) {
         this.displayErrorDialog(var1, var2.toString());
      }
   }

   void displayStatusDialog(Window var1, String var2) {
      ToolDialog var3 = new ToolDialog(PolicyTool.getMessage("Status"), this.tool, this, true);
      if (var1 == null) {
         this.getLocationOnScreen();
      } else {
         var1.getLocationOnScreen();
      }

      var3.setLayout(new GridBagLayout());
      JLabel var5 = new JLabel(var2);
      this.addNewComponent(var3, var5, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1);
      JButton var6 = new JButton(PolicyTool.getMessage("OK"));
      StatusOKButtonListener var7 = new StatusOKButtonListener(var3);
      var6.addActionListener(var7);
      this.addNewComponent(var3, var6, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3);
      var3.getRootPane().setDefaultButton(var6);
      var3.getRootPane().registerKeyboardAction(var7, escKey, 2);
      var3.pack();
      var3.setLocationRelativeTo(var1);
      var3.setVisible(true);
   }

   void displayWarningLog(Window var1) {
      ToolDialog var2 = new ToolDialog(PolicyTool.getMessage("Warning"), this.tool, this, true);
      if (var1 == null) {
         this.getLocationOnScreen();
      } else {
         var1.getLocationOnScreen();
      }

      var2.setLayout(new GridBagLayout());
      JTextArea var4 = new JTextArea();
      var4.setEditable(false);

      for(int var5 = 0; var5 < this.tool.warnings.size(); ++var5) {
         var4.append((String)this.tool.warnings.elementAt(var5));
         var4.append(PolicyTool.getMessage("NEWLINE"));
      }

      this.addNewComponent(var2, var4, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, BOTTOM_PADDING);
      var4.setFocusable(false);
      JButton var7 = new JButton(PolicyTool.getMessage("OK"));
      CancelButtonListener var6 = new CancelButtonListener(var2);
      var7.addActionListener(var6);
      this.addNewComponent(var2, var7, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3, LR_PADDING);
      var2.getRootPane().setDefaultButton(var7);
      var2.getRootPane().registerKeyboardAction(var6, escKey, 2);
      var2.pack();
      var2.setLocationRelativeTo(var1);
      var2.setVisible(true);
   }

   char displayYesNoDialog(Window var1, String var2, String var3, String var4, String var5) {
      final ToolDialog var6 = new ToolDialog(var2, this.tool, this, true);
      if (var1 == null) {
         this.getLocationOnScreen();
      } else {
         var1.getLocationOnScreen();
      }

      var6.setLayout(new GridBagLayout());
      JTextArea var8 = new JTextArea(var3, 10, 50);
      var8.setEditable(false);
      var8.setLineWrap(true);
      var8.setWrapStyleWord(true);
      JScrollPane var9 = new JScrollPane(var8, 20, 31);
      this.addNewComponent(var6, var9, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1);
      var8.setFocusable(false);
      JPanel var10 = new JPanel();
      var10.setLayout(new GridBagLayout());
      final StringBuffer var11 = new StringBuffer();
      JButton var12 = new JButton(var4);
      var12.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            var11.append('Y');
            var6.setVisible(false);
            var6.dispose();
         }
      });
      this.addNewComponent(var10, var12, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, LR_PADDING);
      var12 = new JButton(var5);
      var12.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            var11.append('N');
            var6.setVisible(false);
            var6.dispose();
         }
      });
      this.addNewComponent(var10, var12, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, LR_PADDING);
      this.addNewComponent(var6, var10, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3);
      var6.pack();
      var6.setLocationRelativeTo(var1);
      var6.setVisible(true);
      return var11.length() > 0 ? var11.charAt(0) : 'N';
   }

   static {
      TEXTFIELD_HEIGHT = (new JComboBox()).getPreferredSize().height;
   }
}
