package com.jesper;

import java.util.Date;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Long now = new Date().getTime();
		Long trans = Long.parseLong("1618211057275");
		System.out.println("now:"+now);
		System.out.println("trans:"+trans);
		System.out.println("diff:"+(now-trans));
		
	}

}
