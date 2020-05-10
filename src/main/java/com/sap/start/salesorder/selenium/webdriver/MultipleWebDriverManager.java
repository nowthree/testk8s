package com.sap.start.salesorder.selenium.webdriver;

import java.io.File;
import java.util.Map;

import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class MultipleWebDriverManager {

	WebDriver webDriver;

	public MultipleWebDriverManager() {
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
		//options.addArguments("headless");
		System.out.println("platform: " + currentPlatform);
		switch (currentPlatform) {
		case WINDOWS:
			chromeDriverPath = "C:\\developmenttools\\START\\webdrivers\\chromedriver.exe";
			//chromeDriverPath = "C:\\Program Files (x86)\\ChromeDriver\\chromedriver.exe";
			options.addArguments("--ignore-certificate-errors");
			capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			capability.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
			break;
		case UNIX:
			System.out.println("unix");
			chromeDriverPath = "/usr/bin/chromedriver";
			options.setHeadless(true);
			options.addArguments("headless");
			options.addArguments("window-size=1920,1080");
			options.addArguments("--headless");
			options.addArguments("--disable-gpu");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
			options.addArguments("--ignore-certificate-errors");
			capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			capability.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
			
//			final Proxy proxy = new Proxy();
//			proxy.setProxyType(ProxyType.MANUAL);
//			proxy.setHttpProxy("http://proxy.wdf.sap.corp:8080");
//			proxy.setSslProxy("http://proxy.wdf.sap.corp:8080");
//			proxy.setNoProxy("sap.corp,localhost,127.0.0.1,svcqass.ariba.com");
//			capability.setCapability(CapabilityType.PROXY, proxy);

			
			break;
		default:
		}
		options.merge(capability);
		ChromeDriverService service = new ChromeDriverService.Builder()
				.usingDriverExecutable(new File(chromeDriverPath)).usingAnyFreePort().build();
		WebDriver driver = new ChromeDriver(service, options);
		setWebDriver(driver);

	}

	public void shutDownDriver() {
		this.getWebDriver().quit();
	}

	public WebDriver getWebDriver() {
		return webDriver;
//		return new ChromeDriver();
	}

	public void setWebDriver(WebDriver driver) {
		webDriver = driver;
	}

}
