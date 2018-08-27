package com.kabryxis.spiritcraft;

import java.util.Scanner;

public class Test {
	
	public static void main(String[] a) {
		Scanner scanner = new Scanner(System.in);
		double cx = 0.0;
		double cy = 1.0;
		double cz = 0.0;
		while(scanner.hasNextLine()) {
			String input = scanner.nextLine();
			if(input.equalsIgnoreCase("stop")) break;
			String[] args = input.split(" ");
			double px = Double.parseDouble(args[0]);
			double py = Double.parseDouble(args[1]);
			double pz = Double.parseDouble(args[2]);
			double motX = cx - px;
			double motY = cy - py;
			double motZ = cz - pz;
			double dist = motX * motX + motY * motY + motZ * motZ;
			double strength = 128.0 / dist / 1000.0;
			System.out.println(dist + ": " + motX + "," + motY + "," + motZ + " - " + (motX * strength) + "," + (motY * strength) + "," + (motZ * strength));
		}
		scanner.close();
		System.exit(0);
	}
	
}
