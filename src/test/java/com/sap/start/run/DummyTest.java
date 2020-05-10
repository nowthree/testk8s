package com.sap.start.run;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;



public class DummyTest  {
	/*
	 * @Test public void externalLib() { ChromeDriver driver = new ChromeDriver();
	 * String property = "<iDoAction>: function () {\r\n" +
	 * "    element(by.control({\r\n" +
	 * "        controlType: \"sap.ushell.ui.launchpad.Tile\",\r\n" +
	 * "        viewName: \"sap.ushell.components.homepage.DashboardContent\",\r\n"
	 * + "        bindingPath: {\r\n" +
	 * "            path: \"/groups/11/tiles/0\",\r\n" +
	 * "            propertyPath: \"long\"\r\n" + "        }\r\n" + "    }));\r\n" +
	 * "}"; driver.findElement(FindBy.UI5(property)); }
	 */

    @Test
    public void create() {
    	try {
    		
    		System.out.println(getClass().getResource("/scripts").getPath());
    		
    		
    		
    		try (Stream<Path> walk = Files.walk(Paths.get(getClass().getResource("/scripts/").toURI()))) {

    			List<String> result = walk.map(x -> x.toString())
    					.filter(f -> f.endsWith("2NV.json")).sorted().collect(Collectors.toList());

    			result.forEach(filename -> {
					try {
						RunScript.run(new File(filename));
					} catch (Exception e) {
						assertTrue(e.getMessage(), false);
					}
				});

    		} catch (IOException e) {
    			e.printStackTrace();
    		}
 			
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}
