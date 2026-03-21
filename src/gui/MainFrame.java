package gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    // ── Brand Colors ──────────────────────────
    public static final Color PRIMARY       = new Color(13, 71, 161);   // deep blue
    public static final Color PRIMARY_LIGHT = new Color(25, 118, 210);  // medium blue
    public static final Color ACCENT        = new Color(255, 152, 0);   // orange accent
    public static final Color BG            = new Color(245, 247, 250); // off white bg
    public static final Color WHITE         = Color.WHITE;
    public static final Color TEXT_DARK     = new Color(18, 18, 18);
    public static final Color TEXT_GREY     = new Color(100, 100, 100);
    public static final Color SUCCESS       = new Color(46, 125, 50);
    public static final Color DANGER        = new Color(183, 28, 28);
    public static final Color BORDER        = new Color(207, 216, 220);

    // ── Fonts ─────────────────────────────────
    public static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADING   = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_SUBHEAD   = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_BODY      = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL     = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BTN       = new Font("Segoe UI", Font.BOLD,  13);

    private static MainFrame instance;
    private JPanel contentPanel;

    public MainFrame() {
        instance = this;
        setTitle("🗳️  Election Simulator — India");
        setSize(1000, 680);
        setMinimumSize(new Dimension(900, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        // ── Top Header Bar ─────────────────────
        JPanel header = buildHeader();
        add(header, BorderLayout.NORTH);

        // ── Content Panel (screens swap here) ──
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG);
        add(contentPanel, BorderLayout.CENTER);

        // ── Footer ─────────────────────────────
        JPanel footer = buildFooter();
        add(footer, BorderLayout.SOUTH);

        // ── Show login screen first ─────────────
        showScreen(new LoginScreen());

        setVisible(true);
    }

    // ── Header Bar ────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setPreferredSize(new Dimension(1000, 64));
        header.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        // Left — logo + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel logo = new JLabel("🗳️");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("ELECTION SIMULATOR");
        title.setFont(FONT_TITLE);
        title.setForeground(WHITE);

        JLabel subtitle = new JLabel("Electronic Voting Management System");
        subtitle.setFont(FONT_SMALL);
        subtitle.setForeground(new Color(187, 222, 251));

        titleBlock.add(title);
        titleBlock.add(subtitle);

        left.add(logo);
        left.add(titleBlock);

        // Right — badge
        JLabel badge = new JLabel("  SECURE & VERIFIED  ");
        badge.setFont(FONT_SMALL);
        badge.setForeground(PRIMARY);
        badge.setBackground(ACCENT);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        header.add(left,  BorderLayout.WEST);
        header.add(badge, BorderLayout.EAST);

        // Vertical centering
        header.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        return header;
    }

    // ── Footer Bar ────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(224, 229, 233));
        footer.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JLabel text = new JLabel("Election Commission of India  •  Powered by Election Simulator  •  Secure EVM System");
        text.setFont(FONT_SMALL);
        text.setForeground(TEXT_GREY);
        footer.add(text);

        return footer;
    }

    // ── Screen Switcher ───────────────────────
    public static void showScreen(JPanel screen) {
        instance.contentPanel.removeAll();
        instance.contentPanel.add(screen, BorderLayout.CENTER);
        instance.contentPanel.revalidate();
        instance.contentPanel.repaint();
    }

    // ── Reusable UI Components ────────────────

    // Primary button (blue)
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(PRIMARY_LIGHT);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        return btn;
    }

    // Accent button (orange)
    public static JButton accentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(ACCENT);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        return btn;
    }

    // Danger button (red)
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(DANGER);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        return btn;
    }

    // Success button (green)
    public static JButton successButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(SUCCESS);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        return btn;
    }

    // Styled text field
    public static JTextField styledField(int cols) {
        JTextField field = new JTextField(cols);
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return field;
    }

    // Styled password field
    public static JPasswordField styledPasswordField(int cols) {
        JPasswordField field = new JPasswordField(cols);
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return field;
    }

    // Card panel (white box with border)
    public static JPanel cardPanel() {
        JPanel card = new JPanel();
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));
        return card;
    }

    // Section label
    public static JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_HEADING);
        label.setForeground(PRIMARY);
        return label;
    }

    // Info label
    public static JLabel infoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_GREY);
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}