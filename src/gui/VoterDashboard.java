package gui;

import persons.Candidate;
import persons.Voter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class VoterDashboard extends JPanel {

    private Voter voter;
    private JPanel mainContent;
    // private JLabel statusLabel;      // ← ADD THIS LINE
    private JLabel topVotedStatus;   // ← ADD THIS LINE
    public VoterDashboard(Voter voter) {
        this.voter = voter;
        setLayout(new BorderLayout());
        setBackground(MainFrame.BG);

        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);

        mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(MainFrame.BG);
        mainContent.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        add(mainContent, BorderLayout.CENTER);

        showHome();
    }

    // ── Top Bar ───────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(MainFrame.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MainFrame.BORDER),
                BorderFactory.createEmptyBorder(12, 24, 12, 24)));

        JLabel welcome = new JLabel("Welcome, " + voter.getName()
                + "  |  Voter ID: " + voter.getVoterID());
        welcome.setFont(MainFrame.FONT_SUBHEAD);
        welcome.setForeground(MainFrame.TEXT_DARK);

        // ── Make this a field so we can update it ──
        topVotedStatus = new JLabel(getVotedStatusText());
        topVotedStatus.setFont(MainFrame.FONT_SMALL);
        topVotedStatus.setForeground(
                voter.hasVoted() ? MainFrame.SUCCESS : MainFrame.DANGER);

        JButton logoutBtn = MainFrame.dangerButton("Logout");
        logoutBtn.addActionListener(e -> MainFrame.showScreen(new LoginScreen()));

        bar.add(welcome,       BorderLayout.WEST);
        bar.add(topVotedStatus, BorderLayout.CENTER);
        bar.add(logoutBtn,     BorderLayout.EAST);

        return bar;
    }

// ── Helper to get voted status text ───────────
private String getVotedStatusText() {
    return voter.hasVoted()
            ? "  You have voted"
            : "  You have not voted yet";
}

    // ── Sidebar ───────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(MainFrame.SUCCESS);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));

        JLabel menuLabel = new JLabel("  VOTER MENU");
        menuLabel.setFont(MainFrame.FONT_SMALL);
        menuLabel.setForeground(new Color(200, 230, 201));
        menuLabel.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(menuLabel);
        sidebar.add(Box.createVerticalStrut(12));

        String[][] items = {
            {"🏠", "Home"},
            {"🗳️",  "Cast Vote"},
            {"👤", "My Profile"},
            {"📋", "My Constituency"},
        };

        for (String[] item : items) {
            JButton btn = sidebarButton(item[0] + "  " + item[1]);
            btn.addActionListener(e -> handleAction(item[1]));
            sidebar.add(btn);
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton sidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(MainFrame.FONT_BODY);
        btn.setForeground(MainFrame.WHITE);
        btn.setBackground(MainFrame.SUCCESS);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setMaximumSize(new Dimension(200, 44));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(27, 94, 32));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(MainFrame.SUCCESS);
            }
        });
        return btn;
    }

    // ── Sidebar Actions ───────────────────────
    private void handleAction(String action) {
        mainContent.removeAll();
        switch (action) {
            case "Home"             -> showHome();
            case "Cast Vote"        -> showCastVote();
            case "My Profile"       -> showProfile();
            case "My Constituency"  -> showConstituency();
        }
        mainContent.revalidate();
        mainContent.repaint();
    }

    // ── Home ──────────────────────────────────
    private void showHome() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Status card
        JPanel statusCard = new JPanel();
        statusCard.setLayout(new BoxLayout(statusCard, BoxLayout.Y_AXIS));
        statusCard.setBackground(MainFrame.WHITE);
        statusCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER),
                BorderFactory.createEmptyBorder(24, 32, 24, 32)));
        statusCard.setMaximumSize(new Dimension(700, 160));
        statusCard.setAlignmentX(LEFT_ALIGNMENT);

        JLabel greeting = new JLabel("Hello, " + voter.getName() + "! 👋");
        greeting.setFont(MainFrame.FONT_TITLE);
        greeting.setForeground(MainFrame.PRIMARY);

        JLabel votingStatus;
        if (voter.hasVoted()) {
            votingStatus = new JLabel("✅ You have successfully cast your vote.");
            votingStatus.setForeground(MainFrame.SUCCESS);
        } else {
            votingStatus = new JLabel("🗳️  You have not voted yet. Cast your vote now!");
            votingStatus.setForeground(MainFrame.DANGER);
        }
        votingStatus.setFont(MainFrame.FONT_BODY);

        String electionStatus = LoginScreen.election != null
                ? (LoginScreen.election.isOpen() ? "🟢 OPEN" : "🔴 CLOSED")
                : "Not Setup";
        JLabel elecLabel = new JLabel("Election Status: " + electionStatus);
        elecLabel.setFont(MainFrame.FONT_BODY);
        elecLabel.setForeground(MainFrame.TEXT_GREY);

        statusCard.add(greeting);
        statusCard.add(Box.createVerticalStrut(8));
        statusCard.add(votingStatus);
        statusCard.add(Box.createVerticalStrut(6));
        statusCard.add(elecLabel);

        // Info cards row
        JPanel infoRow = new JPanel(new GridLayout(1, 3, 16, 0));
        infoRow.setOpaque(false);
        infoRow.setMaximumSize(new Dimension(700, 100));
        infoRow.setAlignmentX(LEFT_ALIGNMENT);

        infoRow.add(miniCard("📍 Constituency", voter.getConstituencyID(), MainFrame.PRIMARY));
        infoRow.add(miniCard("🗳️  Voted",
                voter.hasVoted() ? "Yes ✅" : "No ❌", MainFrame.SUCCESS));
        infoRow.add(miniCard("🔐 Voter ID", voter.getVoterID(),
                new Color(106, 27, 154)));

        // Cast vote button (if not voted)
        if (!voter.hasVoted()) {
            JButton castBtn = MainFrame.successButton("🗳️  Cast Your Vote Now");
            castBtn.setAlignmentX(LEFT_ALIGNMENT);
            castBtn.addActionListener(e -> showCastVote());

            panel.add(statusCard);
            panel.add(Box.createVerticalStrut(16));
            panel.add(infoRow);
            panel.add(Box.createVerticalStrut(20));
            panel.add(castBtn);
        } else {
            panel.add(statusCard);
            panel.add(Box.createVerticalStrut(16));
            panel.add(infoRow);
        }

        mainContent.add(MainFrame.sectionLabel("🏠 Voter Dashboard"), BorderLayout.NORTH);
        mainContent.add(panel, BorderLayout.CENTER);
    }

    private JPanel miniCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(MainFrame.FONT_HEADING);
        valueLabel.setForeground(color);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(MainFrame.FONT_SMALL);
        titleLabel.setForeground(MainFrame.TEXT_GREY);

        card.add(valueLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(titleLabel);
        return card;
    }

    // ── Cast Vote ─────────────────────────────
    private void showCastVote() {
        if (LoginScreen.election == null) {
            showMsg("❌ No election has been setup yet."); return;
        }
        if (!LoginScreen.election.isOpen()) {
            showMsg("❌ Election is currently closed. Voting not allowed."); return;
        }
        if (voter.hasVoted()) {
            showMsg("✅ You have already cast your vote!"); return;
        }
        MainFrame.showScreen(new BallotScreen(voter, this));
    }

    // ── My Profile ────────────────────────────
    private void showProfile() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER),
                BorderFactory.createEmptyBorder(28, 36, 28, 36)));
        card.setMaximumSize(new Dimension(500, 400));
        card.setAlignmentX(CENTER_ALIGNMENT);

        JLabel icon = new JLabel("👤", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        card.add(icon);
        card.add(Box.createVerticalStrut(12));
        addProfileRow(card, "Full Name",     voter.getName());
        addProfileRow(card, "Age",           String.valueOf(voter.getAge()));
        addProfileRow(card, "Phone",         voter.getPhoneNumber());
        addProfileRow(card, "Voter ID",      voter.getVoterID());
        addProfileRow(card, "Constituency",  voter.getConstituencyID());
        addProfileRow(card, "Voted",         voter.hasVoted() ? "✅ Yes" : "❌ No");

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);

        mainContent.add(MainFrame.sectionLabel("👤 My Profile"), BorderLayout.NORTH);
        mainContent.add(wrapper, BorderLayout.CENTER);
    }

    private void addProfileRow(JPanel card, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(440, 36));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MainFrame.BORDER));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(MainFrame.FONT_SUBHEAD);
        labelComp.setForeground(MainFrame.TEXT_GREY);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(MainFrame.FONT_BODY);
        valueComp.setForeground(MainFrame.TEXT_DARK);

        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.EAST);

        card.add(row);
        card.add(Box.createVerticalStrut(8));
    }

    // ── My Constituency ───────────────────────
    private void showConstituency() {
        if (LoginScreen.election == null) {
            showMsg("❌ No election setup yet."); return;
        }

        ArrayList<Candidate> candidates =
                LoginScreen.election.getCandidatesByConstituency(voter.getConstituencyID());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel sub = MainFrame.infoLabel("Constituency ID: " + voter.getConstituencyID());
        sub.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(sub);
        panel.add(Box.createVerticalStrut(16));

        if (candidates.isEmpty()) {
            panel.add(MainFrame.infoLabel("No candidates found in your constituency."));
        } else {
            for (Candidate c : candidates) {
                panel.add(buildCandidateCard(c));
                panel.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(MainFrame.BG);

        mainContent.add(MainFrame.sectionLabel("📋 Candidates in Your Constituency"),
                BorderLayout.NORTH);
        mainContent.add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildCandidateCard(Candidate c) {
        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setBackground(MainFrame.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        card.setMaximumSize(new Dimension(700, 90));
        card.setAlignmentX(LEFT_ALIGNMENT);

        // Avatar
        JLabel avatar = new JLabel("👤");
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        // Info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel name = new JLabel(c.getName());
        name.setFont(MainFrame.FONT_SUBHEAD);
        name.setForeground(MainFrame.TEXT_DARK);

        JLabel details = new JLabel("ID: " + c.getCandidateID()
                + "   |   Party: " + c.getPartyID()
                + "   |   Age: " + c.getAge());
        details.setFont(MainFrame.FONT_SMALL);
        details.setForeground(MainFrame.TEXT_GREY);

        info.add(name);
        info.add(Box.createVerticalStrut(4));
        info.add(details);

        // Verified badge
        JLabel badge = new JLabel(c.isDualVerified() ? "✅ Dual Verified" : "");
        badge.setFont(MainFrame.FONT_SMALL);
        badge.setForeground(MainFrame.SUCCESS);

        card.add(avatar, BorderLayout.WEST);
        card.add(info,   BorderLayout.CENTER);
        card.add(badge,  BorderLayout.EAST);

        return card;
    }

    // ── Helper ────────────────────────────────
    private void showMsg(String msg) {
        JLabel label = new JLabel(msg, SwingConstants.CENTER);
        label.setFont(MainFrame.FONT_HEADING);
        label.setForeground(MainFrame.TEXT_GREY);
        mainContent.add(label, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }

    // Called by BallotScreen after voting
    public void refreshAfterVoting() {
        // ── Update top bar status label ────────────
        if (topVotedStatus != null) {
            topVotedStatus.setText(getVotedStatusText());
            topVotedStatus.setForeground(MainFrame.SUCCESS);
        }

        // ── Refresh main content ───────────────────
        mainContent.removeAll();
        showHome();
        mainContent.revalidate();
        mainContent.repaint();
    }
}