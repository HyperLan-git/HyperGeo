package com.hyper;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import com.hyper.components.cr.Pane3D;
import com.hyper.components.rr.Pane2D;

public class HyperGeo implements ActionListener {
	public static final String HELP_TEXT = "Entrez une couleur totalement transparente pour avoir un arc-en-ciel dans le graphe 3d\n"
			+ "Attention à ne pas travailler avec des valeurs trop précises, les erreurs des nombres à virgule flottante s'accumulent...\n"
			+ "Liste des fonctions utilisables dans les fonctions définies par l'utilisateur (en plus des autres définies plus haut):\n"
			+ "cosinus = cos(x), sinus = sin(x), tangente = tan(x)\n"
			+ "secant = sec(x), cosecant = cosec(x), cotangente = ctan(x)\n"
			+ "arccosinus = asin(x), arcsinus = asin(x), arctangente = atan(x)\n"
			+ "logarithme néperien = ln(x), logarithme de base n = log(x, n)\n"
			+ "exponentielle = exp(x), racine carrée = sqrt(x)\n"
			+ "cosinus, sinus et tangente hyperbolique = cosh(x), sinh(x) et tanh(x)\n"
			+ "valeur absolue = abs(x), fonction signe = sgn(x), fonction erreur = erf(x)\n"
			+ "partie entiére par défaut = floor(x), par excés = ceil(x)\n"
			+ "a modulo b = mod(a, b), Coefficient binomial = C(n, k)\n"
			+ "intégrale de f(x) par rapport à x entre a et b = int(f(x), x, a, b), dérivée = der(f(x), x)\n"
			+ "\nConstantes : pi, e, [phi], [PN]\n"
			+ "\n\n\n\n\n\n\n"
			+ "Plus d'infos : http://mathparser.org/mxparser-math-collection/";

	private JFrame window;

	private JMenuItem options, about, help, quit;

	private JTabbedPane pane;

	private Pane2D pane2D;

	private Pane3D pane3D;

	public HyperGeo() {
		System.setProperty("sun.awt.noerasebackground", "true");
		
		window = new JFrame("HyperGeo");
		pane2D = new Pane2D(window);
		pane3D = new Pane3D(window);

		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setMinimumSize(new Dimension(600, 500));

		pane = new JTabbedPane();
		pane.addTab("Fonctions de réel à réel", pane2D);
		pane.addTab("Fonctions de complexes à réel", pane3D);
		window.setContentPane(pane);

		initBar();

		window.pack();
		window.setVisible(true);
	}

	private void initBar() {
		JMenuBar bar = new JMenuBar();

		options = new JMenuItem("Options");
		about = new JMenuItem("à".toUpperCase() + " propos");
		help = new JMenuItem("Aide");
		quit = new JMenuItem("Quitter");

		options.setMnemonic('O');
		about.setMnemonic('P');
		help.setMnemonic('A');
		quit.setMnemonic('Q');

		options.addActionListener(this);
		about.addActionListener(this);
		help.addActionListener(this);
		quit.addActionListener(this);

		bar.add(options);
		bar.add(about);
		bar.add(help);
		bar.add(quit);

		window.setJMenuBar(bar);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if(o.equals(options)) {
			if(pane2D.equals(pane.getSelectedComponent())) pane2D.options();
			if(pane3D.equals(pane.getSelectedComponent())) pane3D.options();
		} else if(o.equals(about)) {
			JOptionPane.showMessageDialog(window, "Codé par : HyperLan\nDes suggestions, remarques ?\nContactez moi : rekikyouness6@gmail.com",
					about.getText(), JOptionPane.INFORMATION_MESSAGE);
		} else if(o.equals(help)) {
			JOptionPane.showMessageDialog(window, HELP_TEXT, help.getText(), JOptionPane.INFORMATION_MESSAGE);
		} else if(o.equals(quit)) {
			window.dispose();
		}
	}

	public static final double roundOff(double N, int significantDigits) {
		if(N == 0) return N;
		int powerOfTen = 0;
		double j, normalized, result;

		for(j = Math.abs(N); j < 1; j *= 10f)
			powerOfTen--;

		for(j = Math.abs(N); j > 10; j /= 10f)
			powerOfTen++;

		normalized = N/pow(10, powerOfTen);
		result = Math.round(normalized*pow(10, significantDigits-1))*pow(10, -significantDigits+1+powerOfTen);
		return result;
	}

	public static final double pow(double a, int power) {
		if(power == 0) return 1;
		if(power == 1) return a;
		if(power < 0) return 1.0/pow(a, -power);
		if((power%2) == 0) return pow(a*a, power/2);	//even	   a^x = (a^2)^x/2
		return a*pow(a*a, power/2);						//odd  	   a^x = a*(a^2)^(x-1)/2
	}
}
