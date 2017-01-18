package com.acertaininventorymanager.business;

public class Temp {

	public Temp() {
		
	}

	public static void main (String[] args){
		System.out.println(Thread.currentThread().hashCode());
		for (int i = 0; i < 10; i++){
		new Thread() {
			public void run(){
				System.out.println(Thread.currentThread().hashCode() / 4096);
			}
		}.start();
		}
	}
}
