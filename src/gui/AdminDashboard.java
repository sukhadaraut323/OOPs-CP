package gui;

import core.*;
import election.GeneralElection;
import persons.Candidate;
import persons.ElectionOfficer;
import persons.Voter;
import utils.Validator;
import voting.EVMVoting;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

public class AdminDashboard extends JPanel {

    private ElectionOfficer admin;
    private JPanel mainContent;
    private JLabel statusLabel;

    public AdminDashboard(ElectionOfficer admin) {
        this.admin = admin;
        setLayout(new BorderLayout());
        setBackground(MainFrame.BG);

        add(buildSidebar(),  BorderLayout.WEST);
        add(buildTopBar(),   BorderLayout.NORTH);

        mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(MainFrame.BG);
        mainContent.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        add(mainContent, BorderLayout.CENTER);

        showHome();
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(MainFrame.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MainFrame.BORDER),
                BorderFactory.createEmptyBorder(12, 24, 12, 24)));

        JLabel welcome = new JLabel("Welcome, " + admin.getName()
                + "  |  Admin Panel");
        welcome.setFont(MainFrame.FONT_SUBHEAD);
        welcome.setForeground(MainFrame.TEXT_DARK);

        statusLabel = new JLabel(getElectionStatus());
        statusLabel.setFont(MainFrame.FONT_SMALL);
        statusLabel.setForeground(MainFrame.TEXT_GREY);

        JButton logoutBtn = MainFrame.dangerButton("Logout");
        logoutBtn.addActionListener(e -> MainFrame.showScreen(new LoginScreen()));

        bar.add(welcome,     BorderLayout.WEST);
        bar.add(statusLabel, BorderLayout.CENTER);
        bar.add(logoutBtn,   BorderLayout.EAST);

        return bar;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(21, 101, 192));
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));

        JLabel menuLabel = new JLabel("  ADMIN MENU");
        menuLabel.setFont(MainFrame.FONT_SMALL);
        menuLabel.setForeground(new Color(144, 202, 249));
        menuLabel.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(menuLabel);
        sidebar.add(Box.createVerticalStrut(12));

        String[] items = {
            "Home",
            "Setup Election",
            "Add Constituency",
            "Add Party",
            "Add Candidate",
            "Open Election",
            "Close Election",
            "View Results",
            "View Voters",
            "View Ballots"
        };

        for (String item : items) {
            JButton btn = sidebarButton(item);
            btn.addActionListener(e -> handleSidebarAction(item));
            sidebar.add(btn);
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton sidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(MainFrame.FONT_BODY);
        btn.setForeground(MainFrame.WHITE);
        btn.setBackground(new Color(21, 101, 192));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setMaximumSize(new Dimension(210, 44));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(13, 71, 161));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(21, 101, 192));
            }
        });
        return btn;
    }

    private void handleSidebarAction(String action) {
        mainContent.removeAll();
        switch (action) {
            case "Home"             -> showHome();
            case "Setup Election"   -> showSetupElection();
            case "Add Constituency" -> showAddConstituency();
            case "Add Party"        -> showAddParty();
            case "Add Candidate"    -> showAddCandidate();
            case "Open Election"    -> openElection();
            case "Close Election"   -> closeElection();
            case "View Results"     -> showResults();
            case "View Voters"      -> showVoters();
            case "View Ballots"     -> showBallots();
        }
        mainContent.revalidate();
        mainContent.repaint();
        statusLabel.setText(getElectionStatus());
    }

    private void showHome() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 16, 16));
        panel.setOpaque(false);

        int totalVoters     = LoginScreen.voters.size();
        int totalCandidates = LoginScreen.election != null
                ? LoginScreen.election.getCandidates().size() : 0;
        int totalBallots    = LoginScreen.evmVoting != null
                ? LoginScreen.evmVoting.getTotalVotesCast() : 0;
        int totalParties    = LoginScreen.election != null
                ? LoginScreen.election.getParties().size() : 0;
        int totalConst      = LoginScreen.election != null
                ? LoginScreen.election.getConstituencies().size() : 0;
        String elecStatus   = LoginScreen.election != null
                ? (LoginScreen.election.isOpen() ? "OPEN" : "CLOSED")
                : "Not Setup";

        panel.add(statCard("Total Voters",      String.valueOf(totalVoters),
                MainFrame.PRIMARY));
        panel.add(statCard("Candidates",        String.valueOf(totalCandidates),
                new Color(106, 27, 154)));
        panel.add(statCard("Ballots Cast",      String.valueOf(totalBallots),
                MainFrame.SUCCESS));
        panel.add(statCard("Parties",           String.valueOf(totalParties),
                new Color(230, 81, 0)));
        panel.add(statCard("Constituencies",    String.valueOf(totalConst),
                new Color(0, 96, 100)));
        panel.add(statCard("Election Status",   elecStatus,
                MainFrame.PRIMARY_LIGHT));

        JLabel heading = MainFrame.sectionLabel("Dashboard Overview");
        mainContent.add(heading, BorderLayout.NORTH);
        mainContent.add(panel,   BorderLayout.CENTER);
    }

    private JPanel statCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(MainFrame.FONT_BODY);
        titleLabel.setForeground(MainFrame.TEXT_GREY);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        card.add(valueLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(titleLabel);
        return card;
    }

    private void showSetupElection() {
        JPanel card = formCard("Setup Election");

        JTextField idField    = MainFrame.styledField(20);
        JTextField nameField  = MainFrame.styledField(20);
        JTextField startField = MainFrame.styledField(20);
        JTextField endField   = MainFrame.styledField(20);

        addFormRow(card, "Election ID",   idField);
        addFormRow(card, "Election Name", nameField);
        addFormRow(card, "Start Date (DD-MM-YYYY)", startField);
        addFormRow(card, "End Date (DD-MM-YYYY)",   endField);

        JLabel msg = msgLabel();
        card.add(msg);

        JButton btn = MainFrame.primaryButton("Create Election");
        btn.addActionListener(e -> {
            String id    = idField.getText().trim();
            String name  = nameField.getText().trim();
            String start = startField.getText().trim();
            String end   = endField.getText().trim();

            if (id.isEmpty() || name.isEmpty()
                    || start.isEmpty() || end.isEmpty()) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText("Please fill in all fields.");
                return;
            }

            LoginScreen.election  = new GeneralElection(id, name, start, end);
            LoginScreen.evmVoting = new EVMVoting(
                    LoginScreen.election, new Scanner(System.in));

            msg.setForeground(MainFrame.SUCCESS);
            msg.setText("Election created: " + name);
            statusLabel.setText(getElectionStatus());
        });

        card.add(Box.createVerticalStrut(16));
        card.add(btn);
        showCard(card);
    }

    private void showAddConstituency() {
        if (!checkElection()) return;
        JPanel card = formCard("Add Constituency");

        JTextField idField   = MainFrame.styledField(20);
        JTextField nameField = MainFrame.styledField(20);

        addFormRow(card, "Constituency ID",   idField);
        addFormRow(card, "Constituency Name", nameField);

        JLabel msg = msgLabel();
        card.add(msg);

        JButton btn = MainFrame.primaryButton("Add Constituency");
        btn.addActionListener(e -> {
            String id   = idField.getText().trim();
            String name = nameField.getText().trim();

            if (id.isEmpty() || name.isEmpty()) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText("Please fill in all fields.");
                return;
            }

            // Check duplicate
            for (Constituency c : LoginScreen.election.getConstituencies()) {
                if (c.getConstituencyID().equalsIgnoreCase(id)) {
                    msg.setForeground(MainFrame.DANGER);
                    msg.setText("Constituency ID already exists.");
                    return;
                }
            }

            LoginScreen.election.addConstituency(new Constituency(id, name));
            msg.setForeground(MainFrame.SUCCESS);
            msg.setText("Constituency added: " + name);
            idField.setText("");
            nameField.setText("");
        });

        card.add(Box.createVerticalStrut(16));
        card.add(btn);
        showCard(card);
    }

    private void showAddParty() {
        if (!checkElection()) return;
        JPanel card = formCard("Add Party");

        JTextField idField     = MainFrame.styledField(20);
        JTextField nameField   = MainFrame.styledField(20);
        JTextField symbolField = MainFrame.styledField(20);

        addFormRow(card, "Party ID",     idField);
        addFormRow(card, "Party Name",   nameField);
        addFormRow(card, "Party Symbol", symbolField);

        JLabel msg = msgLabel();
        card.add(msg);

        JButton btn = MainFrame.primaryButton("Add Party");
        btn.addActionListener(e -> {
            String id     = idField.getText().trim();
            String name   = nameField.getText().trim();
            String symbol = symbolField.getText().trim();

            if (id.isEmpty() || name.isEmpty() || symbol.isEmpty()) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText("Please fill in all fields.");
                return;
            }

            // Check duplicate
            for (Party p : LoginScreen.election.getParties()) {
                if (p.getPartyID().equalsIgnoreCase(id)) {
                    msg.setForeground(MainFrame.DANGER);
                    msg.setText("Party ID already exists.");
                    return;
                }
            }

            LoginScreen.election.addParty(new Party(id, name, symbol));
            msg.setForeground(MainFrame.SUCCESS);
            msg.setText("Party added: " + name);
            idField.setText("");
            nameField.setText("");
            symbolField.setText("");
        });

        card.add(Box.createVerticalStrut(16));
        card.add(btn);
        showCard(card);
    }

    private void showAddCandidate() {
        if (!checkElection()) return;

        // ── Form card ─────────────────────────────
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER),
                BorderFactory.createEmptyBorder(28, 32, 28, 32)));

        JLabel heading = MainFrame.sectionLabel("Add Candidate");
        heading.setAlignmentX(LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(20));

        JTextField nameField         = MainFrame.styledField(20);
        JTextField ageField          = MainFrame.styledField(20);
        JTextField phoneField        = MainFrame.styledField(20);
        JTextField candidateIDField  = MainFrame.styledField(20);
        JTextField voterIDField      = MainFrame.styledField(20);
        JTextField documentField     = MainFrame.styledField(20);
        JTextField partyIDField      = MainFrame.styledField(20);
        JTextField constituencyField = MainFrame.styledField(20);

        addFormRow(card, "Full Name",       nameField);
        addFormRow(card, "Age",             ageField);
        addFormRow(card, "Phone",           phoneField);
        addFormRow(card, "Candidate ID",    candidateIDField);
        addFormRow(card, "Voter ID",        voterIDField);
        addFormRow(card, "Aadhaar / PAN",   documentField);
        addFormRow(card, "Party ID",        partyIDField);
        addFormRow(card, "Constituency ID", constituencyField);

        JLabel hint = new JLabel(
                "Voter ID: 10 alphanumeric  |  Aadhaar: 12 digits  |  PAN: ABCDE1234F");
        hint.setFont(MainFrame.FONT_SMALL);
        hint.setForeground(MainFrame.TEXT_GREY);
        hint.setAlignmentX(LEFT_ALIGNMENT);
        card.add(hint);
        card.add(Box.createVerticalStrut(8));

        JLabel msg = msgLabel();
        card.add(msg);
        card.add(Box.createVerticalStrut(8));

        JButton btn = MainFrame.primaryButton("Add Candidate");
        btn.setAlignmentX(LEFT_ALIGNMENT);

        btn.addActionListener(e -> {
            String name         = nameField.getText().trim();
            String ageStr       = ageField.getText().trim();
            String phone        = phoneField.getText().trim();
            String candidateID  = candidateIDField.getText().trim();
            String voterID      = voterIDField.getText().trim();
            String document     = documentField.getText().trim();
            String partyID      = partyIDField.getText().trim();
            String constituency = constituencyField.getText().trim();

            // Empty check
            if (name.isEmpty() || ageStr.isEmpty() || phone.isEmpty()
                    || candidateID.isEmpty() || voterID.isEmpty()
                    || document.isEmpty() || partyID.isEmpty()
                    || constituency.isEmpty()) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText("Please fill in all fields.");
                return;
            }

            // Name check
            if (!Validator.isValidName(name)) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText("Name must contain only letters (min 3 chars).");
                return;
            }

            // Age check
            int age;
            try { age = Integer.parseInt(ageStr); }
            catch (NumberFormatException ex) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText("Age must be a valid number.");
                return;
            }
            if (!Validator.isValidAge(age)) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText("Candidate must be between 18 and 120 years.");
                return;
            }

            // Phone check
            if (!Validator.isValidPhone(phone)) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText("Phone: 10 digits starting with 6-9.");
                return;
            }

            // VoterID check
            if (!Validator.isValidVoterID(voterID)) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText(Validator.voterIDError());
                return;
            }

            // Document check
            if (!Validator.isValidDocument(document)) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText(Validator.documentError());
                return;
            }

            // Constituency exists check
            Constituency constCheck =
                    LoginScreen.election.getConstituencyByID(constituency);
            if (constCheck == null) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText("Constituency ID not found. Add it first.");
                return;
            }

            // Party exists check
            boolean partyFound = false;
            for (Party p : LoginScreen.election.getParties()) {
                if (p.getPartyID().equalsIgnoreCase(partyID)) {
                    partyFound = true; break;
                }
            }
            if (!partyFound) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText("Party ID not found. Add party first.");
                return;
            }

            // Create candidate
            Candidate newCandidate = new Candidate(name, age, phone,
                    candidateID, voterID, document, partyID, constituency);

            ArrayList<Candidate> allCandidates =
                    LoginScreen.election.getCandidates();

            // Duplicate name check
            if (DocumentVerifier.isDuplicateName(name, allCandidates)) {
                ArrayList<Candidate> duplicates =
                        DocumentVerifier.getDuplicates(name, allCandidates);
                Candidate existing = duplicates.get(0);

                int result = JOptionPane.showConfirmDialog(this,
                        "Duplicate name detected: " + name + "\n"
                        + "Dual verification required for BOTH candidates.\n"
                        + "Proceed?",
                        "Duplicate Name Detected",
                        JOptionPane.YES_NO_OPTION);

                if (result != JOptionPane.YES_OPTION) {
                    msg.setForeground(MainFrame.DANGER);
                    msg.setText("Candidate registration cancelled.");
                    return;
                }

                String exVoterID = JOptionPane.showInputDialog(this,
                        "Re-verify EXISTING candidate: " + existing.getName()
                        + "\nEnter their Voter ID:");
                String exDoc = JOptionPane.showInputDialog(this,
                        "Enter their Aadhaar/PAN:");
                String newVoterID2 = JOptionPane.showInputDialog(this,
                        "Verify NEW candidate: " + newCandidate.getName()
                        + "\nEnter their Voter ID:");
                String newDoc = JOptionPane.showInputDialog(this,
                        "Enter their Aadhaar/PAN:");

                boolean verified = DocumentVerifier.dualVerifyBoth(
                        existing, exVoterID, exDoc,
                        newCandidate, newVoterID2, newDoc);

                if (!verified) {
                    msg.setForeground(MainFrame.DANGER);
                    msg.setText("Dual verification failed. Candidate rejected.");
                    return;
                }
            }

            // Add candidate
            LoginScreen.election.addCandidate(newCandidate);
            constCheck.addCandidate(candidateID);

            for (Party p : LoginScreen.election.getParties()) {
                if (p.getPartyID().equalsIgnoreCase(partyID)) {
                    p.addCandidate(candidateID); break;
                }
            }

            msg.setForeground(MainFrame.SUCCESS);
            msg.setText("Candidate added: " + name);

            // Clear all fields
            nameField.setText(""); ageField.setText("");
            phoneField.setText(""); candidateIDField.setText("");
            voterIDField.setText(""); documentField.setText("");
            partyIDField.setText(""); constituencyField.setText("");

            // Scroll back to top after adding
            SwingUtilities.invokeLater(() -> {
                JScrollPane sp = (JScrollPane) SwingUtilities
                        .getAncestorOfClass(JScrollPane.class, card);
                if (sp != null)
                    sp.getVerticalScrollBar().setValue(0);
            });
        });

        card.add(btn);

        // ── Wrap card in ScrollPane ───────────────
        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(MainFrame.WHITE);

        // ── Show in main content ──────────────────
        mainContent.removeAll();
        mainContent.add(scrollPane, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }

    private void openElection() {
        if (!checkElection()) return;

        if (LoginScreen.election.getCandidates().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Add at least one candidate before opening election!",
                    "Cannot Open", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (LoginScreen.election.getConstituencies().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Add at least one constituency before opening election!",
                    "Cannot Open", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LoginScreen.election.openElection();
        LoginScreen.election.conductElection();
        JOptionPane.showMessageDialog(this,
                "Election is now OPEN for voting!",
                "Election Opened", JOptionPane.INFORMATION_MESSAGE);
        showHome();
    }

    private void closeElection() {
        if (!checkElection()) return;
        if (!LoginScreen.election.isOpen()) {
            JOptionPane.showMessageDialog(this,
                    "Election is already closed.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        LoginScreen.election.closeElection();
        JOptionPane.showMessageDialog(this,
                "Election is now CLOSED.",
                "Election Closed", JOptionPane.INFORMATION_MESSAGE);
        showHome();
    }

    private void showResults() {
        if (!checkElection()) return;
        if (LoginScreen.election.isOpen()) {
            JOptionPane.showMessageDialog(this,
                    "Please close the election before viewing results!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        MainFrame.showScreen(new ResultScreen());
    }

    private void showVoters() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.add(MainFrame.sectionLabel("Registered Voters"),
                BorderLayout.NORTH);

        String[] cols = {"Name", "Age", "Voter ID", "Constituency", "Voted?"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        for (Voter v : LoginScreen.voters) {
            model.addRow(new Object[]{
                v.getName(), v.getAge(), v.getVoterID(),
                v.getConstituencyID(), v.hasVoted() ? "Yes" : "No"
            });
        }

        JTable table = styledTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        showCard(panel);
    }

    private void showBallots() {
        if (LoginScreen.evmVoting == null) {
            JOptionPane.showMessageDialog(this,
                    "EVM not initialized yet.");
            return;
        }

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.add(MainFrame.sectionLabel("Cast Ballots"), BorderLayout.NORTH);

        String[] cols = {"Ballot ID", "Voter ID", "Candidate ID",
                         "Constituency", "Timestamp"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        for (var b : LoginScreen.evmVoting.getBallots()) {
            model.addRow(new Object[]{
                b.getBallotID(), b.getVoterID(), b.getCandidateID(),
                b.getConstituencyID(), b.getTimestamp()
            });
        }

        JTable table = styledTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        showCard(panel);
    }

    // ── Helpers ───────────────────────────────

    private boolean checkElection() {
        if (LoginScreen.election == null) {
            JOptionPane.showMessageDialog(this,
                    "No election setup yet!\nGo to 'Setup Election' first.",
                    "No Election", JOptionPane.WARNING_MESSAGE);
            showHome();
            return false;
        }
        return true;
    }

    private String getElectionStatus() {
        if (LoginScreen.election == null)
            return "  |  No election setup yet";
        return "  |  " + LoginScreen.election.getElectionName()
                + "  |  "
                + (LoginScreen.election.isOpen() ? "OPEN" : "CLOSED");
    }

    private JPanel formCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER),
                BorderFactory.createEmptyBorder(28, 32, 28, 32)));

        JLabel heading = MainFrame.sectionLabel(title);
        heading.setAlignmentX(LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(20));
        return card;
    }

    private void addFormRow(JPanel card, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(MainFrame.FONT_SUBHEAD);
        label.setForeground(MainFrame.TEXT_DARK);
        label.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(380, 38));
        field.setAlignmentX(LEFT_ALIGNMENT);
        card.add(label);
        card.add(Box.createVerticalStrut(4));
        card.add(field);
        card.add(Box.createVerticalStrut(12));
    }

    private JLabel msgLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(MainFrame.FONT_SMALL);
        label.setForeground(MainFrame.SUCCESS);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private void showCard(JPanel card) {
        mainContent.removeAll();
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(MainFrame.BG);
        wrapper.add(card);
        mainContent.add(wrapper, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(MainFrame.FONT_BODY);
        table.setRowHeight(32);
        table.getTableHeader().setFont(MainFrame.FONT_SUBHEAD);
        table.getTableHeader().setBackground(MainFrame.PRIMARY);
        table.getTableHeader().setForeground(MainFrame.WHITE);
        table.setGridColor(MainFrame.BORDER);
        table.setSelectionBackground(new Color(187, 222, 251));
        table.setDefaultEditor(Object.class, null);
        return table;
    }
}