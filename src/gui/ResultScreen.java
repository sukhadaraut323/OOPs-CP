package gui;

import core.Result;
import election.GeneralElection;
import persons.Candidate;
import core.Party;
import core.Constituency;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ResultScreen extends JPanel {

    private GeneralElection election;
    private HashMap<String, Integer> partySeats = new HashMap<>();
    private ArrayList<Result> results = new ArrayList<>();

    public ResultScreen() {
        this.election = LoginScreen.election;
        setLayout(new BorderLayout());
        setBackground(MainFrame.BG);

        computeResults();

        add(buildTopBar(),     BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);
        add(buildBottomBar(),  BorderLayout.SOUTH);
    }

    // ── Compute Results ───────────────────────
    private void computeResults() {
        if (election == null) return;

        // Initialize party seats
        for (Party p : election.getParties()) {
            partySeats.put(p.getPartyID(), 0);
        }

        // Calculate winner per constituency
        for (Constituency constituency : election.getConstituencies()) {
            ArrayList<Candidate> constCandidates =
                    election.getCandidatesByConstituency(constituency.getConstituencyID());

            if (constCandidates.isEmpty()) continue;

            // Find winner
            Candidate winner = null;
            int maxVotes     = -1;
            int totalVotes   = 0;
            int secondMax    = 0;

            for (Candidate c : constCandidates) {
                totalVotes += c.getVoteCount();
                if (c.getVoteCount() > maxVotes) {
                    secondMax = maxVotes;
                    maxVotes  = c.getVoteCount();
                    winner    = c;
                }
            }

            if (winner != null) {
                Result result = new Result(
                        constituency.getConstituencyID(),
                        constituency.getConstituencyName(),
                        winner);
                result.setTotalVotesCast(totalVotes);
                result.setMarginOfVictory(maxVotes - secondMax);
                results.add(result);

                // Update party seats
                partySeats.put(winner.getPartyID(),
                        partySeats.getOrDefault(winner.getPartyID(), 0) + 1);
            }
        }
    }

    // ── Top Bar ───────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(MainFrame.PRIMARY);
        bar.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("📊 Election Results");
        title.setFont(MainFrame.FONT_TITLE);
        title.setForeground(MainFrame.WHITE);

        String elecName = election != null ? election.getElectionName() : "N/A";
        JLabel sub = new JLabel("Official Results — " + elecName);
        sub.setFont(MainFrame.FONT_SMALL);
        sub.setForeground(new Color(187, 222, 251));

        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);

        JButton backBtn = MainFrame.accentButton("← Back to Admin");
        backBtn.addActionListener(e ->
                MainFrame.showScreen(new AdminDashboard(LoginScreen.getAdmin())));

        bar.add(left,    BorderLayout.WEST);
        bar.add(backBtn, BorderLayout.EAST);

        return bar;
    }

    // ── Main Content ──────────────────────────
    private JPanel buildMainContent() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBackground(MainFrame.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Left — party seats + overall winner
        panel.add(buildLeftPanel(),  BorderLayout.WEST);

        // Right — constituency wise results table
        panel.add(buildRightPanel(), BorderLayout.CENTER);

        return panel;
    }

    // ── Left Panel ────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(280, 0));

        // Overall winner card
        panel.add(buildOverallWinnerCard());
        panel.add(Box.createVerticalStrut(16));

        // Party seats card
        panel.add(buildPartySeatsCard());
        panel.add(Box.createVerticalStrut(16));

        // Stats card
        panel.add(buildStatsCard());

        return panel;
    }

    // ── Overall Winner Card ───────────────────
    private JPanel buildOverallWinnerCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.PRIMARY);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.PRIMARY),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel trophy = new JLabel("🏆", SwingConstants.CENTER);
        trophy.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        trophy.setAlignmentX(CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Overall Winner");
        heading.setFont(MainFrame.FONT_SUBHEAD);
        heading.setForeground(new Color(187, 222, 251));
        heading.setAlignmentX(CENTER_ALIGNMENT);

        // Find winning party
        String winnerParty = "N/A";
        int maxSeats       = -1;
        String winnerID    = "";

        for (String partyID : partySeats.keySet()) {
            int seats = partySeats.get(partyID);
            if (seats > maxSeats) {
                maxSeats  = seats;
                winnerID  = partyID;
            }
        }

        // Get party name
        for (Party p : election.getParties()) {
            if (p.getPartyID().equals(winnerID)) {
                winnerParty = p.getPartyName();
                break;
            }
        }

        JLabel partyName = new JLabel(winnerParty);
        partyName.setFont(MainFrame.FONT_HEADING);
        partyName.setForeground(MainFrame.ACCENT);
        partyName.setAlignmentX(CENTER_ALIGNMENT);

        JLabel seats = new JLabel(maxSeats + " Seats Won");
        seats.setFont(MainFrame.FONT_BODY);
        seats.setForeground(MainFrame.WHITE);
        seats.setAlignmentX(CENTER_ALIGNMENT);

        card.add(trophy);
        card.add(Box.createVerticalStrut(8));
        card.add(heading);
        card.add(Box.createVerticalStrut(6));
        card.add(partyName);
        card.add(Box.createVerticalStrut(4));
        card.add(seats);

        return card;
    }

    // ── Party Seats Card ──────────────────────
    private JPanel buildPartySeatsCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        JLabel heading = MainFrame.sectionLabel("🏛️  Party-wise Seats");
        heading.setAlignmentX(LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(12));

        int totalSeats = results.size();

        for (Party p : election.getParties()) {
            int seats = partySeats.getOrDefault(p.getPartyID(), 0);
            card.add(buildPartyRow(p, seats, totalSeats));
            card.add(Box.createVerticalStrut(10));
        }

        return card;
    }

    // ── Party Row with Progress Bar ───────────
    private JPanel buildPartyRow(Party p, int seats, int totalSeats) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(240, 56));
        row.setAlignmentX(LEFT_ALIGNMENT);

        JPanel labelRow = new JPanel(new BorderLayout());
        labelRow.setOpaque(false);

        JLabel nameLabel = new JLabel(p.getPartySymbol() + "  " + p.getPartyName());
        nameLabel.setFont(MainFrame.FONT_SUBHEAD);
        nameLabel.setForeground(MainFrame.TEXT_DARK);

        JLabel seatLabel = new JLabel(seats + " seats");
        seatLabel.setFont(MainFrame.FONT_SMALL);
        seatLabel.setForeground(MainFrame.TEXT_GREY);

        labelRow.add(nameLabel, BorderLayout.WEST);
        labelRow.add(seatLabel, BorderLayout.EAST);

        // Progress bar
        JProgressBar bar = new JProgressBar(0, Math.max(totalSeats, 1));
        bar.setValue(seats);
        bar.setStringPainted(false);
        bar.setBackground(new Color(224, 229, 233));
        bar.setForeground(MainFrame.PRIMARY_LIGHT);
        bar.setBorderPainted(false);
        bar.setMaximumSize(new Dimension(240, 8));
        bar.setPreferredSize(new Dimension(240, 8));

        row.add(labelRow);
        row.add(Box.createVerticalStrut(4));
        row.add(bar);

        return row;
    }

    // ── Stats Card ────────────────────────────
    private JPanel buildStatsCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        JLabel heading = MainFrame.sectionLabel("📈 Statistics");
        heading.setAlignmentX(LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(12));

        int totalVoters  = LoginScreen.voters.size();
        int totalBallots = LoginScreen.evmVoting != null
                ? LoginScreen.evmVoting.getTotalVotesCast() : 0;
        int turnout      = totalVoters > 0
                ? (totalBallots * 100 / totalVoters) : 0;

        addStatRow(card, "Total Voters",       String.valueOf(totalVoters));
        addStatRow(card, "Votes Cast",         String.valueOf(totalBallots));
        addStatRow(card, "Voter Turnout",      turnout + "%");
        addStatRow(card, "Constituencies",     String.valueOf(results.size()));
        addStatRow(card, "Parties Contested",  String.valueOf(election.getParties().size()));

        return card;
    }

    private void addStatRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(240, 28));
        row.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, MainFrame.BORDER));

        JLabel l = new JLabel(label);
        l.setFont(MainFrame.FONT_SMALL);
        l.setForeground(MainFrame.TEXT_GREY);

        JLabel v = new JLabel(value);
        v.setFont(MainFrame.FONT_SUBHEAD);
        v.setForeground(MainFrame.PRIMARY);

        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        panel.add(row);
        panel.add(Box.createVerticalStrut(6));
    }

    // ── Right Panel — Constituency Table ──────
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JLabel heading = MainFrame.sectionLabel("📍 Constituency-wise Results");
        panel.add(heading, BorderLayout.NORTH);

        // Table
        String[] cols = {"Constituency", "Winner", "Party", "Votes Won", "Margin", "Total Votes"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Result r : results) {
            Candidate winner   = r.getWinner();
            String partyName   = getPartyName(winner.getPartyID());
            model.addRow(new Object[]{
                r.getConstituencyName(),
                winner.getName(),
                partyName,
                r.getWinningVotes(),
                r.getMarginOfVictory(),
                r.getTotalVotesCast()
            });
        }

        JTable table = new JTable(model);
        table.setFont(MainFrame.FONT_BODY);
        table.setRowHeight(34);
        table.getTableHeader().setFont(MainFrame.FONT_SUBHEAD);
        table.getTableHeader().setBackground(MainFrame.PRIMARY);
        table.getTableHeader().setForeground(MainFrame.WHITE);
        table.setGridColor(MainFrame.BORDER);
        table.setSelectionBackground(new Color(187, 222, 251));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        // Alternate row colors
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (sel) {
                    setBackground(new Color(187, 222, 251));
                } else if (row % 2 == 0) {
                    setBackground(MainFrame.WHITE);
                } else {
                    setBackground(new Color(245, 247, 250));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER));
        scroll.getViewport().setBackground(MainFrame.WHITE);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ── Bottom Bar ────────────────────────────
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        bar.setBackground(MainFrame.WHITE);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MainFrame.BORDER));

        JLabel note = new JLabel(
                "🔒 Results are final and certified by the Election Commission of India");
        note.setFont(MainFrame.FONT_SMALL);
        note.setForeground(MainFrame.TEXT_GREY);

        JButton printBtn = MainFrame.primaryButton("🖨️  Print Results");
        printBtn.addActionListener(e -> printResults());

        bar.add(note);
        bar.add(Box.createHorizontalStrut(20));
        bar.add(printBtn);

        return bar;
    }

    // ── Print Results ─────────────────────────
    private void printResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════\n");
        sb.append("       OFFICIAL ELECTION RESULTS     \n");
        sb.append("════════════════════════════════════\n\n");

        if (election != null) {
            sb.append("Election : ").append(election.getElectionName()).append("\n\n");
        }

        sb.append("CONSTITUENCY-WISE RESULTS:\n");
        sb.append("────────────────────────────────────\n");
        for (Result r : results) {
            sb.append("📍 ").append(r.getConstituencyName()).append("\n");
            sb.append("   Winner : ").append(r.getWinner().getName()).append("\n");
            sb.append("   Party  : ").append(getPartyName(r.getWinner().getPartyID())).append("\n");
            sb.append("   Votes  : ").append(r.getWinningVotes())
              .append(" / ").append(r.getTotalVotesCast()).append("\n");
            sb.append("   Margin : ").append(r.getMarginOfVictory()).append("\n\n");
        }

        sb.append("PARTY-WISE SEATS:\n");
        sb.append("────────────────────────────────────\n");
        for (Party p : election.getParties()) {
            sb.append("  ").append(p.getPartyName())
              .append(" : ").append(partySeats.getOrDefault(p.getPartyID(), 0))
              .append(" seats\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);

        JOptionPane.showMessageDialog(this,
                new JScrollPane(textArea),
                "Election Results Report",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Helper ────────────────────────────────
    private String getPartyName(String partyID) {
        for (Party p : election.getParties()) {
            if (p.getPartyID().equals(partyID)) return p.getPartyName();
        }
        return "Independent";
    }
}