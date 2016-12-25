package com.smotricz.pinger;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

/**
 * One element in the HostPane's list of host specs.
 */
public class HostWidget extends JPanel {

    public final Host host;

    private JTextField tfName  = new JTextField("");
    private JLabel lblAddr = new JLabel("");

    private final AbstractAction upAction = new AbstractAction("Up") {
        public void actionPerformed(ActionEvent actionEvent) {
            Hosts.up(host);
        }
    };

    private final AbstractAction downAction = new AbstractAction("Down") {
        public void actionPerformed(ActionEvent actionEvent) {
        	Hosts.down(host);
        }
    };

    private AbstractAction lookupAction = new AbstractAction("Lookup") {
        public void actionPerformed(ActionEvent actionEvent) {
        	host.doLookup();
        }
    };

    private final AbstractAction deleteAction = new AbstractAction("Delete...") {
        public void actionPerformed(ActionEvent actionEvent) {
        	if (JOptionPane.showConfirmDialog(
	        			HostWidget.this, 
	        			"Delete host " + host.name + "?",
	        			"Confirm",
	        			JOptionPane.OK_CANCEL_OPTION)
	        		== JOptionPane.OK_OPTION) {
        		Hosts.remove(host);
        	}
        }
    };

    private final AbstractAction hideAction = new AbstractAction("Hide") {
        public void actionPerformed(ActionEvent actionEvent) {
        	host.setHidden(cbxHide.isSelected());
        	Hosts.informListeners();
        }
    };
    
    private final FocusAdapter nameFocusAdapter = new FocusAdapter() {
		public void focusLost(FocusEvent e) {
			host.setName(tfName.getText());
		}
    };

    private JButton btnUp = new JButton(this.upAction);
    private JButton btnDown = new JButton(this.downAction);
    private JButton btnLookup = new JButton(this.lookupAction);
    private JButton btnDelete = new JButton(this.deleteAction);
    private JCheckBox cbxHide = new JCheckBox(this.hideAction);


    /**
     * Constructor.
     * @param spec
     * @param hostPane
     */
    public HostWidget(Host host, HostPane hostPane) {
        super(new GridBagLayout());
        this.host = host;

        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.1;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Up
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(btnUp, gbc);

        // Down
        gbc.gridy = 1;
        add(btnDown, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // Hostname
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        add(new JLabel("Hostname:"), gbc);

        // Name field
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(tfName, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;

        // Filler
        gbc.gridx = 4;
        gbc.weightx = 10.0;
        add(new Label(""), gbc);
        gbc.weightx = 1.0;

        // Hide
        gbc.gridx = 5;
        add(cbxHide, gbc);

        // Lookup
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(btnLookup, gbc);

        // IP Addr label
        gbc.gridx = 2;
        add(new JLabel("IP addr:"), gbc);

        // IP Addr display
        lblAddr.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        lblAddr.setText("888.888.888.888");
        Dimension minSz = lblAddr.getMinimumSize();
        lblAddr.setText("?.?.?.?");
        lblAddr.setPreferredSize(minSz);
        lblAddr.setMinimumSize(minSz);
        gbc.gridx = 3;
        add(lblAddr, gbc);

        // Delete
        gbc.gridx = 5;
        add(btnDelete, gbc);

        // Set attributes
        cbxHide.setSelected(host.hidden);
        tfName.setText(host.name);
        lblAddr.setText(host.addressString());
        
        tfName.addFocusListener(nameFocusAdapter);
    }
    

    @Override
	public Dimension getPreferredSize() {
    	return new Dimension(
    			super.getPreferredSize().width,
    			super.getMinimumSize().height);
	}

    @Override
	public Dimension getMaximumSize() {
    	return new Dimension(
    			super.getMinimumSize().width,
    			super.getMinimumSize().height);
	}

    // #################### The plumbing ####################


	/** This lets HostPane tell us we're the first host widget. */
    public void markFirst() {
    	this.btnUp.setEnabled(false);
    }
    
    /** This lets HostPane tell us we're the last host widget. */
    public void markLast() {
    	this.btnDown.setEnabled(false);
    }
    
    /**
     * Focus on the host name after an add().
     */
    public void focusOnName() {
        this.tfName.requestFocus();
    }

}
