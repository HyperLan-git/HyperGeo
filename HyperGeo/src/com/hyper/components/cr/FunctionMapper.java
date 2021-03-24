package com.hyper.components.cr;

import org.jzy3d.plot3d.builder.Mapper;
import org.mariuszgromada.math.mxparser.Function;

public class FunctionMapper extends Mapper {
	private final Function function;

	public FunctionMapper() {
		this(new Function(""));
	}

	public FunctionMapper(Function function) {
		this.function = function;
	}

	@Override
	public double f(double x, double y) {
		double value = function.calculate(x, y);
		if(!Double.isNaN(value))
			return value;
		return 0;
	}

}
