package com.hyper.components.rr;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.hyper.dialog.options.OptionDialog2D;
import com.hyper.io.EventHandler2d;

public class Pane2D extends JPanel {
	private JPanel rightPane;

	private OptionDialog2D dialog;

	private Matrix2DEditor editor;

	private JSplitPane content;

	private Canvas2D canvas;

	private FunctionPanel functions;

	public Pane2D(JFrame window) {
		this.setLayout(new BorderLayout());

		this.rightPane = new JPanel();
		this.rightPane.setLayout(new BorderLayout());

		this.canvas = new Canvas2D(this);
		this.functions = new FunctionPanel();
		this.content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, canvas, rightPane);

		this.editor = new Matrix2DEditor(canvas);

		this.dialog = new OptionDialog2D(window, this.canvas);

		this.rightPane.add(functions, BorderLayout.CENTER);
		this.rightPane.add(editor, BorderLayout.SOUTH);

		new EventHandler2d(this.canvas, this.functions, this.content);

		this.add(content, BorderLayout.CENTER);
	}

	public void options() {
		dialog.read();
		dialog.setVisible(true);
	}

	public FunctionPanel getFunctionPanel() {
		return functions;
	}
}
