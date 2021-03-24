package com.hyper.dialog.options;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.hyper.components.rr.Canvas2D;

public class OptionDialog2D extends JDialog implements ActionListener {
	public static final Dimension MINIMUM_SIZE = new Dimension(300, 200);

	private JPanel mainPanel = new JPanel(),
			formContainer = new JPanel(),
			vWinPane = new JPanel(), vWinEdit = new JPanel(),
			gridEdit = new JPanel(),
			confirmPane = new JPanel();

	private JTextField xMinEdit = new JTextField(), xMaxEdit = new JTextField(),
			yMinEdit = new JTextField(), yMaxEdit = new JTextField(),
			gridXEdit = new JTextField(), gridYEdit = new JTextField();

	private JCheckBox showGrid = new JCheckBox();

	private JButton ok = new JButton("OK"), apply = new JButton("Appliquer"), cancel = new JButton("Annuler");

	private JLabel error = new JLabel(),
			x = new JLabel("x"), y = new JLabel("y"),
			gridX = new JLabel("x"), gridY = new JLabel("y"), showGridLabel = new JLabel("Grille");

	private Canvas2D parent;

	public OptionDialog2D(JFrame window, Canvas2D canvas) {
		super(window);
		this.setMinimumSize(MINIMUM_SIZE);
		this.parent = canvas;

		vWinPane.setBorder(new TitledBorder("View window"));
		vWinPane.setPreferredSize(new Dimension(100, 100));
		vWinPane.setMinimumSize(new Dimension(100, 100));
		formContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
		mainPanel.setLayout(new BorderLayout());
		vWinPane.setLayout(new BorderLayout());
		vWinEdit.setLayout(new GridLayout(3, 2));
		gridEdit.setBorder(new TitledBorder("Échelle"));

		read();

		error.setForeground(Color.RED);

		error.setHorizontalAlignment(JLabel.CENTER);
		x.setHorizontalAlignment(JLabel.CENTER);
		y.setHorizontalAlignment(JLabel.CENTER);

		vWinEdit.add(x);
		vWinEdit.add(y);
		vWinEdit.add(xMaxEdit);
		vWinEdit.add(yMaxEdit);
		vWinEdit.add(xMinEdit);
		vWinEdit.add(yMinEdit);
		vWinPane.add(vWinEdit, BorderLayout.CENTER);
		gridEdit.add(gridX);
		gridEdit.add(gridXEdit);
		gridEdit.add(gridY);
		gridEdit.add(gridYEdit);
		gridEdit.add(showGridLabel);
		gridEdit.add(showGrid);
		formContainer.add(vWinPane);
		formContainer.add(Box.createHorizontalGlue());
		formContainer.add(gridEdit);
		mainPanel.add(formContainer, BorderLayout.CENTER);
		mainPanel.add(error, BorderLayout.SOUTH);
		confirmPane.add(ok);
		confirmPane.add(apply);
		confirmPane.add(cancel);

		this.setLayout(new BorderLayout());
		this.add(mainPanel, BorderLayout.CENTER);
		this.add(confirmPane, BorderLayout.SOUTH);

		ok.addActionListener(this);
		apply.addActionListener(this);
		cancel.addActionListener(this);
	}

	public void read() {
		BigDecimal[] vWin = parent.getVWin();
		BigDecimal[] gridScale = parent.getGridStepping();
		xMinEdit.setText(String.valueOf(vWin[0]));
		xMaxEdit.setText(String.valueOf(vWin[1]));
		yMinEdit.setText(String.valueOf(vWin[2]));
		yMaxEdit.setText(String.valueOf(vWin[3]));

		showGrid.setSelected(parent.showsGrid());
		gridXEdit.setText(String.valueOf(gridScale[0]));
		gridYEdit.setText(String.valueOf(gridScale[1]));
	}

	private void write() {
		BigDecimal xMin = getValue(xMinEdit), xMax = getValue(xMaxEdit),
				yMin = getValue(yMinEdit), yMax = getValue(yMaxEdit),
				gridX = getValue(gridXEdit), gridY = getValue(gridYEdit);
		if(xMin == null || xMax == null || yMin == null || yMax == null
				|| gridX == null || gridY == null) return;
		parent.setVWin(xMin, xMax, yMin, yMax);
		parent.setGridStepping(gridX, gridY);
		parent.showGrid(showGrid.isSelected());
		
		parent.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		error.setText("");
		if(e.getSource().equals(ok)) {
			this.setVisible(false);
			write();
		} else if(e.getSource().equals(apply)) {
			write();
		} else if (e.getSource().equals(cancel)) {
			this.setVisible(false);
		}
	}

	private BigDecimal getValue(JTextField field) {
		try {
			return new BigDecimal(field.getText());
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
			if(error.getText().isEmpty())
				error.setText("Entrée non reconnue : " + e.getLocalizedMessage());
			else
				error.setText(error.getText() + ", Entrée non reconnue : " + e.getLocalizedMessage());
			return null;
		}
	}
}
