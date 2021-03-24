package com.hyper.components.cr;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.hyper.components.rr.FunctionPanel;
import com.hyper.dialog.options.OptionDialog3D;
import com.hyper.io.EventHandler3d;

public class Pane3D extends JPanel {
	private OptionDialog3D options;

	private JSplitPane content;

	private ChartHandler chart;

	private FunctionPanel functions;

	public Pane3D(JFrame window) {
		JPanel leftBackground = new JPanel();
		this.setLayout(new BorderLayout());
		leftBackground.setLayout(new BorderLayout());

		leftBackground.setBackground(Color.WHITE);

		this.functions = new FunctionPanel("y");//yes but WHY -lewhy
		this.chart = new ChartHandler(this);
		leftBackground.add(this.chart, BorderLayout.CENTER);
		this.content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, leftBackground, functions);

		options = new OptionDialog3D(window, this.chart);

		new EventHandler3d(this.chart, this.functions);

		this.add(content, BorderLayout.CENTER);
	}

	public void options() {
		options.update();
		options.setVisible(true);
	}

	public FunctionPanel getFunctionPanel() {
		return functions;
	}
}
