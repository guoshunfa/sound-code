package sun.security.tools.policytool;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.security.InvalidParameterException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import sun.security.provider.PolicyParser;

class ToolDialog extends JDialog {
   private static final long serialVersionUID = -372244357011301190L;
   static final KeyStroke escKey = KeyStroke.getKeyStroke(27, 0);
   public static final int NOACTION = 0;
   public static final int QUIT = 1;
   public static final int NEW = 2;
   public static final int OPEN = 3;
   public static final String ALL_PERM_CLASS = "java.security.AllPermission";
   public static final String FILE_PERM_CLASS = "java.io.FilePermission";
   public static final String X500_PRIN_CLASS = "javax.security.auth.x500.X500Principal";
   public static final String PERM = PolicyTool.getMessage("Permission.");
   public static final String PRIN_TYPE = PolicyTool.getMessage("Principal.Type.");
   public static final String PRIN_NAME = PolicyTool.getMessage("Principal.Name.");
   public static final String PERM_NAME = PolicyTool.getMessage("Target.Name.");
   public static final String PERM_ACTIONS = PolicyTool.getMessage("Actions.");
   public static final int PE_CODEBASE_LABEL = 0;
   public static final int PE_CODEBASE_TEXTFIELD = 1;
   public static final int PE_SIGNEDBY_LABEL = 2;
   public static final int PE_SIGNEDBY_TEXTFIELD = 3;
   public static final int PE_PANEL0 = 4;
   public static final int PE_ADD_PRIN_BUTTON = 0;
   public static final int PE_EDIT_PRIN_BUTTON = 1;
   public static final int PE_REMOVE_PRIN_BUTTON = 2;
   public static final int PE_PRIN_LABEL = 5;
   public static final int PE_PRIN_LIST = 6;
   public static final int PE_PANEL1 = 7;
   public static final int PE_ADD_PERM_BUTTON = 0;
   public static final int PE_EDIT_PERM_BUTTON = 1;
   public static final int PE_REMOVE_PERM_BUTTON = 2;
   public static final int PE_PERM_LIST = 8;
   public static final int PE_PANEL2 = 9;
   public static final int PE_CANCEL_BUTTON = 1;
   public static final int PE_DONE_BUTTON = 0;
   public static final int PRD_DESC_LABEL = 0;
   public static final int PRD_PRIN_CHOICE = 1;
   public static final int PRD_PRIN_TEXTFIELD = 2;
   public static final int PRD_NAME_LABEL = 3;
   public static final int PRD_NAME_TEXTFIELD = 4;
   public static final int PRD_CANCEL_BUTTON = 6;
   public static final int PRD_OK_BUTTON = 5;
   public static final int PD_DESC_LABEL = 0;
   public static final int PD_PERM_CHOICE = 1;
   public static final int PD_PERM_TEXTFIELD = 2;
   public static final int PD_NAME_CHOICE = 3;
   public static final int PD_NAME_TEXTFIELD = 4;
   public static final int PD_ACTIONS_CHOICE = 5;
   public static final int PD_ACTIONS_TEXTFIELD = 6;
   public static final int PD_SIGNEDBY_LABEL = 7;
   public static final int PD_SIGNEDBY_TEXTFIELD = 8;
   public static final int PD_CANCEL_BUTTON = 10;
   public static final int PD_OK_BUTTON = 9;
   public static final int EDIT_KEYSTORE = 0;
   public static final int KSD_NAME_LABEL = 0;
   public static final int KSD_NAME_TEXTFIELD = 1;
   public static final int KSD_TYPE_LABEL = 2;
   public static final int KSD_TYPE_TEXTFIELD = 3;
   public static final int KSD_PROVIDER_LABEL = 4;
   public static final int KSD_PROVIDER_TEXTFIELD = 5;
   public static final int KSD_PWD_URL_LABEL = 6;
   public static final int KSD_PWD_URL_TEXTFIELD = 7;
   public static final int KSD_CANCEL_BUTTON = 9;
   public static final int KSD_OK_BUTTON = 8;
   public static final int USC_LABEL = 0;
   public static final int USC_PANEL = 1;
   public static final int USC_YES_BUTTON = 0;
   public static final int USC_NO_BUTTON = 1;
   public static final int USC_CANCEL_BUTTON = 2;
   public static final int CRPE_LABEL1 = 0;
   public static final int CRPE_LABEL2 = 1;
   public static final int CRPE_PANEL = 2;
   public static final int CRPE_PANEL_OK = 0;
   public static final int CRPE_PANEL_CANCEL = 1;
   private static final int PERMISSION = 0;
   private static final int PERMISSION_NAME = 1;
   private static final int PERMISSION_ACTIONS = 2;
   private static final int PERMISSION_SIGNEDBY = 3;
   private static final int PRINCIPAL_TYPE = 4;
   private static final int PRINCIPAL_NAME = 5;
   static final int TEXTFIELD_HEIGHT;
   public static ArrayList<Perm> PERM_ARRAY;
   public static ArrayList<Prin> PRIN_ARRAY;
   PolicyTool tool;
   ToolWindow tw;

   ToolDialog(String var1, PolicyTool var2, ToolWindow var3, boolean var4) {
      super((Frame)var3, var4);
      this.setTitle(var1);
      this.tool = var2;
      this.tw = var3;
      this.addWindowListener(new ChildWindowListener(this));
      ((JPanel)this.getContentPane()).setBorder(new EmptyBorder(6, 6, 6, 6));
   }

   public Component getComponent(int var1) {
      Component var2 = this.getContentPane().getComponent(var1);
      if (var2 instanceof JScrollPane) {
         var2 = ((JScrollPane)var2).getViewport().getView();
      }

      return var2;
   }

   static Perm getPerm(String var0, boolean var1) {
      for(int var2 = 0; var2 < PERM_ARRAY.size(); ++var2) {
         Perm var3 = (Perm)PERM_ARRAY.get(var2);
         if (var1) {
            if (var3.FULL_CLASS.equals(var0)) {
               return var3;
            }
         } else if (var3.CLASS.equals(var0)) {
            return var3;
         }
      }

      return null;
   }

   static Prin getPrin(String var0, boolean var1) {
      for(int var2 = 0; var2 < PRIN_ARRAY.size(); ++var2) {
         Prin var3 = (Prin)PRIN_ARRAY.get(var2);
         if (var1) {
            if (var3.FULL_CLASS.equals(var0)) {
               return var3;
            }
         } else if (var3.CLASS.equals(var0)) {
            return var3;
         }
      }

      return null;
   }

   void displayPolicyEntryDialog(boolean var1) {
      int var2 = 0;
      PolicyEntry[] var3 = null;
      TaggedList var4 = new TaggedList(3, false);
      var4.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Principal.List"));
      var4.addMouseListener(new EditPrinButtonListener(this.tool, this.tw, this, var1));
      TaggedList var5 = new TaggedList(10, false);
      var5.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Permission.List"));
      var5.addMouseListener(new EditPermButtonListener(this.tool, this.tw, this, var1));
      Point var6 = this.tw.getLocationOnScreen();
      this.setLayout(new GridBagLayout());
      this.setResizable(true);
      JButton var10;
      if (var1) {
         var3 = this.tool.getEntry();
         JList var7 = (JList)this.tw.getComponent(3);
         var2 = var7.getSelectedIndex();
         LinkedList var8 = var3[var2].getGrantEntry().principals;

         PolicyParser.PrincipalEntry var11;
         for(int var9 = 0; var9 < var8.size(); ++var9) {
            var10 = null;
            var11 = (PolicyParser.PrincipalEntry)var8.get(var9);
            var4.addTaggedItem(PrincipalEntryToUserFriendlyString(var11), var11);
         }

         Vector var17 = var3[var2].getGrantEntry().permissionEntries;

         for(int var19 = 0; var19 < var17.size(); ++var19) {
            var11 = null;
            PolicyParser.PermissionEntry var12 = (PolicyParser.PermissionEntry)var17.elementAt(var19);
            var5.addTaggedItem(PermissionEntryToUserFriendlyString(var12), var12);
         }
      }

      JLabel var15 = new JLabel();
      this.tw.addNewComponent(this, var15, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_PADDING);
      JTextField var16 = var1 ? new JTextField(var3[var2].getGrantEntry().codeBase) : new JTextField();
      ToolWindow.configureLabelFor(var15, var16, "CodeBase.");
      var16.setPreferredSize(new Dimension(var16.getPreferredSize().width, TEXTFIELD_HEIGHT));
      var16.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Code.Base"));
      this.tw.addNewComponent(this, var16, 1, 1, 0, 1, 1, 1.0D, 0.0D, 1);
      var15 = new JLabel();
      this.tw.addNewComponent(this, var15, 2, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_PADDING);
      var16 = var1 ? new JTextField(var3[var2].getGrantEntry().signedBy) : new JTextField();
      ToolWindow.configureLabelFor(var15, var16, "SignedBy.");
      var16.setPreferredSize(new Dimension(var16.getPreferredSize().width, TEXTFIELD_HEIGHT));
      var16.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Signed.By."));
      this.tw.addNewComponent(this, var16, 3, 1, 1, 1, 1, 1.0D, 0.0D, 1);
      JPanel var18 = new JPanel();
      var18.setLayout(new GridBagLayout());
      var10 = new JButton();
      ToolWindow.configureButton(var10, "Add.Principal");
      var10.addActionListener(new AddPrinButtonListener(this.tool, this.tw, this, var1));
      this.tw.addNewComponent(var18, var10, 0, 0, 0, 1, 1, 100.0D, 0.0D, 2);
      var10 = new JButton();
      ToolWindow.configureButton(var10, "Edit.Principal");
      var10.addActionListener(new EditPrinButtonListener(this.tool, this.tw, this, var1));
      this.tw.addNewComponent(var18, var10, 1, 1, 0, 1, 1, 100.0D, 0.0D, 2);
      var10 = new JButton();
      ToolWindow.configureButton(var10, "Remove.Principal");
      var10.addActionListener(new RemovePrinButtonListener(this.tool, this.tw, this, var1));
      this.tw.addNewComponent(var18, var10, 2, 2, 0, 1, 1, 100.0D, 0.0D, 2);
      this.tw.addNewComponent(this, var18, 4, 1, 2, 1, 1, 0.0D, 0.0D, 2, ToolWindow.LITE_BOTTOM_PADDING);
      var15 = new JLabel();
      this.tw.addNewComponent(this, var15, 5, 0, 3, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
      JScrollPane var20 = new JScrollPane(var4);
      ToolWindow.configureLabelFor(var15, var20, "Principals.");
      this.tw.addNewComponent(this, var20, 6, 1, 3, 3, 1, 0.0D, (double)var4.getVisibleRowCount(), 1, ToolWindow.BOTTOM_PADDING);
      var18 = new JPanel();
      var18.setLayout(new GridBagLayout());
      var10 = new JButton();
      ToolWindow.configureButton(var10, ".Add.Permission");
      var10.addActionListener(new AddPermButtonListener(this.tool, this.tw, this, var1));
      this.tw.addNewComponent(var18, var10, 0, 0, 0, 1, 1, 100.0D, 0.0D, 2);
      var10 = new JButton();
      ToolWindow.configureButton(var10, ".Edit.Permission");
      var10.addActionListener(new EditPermButtonListener(this.tool, this.tw, this, var1));
      this.tw.addNewComponent(var18, var10, 1, 1, 0, 1, 1, 100.0D, 0.0D, 2);
      var10 = new JButton();
      ToolWindow.configureButton(var10, "Remove.Permission");
      var10.addActionListener(new RemovePermButtonListener(this.tool, this.tw, this, var1));
      this.tw.addNewComponent(var18, var10, 2, 2, 0, 1, 1, 100.0D, 0.0D, 2);
      this.tw.addNewComponent(this, var18, 7, 0, 4, 2, 1, 0.0D, 0.0D, 2, ToolWindow.LITE_BOTTOM_PADDING);
      var20 = new JScrollPane(var5);
      this.tw.addNewComponent(this, var20, 8, 0, 5, 3, 1, 0.0D, (double)var5.getVisibleRowCount(), 1, ToolWindow.BOTTOM_PADDING);
      var18 = new JPanel();
      var18.setLayout(new GridBagLayout());
      JButton var21 = new JButton(PolicyTool.getMessage("Done"));
      var21.addActionListener(new AddEntryDoneButtonListener(this.tool, this.tw, this, var1));
      this.tw.addNewComponent(var18, var21, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
      JButton var13 = new JButton(PolicyTool.getMessage("Cancel"));
      CancelButtonListener var14 = new CancelButtonListener(this);
      var13.addActionListener(var14);
      this.tw.addNewComponent(var18, var13, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
      this.tw.addNewComponent(this, var18, 9, 0, 6, 2, 1, 0.0D, 0.0D, 3);
      this.getRootPane().setDefaultButton(var21);
      this.getRootPane().registerKeyboardAction(var14, escKey, 2);
      this.pack();
      this.setLocationRelativeTo(this.tw);
      this.setVisible(true);
   }

   PolicyEntry getPolicyEntryFromDialog() throws InvalidParameterException, MalformedURLException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, CertificateException, IOException, Exception {
      JTextField var1 = (JTextField)this.getComponent(1);
      String var2 = null;
      if (!var1.getText().trim().equals("")) {
         var2 = new String(var1.getText().trim());
      }

      var1 = (JTextField)this.getComponent(3);
      String var3 = null;
      if (!var1.getText().trim().equals("")) {
         var3 = new String(var1.getText().trim());
      }

      PolicyParser.GrantEntry var4 = new PolicyParser.GrantEntry(var3, var2);
      LinkedList var5 = new LinkedList();
      TaggedList var6 = (TaggedList)this.getComponent(6);

      for(int var7 = 0; var7 < var6.getModel().getSize(); ++var7) {
         var5.add((PolicyParser.PrincipalEntry)var6.getObject(var7));
      }

      var4.principals = var5;
      Vector var10 = new Vector();
      TaggedList var8 = (TaggedList)this.getComponent(8);

      for(int var9 = 0; var9 < var8.getModel().getSize(); ++var9) {
         var10.addElement((PolicyParser.PermissionEntry)var8.getObject(var9));
      }

      var4.permissionEntries = var10;
      PolicyEntry var11 = new PolicyEntry(this.tool, var4);
      return var11;
   }

   void keyStoreDialog(int var1) {
      Point var2 = this.tw.getLocationOnScreen();
      this.setLayout(new GridBagLayout());
      if (var1 == 0) {
         JLabel var3 = new JLabel();
         this.tw.addNewComponent(this, var3, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
         JTextField var4 = new JTextField(this.tool.getKeyStoreName(), 30);
         ToolWindow.configureLabelFor(var3, var4, "KeyStore.URL.");
         var4.setPreferredSize(new Dimension(var4.getPreferredSize().width, TEXTFIELD_HEIGHT));
         var4.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.U.R.L."));
         this.tw.addNewComponent(this, var4, 1, 1, 0, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
         var3 = new JLabel();
         this.tw.addNewComponent(this, var3, 2, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
         var4 = new JTextField(this.tool.getKeyStoreType(), 30);
         ToolWindow.configureLabelFor(var3, var4, "KeyStore.Type.");
         var4.setPreferredSize(new Dimension(var4.getPreferredSize().width, TEXTFIELD_HEIGHT));
         var4.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.Type."));
         this.tw.addNewComponent(this, var4, 3, 1, 1, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
         var3 = new JLabel();
         this.tw.addNewComponent(this, var3, 4, 0, 2, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
         var4 = new JTextField(this.tool.getKeyStoreProvider(), 30);
         ToolWindow.configureLabelFor(var3, var4, "KeyStore.Provider.");
         var4.setPreferredSize(new Dimension(var4.getPreferredSize().width, TEXTFIELD_HEIGHT));
         var4.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.Provider."));
         this.tw.addNewComponent(this, var4, 5, 1, 2, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
         var3 = new JLabel();
         this.tw.addNewComponent(this, var3, 6, 0, 3, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
         var4 = new JTextField(this.tool.getKeyStorePwdURL(), 30);
         ToolWindow.configureLabelFor(var3, var4, "KeyStore.Password.URL.");
         var4.setPreferredSize(new Dimension(var4.getPreferredSize().width, TEXTFIELD_HEIGHT));
         var4.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.Password.U.R.L."));
         this.tw.addNewComponent(this, var4, 7, 1, 3, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
         JButton var5 = new JButton(PolicyTool.getMessage("OK"));
         var5.addActionListener(new ChangeKeyStoreOKButtonListener(this.tool, this.tw, this));
         this.tw.addNewComponent(this, var5, 8, 0, 4, 1, 1, 0.0D, 0.0D, 3);
         JButton var6 = new JButton(PolicyTool.getMessage("Cancel"));
         CancelButtonListener var7 = new CancelButtonListener(this);
         var6.addActionListener(var7);
         this.tw.addNewComponent(this, var6, 9, 1, 4, 1, 1, 0.0D, 0.0D, 3);
         this.getRootPane().setDefaultButton(var5);
         this.getRootPane().registerKeyboardAction(var7, escKey, 2);
      }

      this.pack();
      this.setLocationRelativeTo(this.tw);
      this.setVisible(true);
   }

   void displayPrincipalDialog(boolean var1, boolean var2) {
      PolicyParser.PrincipalEntry var3 = null;
      TaggedList var4 = (TaggedList)this.getComponent(6);
      int var5 = var4.getSelectedIndex();
      if (var2) {
         var3 = (PolicyParser.PrincipalEntry)var4.getObject(var5);
      }

      ToolDialog var6 = new ToolDialog(PolicyTool.getMessage("Principals"), this.tool, this.tw, true);
      var6.addWindowListener(new ChildWindowListener(var6));
      Point var7 = this.getLocationOnScreen();
      var6.setLayout(new GridBagLayout());
      var6.setResizable(true);
      JLabel var8 = var2 ? new JLabel(PolicyTool.getMessage(".Edit.Principal.")) : new JLabel(PolicyTool.getMessage(".Add.New.Principal."));
      this.tw.addNewComponent(var6, var8, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.TOP_BOTTOM_PADDING);
      JComboBox var9 = new JComboBox();
      var9.addItem(PRIN_TYPE);
      var9.getAccessibleContext().setAccessibleName(PRIN_TYPE);

      for(int var10 = 0; var10 < PRIN_ARRAY.size(); ++var10) {
         Prin var11 = (Prin)PRIN_ARRAY.get(var10);
         var9.addItem(var11.CLASS);
      }

      if (var2) {
         if ("WILDCARD_PRINCIPAL_CLASS".equals(var3.getPrincipalClass())) {
            var9.setSelectedItem(PRIN_TYPE);
         } else {
            Prin var14 = getPrin(var3.getPrincipalClass(), true);
            if (var14 != null) {
               var9.setSelectedItem(var14.CLASS);
            }
         }
      }

      var9.addItemListener(new PrincipalTypeMenuListener(var6));
      this.tw.addNewComponent(var6, var9, 1, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_PADDING);
      JTextField var15 = var2 ? new JTextField(var3.getDisplayClass(), 30) : new JTextField(30);
      var15.setPreferredSize(new Dimension(var15.getPreferredSize().width, TEXTFIELD_HEIGHT));
      var15.getAccessibleContext().setAccessibleName(PRIN_TYPE);
      this.tw.addNewComponent(var6, var15, 2, 1, 1, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_PADDING);
      var8 = new JLabel(PRIN_NAME);
      var15 = var2 ? new JTextField(var3.getDisplayName(), 40) : new JTextField(40);
      var15.setPreferredSize(new Dimension(var15.getPreferredSize().width, TEXTFIELD_HEIGHT));
      var15.getAccessibleContext().setAccessibleName(PRIN_NAME);
      this.tw.addNewComponent(var6, var8, 3, 0, 2, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_PADDING);
      this.tw.addNewComponent(var6, var15, 4, 1, 2, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_PADDING);
      JButton var16 = new JButton(PolicyTool.getMessage("OK"));
      var16.addActionListener(new NewPolicyPrinOKButtonListener(this.tool, this.tw, this, var6, var2));
      this.tw.addNewComponent(var6, var16, 5, 0, 3, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
      JButton var12 = new JButton(PolicyTool.getMessage("Cancel"));
      CancelButtonListener var13 = new CancelButtonListener(var6);
      var12.addActionListener(var13);
      this.tw.addNewComponent(var6, var12, 6, 1, 3, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
      var6.getRootPane().setDefaultButton(var16);
      var6.getRootPane().registerKeyboardAction(var13, escKey, 2);
      var6.pack();
      var6.setLocationRelativeTo(this.tw);
      var6.setVisible(true);
   }

   void displayPermissionDialog(boolean var1, boolean var2) {
      PolicyParser.PermissionEntry var3 = null;
      TaggedList var4 = (TaggedList)this.getComponent(8);
      int var5 = var4.getSelectedIndex();
      if (var2) {
         var3 = (PolicyParser.PermissionEntry)var4.getObject(var5);
      }

      ToolDialog var6 = new ToolDialog(PolicyTool.getMessage("Permissions"), this.tool, this.tw, true);
      var6.addWindowListener(new ChildWindowListener(var6));
      Point var7 = this.getLocationOnScreen();
      var6.setLayout(new GridBagLayout());
      var6.setResizable(true);
      JLabel var8 = var2 ? new JLabel(PolicyTool.getMessage(".Edit.Permission.")) : new JLabel(PolicyTool.getMessage(".Add.New.Permission."));
      this.tw.addNewComponent(var6, var8, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.TOP_BOTTOM_PADDING);
      JComboBox var9 = new JComboBox();
      var9.addItem(PERM);
      var9.getAccessibleContext().setAccessibleName(PERM);

      Perm var11;
      for(int var10 = 0; var10 < PERM_ARRAY.size(); ++var10) {
         var11 = (Perm)PERM_ARRAY.get(var10);
         var9.addItem(var11.CLASS);
      }

      this.tw.addNewComponent(var6, var9, 1, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
      JTextField var14 = var2 ? new JTextField(var3.permission, 30) : new JTextField(30);
      var14.setPreferredSize(new Dimension(var14.getPreferredSize().width, TEXTFIELD_HEIGHT));
      var14.getAccessibleContext().setAccessibleName(PERM);
      if (var2) {
         var11 = getPerm(var3.permission, true);
         if (var11 != null) {
            var9.setSelectedItem(var11.CLASS);
         }
      }

      this.tw.addNewComponent(var6, var14, 2, 1, 1, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
      var9.addItemListener(new PermissionMenuListener(var6));
      var9 = new JComboBox();
      var9.addItem(PERM_NAME);
      var9.getAccessibleContext().setAccessibleName(PERM_NAME);
      var14 = var2 ? new JTextField(var3.name, 40) : new JTextField(40);
      var14.setPreferredSize(new Dimension(var14.getPreferredSize().width, TEXTFIELD_HEIGHT));
      var14.getAccessibleContext().setAccessibleName(PERM_NAME);
      if (var2) {
         this.setPermissionNames(getPerm(var3.permission, true), var9, var14);
      }

      this.tw.addNewComponent(var6, var9, 3, 0, 2, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
      this.tw.addNewComponent(var6, var14, 4, 1, 2, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
      var9.addItemListener(new PermissionNameMenuListener(var6));
      var9 = new JComboBox();
      var9.addItem(PERM_ACTIONS);
      var9.getAccessibleContext().setAccessibleName(PERM_ACTIONS);
      var14 = var2 ? new JTextField(var3.action, 40) : new JTextField(40);
      var14.setPreferredSize(new Dimension(var14.getPreferredSize().width, TEXTFIELD_HEIGHT));
      var14.getAccessibleContext().setAccessibleName(PERM_ACTIONS);
      if (var2) {
         this.setPermissionActions(getPerm(var3.permission, true), var9, var14);
      }

      this.tw.addNewComponent(var6, var9, 5, 0, 3, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
      this.tw.addNewComponent(var6, var14, 6, 1, 3, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
      var9.addItemListener(new PermissionActionsMenuListener(var6));
      var8 = new JLabel(PolicyTool.getMessage("Signed.By."));
      this.tw.addNewComponent(var6, var8, 7, 0, 4, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
      var14 = var2 ? new JTextField(var3.signedBy, 40) : new JTextField(40);
      var14.setPreferredSize(new Dimension(var14.getPreferredSize().width, TEXTFIELD_HEIGHT));
      var14.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Signed.By."));
      this.tw.addNewComponent(var6, var14, 8, 1, 4, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
      JButton var15 = new JButton(PolicyTool.getMessage("OK"));
      var15.addActionListener(new NewPolicyPermOKButtonListener(this.tool, this.tw, this, var6, var2));
      this.tw.addNewComponent(var6, var15, 9, 0, 5, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
      JButton var12 = new JButton(PolicyTool.getMessage("Cancel"));
      CancelButtonListener var13 = new CancelButtonListener(var6);
      var12.addActionListener(var13);
      this.tw.addNewComponent(var6, var12, 10, 1, 5, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
      var6.getRootPane().setDefaultButton(var15);
      var6.getRootPane().registerKeyboardAction(var13, escKey, 2);
      var6.pack();
      var6.setLocationRelativeTo(this.tw);
      var6.setVisible(true);
   }

   PolicyParser.PrincipalEntry getPrinFromDialog() throws Exception {
      JTextField var1 = (JTextField)this.getComponent(2);
      String var2 = new String(var1.getText().trim());
      var1 = (JTextField)this.getComponent(4);
      String var3 = new String(var1.getText().trim());
      if (var2.equals("*")) {
         var2 = "WILDCARD_PRINCIPAL_CLASS";
      }

      if (var3.equals("*")) {
         var3 = "WILDCARD_PRINCIPAL_NAME";
      }

      Object var4 = null;
      if (var2.equals("WILDCARD_PRINCIPAL_CLASS") && !var3.equals("WILDCARD_PRINCIPAL_NAME")) {
         throw new Exception(PolicyTool.getMessage("Cannot.Specify.Principal.with.a.Wildcard.Class.without.a.Wildcard.Name"));
      } else if (var3.equals("")) {
         throw new Exception(PolicyTool.getMessage("Cannot.Specify.Principal.without.a.Name"));
      } else {
         if (var2.equals("")) {
            var2 = "PolicyParser.REPLACE_NAME";
            this.tool.warnings.addElement("Warning: Principal name '" + var3 + "' specified without a Principal class.\n\t'" + var3 + "' will be interpreted as a key store alias.\n\tThe final principal class will be " + "javax.security.auth.x500.X500Principal" + ".\n\tThe final principal name will be determined by the following:\n\n\tIf the key store entry identified by '" + var3 + "'\n\tis a key entry, then the principal name will be\n\tthe subject distinguished name from the first\n\tcertificate in the entry's certificate chain.\n\n\tIf the key store entry identified by '" + var3 + "'\n\tis a trusted certificate entry, then the\n\tprincipal name will be the subject distinguished\n\tname from the trusted public key certificate.");
            this.tw.displayStatusDialog(this, "'" + var3 + "' will be interpreted as a key store alias.  View Warning Log for details.");
         }

         return new PolicyParser.PrincipalEntry(var2, var3);
      }
   }

   PolicyParser.PermissionEntry getPermFromDialog() {
      JTextField var1 = (JTextField)this.getComponent(2);
      String var2 = new String(var1.getText().trim());
      var1 = (JTextField)this.getComponent(4);
      String var3 = null;
      if (!var1.getText().trim().equals("")) {
         var3 = new String(var1.getText().trim());
      }

      if (!var2.equals("") && (var2.equals("java.security.AllPermission") || var3 != null)) {
         if (var2.equals("java.io.FilePermission") && var3.lastIndexOf("\\\\") > 0) {
            char var4 = this.tw.displayYesNoDialog(this, PolicyTool.getMessage("Warning"), PolicyTool.getMessage("Warning.File.name.may.include.escaped.backslash.characters.It.is.not.necessary.to.escape.backslash.characters.the.tool.escapes"), PolicyTool.getMessage("Retain"), PolicyTool.getMessage("Edit"));
            if (var4 != 'Y') {
               throw new NoDisplayException();
            }
         }

         var1 = (JTextField)this.getComponent(6);
         String var13 = null;
         if (!var1.getText().trim().equals("")) {
            var13 = new String(var1.getText().trim());
         }

         var1 = (JTextField)this.getComponent(8);
         String var5 = null;
         if (!var1.getText().trim().equals("")) {
            var5 = new String(var1.getText().trim());
         }

         PolicyParser.PermissionEntry var6 = new PolicyParser.PermissionEntry(var2, var3, var13);
         var6.signedBy = var5;
         if (var5 != null) {
            String[] var7 = this.tool.parseSigners(var6.signedBy);

            for(int var8 = 0; var8 < var7.length; ++var8) {
               try {
                  PublicKey var9 = this.tool.getPublicKeyAlias(var7[var8]);
                  if (var9 == null) {
                     MessageFormat var10 = new MessageFormat(PolicyTool.getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
                     Object[] var11 = new Object[]{var7[var8]};
                     this.tool.warnings.addElement(var10.format(var11));
                     this.tw.displayStatusDialog(this, var10.format(var11));
                  }
               } catch (Exception var12) {
                  this.tw.displayErrorDialog(this, (Throwable)var12);
               }
            }
         }

         return var6;
      } else {
         throw new InvalidParameterException(PolicyTool.getMessage("Permission.and.Target.Name.must.have.a.value"));
      }
   }

   void displayConfirmRemovePolicyEntry() {
      JList var1 = (JList)this.tw.getComponent(3);
      int var2 = var1.getSelectedIndex();
      PolicyEntry[] var3 = this.tool.getEntry();
      Point var4 = this.tw.getLocationOnScreen();
      this.setLayout(new GridBagLayout());
      JLabel var5 = new JLabel(PolicyTool.getMessage("Remove.this.Policy.Entry."));
      this.tw.addNewComponent(this, var5, 0, 0, 0, 2, 1, 0.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
      var5 = new JLabel(var3[var2].codebaseToString());
      this.tw.addNewComponent(this, var5, 1, 0, 1, 2, 1, 0.0D, 0.0D, 1);
      var5 = new JLabel(var3[var2].principalsToString().trim());
      this.tw.addNewComponent(this, var5, 2, 0, 2, 2, 1, 0.0D, 0.0D, 1);
      Vector var6 = var3[var2].getGrantEntry().permissionEntries;

      for(int var7 = 0; var7 < var6.size(); ++var7) {
         PolicyParser.PermissionEntry var8 = (PolicyParser.PermissionEntry)var6.elementAt(var7);
         String var9 = PermissionEntryToUserFriendlyString(var8);
         var5 = new JLabel("    " + var9);
         if (var7 == var6.size() - 1) {
            this.tw.addNewComponent(this, var5, 3 + var7, 1, 3 + var7, 1, 1, 0.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
         } else {
            this.tw.addNewComponent(this, var5, 3 + var7, 1, 3 + var7, 1, 1, 0.0D, 0.0D, 1);
         }
      }

      JPanel var11 = new JPanel();
      var11.setLayout(new GridBagLayout());
      JButton var12 = new JButton(PolicyTool.getMessage("OK"));
      var12.addActionListener(new ConfirmRemovePolicyEntryOKButtonListener(this.tool, this.tw, this));
      this.tw.addNewComponent(var11, var12, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
      JButton var13 = new JButton(PolicyTool.getMessage("Cancel"));
      CancelButtonListener var10 = new CancelButtonListener(this);
      var13.addActionListener(var10);
      this.tw.addNewComponent(var11, var13, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
      this.tw.addNewComponent(this, var11, 3 + var6.size(), 0, 3 + var6.size(), 2, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
      this.getRootPane().setDefaultButton(var12);
      this.getRootPane().registerKeyboardAction(var10, escKey, 2);
      this.pack();
      this.setLocationRelativeTo(this.tw);
      this.setVisible(true);
   }

   void displaySaveAsDialog(int var1) {
      FileDialog var2 = new FileDialog(this.tw, PolicyTool.getMessage("Save.As"), 1);
      var2.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent var1) {
            var1.getWindow().setVisible(false);
         }
      });
      var2.setVisible(true);
      if (var2.getFile() != null && !var2.getFile().equals("")) {
         File var3 = new File(var2.getDirectory(), var2.getFile());
         String var4 = var3.getPath();
         var2.dispose();

         try {
            this.tool.savePolicy(var4);
            MessageFormat var5 = new MessageFormat(PolicyTool.getMessage("Policy.successfully.written.to.filename"));
            Object[] var6 = new Object[]{var4};
            this.tw.displayStatusDialog((Window)null, var5.format(var6));
            JTextField var7 = (JTextField)this.tw.getComponent(1);
            var7.setText(var4);
            this.tw.setVisible(true);
            this.userSaveContinue(this.tool, this.tw, this, var1);
         } catch (FileNotFoundException var8) {
            if (var4 != null && !var4.equals("")) {
               this.tw.displayErrorDialog((Window)null, (Throwable)var8);
            } else {
               this.tw.displayErrorDialog((Window)null, (Throwable)(new FileNotFoundException(PolicyTool.getMessage("null.filename"))));
            }
         } catch (Exception var9) {
            this.tw.displayErrorDialog((Window)null, (Throwable)var9);
         }

      }
   }

   void displayUserSave(int var1) {
      if (this.tool.modified) {
         Point var2 = this.tw.getLocationOnScreen();
         this.setLayout(new GridBagLayout());
         JLabel var3 = new JLabel(PolicyTool.getMessage("Save.changes."));
         this.tw.addNewComponent(this, var3, 0, 0, 0, 3, 1, 0.0D, 0.0D, 1, ToolWindow.L_TOP_BOTTOM_PADDING);
         JPanel var4 = new JPanel();
         var4.setLayout(new GridBagLayout());
         JButton var5 = new JButton();
         ToolWindow.configureButton(var5, "Yes");
         var5.addActionListener(new UserSaveYesButtonListener(this, this.tool, this.tw, var1));
         this.tw.addNewComponent(var4, var5, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_BOTTOM_PADDING);
         JButton var6 = new JButton();
         ToolWindow.configureButton(var6, "No");
         var6.addActionListener(new UserSaveNoButtonListener(this, this.tool, this.tw, var1));
         this.tw.addNewComponent(var4, var6, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_BOTTOM_PADDING);
         JButton var7 = new JButton();
         ToolWindow.configureButton(var7, "Cancel");
         CancelButtonListener var8 = new CancelButtonListener(this);
         var7.addActionListener(var8);
         this.tw.addNewComponent(var4, var7, 2, 2, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_BOTTOM_PADDING);
         this.tw.addNewComponent(this, var4, 1, 0, 1, 1, 1, 0.0D, 0.0D, 1);
         this.getRootPane().registerKeyboardAction(var8, escKey, 2);
         this.pack();
         this.setLocationRelativeTo(this.tw);
         this.setVisible(true);
      } else {
         this.userSaveContinue(this.tool, this.tw, this, var1);
      }

   }

   void userSaveContinue(PolicyTool var1, ToolWindow var2, ToolDialog var3, int var4) {
      JList var5;
      JTextField var6;
      switch(var4) {
      case 1:
         var2.setVisible(false);
         var2.dispose();
         System.exit(0);
      case 2:
         try {
            var1.openPolicy((String)null);
         } catch (Exception var12) {
            var1.modified = false;
            var2.displayErrorDialog((Window)null, (Throwable)var12);
         }

         var5 = new JList(new DefaultListModel());
         var5.setVisibleRowCount(15);
         var5.setSelectionMode(0);
         var5.addMouseListener(new PolicyListListener(var1, var2));
         var2.replacePolicyList(var5);
         var6 = (JTextField)var2.getComponent(1);
         var6.setText("");
         var2.setVisible(true);
         break;
      case 3:
         FileDialog var7 = new FileDialog(var2, PolicyTool.getMessage("Open"), 0);
         var7.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent var1) {
               var1.getWindow().setVisible(false);
            }
         });
         var7.setVisible(true);
         if (var7.getFile() == null || var7.getFile().equals("")) {
            return;
         }

         String var8 = (new File(var7.getDirectory(), var7.getFile())).getPath();

         try {
            var1.openPolicy(var8);
            DefaultListModel var9 = new DefaultListModel();
            var5 = new JList(var9);
            var5.setVisibleRowCount(15);
            var5.setSelectionMode(0);
            var5.addMouseListener(new PolicyListListener(var1, var2));
            PolicyEntry[] var14 = var1.getEntry();
            if (var14 != null) {
               for(int var15 = 0; var15 < var14.length; ++var15) {
                  var9.addElement(var14[var15].headerToString());
               }
            }

            var2.replacePolicyList(var5);
            var1.modified = false;
            var6 = (JTextField)var2.getComponent(1);
            var6.setText(var8);
            var2.setVisible(true);
            if (var1.newWarning) {
               var2.displayStatusDialog((Window)null, PolicyTool.getMessage("Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information."));
            }
         } catch (Exception var13) {
            var5 = new JList(new DefaultListModel());
            var5.setVisibleRowCount(15);
            var5.setSelectionMode(0);
            var5.addMouseListener(new PolicyListListener(var1, var2));
            var2.replacePolicyList(var5);
            var1.setPolicyFileName((String)null);
            var1.modified = false;
            var6 = (JTextField)var2.getComponent(1);
            var6.setText("");
            var2.setVisible(true);
            MessageFormat var10 = new MessageFormat(PolicyTool.getMessage("Could.not.open.policy.file.policyFile.e.toString."));
            Object[] var11 = new Object[]{var8, var13.toString()};
            var2.displayErrorDialog((Window)null, (String)var10.format(var11));
         }
      }

   }

   void setPermissionNames(Perm var1, JComboBox var2, JTextField var3) {
      var2.removeAllItems();
      var2.addItem(PERM_NAME);
      if (var1 == null) {
         var3.setEditable(true);
      } else if (var1.TARGETS == null) {
         var3.setEditable(false);
      } else {
         var3.setEditable(true);

         for(int var4 = 0; var4 < var1.TARGETS.length; ++var4) {
            var2.addItem(var1.TARGETS[var4]);
         }
      }

   }

   void setPermissionActions(Perm var1, JComboBox var2, JTextField var3) {
      var2.removeAllItems();
      var2.addItem(PERM_ACTIONS);
      if (var1 == null) {
         var3.setEditable(true);
      } else if (var1.ACTIONS == null) {
         var3.setEditable(false);
      } else {
         var3.setEditable(true);

         for(int var4 = 0; var4 < var1.ACTIONS.length; ++var4) {
            var2.addItem(var1.ACTIONS[var4]);
         }
      }

   }

   static String PermissionEntryToUserFriendlyString(PolicyParser.PermissionEntry var0) {
      String var1 = var0.permission;
      if (var0.name != null) {
         var1 = var1 + " " + var0.name;
      }

      if (var0.action != null) {
         var1 = var1 + ", \"" + var0.action + "\"";
      }

      if (var0.signedBy != null) {
         var1 = var1 + ", signedBy " + var0.signedBy;
      }

      return var1;
   }

   static String PrincipalEntryToUserFriendlyString(PolicyParser.PrincipalEntry var0) {
      StringWriter var1 = new StringWriter();
      PrintWriter var2 = new PrintWriter(var1);
      var0.write(var2);
      return var1.toString();
   }

   static {
      TEXTFIELD_HEIGHT = (new JComboBox()).getPreferredSize().height;
      PERM_ARRAY = new ArrayList();
      PERM_ARRAY.add(new AllPerm());
      PERM_ARRAY.add(new AudioPerm());
      PERM_ARRAY.add(new AuthPerm());
      PERM_ARRAY.add(new AWTPerm());
      PERM_ARRAY.add(new DelegationPerm());
      PERM_ARRAY.add(new FilePerm());
      PERM_ARRAY.add(new URLPerm());
      PERM_ARRAY.add(new InqSecContextPerm());
      PERM_ARRAY.add(new LogPerm());
      PERM_ARRAY.add(new MgmtPerm());
      PERM_ARRAY.add(new MBeanPerm());
      PERM_ARRAY.add(new MBeanSvrPerm());
      PERM_ARRAY.add(new MBeanTrustPerm());
      PERM_ARRAY.add(new NetPerm());
      PERM_ARRAY.add(new PrivCredPerm());
      PERM_ARRAY.add(new PropPerm());
      PERM_ARRAY.add(new ReflectPerm());
      PERM_ARRAY.add(new RuntimePerm());
      PERM_ARRAY.add(new SecurityPerm());
      PERM_ARRAY.add(new SerialPerm());
      PERM_ARRAY.add(new ServicePerm());
      PERM_ARRAY.add(new SocketPerm());
      PERM_ARRAY.add(new SQLPerm());
      PERM_ARRAY.add(new SSLPerm());
      PERM_ARRAY.add(new SubjDelegPerm());
      PRIN_ARRAY = new ArrayList();
      PRIN_ARRAY.add(new KrbPrin());
      PRIN_ARRAY.add(new X500Prin());
   }
}
