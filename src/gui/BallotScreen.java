package gui;

import core.Ballot;
import persons.Candidate;
import persons.Voter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BallotScreen extends JPanel {

    private Voter voter;
    private VoterDashboard voterDashboard;
    private ArrayList<Candidate> candidates;
    private Candidate selectedCandidate = null;
    private ArrayList<JPanel> candidateCards = new ArrayList<>();

    public BallotScreen(Voter voter, VoterDashboard dashboard) {
        this.voter         = voter;
        this.voterDashboard = dashboard;
        this.candidates    = LoginScreen.election != null
                ? LoginScreen.election.getCandidatesByConstituency(voter.getConstituencyID())
                : new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(MainFrame.BG);

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildBallotArea(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    // ── Top Bar ───────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(MainFrame.PRIMARY);
        bar.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("🗳️  Electronic Voting Machine (EVM)");
        title.setFont(MainFrame.FONT_HEADING);
        title.setForeground(MainFrame.WHITE);

        JLabel sub = new JLabel("Voter: " + voter.getName()
                + "  |  Constituency: " + voter.getConstituencyID());
        sub.setFont(MainFrame.FONT_SMALL);
        sub.setForeground(new Color(187, 222, 251));

        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);

        JButton backBtn = MainFrame.accentButton("← Back");
        backBtn.addActionListener(e ->
                MainFrame.showScreen(voterDashboard));

        bar.add(left,    BorderLayout.WEST);
        bar.add(backBtn, BorderLayout.EAST);

        return bar;
    }

    // ── Ballot Area ───────────────────────────
    private JPanel buildBallotArea() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(MainFrame.BG);

        JPanel ballotBox = new JPanel();
        ballotBox.setLayout(new BoxLayout(ballotBox, BoxLayout.Y_AXIS));
        ballotBox.setBackground(MainFrame.WHITE);
        ballotBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER),
                BorderFactory.createEmptyBorder(24, 32, 24, 32)));
        ballotBox.setPreferredSize(new Dimension(600, 420));

        // Ballot header
        JLabel ballotTitle = new JLabel("OFFICIAL BALLOT — General Election");
        ballotTitle.setFont(MainFrame.FONT_HEADING);
        ballotTitle.setForeground(MainFrame.PRIMARY);
        ballotTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel instruction = new JLabel("Select one candidate by clicking on their name");
        instruction.setFont(MainFrame.FONT_SMALL);
        instruction.setForeground(MainFrame.TEXT_GREY);
        instruction.setAlignmentX(CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(540, 2));
        sep.setForeground(MainFrame.BORDER);

        ballotBox.add(ballotTitle);
        ballotBox.add(Box.createVerticalStrut(4));
        ballotBox.add(instruction);
        ballotBox.add(Box.createVerticalStrut(12));
        ballotBox.add(sep);
        ballotBox.add(Box.createVerticalStrut(16));

        if (candidates.isEmpty()) {
            JLabel noCandidate = new JLabel("No candidates in your constituency.");
            noCandidate.setFont(MainFrame.FONT_BODY);
            noCandidate.setForeground(MainFrame.TEXT_GREY);
            noCandidate.setAlignmentX(CENTER_ALIGNMENT);
            ballotBox.add(noCandidate);
        } else {
            for (int i = 0; i < candidates.size(); i++) {
                Candidate c = candidates.get(i);
                JPanel card = buildCandidateRow(i + 1, c);
                candidateCards.add(card);
                ballotBox.add(card);
                ballotBox.add(Box.createVerticalStrut(10));
            }
        }

        wrapper.add(ballotBox);
        return wrapper;
    }

    // ── Candidate Row ─────────────────────────
    private JPanel buildCandidateRow(int num, Candidate c) {
        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setBackground(MainFrame.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)));
        card.setMaximumSize(new Dimension(540, 72));
        card.setAlignmentX(CENTER_ALIGNMENT);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Number
        JLabel numLabel = new JLabel(String.valueOf(num));
        numLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        numLabel.setForeground(MainFrame.TEXT_GREY);
        numLabel.setPreferredSize(new Dimension(28, 0));

        // Info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel name = new JLabel(c.getName());
        name.setFont(MainFrame.FONT_SUBHEAD);
        name.setForeground(MainFrame.TEXT_DARK);

        JLabel details = new JLabel("Party: " + c.getPartyID()
                + "   ID: " + c.getCandidateID()
                + (c.isDualVerified() ? "   ✅ Verified" : ""));
        details.setFont(MainFrame.FONT_SMALL);
        details.setForeground(MainFrame.TEXT_GREY);

        info.add(name);
        info.add(Box.createVerticalStrut(3));
        info.add(details);

        // Selection indicator
        JLabel selectIcon = new JLabel("○");
        selectIcon.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        selectIcon.setForeground(MainFrame.TEXT_GREY);

        card.add(numLabel,    BorderLayout.WEST);
        card.add(info,        BorderLayout.CENTER);
        card.add(selectIcon,  BorderLayout.EAST);

        // Click to select
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectCandidate(c, card, selectIcon);
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (selectedCandidate != c)
                    card.setBackground(new Color(227, 242, 253));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (selectedCandidate != c)
                    card.setBackground(MainFrame.WHITE);
            }
        });

        return card;
    }

    // ── Select Candidate ──────────────────────
    private void selectCandidate(Candidate c, JPanel card, JLabel icon) {
        // Check dual verify
        if (isDuplicateInConstituency(c) && !c.isDualVerified()) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ This candidate is not dual verified yet.\nVoting not allowed.",
                    "Verification Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Reset all cards
        for (JPanel p : candidateCards) {
            p.setBackground(MainFrame.WHITE);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(MainFrame.BORDER),
                    BorderFactory.createEmptyBorder(14, 20, 14, 20)));
            // Reset icons
            Component[] comps = p.getComponents();
            for (Component comp : comps) {
                if (comp instanceof JLabel lbl && lbl.getText().equals("●")) {
                    lbl.setText("○");
                    lbl.setForeground(MainFrame.TEXT_GREY);
                }
            }
        }

        // Highlight selected
        selectedCandidate = c;
        card.setBackground(new Color(227, 242, 253));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.PRIMARY, 2),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)));
        icon.setText("●");
        icon.setForeground(MainFrame.PRIMARY);
    }

    // ── Bottom Bar ────────────────────────────
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        bar.setBackground(MainFrame.WHITE);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MainFrame.BORDER));

        JLabel note = new JLabel("⚠️  Your vote is final and cannot be changed once submitted.");
        note.setFont(MainFrame.FONT_SMALL);
        note.setForeground(MainFrame.TEXT_GREY);

        JButton cancelBtn = MainFrame.dangerButton("Cancel");
        cancelBtn.addActionListener(e -> MainFrame.showScreen(voterDashboard));

        JButton voteBtn = MainFrame.successButton("✅  Cast My Vote");
        voteBtn.addActionListener(e -> castVote());

        bar.add(note);
        bar.add(Box.createHorizontalStrut(20));
        bar.add(cancelBtn);
        bar.add(voteBtn);

        return bar;
    }

    // ── Cast Vote ─────────────────────────────
    private void castVote() {
        if (selectedCandidate == null) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Please select a candidate first!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirm dialog
        int confirm = JOptionPane.showConfirmDialog(this,
                "You are voting for:\n\n"
                + "  Name  : " + selectedCandidate.getName() + "\n"
                + "  Party : " + selectedCandidate.getPartyID() + "\n"
                + "  ID    : " + selectedCandidate.getCandidateID() + "\n\n"
                + "This action cannot be undone. Confirm?",
                "Confirm Vote", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        // Cast vote
        voter.castVote(selectedCandidate.getCandidateID());
        selectedCandidate.incrementVote();

        // Generate ballot
        String ballotID = "BAL" + String.format("%04d",
                LoginScreen.evmVoting != null
                        ? LoginScreen.evmVoting.getTotalVotesCast() + 1 : 1);

        Ballot ballot = new Ballot(ballotID, voter.getVoterID(),
                selectedCandidate.getCandidateID(), voter.getConstituencyID());

        if (LoginScreen.evmVoting != null) {
            LoginScreen.evmVoting.getBallots().add(ballot);
        }

        // Show receipt
        showVoteReceipt(ballot);
    }

    // ── Vote Receipt ──────────────────────────
    private void showVoteReceipt(Ballot ballot) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Vote Cast Successfully");
        dialog.setModal(true);
        dialog.setSize(400, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(MainFrame.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JLabel successIcon = new JLabel("✅", SwingConstants.CENTER);
        successIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        successIcon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel successTitle = new JLabel("Vote Cast Successfully!");
        successTitle.setFont(MainFrame.FONT_HEADING);
        successTitle.setForeground(MainFrame.SUCCESS);
        successTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Thank you for participating in the election.");
        sub.setFont(MainFrame.FONT_SMALL);
        sub.setForeground(MainFrame.TEXT_GREY);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(340, 2));

        addReceiptRow(content, "Ballot ID",    ballot.getBallotID());
        addReceiptRow(content, "Voter ID",     ballot.getVoterID());
        addReceiptRow(content, "Candidate",    selectedCandidate.getName());
        addReceiptRow(content, "Party",        selectedCandidate.getPartyID());
        addReceiptRow(content, "Timestamp",    ballot.getTimestamp());

        JButton doneBtn = MainFrame.successButton("Done");
        doneBtn.setAlignmentX(CENTER_ALIGNMENT);
        doneBtn.addActionListener(e -> {
            dialog.dispose();
            voterDashboard.refreshAfterVoting();
            MainFrame.showScreen(voterDashboard);
        });

        content.add(successIcon);
        content.add(Box.createVerticalStrut(6));
        content.add(successTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(sub);
        content.add(Box.createVerticalStrut(16));
        content.add(sep);
        content.add(Box.createVerticalStrut(12));
        content.add(doneBtn);

        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void addReceiptRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(340, 28));

        JLabel l = new JLabel(label + ":");
        l.setFont(MainFrame.FONT_SMALL);
        l.setForeground(MainFrame.TEXT_GREY);

        JLabel v = new JLabel(value);
        v.setFont(MainFrame.FONT_SUBHEAD);
        v.setForeground(MainFrame.TEXT_DARK);

        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        panel.add(row);
        panel.add(Box.createVerticalStrut(6));
    }

    private boolean isDuplicateInConstituency(Candidate target) {
        int count = 0;
        for (Candidate c : candidates) {
            if (c.getName().equalsIgnoreCase(target.getName())) count++;
        }
        return count > 1;
    }
}