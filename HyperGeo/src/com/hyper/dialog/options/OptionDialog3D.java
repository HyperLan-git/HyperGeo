package com.hyper.dialog.options;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.joml.Vector2i;
import org.jzy3d.maths.Range;

import com.hyper.components.cr.ChartHandler;

public class OptionDialog3D extends JDialog implements ActionListener {
	public static final Dimension MINIMUM_SIZE = new Dimension(300, 200);

	private JPanel mainPanel = new JPanel(),
			vWinContainer = new JPanel(), vWinPane = new JPanel(), vWinEdit = new JPanel(),
			stepsPane = new JPanel(), vWinSteps = new JPanel(),
			confirmPane = new JPanel();

	private JTextField xMinEdit = new JTextField(), xMaxEdit = new JTextField(),
			yMinEdit = new JTextField(), yMaxEdit = new JTextField(),
			zMinEdit = new JTextField(), zMaxEdit = new JTextField(),
			xSteps = new JTextField(), ySteps = new JTextField();

	private JButton ok = new JButton("OK"), apply = new JButton("Appliquer"), cancel = new JButton("Cancel");

	private JLabel error = new JLabel(),
			x = new JLabel("x"), y = new JLabel("y"), z = new JLabel("z"),
			xStepsLabel = new JLabel("        x"), yStepsLabel = new JLabel("        y");

	private ChartHandler parent;

	public OptionDialog3D(JFrame window, ChartHandler parent) {
		super(window);
		this.setMinimumSize(MINIMUM_SIZE);
		this.parent = parent;

		vWinPane.setBorder(new TitledBorder("View window"));
		stepsPane.setBorder(new TitledBorder("Étapes de calcul"));
		vWinPane.setPreferredSize(new Dimension(150, 100));
		vWinPane.setMinimumSize(new Dimension(150, 100));
		stepsPane.setPreferredSize(new Dimension(120, 75));
		stepsPane.setMinimumSize(new Dimension(120, 75));
		mainPanel.setLayout(new BorderLayout());
		vWinContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
		vWinPane.setLayout(new BorderLayout());
		stepsPane.setLayout(new BorderLayout());
		vWinEdit.setLayout(new GridLayout(3, 3));
		vWinSteps.setLayout(new GridLayout(2, 2));

		update();

		error.setForeground(Color.RED);

		error.setHorizontalAlignment(JLabel.CENTER);
		x.setHorizontalAlignment(JLabel.CENTER);
		y.setHorizontalAlignment(JLabel.CENTER);
		z.setHorizontalAlignment(JLabel.CENTER);

		vWinEdit.add(x);
		vWinEdit.add(y);
		vWinEdit.add(z);
		vWinEdit.add(xMaxEdit);
		vWinEdit.add(yMaxEdit);
		vWinEdit.add(zMaxEdit);
		vWinEdit.add(xMinEdit);
		vWinEdit.add(yMinEdit);
		vWinEdit.add(zMinEdit);
		vWinSteps.add(xStepsLabel);
		vWinSteps.add(yStepsLabel);
		vWinSteps.add(xSteps);
		vWinSteps.add(ySteps);
		stepsPane.add(vWinSteps, BorderLayout.CENTER);
		vWinPane.add(vWinEdit, BorderLayout.CENTER);
		vWinPane.add(error, BorderLayout.SOUTH);
		vWinContainer.add(vWinPane);
		vWinContainer.add(stepsPane);
		mainPanel.add(vWinContainer, BorderLayout.CENTER);
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

	public void update() {
		Vector2i stepping = parent.getStepping();
		Range xRange = parent.getVWinX(),
				yRange = parent.getVWinY(),
				zRange = parent.getVWinZ();
		xMinEdit.setText(String.valueOf(xRange.getMin()));
		xMaxEdit.setText(String.valueOf(xRange.getMax()));
		yMinEdit.setText(String.valueOf(yRange.getMin()));
		yMaxEdit.setText(String.valueOf(yRange.getMax()));
		zMinEdit.setText(String.valueOf(zRange.getMin()));
		zMaxEdit.setText(String.valueOf(zRange.getMax()));
		xSteps.setText(String.valueOf(stepping.x));
		ySteps.setText(String.valueOf(stepping.y));
	}

	private void write() {
		Range xRange = new Range(getValue(xMinEdit), getValue(xMaxEdit)),
				yRange = new Range(getValue(yMinEdit), getValue(yMaxEdit)),
				zRange = new Range(getValue(zMinEdit), getValue(zMaxEdit));
		if(!xRange.valid() || !yRange.valid()) return;
		this.parent.setVWinX(xRange);
		this.parent.setVWinY(yRange);
		this.parent.setVWinZ(zRange);
		this.parent.setStepping(getIntValue(xSteps), getIntValue(ySteps));
		this.parent.updateGraph();
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

	private float getValue(JTextField field) {
		try {
			return Float.parseFloat(field.getText());
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
			if(error.getText().isEmpty())
				error.setText("Entrée non reconnue : " + e.getLocalizedMessage());
			else
				error.setText(error.getText() + ", Entrée non reconnue : " + e.getLocalizedMessage());
			return Float.NaN;
		}
	}
	private int getIntValue(JTextField field) {
		try {
			return Integer.parseInt(field.getText());
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
			if(error.getText().isEmpty())
				error.setText("Entrée non reconnue : " + e.getLocalizedMessage());
			else
				error.setText(error.getText() + ", Entrée non reconnue : " + e.getLocalizedMessage());
			return 0;
		}
	}
}
