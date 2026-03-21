package gui;

import core.*;
import election.GeneralElection;
import persons.Candidate;
import persons.ElectionOfficer;
import persons.Voter;
import utils.Validator;
import voting.EVMVoting;
import core.PendingVerification;

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
            "View Ballots",
            "Pending Verifications"
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
            case "Pending Verifications" -> showPendingVerifications();
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

        panel.add(statCard("Total Voters",    String.valueOf(totalVoters),
                MainFrame.PRIMARY));
        panel.add(statCard("Candidates",      String.valueOf(totalCandidates),
                new Color(106, 27, 154)));
        panel.add(statCard("Ballots Cast",    String.valueOf(totalBallots),
                MainFrame.SUCCESS));
        panel.add(statCard("Parties",         String.valueOf(totalParties),
                new Color(230, 81, 0)));
        panel.add(statCard("Constituencies",  String.valueOf(totalConst),
                new Color(0, 96, 100)));
        panel.add(statCard("Election Status", elecStatus,
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

        addFormRow(card, "Election ID",             idField);
        addFormRow(card, "Election Name",           nameField);
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

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(MainFrame.BG);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER),
                BorderFactory.createEmptyBorder(24, 32, 24, 32)));
        card.setPreferredSize(new Dimension(700, 520));

        JLabel heading = MainFrame.sectionLabel("Add Candidate");
        heading.setAlignmentX(LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(16));

        // ── 2 column grid ─────────────────────
        JPanel grid = new JPanel(new GridLayout(4, 4, 16, 12));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(660, 260));
        grid.setAlignmentX(LEFT_ALIGNMENT);

        JTextField nameField         = MainFrame.styledField(12);
        JTextField ageField          = MainFrame.styledField(12);
        JTextField phoneField        = MainFrame.styledField(12);
        JTextField candidateIDField  = MainFrame.styledField(12);
        JTextField voterIDField      = MainFrame.styledField(12);
        JTextField documentField     = MainFrame.styledField(12);
        JTextField partyIDField      = MainFrame.styledField(12);
        JTextField constituencyField = MainFrame.styledField(12);

        grid.add(gridLabel("Full Name"));
        grid.add(nameField);
        grid.add(gridLabel("Age"));
        grid.add(ageField);

        grid.add(gridLabel("Phone"));
        grid.add(phoneField);
        grid.add(gridLabel("Candidate ID"));
        grid.add(candidateIDField);

        grid.add(gridLabel("Voter ID"));
        grid.add(voterIDField);
        grid.add(gridLabel("Aadhaar / PAN"));
        grid.add(documentField);

        grid.add(gridLabel("Party ID"));
        grid.add(partyIDField);
        grid.add(gridLabel("Constituency ID"));
        grid.add(constituencyField);

        card.add(grid);
        card.add(Box.createVerticalStrut(12));

        // ── Hint ──────────────────────────────
        JLabel hint = new JLabel(
                "Voter ID: 10 alphanumeric  |  Aadhaar: 12 digits  |  PAN: ABCDE1234F");
        hint.setFont(MainFrame.FONT_SMALL);
        hint.setForeground(MainFrame.TEXT_GREY);
        hint.setAlignmentX(LEFT_ALIGNMENT);
        card.add(hint);
        card.add(Box.createVerticalStrut(10));

        // ── Message label ─────────────────────
        JLabel msg = new JLabel(" ");
        msg.setFont(MainFrame.FONT_SMALL);
        msg.setAlignmentX(LEFT_ALIGNMENT);
        card.add(msg);
        card.add(Box.createVerticalStrut(12));

        // ── Button row ────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(660, 44));

        JButton btn      = MainFrame.primaryButton("Add Candidate");
        JButton clearBtn = MainFrame.dangerButton("Clear Fields");

        btnRow.add(btn);
        btnRow.add(Box.createHorizontalStrut(12));
        btnRow.add(clearBtn);
        card.add(btnRow);

        // ── Clear button ──────────────────────
        clearBtn.addActionListener(e -> {
            nameField.setText(""); ageField.setText("");
            phoneField.setText(""); candidateIDField.setText("");
            voterIDField.setText(""); documentField.setText("");
            partyIDField.setText(""); constituencyField.setText("");
            msg.setText(" ");
        });

        // ── Add button ────────────────────────
        btn.addActionListener(e -> {
            String name         = nameField.getText().trim();
            String ageStr       = ageField.getText().trim();
            String phone        = phoneField.getText().trim();
            String candidateID  = candidateIDField.getText().trim();
            String voterID      = voterIDField.getText().trim();
            String document     = documentField.getText().trim();
            String partyID      = partyIDField.getText().trim();
            String constituency = constituencyField.getText().trim();

            // ── Step 1: Empty check ────────────
            if (name.isEmpty() || ageStr.isEmpty() || phone.isEmpty()
                    || candidateID.isEmpty() || voterID.isEmpty()
                    || document.isEmpty() || partyID.isEmpty()
                    || constituency.isEmpty()) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText("Please fill in all fields.");
                return;
            }

            // ── Step 2: Name check ─────────────
            if (!Validator.isValidName(name)) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText("Name must contain only letters (min 3 chars).");
                return;
            }

            // ── Step 3: Age check ──────────────
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

            // ── Step 4: Phone check ────────────
            if (!Validator.isValidPhone(phone)) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText("Phone: 10 digits starting with 6-9.");
                return;
            }

            // ── Step 5: VoterID check ──────────
            if (!Validator.isValidVoterID(voterID)) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText(Validator.voterIDError());
                return;
            }

            // ── Step 6: Document check ─────────
            if (!Validator.isValidDocument(document)) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText(Validator.documentError());
                return;
            }

            // ── Step 7: Constituency check ─────
            Constituency constCheck =
                    LoginScreen.election.getConstituencyByID(constituency);
            if (constCheck == null) {
                msg.setForeground(MainFrame.DANGER);
                msg.setText("Constituency ID not found. Add it first.");
                return;
            }

            // ── Step 8: Party check ────────────
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

            // ── Step 9: Create candidate ───────
            Candidate newCandidate = new Candidate(name, age, phone,
                    candidateID, voterID, document, partyID, constituency);

            ArrayList<Candidate> allCandidates =
                    LoginScreen.election.getCandidates();

            // ── Step 10: Duplicate name check ──
            if (DocumentVerifier.isDuplicateCandidateName(
                    name, allCandidates)) {

                ArrayList<Candidate> duplicates =
                        DocumentVerifier.getDuplicateCandidates(
                                name, allCandidates);
                Candidate existing = duplicates.get(0);

                int proceed = JOptionPane.showConfirmDialog(null,
                        "Duplicate name detected: " + name + "\n\n"
                        + "Existing Candidate ID : " + existing.getCandidateID() + "\n\n"
                        + "Dual verification required.\n"
                        + "Original determined by document match.",
                        "Duplicate Name Detected",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (proceed != JOptionPane.OK_OPTION) {
                    msg.setForeground(MainFrame.DANGER);
                    msg.setText("Candidate registration cancelled.");
                    return;
                }

                // Verify existing candidate
                JOptionPane.showMessageDialog(null,
                        "Step 1 of 2: Re-verify the EXISTING candidate.\n"
                        + "Name: " + existing.getName()
                        + "  |  ID: " + existing.getCandidateID(),
                        "Verify Existing Candidate",
                        JOptionPane.INFORMATION_MESSAGE);

                String exVoterID = JOptionPane.showInputDialog(null,
                        "Enter EXISTING candidate's Voter ID:");
                if (exVoterID == null) {
                    msg.setForeground(MainFrame.DANGER);
                    msg.setText("Registration cancelled."); return;
                }

                String exDoc = JOptionPane.showInputDialog(null,
                        "Enter EXISTING candidate's Aadhaar / PAN:");
                if (exDoc == null) {
                    msg.setForeground(MainFrame.DANGER);
                    msg.setText("Registration cancelled."); return;
                }

                // Verify new candidate
                JOptionPane.showMessageDialog(null,
                        "Step 2 of 2: Verify the NEW candidate.\n"
                        + "Name: " + newCandidate.getName()
                        + "  |  ID: " + newCandidate.getCandidateID(),
                        "Verify New Candidate",
                        JOptionPane.INFORMATION_MESSAGE);

                String newVoterID2 = JOptionPane.showInputDialog(null,
                        "Enter NEW candidate's Voter ID:");
                if (newVoterID2 == null) {
                    msg.setForeground(MainFrame.DANGER);
                    msg.setText("Registration cancelled."); return;
                }

                String newDoc = JOptionPane.showInputDialog(null,
                        "Enter NEW candidate's Aadhaar / PAN:");
                if (newDoc == null) {
                    msg.setForeground(MainFrame.DANGER);
                    msg.setText("Registration cancelled."); return;
                }

                // Run dual verification
                String verifyResult = DocumentVerifier.dualVerifyCandidates(
                        existing, exVoterID, exDoc,
                        newCandidate, newVoterID2, newDoc);

                switch (verifyResult) {
                    case "FAILED" -> {
                        JOptionPane.showMessageDialog(null,
                                "Dual verification FAILED.\n"
                                + "Documents do not match for either candidate.\n"
                                + "Registration rejected.",
                                "Verification Failed",
                                JOptionPane.ERROR_MESSAGE);
                        msg.setForeground(MainFrame.DANGER);
                        msg.setText("Verification failed. Registration rejected.");
                        return;
                    }
                    case "SAME" -> {
                        JOptionPane.showMessageDialog(null,
                                "Same person detected!\n"
                                + "Both entries share identical documents.\n"
                                + "Duplicate registration rejected.",
                                "Duplicate Detected",
                                JOptionPane.ERROR_MESSAGE);
                        msg.setForeground(MainFrame.DANGER);
                        msg.setText("Same person detected. Rejected.");
                        return;
                    }
                    case "EXISTING" -> {
                        // Existing is original — block new
                        JOptionPane.showMessageDialog(null,
                                "Result: EXISTING candidate is the original.\n\n"
                                + "New candidate documents do not match.\n"
                                + "New candidate registration BLOCKED.",
                                "Registration Blocked",
                                JOptionPane.ERROR_MESSAGE);
                        msg.setForeground(MainFrame.DANGER);
                        msg.setText("Blocked. Existing candidate is the original.");
                        return;
                    }
                    case "NEW" -> {
                        // New is original — remove existing, add new
                        JOptionPane.showMessageDialog(null,
                                "Result: NEW candidate is the original.\n\n"
                                + "Existing candidate had incorrect documents.\n"
                                + "Existing candidate removed. New will be added.",
                                "Existing Candidate Removed",
                                JOptionPane.WARNING_MESSAGE);

                        // Remove existing from election
                        LoginScreen.election.getCandidates().remove(existing);

                        // Remove from constituency
                        Constituency existingConst = LoginScreen.election
                                .getConstituencyByID(
                                        existing.getConstituencyID());
                        if (existingConst != null) {
                            existingConst.getCandidateIDs()
                                    .remove(existing.getCandidateID());
                        }

                        // Remove from party
                        for (Party p : LoginScreen.election.getParties()) {
                            p.getCandidateIDs()
                                    .remove(existing.getCandidateID());
                        }
                        // Fall through to add new candidate
                    }
                    case "BOTH" -> {
                        // Both are originals — allow new
                        JOptionPane.showMessageDialog(null,
                                "Result: BOTH candidates are genuine originals.\n\n"
                                + "Documents are completely different.\n"
                                + "New candidate approved.",
                                "Both Verified",
                                JOptionPane.INFORMATION_MESSAGE);
                        // Fall through to add new candidate
                    }
                }
            }

            // ── Step 11: Add candidate ─────────
            // Reaches here if:
            // - No duplicate name
            // - Duplicate but result was NEW or BOTH
            LoginScreen.election.addCandidate(newCandidate);
            constCheck.addCandidate(candidateID);

            for (Party p : LoginScreen.election.getParties()) {
                if (p.getPartyID().equalsIgnoreCase(partyID)) {
                    p.addCandidate(candidateID); break;
                }
            }

            msg.setForeground(MainFrame.SUCCESS);
            msg.setText("Candidate added successfully: " + name);

            // Clear all fields
            nameField.setText(""); ageField.setText("");
            phoneField.setText(""); candidateIDField.setText("");
            voterIDField.setText(""); documentField.setText("");
            partyIDField.setText(""); constituencyField.setText("");
        });

        outer.add(card);

        mainContent.removeAll();
        mainContent.add(outer, BorderLayout.CENTER);
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
                    "No election setup yet!\nGo to Setup Election first.",
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

    private JLabel gridLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(MainFrame.FONT_SUBHEAD);
        label.setForeground(MainFrame.TEXT_DARK);
        return label;
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

    private void showPendingVerifications() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JLabel heading = MainFrame.sectionLabel("Pending Voter Verifications");
        panel.add(heading, BorderLayout.NORTH);

        // Filter only PENDING ones
        ArrayList<PendingVerification> pending = new ArrayList<>();
        for (PendingVerification pv : LoginScreen.pendingVerifications) {
            if (pv.getStatus().equals("PENDING")) pending.add(pv);
        }

        if (pending.isEmpty()) {
            JLabel none = MainFrame.infoLabel(
                    "No pending verifications. All voters are verified.");
            none.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(none, BorderLayout.CENTER);
            showCard(panel);
            return;
        }

        // List of pending cards
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        for (PendingVerification pv : pending) {
            listPanel.add(buildPendingCard(pv));
            listPanel.add(Box.createVerticalStrut(12));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(MainFrame.BG);
        panel.add(scroll, BorderLayout.CENTER);

        showCard(panel);
    }

    private JPanel buildPendingCard(PendingVerification pv) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.ACCENT, 2),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        card.setMaximumSize(new Dimension(750, 260));
        card.setAlignmentX(LEFT_ALIGNMENT);

        // ── Header ────────────────────────────────
        JLabel title = new JLabel("PENDING: Duplicate Name — "
                + pv.getExistingVoter().getName());
        title.setFont(MainFrame.FONT_HEADING);
        title.setForeground(MainFrame.ACCENT);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Both voters are blocked from voting until you verify.");
        subtitle.setFont(MainFrame.FONT_SMALL);
        subtitle.setForeground(MainFrame.TEXT_GREY);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(12));

        // ── Voter info row ────────────────────────
        JPanel infoRow = new JPanel(new GridLayout(1, 2, 16, 0));
        infoRow.setOpaque(false);
        infoRow.setMaximumSize(new Dimension(720, 80));
        infoRow.setAlignmentX(LEFT_ALIGNMENT);

        infoRow.add(voterInfoBox("Existing Voter",
                pv.getExistingVoter(), MainFrame.PRIMARY));
        infoRow.add(voterInfoBox("New Voter",
                pv.getNewVoter(), MainFrame.SUCCESS));

        card.add(infoRow);
        card.add(Box.createVerticalStrut(16));

        // ── Verify button ─────────────────────────
        JLabel verifyMsg = new JLabel(" ");
        verifyMsg.setFont(MainFrame.FONT_SMALL);
        verifyMsg.setAlignmentX(LEFT_ALIGNMENT);

        JButton verifyBtn = MainFrame.primaryButton("Verify Both Voters");
        verifyBtn.setAlignmentX(LEFT_ALIGNMENT);

        verifyBtn.addActionListener(e ->
                handlePendingVerification(pv, verifyMsg));

        card.add(verifyBtn);
        card.add(Box.createVerticalStrut(6));
        card.add(verifyMsg);

        return card;
    }

    private JPanel voterInfoBox(String label, Voter voter, Color color) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(MainFrame.BG);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JLabel typeLabel = new JLabel(label);
        typeLabel.setFont(MainFrame.FONT_SUBHEAD);
        typeLabel.setForeground(color);

        JLabel nameLabel = new JLabel("Name     : " + voter.getName());
        nameLabel.setFont(MainFrame.FONT_BODY);
        nameLabel.setForeground(MainFrame.TEXT_DARK);

        JLabel idLabel = new JLabel("Voter ID : " + voter.getVoterID());
        idLabel.setFont(MainFrame.FONT_SMALL);
        idLabel.setForeground(MainFrame.TEXT_GREY);

        JLabel constLabel = new JLabel("Const    : " + voter.getConstituencyID());
        constLabel.setFont(MainFrame.FONT_SMALL);
        constLabel.setForeground(MainFrame.TEXT_GREY);

        box.add(typeLabel);
        box.add(Box.createVerticalStrut(4));
        box.add(nameLabel);
        box.add(Box.createVerticalStrut(2));
        box.add(idLabel);
        box.add(Box.createVerticalStrut(2));
        box.add(constLabel);

        return box;
    }

    private void handlePendingVerification(PendingVerification pv,
            JLabel verifyMsg) {

        Voter existing = pv.getExistingVoter();
        Voter newVoter = pv.getNewVoter();

        JOptionPane.showMessageDialog(null,
                "You will now verify both voters.\n\n"
                + "Enter the documents exactly as submitted\n"
                + "by each voter during registration.",
                "Admin Verification",
                JOptionPane.INFORMATION_MESSAGE);

        // ── Verify existing voter ──────────────────
        JOptionPane.showMessageDialog(null,
                "Step 1 of 2: Verify EXISTING voter\n\n"
                + "Name     : " + existing.getName() + "\n"
                + "Voter ID : " + existing.getVoterID(),
                "Verify Existing Voter",
                JOptionPane.INFORMATION_MESSAGE);

        String exVoterID = JOptionPane.showInputDialog(null,
                "Enter EXISTING voter's Voter ID:");
        if (exVoterID == null) {
            verifyMsg.setForeground(MainFrame.DANGER);
            verifyMsg.setText("Verification cancelled.");
            return;
        }

        String exDocument = JOptionPane.showInputDialog(null,
                "Enter EXISTING voter's Aadhaar / PAN:");
        if (exDocument == null) {
            verifyMsg.setForeground(MainFrame.DANGER);
            verifyMsg.setText("Verification cancelled.");
            return;
        }

        // ── Verify new voter ───────────────────────
        JOptionPane.showMessageDialog(null,
                "Step 2 of 2: Verify NEW voter\n\n"
                + "Name     : " + newVoter.getName() + "\n"
                + "Voter ID : " + newVoter.getVoterID(),
                "Verify New Voter",
                JOptionPane.INFORMATION_MESSAGE);

        String newVoterID = JOptionPane.showInputDialog(null,
                "Enter NEW voter's Voter ID:");
        if (newVoterID == null) {
            verifyMsg.setForeground(MainFrame.DANGER);
            verifyMsg.setText("Verification cancelled.");
            return;
        }

        String newDocument = JOptionPane.showInputDialog(null,
                "Enter NEW voter's Aadhaar / PAN:");
        if (newDocument == null) {
            verifyMsg.setForeground(MainFrame.DANGER);
            verifyMsg.setText("Verification cancelled.");
            return;
        }

        // ── Run dual verification ──────────────────
        String result = DocumentVerifier.dualVerifyVoters(
                existing, exVoterID, exDocument,
                newVoter, newVoterID, newDocument);

        switch (result) {
            case "FAILED" -> {
                // Both remain blocked
                pv.setStatus("FAILED");
                JOptionPane.showMessageDialog(null,
                        "Verification FAILED.\n\n"
                        + "Documents do not match for either voter.\n"
                        + "Both voters remain blocked.",
                        "Verification Failed",
                        JOptionPane.ERROR_MESSAGE);
                verifyMsg.setForeground(MainFrame.DANGER);
                verifyMsg.setText("Failed. Both voters remain blocked.");
            }
            case "SAME" -> {
                // Same person — block new, unblock existing
                pv.setStatus("REJECTED");
                existing.setVerified(true);   // restore existing
                LoginScreen.voters.remove(newVoter);  // remove new

                // Remove new voter from constituency too
                if (LoginScreen.election != null) {
                    core.Constituency c = LoginScreen.election
                            .getConstituencyByID(newVoter.getConstituencyID());
                    if (c != null) c.getVoterIDs().remove(newVoter.getVoterID());
                }

                JOptionPane.showMessageDialog(null,
                        "Same person detected!\n\n"
                        + "Existing voter restored and can vote.\n"
                        + "New registration rejected and removed.",
                        "Duplicate Rejected",
                        JOptionPane.WARNING_MESSAGE);
                verifyMsg.setForeground(MainFrame.SUCCESS);
                verifyMsg.setText("Same person. Existing voter restored.");
            }
            case "EXISTING" -> {
                // Existing is original — unblock existing, remove new
                pv.setStatus("EXISTING_APPROVED");
                existing.setVerified(true);
                LoginScreen.voters.remove(newVoter);

                if (LoginScreen.election != null) {
                    core.Constituency c = LoginScreen.election
                            .getConstituencyByID(newVoter.getConstituencyID());
                    if (c != null) c.getVoterIDs().remove(newVoter.getVoterID());
                }

                JOptionPane.showMessageDialog(null,
                        "Result: EXISTING voter is the original.\n\n"
                        + "Existing voter approved and can now vote.\n"
                        + "New voter registration rejected and removed.",
                        "Existing Voter Approved",
                        JOptionPane.INFORMATION_MESSAGE);
                verifyMsg.setForeground(MainFrame.SUCCESS);
                verifyMsg.setText("Existing voter approved. New voter removed.");
            }
            case "NEW" -> {
                // New is original — unblock new, remove existing
                pv.setStatus("NEW_APPROVED");
                newVoter.setVerified(true);
                LoginScreen.voters.remove(existing);

                if (LoginScreen.election != null) {
                    core.Constituency c = LoginScreen.election
                            .getConstituencyByID(existing.getConstituencyID());
                    if (c != null) c.getVoterIDs().remove(existing.getVoterID());
                }

                JOptionPane.showMessageDialog(null,
                        "Result: NEW voter is the original.\n\n"
                        + "New voter approved and can now vote.\n"
                        + "Existing voter registration rejected and removed.",
                        "New Voter Approved",
                        JOptionPane.INFORMATION_MESSAGE);
                verifyMsg.setForeground(MainFrame.SUCCESS);
                verifyMsg.setText("New voter approved. Existing voter removed.");
            }
            case "BOTH" -> {
                // Both are originals — unblock both
                pv.setStatus("BOTH_APPROVED");
                existing.setVerified(true);
                newVoter.setVerified(true);

                JOptionPane.showMessageDialog(null,
                        "Result: BOTH voters are genuine originals.\n\n"
                        + "Documents are completely different.\n"
                        + "Both voters approved and can now vote.",
                        "Both Voters Approved",
                        JOptionPane.INFORMATION_MESSAGE);
                verifyMsg.setForeground(MainFrame.SUCCESS);
                verifyMsg.setText("Both voters approved and can now vote.");
            }
        }

        // Refresh pending list
        showPendingVerifications();
    }
}