package gui;

import core.Constituency;
import core.DocumentVerifier;
import persons.Voter;
import utils.Validator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RegisterScreen extends JPanel {

    public RegisterScreen() {
        setLayout(new BorderLayout());
        setBackground(MainFrame.BG);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(MainFrame.BG);
        center.add(buildRegisterCard());
        add(center, BorderLayout.CENTER);
    }

    private JPanel buildRegisterCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER, 1),
                BorderFactory.createEmptyBorder(36, 48, 36, 48)));
        card.setPreferredSize(new Dimension(520, 620));

        // ── Title ──────────────────────────────
        JLabel title = new JLabel("Voter Registration");
        title.setFont(MainFrame.FONT_TITLE);
        title.setForeground(MainFrame.PRIMARY);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Register to participate in the election");
        subtitle.setFont(MainFrame.FONT_BODY);
        subtitle.setForeground(MainFrame.TEXT_GREY);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        // ── Form ───────────────────────────────
        JPanel form = new JPanel(new GridLayout(0, 2, 16, 12));
        form.setOpaque(false);
        form.setMaximumSize(new Dimension(430, 500));
        form.setAlignmentX(CENTER_ALIGNMENT);

        JTextField nameField         = MainFrame.styledField(15);
        JTextField ageField          = MainFrame.styledField(15);
        JTextField phoneField        = MainFrame.styledField(15);
        JTextField voterIDField      = MainFrame.styledField(15);
        JTextField documentField     = MainFrame.styledField(15);
        JTextField constituencyField = MainFrame.styledField(15);

        form.add(formLabel("Full Name"));
        form.add(nameField);
        form.add(formLabel("Age"));
        form.add(ageField);
        form.add(formLabel("Phone Number"));
        form.add(phoneField);
        form.add(formLabel("Voter ID"));
        form.add(voterIDField);
        form.add(formLabel("Aadhaar / PAN"));
        form.add(documentField);
        form.add(formLabel("Constituency ID"));
        form.add(constituencyField);

        // ── Hints ──────────────────────────────
        JLabel hint1 = hintLabel(
                "Voter ID: 10 alphanumeric chars (e.g. ABC1234567)");
        JLabel hint2 = hintLabel(
                "Aadhaar: 12 digits  |  PAN: ABCDE1234F format");
        hint1.setAlignmentX(CENTER_ALIGNMENT);
        hint2.setAlignmentX(CENTER_ALIGNMENT);

        // ── Error label ────────────────────────
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(MainFrame.FONT_SMALL);
        errorLabel.setForeground(MainFrame.DANGER);
        errorLabel.setAlignmentX(CENTER_ALIGNMENT);

        // ── Register button ────────────────────
        JButton registerBtn = MainFrame.primaryButton("Register Now");
        registerBtn.setAlignmentX(CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(430, 44));

        registerBtn.addActionListener(e -> {
            String name         = nameField.getText().trim();
            String ageStr       = ageField.getText().trim();
            String phone        = phoneField.getText().trim();
            String voterID      = voterIDField.getText().trim();
            String document     = documentField.getText().trim();
            String constituency = constituencyField.getText().trim();

            // ── Step 1: Empty check ────────────
            if (name.isEmpty() || ageStr.isEmpty() || phone.isEmpty()
                    || voterID.isEmpty() || document.isEmpty()
                    || constituency.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                return;
            }

            // ── Step 2: Name check ─────────────
            if (!Validator.isValidName(name)) {
                errorLabel.setText(
                        "Name must contain only letters (min 3 chars).");
                return;
            }

            // ── Step 3: Age check ──────────────
            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException ex) {
                errorLabel.setText("Age must be a valid number.");
                return;
            }
            if (!Validator.isValidAge(age)) {
                errorLabel.setText(
                        "You must be between 18 and 120 years old.");
                return;
            }

            // ── Step 4: Phone check ────────────
            if (!Validator.isValidPhone(phone)) {
                errorLabel.setText("Phone: 10 digits starting with 6-9.");
                return;
            }

            // ── Step 5: VoterID format check ───
            if (!Validator.isValidVoterID(voterID)) {
                errorLabel.setText(Validator.voterIDError());
                return;
            }

            // ── Step 6: VoterID duplicate check
            for (Voter v : LoginScreen.voters) {
                if (v.getVoterID().equalsIgnoreCase(voterID)) {
                    errorLabel.setText("This Voter ID is already registered.");
                    return;
                }
            }

            // ── Step 7: Document check ─────────
            if (!Validator.isValidDocument(document)) {
                errorLabel.setText(Validator.documentError());
                return;
            }

            // ── Step 8: Election check ─────────
            if (LoginScreen.election == null) {
                errorLabel.setText(
                        "No election scheduled yet. Contact admin.");
                return;
            }

            // ── Step 9: Constituency check ─────
            Constituency c =
                    LoginScreen.election.getConstituencyByID(constituency);
            if (c == null) {
                errorLabel.setText(
                        "Constituency ID not found. Check with admin.");
                return;
            }

            // ── Step 10: Duplicate NAME check ──
            // Only runs after all validations pass
            if (DocumentVerifier.isDuplicateVoterName(
                    name, LoginScreen.voters)) {

                ArrayList<Voter> duplicates =
                        DocumentVerifier.getDuplicateVoters(
                                name, LoginScreen.voters);
                Voter existingVoter = duplicates.get(0);

                // Warn and ask to proceed
                int proceed = JOptionPane.showConfirmDialog(null,
                        "Another voter with the same name exists:\n\n"
                        + "  Name     : " + existingVoter.getName() + "\n"
                        + "  Voter ID : " + existingVoter.getVoterID() + "\n\n"
                        + "Dual document verification is required.\n"
                        + "The original person will be determined by document match.",
                        "Duplicate Name Detected",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (proceed != JOptionPane.OK_OPTION) {
                    errorLabel.setText("Registration cancelled.");
                    return;
                }

                // Re-verify existing voter
                JOptionPane.showMessageDialog(null,
                        "Step 1 of 2: Re-verify the EXISTING voter.\n"
                        + "Name: " + existingVoter.getName()
                        + "  |  Voter ID: " + existingVoter.getVoterID(),
                        "Verify Existing Voter",
                        JOptionPane.INFORMATION_MESSAGE);

                String exVoterID = JOptionPane.showInputDialog(null,
                        "Enter EXISTING voter's Voter ID:");
                if (exVoterID == null) {
                    errorLabel.setText("Registration cancelled.");
                    return;
                }

                String exDocument = JOptionPane.showInputDialog(null,
                        "Enter EXISTING voter's Aadhaar / PAN:");
                if (exDocument == null) {
                    errorLabel.setText("Registration cancelled.");
                    return;
                }

                // Verify new voter
                JOptionPane.showMessageDialog(null,
                        "Step 2 of 2: Verify the NEW voter.\n"
                        + "Name: " + name
                        + "  |  Voter ID: " + voterID,
                        "Verify New Voter",
                        JOptionPane.INFORMATION_MESSAGE);

                String newVoterID2 = JOptionPane.showInputDialog(null,
                        "Enter NEW voter's Voter ID:");
                if (newVoterID2 == null) {
                    errorLabel.setText("Registration cancelled.");
                    return;
                }

                String newDocument = JOptionPane.showInputDialog(null,
                        "Enter NEW voter's Aadhaar / PAN:");
                if (newDocument == null) {
                    errorLabel.setText("Registration cancelled.");
                    return;
                }

                // Create new voter object for verification
                Voter newVoterObj = new Voter(name, age, phone,
                        voterID, document, constituency);

                // Run dual verification
                String result = DocumentVerifier.dualVerifyVoters(
                        existingVoter, exVoterID, exDocument,
                        newVoterObj, newVoterID2, newDocument);

                switch (result) {
                    case "FAILED" -> {
                        JOptionPane.showMessageDialog(null,
                                "Dual verification FAILED.\n"
                                + "Documents do not match for either voter.\n"
                                + "Registration rejected.",
                                "Verification Failed",
                                JOptionPane.ERROR_MESSAGE);
                        errorLabel.setText(
                                "Verification failed. Registration rejected.");
                        return;
                    }
                    case "SAME" -> {
                        JOptionPane.showMessageDialog(null,
                                "Same person detected!\n"
                                + "You are already registered as a voter.\n"
                                + "Duplicate registration rejected.",
                                "Already Registered",
                                JOptionPane.ERROR_MESSAGE);
                        errorLabel.setText("Already registered. Rejected.");
                        return;
                    }
                    case "EXISTING" -> {
                        JOptionPane.showMessageDialog(null,
                                "Result: EXISTING voter is the original.\n\n"
                                + "New registration does not match genuine documents.\n"
                                + "New registration BLOCKED.",
                                "Registration Blocked",
                                JOptionPane.ERROR_MESSAGE);
                        errorLabel.setText(
                                "Blocked. Existing voter is the original.");
                        return;
                    }
                    case "NEW" -> {
                        JOptionPane.showMessageDialog(null,
                                "Result: NEW voter is the original.\n\n"
                                + "Existing registration had incorrect documents.\n"
                                + "Existing voter removed. New voter will be registered.",
                                "Existing Voter Removed",
                                JOptionPane.WARNING_MESSAGE);

                        // Remove existing voter
                        LoginScreen.voters.remove(existingVoter);

                        // Remove from constituency
                        Constituency existingConst = LoginScreen.election
                                .getConstituencyByID(
                                        existingVoter.getConstituencyID());
                        if (existingConst != null) {
                            existingConst.getVoterIDs()
                                    .remove(existingVoter.getVoterID());
                        }
                        // Fall through to register new voter
                    }
                    case "BOTH" -> {
                        JOptionPane.showMessageDialog(null,
                                "Result: BOTH voters are genuine originals.\n\n"
                                + "Documents are completely different.\n"
                                + "New voter registration approved.",
                                "Both Verified",
                                JOptionPane.INFORMATION_MESSAGE);
                        // Fall through to register new voter
                    }
                }
            }

            // ── Step 11: Register voter ────────
            // Reaches here if:
            // - No duplicate name (normal registration)
            // - Duplicate but result was NEW or BOTH
            c.addVoter(voterID);
            Voter newVoter = new Voter(name, age, phone,
                    voterID, document, constituency);
            LoginScreen.voters.add(newVoter);

            JOptionPane.showMessageDialog(null,
                    "Registration successful!\n\n"
                    + "  Name          : " + name + "\n"
                    + "  Voter ID      : " + voterID + "\n"
                    + "  Constituency  : " + constituency + "\n\n"
                    + "You can now login to cast your vote.",
                    "Registered Successfully",
                    JOptionPane.INFORMATION_MESSAGE);

            MainFrame.showScreen(new LoginScreen());
        });

        // ── Back button ────────────────────────
        JButton backBtn = new JButton("Back to Login");
        backBtn.setFont(MainFrame.FONT_SMALL);
        backBtn.setForeground(MainFrame.PRIMARY_LIGHT);
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setAlignmentX(CENTER_ALIGNMENT);
        backBtn.addActionListener(e ->
                MainFrame.showScreen(new LoginScreen()));

        // ── Assemble card ──────────────────────
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));
        card.add(form);
        card.add(Box.createVerticalStrut(8));
        card.add(hint1);
        card.add(Box.createVerticalStrut(2));
        card.add(hint2);
        card.add(Box.createVerticalStrut(6));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(backBtn);

        return card;
    }

    private JLabel formLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(MainFrame.FONT_SUBHEAD);
        label.setForeground(MainFrame.TEXT_DARK);
        return label;
    }

    private JLabel hintLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(MainFrame.FONT_SMALL);
        label.setForeground(MainFrame.TEXT_GREY);
        return label;
    }
}