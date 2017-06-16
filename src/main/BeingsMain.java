package main;

import sim.display.Console;
import gui.BeingsWithUI;
import model.Beings;

public class BeingsMain {
	public static void main(String[] args) {
        runUI();
	}
	public static void runUI() {
		Beings model = new Beings(System.currentTimeMillis());
		BeingsWithUI gui = new BeingsWithUI(model);
		model.setBeingsWithUI(gui);
		Console console = new Console(gui);
		console.setVisible(true);
	}
}
