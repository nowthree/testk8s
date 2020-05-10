package com.sap.start.run;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class TaAction {

	private static Logger logger = LogManager.getLogger(TaAction.class);
	
	private static final long MAX_WAITING_TIME = 60;
	private static final List<String> truelist = Arrays.asList(new String[]{"true", "1", "t", "y", "yes", "yeah", "certainly", "ok", "okay"});

	public boolean CompareText(Map<String, String> map, WebDriver driver) {
		try {
			System.out.println(map.get("name"));
			System.out.println("calling compareText");
			
			String xpath = map.get("techUISelector");
			String expect_value = map.get("parameter");

			try {
				WebDriverWait wait = new WebDriverWait(driver, MAX_WAITING_TIME);

				WebElement value = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
				String realvalue = value.getText();

				sleep(map, driver);

				return expect_value.equals(realvalue);
			} catch (TimeoutException ex) { // handle your exception
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public boolean CompareBool(Map<String, String> map, WebDriver driver) {
		try {
			System.out.println(map.get("name"));
			System.out.println("calling compareText");
			
			String xpath = map.get("techUISelector");
			String expect_value = map.get("parameter");

			boolean expect_value_bool = truelist.contains(expect_value.toLowerCase());
			
			try {
				WebDriverWait wait = new WebDriverWait(driver, MAX_WAITING_TIME);

				WebElement value = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
				boolean realvalue = value.isSelected();

				sleep(map, driver);

				return expect_value_bool == realvalue;
				
			} catch (TimeoutException ex) { // handle your exception
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public void OpenURL(Map<String, String> map, WebDriver driver) {
		try {
			String url = map.get("parameter");

			driver.get(url);
			sleep(map, driver);
			System.out.println(map.get("name"));
			System.out.println("calling openURL");
		} catch (Exception e) {
			Optional<String> optional = Optional.ofNullable(map.get("optional"));
			if (optional.orElse("false").equals("true")) {
				throw new RuntimeException("SKIP");
			} 
			throw e;
		}
	}
	
	public String RandomID(Map<String, String> map, WebDriver driver) throws Exception {
		String randomID = "test_item_" + new Random().nextInt(999999999);
		
		System.out.println(map.get("name"));
		System.out.println("generating RandomID ");
		
		map.put("name", "Input random ID " + randomID);
		map.put("parameter", randomID);
		
		FindInputBox(map,driver);
		
		return randomID;
	}
	
	public void ClickButton(Map<String, String> map, WebDriver driver) throws Exception {
		try {
			System.out.println(map.get("name"));
			System.out.println("calling clickButton");
			
			String xpath = map.get("techUISelector");
			String value = map.get("parameter");
			String subSelector = map.get("subSelector");
			
			if (value != null && !"".equals(value)) {
				xpath = xpath.replace("value", "'" + value + "'");
			}
			
		    if (subSelector != null && !"".equals(subSelector)) {
		        xpath = "//tr[@role='row' and .//td[string()='"+ subSelector +"']]" + xpath;
		    }
			
		    Optional<WebElement> answer = findElementIframe(xpath, driver);
		    
		    WebElement button;
		    if (answer.isPresent()) {
				button = answer.get();
			} else {
				WebDriverWait wait = new WebDriverWait(driver, MAX_WAITING_TIME);
				button = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
			}
		    
		    int i=0;
		    
		    while (i < 20) {
				try {
					button.click();
					sleep(map, driver);
					break;
				} catch (StaleElementReferenceException | InvalidElementStateException e) {
					e.printStackTrace();
					i = i + 1;
					TimeUnit.SECONDS.sleep(5);
					driver.switchTo().activeElement();// handle special dom tree element, for example ng-switch
					button = driver.findElement(By.xpath(xpath));
					if (e.getMessage().contains("Other element would receive the click")) {
					    Actions builder = new Actions(driver);
					    builder.click(button).build().perform();
					    sleep(map, driver);
					    break;
					}
				} catch (WebDriverException e) {
					String errorMessage = e.getMessage();
					System.out.println("System: " + errorMessage);
					if (errorMessage.contains("Other element would receive the click")) {
						i = i + 1;
						TimeUnit.SECONDS.sleep(5);
					    Actions builder = new Actions(driver);
					    button = driver.findElement(By.xpath(xpath));
					    builder.click(button).build().perform();
					    sleep(map, driver);
					    break;
					} else {
						throw e;
					}
				}
			}
		    
		    if (i == 20) {
		    	throw new RuntimeException("Have Tried " + i + " times, Can't find the button(check box or radio): " + xpath);
		    }
		} catch (Exception e) {
			Optional<String> optional = Optional.ofNullable(map.get("optional"));
			if (optional.orElse("false").equals("true")) {
				throw new RuntimeException("SKIP");
			} 
			throw e;
		}
	}

	public void FindInputBox(Map<String, String> map, WebDriver driver) throws Exception {
		try {
			System.out.println(map.get("name"));
			System.out.println("calling findInputBox");
			
			String xpath = map.get("techUISelector");
			String value = map.get("parameter");
			
			Optional<WebElement> answer = findElementIframe(xpath, driver);
			
			WebElement inputbox;
			
		    int i=0;
		    
		    while (i < 20) {
				try {
					if (answer.isPresent()) {
						inputbox = answer.get();
					} else {
						WebDriverWait wait = new WebDriverWait(driver, MAX_WAITING_TIME);
						inputbox = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
					}
					if (value.contains("Keys.")) {
						String key = value.split("\\.")[1];
						inputbox.sendKeys(Keys.valueOf(key));
						sleep(map, driver);
						return;
					}
					
					if (!"".equals(inputbox.getAttribute("value"))) {
						inputbox.sendKeys(Keys.CONTROL + "a");
					}
					inputbox.sendKeys(value);
					
					sleep(map, driver);
					break;
					
				} catch (StaleElementReferenceException | InvalidElementStateException e) {
					e.printStackTrace();
					i = i + 1;
					TimeUnit.SECONDS.sleep(5);
				} catch (WebDriverException e) {
					e.printStackTrace();
					String errorMessage = e.getMessage();
					if (errorMessage.contains("Other element would receive the click")) {
						i = i + 1;
						TimeUnit.SECONDS.sleep(5);
					} else {
						throw e;
					}
				}
			}
		    
		    if (i == 20) {
		    	throw new RuntimeException("Have Tried " + i + " times, Can't find the inputbox: " + xpath);
		    }
		} catch (Exception e) {
			Optional<String> optional = Optional.ofNullable(map.get("optional"));
			if (optional.orElse("false").equals("true")) {
				throw new RuntimeException("SKIP");
			} 
			throw e;
		}
	}

	public String ReadValueAndSave(Map<String, String> map, WebDriver driver) {
		try {
			System.out.println(map.get("name"));
			System.out.println("calling readValueAndSave");
			
			String xpath = map.get("techUISelector");
			
			try {
				WebDriverWait wait = new WebDriverWait(driver, MAX_WAITING_TIME);

				WebElement value = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
				String realValue = value.getText();
                System.out.println("Read and save the value: " + realValue);
				sleep(map, driver);

				return realValue;
				
			} catch (TimeoutException ex) { // handle your exception
				throw new TimeoutException("Timeout! Can't find element: " + xpath);
			}
		} catch (Exception e) {
			Optional<String> optional = Optional.ofNullable(map.get("optional"));
			if (optional.orElse("false").equals("true")) {
				throw new RuntimeException("SKIP");
			} 
			throw e;
		}
	}
	
	public void ReadValueAssert(Map<String, String> map, WebDriver driver) throws Exception {
		try {
			System.out.println(map.get("name"));
			System.out.println("calling ReadValueAssert");
			
			String xpath = map.get("techUISelector");
			String expect_value = map.get("parameter");
			
			try {
				WebDriverWait wait = new WebDriverWait(driver, MAX_WAITING_TIME);
				WebElement value = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
				Actions actions = new Actions(driver);
				actions.moveToElement(value).perform();
			
				
				String real_value = value.getText();
				
				
				if (real_value.contains(expect_value)) {
					System.out.println("Compare successfully");
					logger.debug("Compare successfully");
				} else {
					System.out.println("Compare failed! " + expect_value + " is not in " + real_value);
					throw new Exception("Compare failed! " + expect_value + " is not in " + real_value);
				}
				
			} catch (TimeoutException ex) { // handle your exception
				throw new TimeoutException("Timeout! Can't find element: " + xpath);
			}
			
			this.sleep(map, driver);
		} catch (Exception e) {
			Optional<String> optional = Optional.ofNullable(map.get("optional"));
			if (optional.orElse("false").equals("true")) {
				throw new RuntimeException("SKIP");
			} 
			throw e;
		}
	}

	private void sleep(Map<String, String> map, WebDriver driver) {
		try {
			int idle_time = Integer.valueOf(map.get("techIdletime")) + 3;
			Thread.sleep(idle_time * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Optional<WebElement> findElementIframe(String xpath, WebDriver driver) {
		
		driver.switchTo().defaultContent(); // before search the iframe, make sure the driver at main dom tree
		
		List<WebElement> iframeList = driver.findElements(By.xpath("//iframe"));
		
		if (!iframeList.isEmpty()) {
			WebDriverWait more_wait = new WebDriverWait(driver, 2);
			//more wait to ensure find all iframe
			more_wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//iframe")));
			iframeList = driver.findElements(By.xpath("//iframe"));
			
			for (WebElement iframe : iframeList) {
				if (iframe.getAttribute("id") != null) {
					try {
						System.out.println("System: find the iframe " + iframe.getAttribute("id"));
						driver.switchTo().frame(iframe);
						
						more_wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
						return Optional.of(driver.findElement(By.xpath(xpath)));
					} catch (TimeoutException | NoSuchFrameException e) {
						System.out.println("System: can't find the element in iframe");
						driver.switchTo().defaultContent();
					}
				}
			}
		}
		return Optional.ofNullable(null);
	}
}
