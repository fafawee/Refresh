package com.fagawee.refresh.svg;

public class SvgExecption  extends RuntimeException{

	public SvgExecption(String s) {
        super(s);
    }

    public SvgExecption(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SvgExecption(Throwable throwable) {
        super(throwable);
    }
	
}
