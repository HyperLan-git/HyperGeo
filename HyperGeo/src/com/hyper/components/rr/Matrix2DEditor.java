package com.hyper.components.rr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.math.BigDecimal;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import com.hyper.math.Matrix2bd;

public class Matrix2DEditor extends JPanel implements CellEditorListener {
	public static final Dimension MINIMUM_SIZE = new Dimension(100, 200);

	public static final String[] COLUMN_NAMES = {"x", "y"};
	
	private JPanel pane;

	private Canvas2D canvas;

	private Matrix2bd matrix = new Matrix2bd();

	private JTable matrixTable = new JTable(new String[][] {{"1.0", "0.0"}, {"0.0", "1.0"}}, COLUMN_NAMES);

	private JLabel error = new JLabel();

	public Matrix2DEditor(Canvas2D canvas) {
		this.setMinimumSize(MINIMUM_SIZE);
		this.setPreferredSize(MINIMUM_SIZE);
		this.setBorder(new TitledBorder("Matrice"));
		this.setLayout(new BorderLayout());

		this.canvas = canvas;

		this.pane = new JPanel();
		this.pane.setLayout(new BorderLayout());
		
		this.matrixTable.getCellEditor(0, 0).addCellEditorListener(this);

		this.error.setForeground(Color.RED);
		this.error.setHorizontalAlignment(JLabel.CENTER);
		
		this.pane.add(new JScrollPane(matrixTable));

		this.add(pane, BorderLayout.CENTER);
		this.add(error, BorderLayout.SOUTH);

		read();
	}

	private void read() {
		BigDecimal x1 = getValue((String) this.matrixTable.getValueAt(0, 0)), x2 = getValue((String)this.matrixTable.getValueAt(0, 1)),
				y1 = getValue((String)this.matrixTable.getValueAt(1, 0)), y2 = getValue((String)this.matrixTable.getValueAt(1, 1));
		error.setText("");

		if(x1 != null)
			matrix.m00(x1);

		if(x2 != null)
			matrix.m10(x2);

		if(y1 != null)
			matrix.m01(y1);

		if(y2 != null)
			matrix.m11(y2);
		
		matrix.checkProperties();

		canvas.setTransform(matrix);

		canvas.repaint();

		write();
	}

	private void write() {
		matrix = canvas.getTransform();
		matrixTable.setValueAt(String.valueOf(matrix.m00()), 0, 0);
		matrixTable.setValueAt(String.valueOf(matrix.m10()), 0, 1);
		matrixTable.setValueAt(String.valueOf(matrix.m01()), 1, 0);
		matrixTable.setValueAt(String.valueOf(matrix.m11()), 1, 1);
	}

	public Matrix2bd getMatrix() {
		return matrix;
	}

	@Override
	public void editingStopped(ChangeEvent e) {
		read();
	}

	@Override
	public void editingCanceled(ChangeEvent e) {
		write();
	}

	private BigDecimal getValue(String value) {
		try {
			return new BigDecimal(value);
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
			if(error.getText().isEmpty())
				error.setText("Entrée non reconnue : " + e.getLocalizedMessage());
			else
				error.setText(error.getText() + ", Entrée non reconnue : " + e.getLocalizedMessage());
			System.out.println(error.getText());
			return null;
		}
	}
}
