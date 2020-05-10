package com.sap.start.run;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RunScriptMain {

	public static void main(String[] args) {
		
		try (Stream<Path> walk = Files.walk(Paths.get(System.getProperty("user.dir") + "\\src\\test\\resources\\scripts"))) {

			List<String> result = walk.map(x -> x.toString())
					.filter(f -> f.endsWith("2NV.json")).sorted().collect(Collectors.toList());
                        
			result.forEach(filename -> {
				try {
					RunScript.run(new File(filename));
				} catch (Exception e) {
					//catch this exception and print out in main method
					e.printStackTrace();
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
