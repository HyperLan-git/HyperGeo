package com.hyper.components.rr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.parsertokens.ParserSymbol;

import com.hyper.components.table.FunctionTable;
import com.hyper.components.table.FunctionTableListener;

public class FunctionPanel extends JPanel {
	public static final String ARGUMENT_1 = "arg1", ARGUMENT_2 = "arg2",		//The regex ignores the match if there is an alphabetical character or _ behind ->(?<![A-Za-z_])
			FUNCTIONS_REGEX = ParserSymbol.nameOnlyTokenRegExp,					//Then it matches the function name then the second regex ->[function_name]+FUNCTION_REGEX_2
			FUNCTION_REFERENCE_REGEX_1 = "(?<![A-Za-z_])",						//Then it matches a ( then anything that is not , or )    ->\\((?<" + ARGUMENT_1 + ">[^\\),]
			FUNCTION_REFERENCE_REGEX_2 = "\\((?<" + ARGUMENT_1 + ">[^\\),]+)"	//(?<name>[something]) makes a capture group which can be gotten with matcher.group("name");
			+ "(,(?<" + ARGUMENT_2 + ">[^\\),]+))?\\)";							/*Then it matches , and anything that is not , or )       ->,(?<" + ARGUMENT_2 + ">[^\\),]+))
																				If there is any and finally there must be a )             ->([The_last_thing])?\\)*/


	/**
	 * //TODO
	 * @param text The input sequence
	 * @param targets Array of sequences to find and replace
	 * @param replacements The replacements (Must be at least targets.length long)
	 * @return A new string which contains text with all the replacements
	 */
	public static String replaceAllAtOnce(CharSequence text, CharSequence[] targets, CharSequence... replacements) {
		// create the pattern joining the keys with '|'
		String regexp = String.join("|", targets);

		StringBuffer sb = new StringBuffer();
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(text);

		while (m.find()) {
			int i;
			for(i = 0; i < replacements.length; i++)
				if(m.group().contentEquals(targets[i])) break;
			m.appendReplacement(sb, replacements[i].toString());
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private FunctionTable functionsTable;

	private JLabel invalid;

	private String[] args;

	public FunctionPanel(String... additionnalArguments) {
		this.args = new String[additionnalArguments.length+1];
		this.args[0] = "x";
		for(int i = 0; i < additionnalArguments.length; i++)
			args[i+1] = additionnalArguments[i];
		//TODO make functions support more than 2 args
		this.invalid = new JLabel("");
		this.invalid.setForeground(Color.RED);

		this.functionsTable = new FunctionTable();

		this.setLayout(new BorderLayout());

		new FunctionTableListener(this);

		this.add(new JScrollPane(functionsTable), BorderLayout.CENTER);
		this.add(invalid, BorderLayout.SOUTH);
		this.setBackground(Color.GRAY);
	}

	public Function[] read() {
		try {
			invalid.setText("");
			Function[] functions = new Function[functionsTable.getRowCount()];
			for(int i = 0; i < functionsTable.getRowCount(); i++) {
				String func = (String)functionsTable.getModel().getValueAt(i, 0),
						define = (String)functionsTable.getModel().getValueAt(i, 1);
				if(func == null || define == null || func.isEmpty() || define.isEmpty()) continue;
				if(!func.matches(FUNCTIONS_REGEX)) {
					invalid.setText("Function number " + (i+1) + " has an invalid name !");
					continue;
				}

				for(int j = 0; j < i; j++) {
					String name = functions[j].getFunctionName();
					Pattern regex = Pattern.compile(FUNCTION_REFERENCE_REGEX_1 + name + FUNCTION_REFERENCE_REGEX_2);
					Matcher matcher;
					do {
						matcher = regex.matcher(define);
						while(matcher.find()) {
							//Replace all instances of somefunction(someargument) with the expression of that function where x is replaced by (someargument)
							String newFunctionExpression = functions[j].getFunctionExpressionString();
							if(args.length == 1)
								newFunctionExpression = newFunctionExpression.replace(args[0], matcher.group(ARGUMENT_1));
							else
								newFunctionExpression = replaceAllAtOnce(newFunctionExpression, args, matcher.group(ARGUMENT_1), matcher.group(ARGUMENT_2));

							define = define.replace(matcher.group(), "(" + newFunctionExpression + ")");
						}
					} while(matcher.find(0));
				}
				define = " " + define.replaceAll("\\s", ""); //space regex selector
				System.out.println(func + " = " + define);
				functions[i] = new Function(func, define, args);
				if(!functions[i].checkSyntax()) {
					invalid.setText("Function " + func + "(x) has an invalid definition !");
					System.out.println(functions[i].getErrorMessage());
				}
			}
			return functions;
		} catch (Exception e) {
			invalid.setText(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	}

	public FunctionTable getTable() {
		return functionsTable;
	}

	public void mousePressed(int x, int y) {
		functionsTable.mousePressed(x, y);
	}
}
