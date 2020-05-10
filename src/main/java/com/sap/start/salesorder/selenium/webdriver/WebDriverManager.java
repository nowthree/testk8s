package com.sap.start.salesorder.selenium.webdriver;

import java.io.File;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class WebDriverManager {

	static WebDriver webDriver;

	public static void initWebDriver() {
		
		System.out.println("Initial webdriver");
		DesiredCapabilities capability = null;
		String chromeDriverPath = "";
		Platform currentPlatform = Platform.getCurrent().family();
		capability = DesiredCapabilities.chrome();
		ChromeOptions options = new ChromeOptions();
		options.addArguments("disable-infobars");
		options.addArguments("--start-maximized");
		options.addArguments("-incognito");
		options.addArguments("--disable-impl-side-painting");
		options.addArguments("--whitelisted-ips");
		switch (currentPlatform) {
		case WINDOWS:
			chromeDriverPath = "C:\\developmenttools\\START\\webdrivers\\chromedriver.exe";
			break;
		case UNIX:
			chromeDriverPath = "/usr/bin/chromedriver";
			options.setHeadless(true);
			options.addArguments("headless");
			options.addArguments("window-size=1920,1080");
			options.addArguments("--headless");
			options.addArguments("--disable-gpu");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
			capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			capability.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
			break;
		default:
		}
		options.merge(capability);
		ChromeDriverService service = new ChromeDriverService.Builder()
				.usingDriverExecutable(new File(chromeDriverPath)).usingAnyFreePort().build();
		WebDriver driver = new ChromeDriver(service, options);
		setWebDriver(driver);

	}

	public static void shutDownDriver() {
		getWebDriver().quit();
	}

	public static WebDriver getWebDriver() {
		return webDriver;
	}

	public static void setWebDriver(WebDriver driver) {
		webDriver = driver;
	}

}
