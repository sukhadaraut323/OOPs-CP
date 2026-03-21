package gui;

import core.Constituency;
import persons.Voter;
import utils.Validator;

import javax.swing.*;
import java.awt.*;

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

        // Title
        JLabel title = new JLabel("Voter Registration");
        title.setFont(MainFrame.FONT_TITLE);
        title.setForeground(MainFrame.PRIMARY);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Register to participate in the election");
        subtitle.setFont(MainFrame.FONT_BODY);
        subtitle.setForeground(MainFrame.TEXT_GREY);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        // Form
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

        // Hints
        JLabel hint1 = hintLabel("Voter ID: 10 alphanumeric chars (e.g. ABC1234567)");
        JLabel hint2 = hintLabel("Aadhaar: 12 digits  |  PAN: ABCDE1234F format");
        hint1.setAlignmentX(CENTER_ALIGNMENT);
        hint2.setAlignmentX(CENTER_ALIGNMENT);

        // Error label
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(MainFrame.FONT_SMALL);
        errorLabel.setForeground(MainFrame.DANGER);
        errorLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Register button
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

            // Empty check
            if (name.isEmpty() || ageStr.isEmpty() || phone.isEmpty()
                    || voterID.isEmpty() || document.isEmpty()
                    || constituency.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                return;
            }

            // Name check
            if (!Validator.isValidName(name)) {
                errorLabel.setText("Name must contain only letters (min 3 chars).");
                return;
            }

            // Age check
            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException ex) {
                errorLabel.setText("Age must be a valid number.");
                return;
            }
            if (!Validator.isValidAge(age)) {
                errorLabel.setText("You must be between 18 and 120 years old.");
                return;
            }

            // Phone check
            if (!Validator.isValidPhone(phone)) {
                errorLabel.setText("Phone: 10 digits starting with 6-9.");
                return;
            }

            // VoterID format check
            if (!Validator.isValidVoterID(voterID)) {
                errorLabel.setText(Validator.voterIDError());
                return;
            }

            // VoterID duplicate check
            for (Voter v : LoginScreen.voters) {
                if (v.getVoterID().equalsIgnoreCase(voterID)) {
                    errorLabel.setText("This Voter ID is already registered.");
                    return;
                }
            }

            // Document check
            if (!Validator.isValidDocument(document)) {
                errorLabel.setText(Validator.documentError());
                return;
            }

            // Election check
            if (LoginScreen.election == null) {
                errorLabel.setText("No election scheduled yet. Contact admin.");
                return;
            }

            // Constituency check
            Constituency c =
                    LoginScreen.election.getConstituencyByID(constituency);
            if (c == null) {
                errorLabel.setText("Constituency ID not found. Check with admin.");
                return;
            }

            // All good — register voter
            c.addVoter(voterID);
            Voter newVoter = new Voter(name, age, phone,
                    voterID, document, constituency);
            LoginScreen.voters.add(newVoter);

            JOptionPane.showMessageDialog(this,
                    "Registration successful!\n"
                    + "Your Voter ID: " + voterID + "\n"
                    + "Constituency : " + constituency + "\n\n"
                    + "You can now login to cast your vote.",
                    "Registered Successfully",
                    JOptionPane.INFORMATION_MESSAGE);

            MainFrame.showScreen(new LoginScreen());
        });

        // Back button
        JButton backBtn = new JButton("Back to Login");
        backBtn.setFont(MainFrame.FONT_SMALL);
        backBtn.setForeground(MainFrame.PRIMARY_LIGHT);
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setAlignmentX(CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> MainFrame.showScreen(new LoginScreen()));

        // Assemble
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