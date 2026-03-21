package gui;

import core.Constituency;
import core.PendingVerification;
import election.GeneralElection;
import persons.ElectionOfficer;
import persons.Voter;
import utils.Validator;
import voting.EVMVoting;
// import core.PendingVerification;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LoginScreen extends JPanel {

    public static GeneralElection election   = null;
    public static EVMVoting        evmVoting = null;
    public static ArrayList<Voter> voters    = new ArrayList<>();
    public static ArrayList<PendingVerification> pendingVerifications = new ArrayList<>();

    private static final String ADMIN_ID  = "ADMIN001";
    private static final String ADMIN_PWD = "admin@123";
    private static final ElectionOfficer admin = new ElectionOfficer(
            "Chief Election Officer", 50, "9000000000", ADMIN_ID, ADMIN_PWD);

    public LoginScreen() {
        setLayout(new BorderLayout());
        setBackground(MainFrame.BG);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(MainFrame.BG);
        center.add(buildLoginCard());
        add(center, BorderLayout.CENTER);
        add(buildInfoPanel(), BorderLayout.EAST);
    }

    private JPanel buildLoginCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER, 1),
                BorderFactory.createEmptyBorder(40, 48, 40, 48)));
        card.setPreferredSize(new Dimension(420, 520));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(MainFrame.FONT_TITLE);
        title.setForeground(MainFrame.PRIMARY);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to Election Simulator");
        subtitle.setFont(MainFrame.FONT_BODY);
        subtitle.setForeground(MainFrame.TEXT_GREY);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        // Tab switcher
        JPanel tabPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        tabPanel.setMaximumSize(new Dimension(340, 40));
        tabPanel.setAlignmentX(CENTER_ALIGNMENT);
        tabPanel.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER));

        JButton adminTab = new JButton("Admin");
        JButton voterTab = new JButton("Voter");
        styleTab(adminTab, true);
        styleTab(voterTab, false);
        tabPanel.add(adminTab);
        tabPanel.add(voterTab);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(340, 300));

        JPanel adminForm = buildAdminForm();
        JPanel voterForm = buildVoterForm();
        formPanel.add(adminForm);

        adminTab.addActionListener(e -> {
            styleTab(adminTab, true);
            styleTab(voterTab, false);
            formPanel.removeAll();
            formPanel.add(adminForm);
            formPanel.revalidate();
            formPanel.repaint();
        });

        voterTab.addActionListener(e -> {
            styleTab(adminTab, false);
            styleTab(voterTab, true);
            formPanel.removeAll();
            formPanel.add(voterForm);
            formPanel.revalidate();
            formPanel.repaint();
        });

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(24));
        card.add(tabPanel);
        card.add(Box.createVerticalStrut(24));
        card.add(formPanel);

        return card;
    }

    private JPanel buildAdminForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel idLabel = fieldLabel("Officer ID");
        JTextField idField = MainFrame.styledField(20);
        idField.setMaximumSize(new Dimension(340, 40));

        JLabel pwdLabel = fieldLabel("Password");
        JPasswordField pwdField = MainFrame.styledPasswordField(20);
        pwdField.setMaximumSize(new Dimension(340, 40));

        JLabel errorLabel = errorLabel();

        JButton loginBtn = MainFrame.primaryButton("Sign In as Admin");
        loginBtn.setAlignmentX(CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(340, 44));

        loginBtn.addActionListener(e -> {
            String id  = idField.getText().trim();
            String pwd = new String(pwdField.getPassword()).trim();

            if (id.isEmpty() || pwd.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                return;
            }

            if (admin.login(id, pwd)) {
                errorLabel.setText("");
                MainFrame.showScreen(new AdminDashboard(admin));
            } else {
                errorLabel.setText("Invalid credentials. Try again.");
            }
        });

        JButton registerBtn = new JButton("New voter? Register here");
        registerBtn.setFont(MainFrame.FONT_SMALL);
        registerBtn.setForeground(MainFrame.PRIMARY_LIGHT);
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerBtn.setAlignmentX(CENTER_ALIGNMENT);
        registerBtn.addActionListener(e ->
                MainFrame.showScreen(new RegisterScreen()));

        panel.add(idLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(idField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(pwdLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(pwdField);
        panel.add(Box.createVerticalStrut(6));
        panel.add(errorLabel);
        panel.add(Box.createVerticalStrut(16));
        panel.add(loginBtn);
        panel.add(Box.createVerticalStrut(12));
        panel.add(registerBtn);

        return panel;
    }

    private JPanel buildVoterForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel idLabel = fieldLabel("Voter ID");
        JTextField idField = MainFrame.styledField(20);
        idField.setMaximumSize(new Dimension(340, 40));

        JLabel hint = new JLabel("10 alphanumeric characters (e.g. ABC1234567)");
        hint.setFont(MainFrame.FONT_SMALL);
        hint.setForeground(MainFrame.TEXT_GREY);
        hint.setAlignmentX(LEFT_ALIGNMENT);

        JLabel errorLabel = errorLabel();

        JButton loginBtn = MainFrame.successButton("Sign In as Voter");
        loginBtn.setAlignmentX(CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(340, 44));

        loginBtn.addActionListener(e -> {
            String voterID = idField.getText().trim();

            // Empty check
            if (voterID.isEmpty()) {
                errorLabel.setText("Please enter your Voter ID.");
                return;
            }

            // Format check
            if (!Validator.isValidVoterID(voterID)) {
                errorLabel.setText(Validator.voterIDError());
                return;
            }

            // Election existence check
            if (election == null) {
                errorLabel.setText("No election scheduled yet. Contact admin.");
                return;
            }

            // Find voter
            Voter found = null;
            for (Voter v : voters) {
                if (v.getVoterID().equalsIgnoreCase(voterID)) {
                    found = v;
                    break;
                }
            }

            if (found == null) {
                errorLabel.setText("Voter ID not found. Please register first.");
                return;
            }

            // Constituency check
            Constituency c =
                    election.getConstituencyByID(found.getConstituencyID());
            if (c == null) {
                errorLabel.setText("Your constituency is not in this election.");
                return;
            }

            errorLabel.setText("");
            MainFrame.showScreen(new VoterDashboard(found));
        });

        JButton registerBtn = new JButton("New voter? Register here");
        registerBtn.setFont(MainFrame.FONT_SMALL);
        registerBtn.setForeground(MainFrame.PRIMARY_LIGHT);
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerBtn.setAlignmentX(CENTER_ALIGNMENT);
        registerBtn.addActionListener(e ->
                MainFrame.showScreen(new RegisterScreen()));

        panel.add(idLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(idField);
        panel.add(Box.createVerticalStrut(4));
        panel.add(hint);
        panel.add(Box.createVerticalStrut(6));
        panel.add(errorLabel);
        panel.add(Box.createVerticalStrut(16));
        panel.add(loginBtn);
        panel.add(Box.createVerticalStrut(12));
        panel.add(registerBtn);

        return panel;
    }

    private JPanel buildInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MainFrame.PRIMARY);
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(48, 32, 48, 32));

        JLabel heading = new JLabel("How It Works");
        heading.setFont(MainFrame.FONT_HEADING);
        heading.setForeground(MainFrame.WHITE);
        heading.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(heading);
        panel.add(Box.createVerticalStrut(24));

        String[][] steps = {
            {"1", "Admin Setup",    "Admin creates election, adds parties & candidates"},
            {"2", "Register",       "Voters register with VoterID & Aadhaar/PAN"},
            {"3", "Cast Your Vote", "Login and vote via secure EVM system"},
            {"4", "View Results",   "Admin announces results after voting closes"}
        };

        for (String[] step : steps) {
            panel.add(buildStepCard(step[0], step[1], step[2]));
            panel.add(Box.createVerticalStrut(16));
        }

        panel.add(Box.createVerticalGlue());

        JLabel secure = new JLabel("End-to-End Secure Voting");
        secure.setFont(MainFrame.FONT_SMALL);
        secure.setForeground(new Color(187, 222, 251));
        secure.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(secure);

        return panel;
    }

    private JPanel buildStepCard(String num, String title, String desc) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(220, 70));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JLabel numLabel = new JLabel(num, SwingConstants.CENTER);
        numLabel.setFont(MainFrame.FONT_SUBHEAD);
        numLabel.setForeground(MainFrame.PRIMARY);
        numLabel.setBackground(MainFrame.ACCENT);
        numLabel.setOpaque(true);
        numLabel.setPreferredSize(new Dimension(28, 28));

        JPanel numWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        numWrapper.setOpaque(false);
        numWrapper.add(numLabel);

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(MainFrame.FONT_SUBHEAD);
        titleLabel.setForeground(MainFrame.WHITE);

        JLabel descLabel = new JLabel(
                "<html><body style='width:160px'>" + desc + "</body></html>");
        descLabel.setFont(MainFrame.FONT_SMALL);
        descLabel.setForeground(new Color(187, 222, 251));

        text.add(titleLabel);
        text.add(descLabel);

        card.add(numWrapper, BorderLayout.WEST);
        card.add(text,       BorderLayout.CENTER);

        return card;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(MainFrame.FONT_SUBHEAD);
        label.setForeground(MainFrame.TEXT_DARK);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private JLabel errorLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(MainFrame.FONT_SMALL);
        label.setForeground(MainFrame.DANGER);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private void styleTab(JButton btn, boolean active) {
        btn.setFont(MainFrame.FONT_SUBHEAD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (active) {
            btn.setBackground(MainFrame.PRIMARY);
            btn.setForeground(MainFrame.WHITE);
        } else {
            btn.setBackground(MainFrame.WHITE);
            btn.setForeground(MainFrame.TEXT_GREY);
        }
    }

    public static ElectionOfficer getAdmin() { return admin; }
}