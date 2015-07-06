package com.whys.scans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class ScriptEngine {

	public void execute(String action) throws IOException {
		
			Runtime runtime = Runtime.getRuntime();
						
			final Process process = runtime.exec(action);

			// Consommation de la sortie standard de l'application externe dans un Thread separe
			new Thread() {
				public void run() {
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
						String line = "";
						try {
							while((line = reader.readLine()) != null) {
								onStandardOutput(line);
							}
						} finally {
							reader.close();
							Thread.currentThread().interrupt();							
							onScriptEnd();
						}
					} catch(IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}.start();

			// Consommation de la sortie d'erreur de l'application externe dans un Thread separe
			new Thread() {
				public void run() {
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
						String line = "";
						try {
							while((line = reader.readLine()) != null) {
								onErrorOutput(line);
							}
						} finally {
							reader.close();
							Thread.currentThread().interrupt();
						}
					} catch(IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}.start();
	}
	
	public abstract void onStandardOutput(String data);
	
	public abstract void onErrorOutput(String data);
	
	public abstract void onScriptEnd();
}

