package com.sap.cloudFnd.testauto.concur;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.Random;

//import org.apache.logging.log4j.message.Message;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;

import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Steps {

	private WebDriver driver;

	private WebDriver initDriver() {
		Proxy proxy = new Proxy();
		proxy.setProxyType(ProxyType.MANUAL);
		proxy.setHttpProxy("http://proxy.wdf.sap.corp:8080");
		proxy.setSslProxy("http://proxy.wdf.sap.corp:8080");
		proxy.setNoProxy("sap.corp,localhost,127.0.0.1,svcqass.ariba.com");

		ChromeOptions options = new ChromeOptions();
		options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
//		options.setCapability(CapabilityType.PROXY, proxy);
		//options.setHeadless(true);
		options.addArguments("--disable-impl-side-painting");
		options.addArguments("--whitelisted-ips");
	    //options.addArguments("window-size=1920,1080");
		//options.addArguments("--headless");
		options.addArguments("--disable-gpu");
		options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		options.addArguments("--no-sandbox"); // Bypass OS security model
		options.addArguments("--ignore-certificate-errors");// ignore certificate error

		return new ChromeDriver(options);
	}

	public Steps() {
		driver = initDriver();
	}

	public void loginConcur(String userName, String password) {
		String concurUrl = "https://www.concursolutions.com/nui/signin";
		System.out.println("[Concur]: Open Concur website");

		WebDriverWait bigWait = new WebDriverWait(driver, 10);
		WebDriverWait lilWait = new WebDriverWait(driver, 50);
		driver.get(concurUrl);
		try {
			String preAgreeXpath = "//a[@class='call']";
			String iframeXpath = "//iframe[@title]";
			bigWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(iframeXpath)));
			System.out.println("[Concur]: iframe found");
			driver.switchTo().frame(driver.findElement(By.xpath(iframeXpath)));
			bigWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(preAgreeXpath)));
			driver.findElement(By.xpath(preAgreeXpath)).click();
			System.out.println("[Concur]: Agree Cookie Setting policy");
			driver.switchTo().defaultContent();
		} catch (TimeoutException e) {

		}




		try {
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='userid']")));
	
			String userXpath = "//input[@id='userid']";
			System.out.println("[Concur]: Input login username " + userName);
			driver.findElement(By.xpath(userXpath)).sendKeys(userName);
			
		} catch (TimeoutException e) {
			e.printStackTrace();
		
		}
		
		String pwdXpath = "//input[@id='password']";
		System.out.println("[Concur]: Input login password " + password);
		driver.findElement(By.xpath(pwdXpath)).sendKeys(password);

		String loginXpath = "//input[@id='btnSubmit']";
		System.out.println("[Concur]: Click login button");
		driver.findElement(By.xpath(loginXpath)).click();


	}

	public void loginS4(String userName, String password) {
		int attemps = 0;
		WebDriverWait wait = new WebDriverWait(driver, 100);
		while (attemps < 2) {
			try {
				String S4Url = "https://bing.com";
				System.out.println("[S4]: Open S4 website");
				driver.get(S4Url);
				
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id=\"sb_form_q\"]")));
				String userXpath = "//*[@id=\"sb_form_q\"]";
				//System.out.println("[S4]: Input login username " + userName);
				driver.findElement(By.xpath(userXpath)).sendKeys(userName);


				String loginXpath = "//*[@id=\"sb_form_go\"]";
				System.out.println("[S4]: Click login button");
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(loginXpath)));
				driver.findElement(By.xpath(loginXpath)).click();
				break;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("[System]: Catch an alert");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				driver.switchTo().parentFrame();
			}
			attemps++;
		}

	}


	public String creatingExpenseReport() {
		WebDriverWait bigWait = new WebDriverWait(driver, 200);
		WebDriverWait lilWait = new WebDriverWait(driver, 50);
		bigWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
				"//body[@id='cnqrBody']/div[@class='container cnqr-app-header']/div[@class='row']/div[@id='cnqr-navbar-primary']/nav[@id='queryChildren0']/ul[@class='nav navbar-nav']/li[contains(@class,'cnqr-nav-expense')]/a[1]")));

		String expenseXpath = "//body[@id='cnqrBody']/div[@class='container cnqr-app-header']/div[@class='row']/div[@id='cnqr-navbar-primary']/nav[@id='queryChildren0']/ul[@class='nav navbar-nav']/li[contains(@class,'cnqr-nav-expense')]/a[1]";
		System.out.println("[Concur]: click on expense button");
		driver.findElement(By.xpath(expenseXpath)).click();

		lilWait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Create New Report')]")));
		String createNewReportXpath = "//div[contains(text(),'Create New Report')]";
		System.out.println("[Concur]: click on create New Report button");
		driver.findElement(By.xpath(createNewReportXpath)).click();

		lilWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='name']")));
		String reportNameXpath = "//input[@name='name']";
		Random reportNameFixed = new Random();
		String reportName = "JimTest" + reportNameFixed.nextInt();
		System.out.println("[Concur]: input report Name");
		driver.findElement(By.xpath(reportNameXpath)).sendKeys(reportName);
		
		System.out.println("[Concur]: Input cost center id 10101101");
		lilWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(string(),'Cost Object ID')]/../../../..//input")));
		driver.findElement(By.xpath("//label[contains(string(),'Cost Object ID')]/../../../..//input")).sendKeys("10101101");
		lilWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(string(),'10101101')]")));
		driver.findElement(By.xpath("//li[contains(string(),'10101101')]")).click();
		
		String nextButtonXpath = "//button[contains(text(), 'Next')]";
		System.out.println("[Concur]: click on Next button");
		lilWait.until(ExpectedConditions.elementToBeClickable(By.xpath(nextButtonXpath)));
		int temp = 0;
		//screenshot module
		List<File> files = new ArrayList<File>();
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		File f = new File("succssful.png");
		f.deleteOnExit();
		while(temp < 2){
			try {
				driver.findElement(By.xpath(nextButtonXpath)).click();
				break;
			} catch (WebDriverException ex) {
				try {
					new Thread().sleep(3000);
					try {
						FileUtils.copyFile(scrFile, f);
						files.add(f);
						sendmail("mark.liu02@sap.com", "noreply@exchange.sap.corp", "",
						"", files);
					} catch (IOException e1) {
						
					}
				} catch (InterruptedException e) {
				}
			}
			temp++;
		}
		


		String cancelButton = "//button[@id='ext-gen44']";
		lilWait.until(ExpectedConditions.elementToBeClickable(By.xpath(cancelButton)));
		int attemp = 0;
		while(attemp < 2){
			try {
				driver.findElement(By.xpath(cancelButton)).click();
				break;
			} catch (WebDriverException ex) {
				try {
					new Thread().sleep(3000);
				} catch (InterruptedException e) {
				}
			}
			attemp++;
		}

		lilWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Add Expense')]")));
		String addExpenseXpath = "//span[contains(text(),'Add Expense')]";
		System.out.println("[Concur]: click Add Expense button");
		driver.findElement(By.xpath(addExpenseXpath)).click();

		lilWait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//input[@placeholder='Search for an expense type']")));
		String searchExpenseTypeXpath = "//input[@placeholder='Search for an expense type']";
		String expenseType = "Breakfast";
		System.out.println("[Concur]: input expense type to search");
		driver.findElement(By.xpath(searchExpenseTypeXpath)).sendKeys(expenseType);
		String Breakfast = "//*[@role='tabpanel' and @class='tab-pane fade in active']/div/div/div/div/ul/li[1]/div";
		lilWait.until(ExpectedConditions
				.elementToBeClickable(By.xpath(Breakfast)));
		driver.findElement(By.xpath(Breakfast)).click();

		try {
			new Thread().sleep(20000);
		} catch (Exception e) {

		};

		JavascriptExecutor js = (JavascriptExecutor) driver;

        //Find element by link text and store in variable "Element"        		
        WebElement Element = driver.findElement(By.xpath("//select[@name='receiptType']"));

        //This will scroll the page till the element is found		
        js.executeScript("arguments[0].scrollIntoView();", Element);
 

		LocalDateTime localDateTime = LocalDateTime.now();
		String dateFixed = localDateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		System.out.println("[Concur]: fix all the expense detail");
		driver.findElement(By.xpath("//input[@name='transactionDate']")).sendKeys(dateFixed);
		// fix target city
		try {
			driver.findElement(By.xpath("//div[@class='cnqr-popper-target cnqr-autocomplete']")).click();
			Thread.sleep(2000);
			driver.findElement(By.xpath("//li[@class='dropdown-item-mru']//a[@id='57097']")).click();
			Thread.sleep(3000);
		} catch (Exception e) {
			
		}

		System.out.println("[Concur]: target city fixed");
		// fix other infomation
		driver.findElement(By.xpath("//input[@name='transactionAmount']")).sendKeys("2");
		driver.findElement(By.xpath("//select[@name='receiptType']")).click();
		driver.findElement(By.xpath("//option[@value='R']")).click();
		driver.findElement(By.xpath("//div[@class='cnqr-muted btn-toolbar']//span[contains(text(),'Save Expense')]"))
				.click();
	
		String submitButton = "//button[@data-nuiexp='reportActionButtons.submitButton']";
		lilWait.until(ExpectedConditions.elementToBeClickable(By.xpath(submitButton)));
		driver.findElement(By.xpath(submitButton)).click();
		String acButton = "//button[contains(text(),'Accept & Continue')]";
		lilWait.until(ExpectedConditions.elementToBeClickable(By.xpath(acButton)));
		driver.findElement(By.xpath(acButton)).click();
		String submitReportButton = "//button[@class='btn btn-lg btn-default']//span[contains(text(),'Submit Report')]";
		lilWait.until(ExpectedConditions.elementToBeClickable(By.xpath(submitReportButton)));
		driver.findElement(By.xpath(submitReportButton)).click();

		return reportName;

	}



	public void finanicalPosting() {
		WebDriverWait bigWait = new WebDriverWait(driver, 200);
		WebDriverWait lilWait = new WebDriverWait(driver, 50);

		try {
			Thread.sleep(5000);
			String setupTileXpath = "//span[contains(text(),'Con') and contains(text(),'Setup')]";
			bigWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(setupTileXpath)));
			System.out.println("[S4]: tile clicked");
			driver.findElement(By.xpath(setupTileXpath)).click();
			//first screen click on finpost and concur document button
			Thread.sleep(20000);
			String finPostDrag = "//div[@id='FIN_POSTING102']";
			String iframeXpath = "//iframe[@id='application-ConcurIntegrationSetup-manage']";
			bigWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(iframeXpath)));
			driver.switchTo().frame(driver.findElement(By.xpath(iframeXpath)));
			bigWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(finPostDrag)));
			driver.findElement(By.xpath(finPostDrag)).click();
			lilWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Concur Documents')]")));
			driver.findElement(By.xpath("//span[contains(text(),'Concur Documents')]")).click();
			//start feedBack loop
			String feedbackBtn = "//div[string()='Feedback Loop']";
			feedbackLoop(feedbackBtn,"Import Documents",bigWait,false);
			String enterBtn = "//div[@title='Enter (Enter)']";
			bigWait.until(ExpectedConditions.elementToBeClickable(By.xpath(enterBtn)));
			driver.findElement(By.xpath(enterBtn)).click();
			bigWait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(enterBtn)));
			Thread.sleep(3000);
			//navback
			navBack(lilWait);
			feedbackLoop(feedbackBtn,"Send Acknowledgement",bigWait,true);
			driver.switchTo().parentFrame();
			Thread.sleep(3000);
			feedbackLoop(feedbackBtn,"Post",bigWait,true);
			navBack(lilWait);
			driver.switchTo().parentFrame();
			Thread.sleep(3000);
			feedbackLoop(feedbackBtn,"Send Confirmation",bigWait,true);
		} catch (InterruptedException | UnhandledAlertException e) {
			e.printStackTrace();
		}

	}
	private void navBack(WebDriverWait wait) {
		String backBtn = "//a[@id='backBtn']";
		driver.switchTo().defaultContent();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(backBtn)));
		driver.findElement(By.xpath(backBtn)).click();
		
	}

	private void feedbackLoop(String dragboxId,String loopAction,WebDriverWait wait,Boolean switchIframe) {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		if(switchIframe){
			String iframeXpath = "//iframe[@id='application-ConcurIntegrationSetup-manage']";
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(iframeXpath)));
			driver.switchTo().frame(driver.findElement(By.xpath(iframeXpath)));
			
		}
		List<WebElement> iframeList = driver.findElements(By.xpath("//iframe"));
		//System.out.println("[System]: ALL Iframes when " + loopAction + " is -------");
		for (WebElement iframe : iframeList) {
			//System.out.println("[System]: " + iframe.getAttribute("id"));
		}
		System.out.println("[S4]: click on "+loopAction+"(feedback loop)");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='rowheader']")));
		if (!driver.findElement(By.xpath("//*[@role='rowheader']")).isSelected()) {
			driver.findElement(By.xpath("//*[@role='rowheader']")).click();
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(dragboxId)));
		driver.findElement(By.xpath(dragboxId)).click();//feedback loop dragable btn.
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Step-by-Step')]")));
		driver.findElement(By.xpath("//span[contains(text(),'Step-by-Step')]")).click();
		if(loopAction.equalsIgnoreCase("Post")) {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[string()='Post']")));
			driver.findElement(By.xpath("//span[string()='Post']")).click();
		}else {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'"+loopAction+"')]")));
			driver.findElement(By.xpath("//span[contains(text(),'"+loopAction+"')]")).click();
		}
	}

	public void sendingPaymentStatusToConcur() {
		WebDriverWait bigWait = new WebDriverWait(driver,200);
		WebDriverWait lilWait = new WebDriverWait(driver, 30);

		try {
			Thread.sleep(20000);
			String setupTileXpath = "//span[contains(text(),'Con') and contains(text(),'Setup')]";
			bigWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(setupTileXpath)));
			System.out.println("[S4]:tile clicked");
			driver.findElement(By.xpath(setupTileXpath)).click();
			//first screen click on finpost and concur document button
			Thread.sleep(20000);
			String finPostDrag = "//div[@id='FIN_POSTING102']";
			String iframeXpath = "//iframe[@id='application-ConcurIntegrationSetup-manage']";
			lilWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(iframeXpath)));
			driver.switchTo().frame(driver.findElement(By.xpath(iframeXpath)));
			bigWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(finPostDrag)));
			driver.findElement(By.xpath(finPostDrag)).click();
			bigWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Concur Documents')]")));
			driver.findElement(By.xpath("//span[contains(text(),'Concur Documents')]")).click();
			//start feedBack loop
			String feedbackBtn = "//div[@id='TRIGGER122']";
			feedbackLoop(feedbackBtn,"Send Payment Status",bigWait,false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	public void payTheInvoice() {
		WebDriverWait bigWait = new WebDriverWait(driver, 200);
		WebDriverWait lilWait = new WebDriverWait(driver, 50);

		String postoutTileXpath = "//span[contains(text(),'Post Out')]";
		bigWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(postoutTileXpath)));
		System.out.println("[s4]:post outgoing payments tile clicked");
		driver.findElement(By.xpath(postoutTileXpath)).click();
		//fill all the form
		LocalDateTime localDateTime = LocalDateTime.now();
		String dateFixed = localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		fixForm("Journal Entry Date",bigWait,dateFixed);
		fixForm("Value Date",bigWait,dateFixed);
		fixForm("G/L Account",bigWait,"11001000");
		fixForm("Amount",bigWait,"2.00");
		System.out.println("[Concur]:Form fixed.");
		
		String postBtnXpath = "//bdi[string()='Post']/ancestor::button[1]";
		System.out.println("[s4]:post button clicked");
		driver.findElement(By.xpath(postBtnXpath)).click();

		
	}
	private void fixForm(String labelName,WebDriverWait wait,String keyValue) {
		
		String basicXpath = "//bdi[string()='"+labelName+"')]/parent::*/parent::div/following-sibling::div[1]/descendant::input[1]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(basicXpath)));
		driver.findElement(By.xpath(basicXpath)).sendKeys(keyValue);
	}
	public void checkStatus(String reportName) {
		WebDriverWait bigWait = new WebDriverWait(driver, 200);
		WebDriverWait lilWait = new WebDriverWait(driver, 50);

		String expenseXpath = "//body[@id='cnqrBody']/div[@class='container cnqr-app-header']/div[@class='row']/div[@id='cnqr-navbar-primary']/nav[@id='queryChildren0']/ul[@class='nav navbar-nav']/li[contains(@class,'cnqr-nav-expense')]/a[1]";
		bigWait.until(ExpectedConditions.elementToBeClickable(By.xpath(expenseXpath)));
		System.out.println("[Concur]: click on expense button");
		driver.findElement(By.xpath(expenseXpath)).click();

		String activeReportXpath = "//button[@id='reportsFilter']";
		lilWait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath(activeReportXpath)));
		System.out.println("[Concur]: click on create Active Report button");
		driver.findElement(By.xpath(activeReportXpath)).click();

		String daysXpath = "//span[contains(text(),'Last 90 Days')]";
		lilWait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath(daysXpath)));
		System.out.println("[Concur]: click on 90days button");
		driver.findElement(By.xpath(daysXpath)).click();

		String targetReport = "//div[contains(text(),'"+reportName+"')]";
		System.out.println("[Concur]: click on target report"+reportName+" item");
		driver.findElement(By.xpath(targetReport)).click();

		lilWait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='report-status']")));
		String processStatus = driver.findElement(By.xpath("//div[@class='report-status']")).getText();
		System.out.println("[Concur]: Process STATUS " + processStatus);

		
	}
	public void editingExpense(String reportName) {
		WebDriverWait bigWait = new WebDriverWait(driver, 200);
		WebDriverWait lilWait = new WebDriverWait(driver, 50);

		String expenseXpath = "//body[@id='cnqrBody']/div[@class='container cnqr-app-header']/div[@class='row']/div[@id='cnqr-navbar-primary']/nav[@id='queryChildren0']/ul[@class='nav navbar-nav']/li[contains(@class,'cnqr-nav-expense')]/a[1]";
		bigWait.until(ExpectedConditions.elementToBeClickable(By.xpath(expenseXpath)));
		System.out.println("[Concur]: click on expense button");
		driver.findElement(By.xpath(expenseXpath)).click();

		String activeReportXpath = "//button[@id='reportsFilter']";
		lilWait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath(activeReportXpath)));
		System.out.println("[Concur]: click on create Active Report button");
		driver.findElement(By.xpath(activeReportXpath)).click();

		String daysXpath = "//span[contains(text(),'Last 90 Days')]";
		lilWait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath(daysXpath)));
		System.out.println("[Concur]: click on 90days button");
		driver.findElement(By.xpath(daysXpath)).click();

		String targetReportXpath = "//div[contains(text(),'"+reportName+"')]";
		lilWait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath(targetReportXpath)));
		System.out.println("[Concur]: choose the "+reportName+"");
		driver.findElement(By.xpath(targetReportXpath)).click();

		String reportDetailsOpener = "//button[@id='reportDetailsOpener']";
		lilWait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath(reportDetailsOpener)));
		System.out.println("[Concur]: click on the reportDetails");
		driver.findElement(By.xpath(reportDetailsOpener)).click();

		String reportHeader = "//span[contains(text(),'Report Header')]";
		lilWait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath(reportHeader)));
		System.out.println("[Concur]: click on report header");
		driver.findElement(By.xpath(reportHeader)).click();

		//label[contains(text(),'Cost Object Type')]

		String costObjectType = "//label[contains(text(),'Cost Object Type')]";
		lilWait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath(costObjectType)));
		System.out.println("[Concur]: click on report header");
		driver.findElement(By.xpath(costObjectType)).click();

		String costCenter = "//a[contains(text(),'DE')]";
		lilWait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath(costCenter)));
		System.out.println("[Concur]: select cost center (DE)");
		driver.findElement(By.xpath(costCenter)).click();

		String saveEdit = "//button[contains(text(),'Save')]";
		lilWait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath(saveEdit)));
		System.out.println("[Concur]: click save");
		driver.findElement(By.xpath(saveEdit)).click();
	}

	public static void sendmail(String to, String from, String subject, String text, List<File> files) {
		Properties prop = System.getProperties();
		prop.put("mail.smtp.host", "mail.sap.corp");
		prop.put("mail.smtp.auth", "false");
		prop.put("mail.smtp.port", "25"); // default port 25

		Session session = Session.getInstance(prop, null);
		Message msg = new MimeMessage(session);

		try {

			msg.setFrom(new InternetAddress(from));

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));

			msg.setSubject(subject);

			// text
			MimeBodyPart p1 = new MimeBodyPart();
			p1.setText(text);

			Multipart mp = new MimeMultipart();
			mp.addBodyPart(p1);
			// file

			for (File file : files) {
				MimeBodyPart p2 = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(file);
				p2.setDataHandler(new DataHandler(fds));
				p2.setFileName(fds.getName());
				mp.addBodyPart(p2);
			}

			msg.setContent(mp);

			Transport.send(msg);

		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}
	
	public static void commitToGitRepo(List<File> files, String reportName, StringBuffer logger,String username, String password) {

		System.out.println();
		
		String myReopName = reportName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

		SimpleDateFormat myformat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		
	
		try (Git git = Git.open(new File(System.getProperty("user.dir")))) {
			System.out.println("Having repository: " + git.getRepository().getDirectory());
			
			System.out.println(git.getRepository().getConfig().getString("remote", "origin", "url"));
			
			Repository repo = git.getRepository();

			String subdir = "result/" + myReopName + "_" + myformat.format(System.currentTimeMillis());
			
			String newdirstr = repo.getDirectory().getParent() + '/' + subdir;

			File newdir = new File(newdirstr);

			newdir.mkdirs();
			
			for (File file : files) {		
				
				File targetfile = new File(newdir, file.getName());			
				FileUtils.copyFile(file, targetfile);

			}
			
			File logfile = new File(newdir, "result.txt");
			
			System.out.println("logfile: " + logfile.getAbsolutePath());
			
			logfile.createNewFile();
			try (PrintWriter out = new PrintWriter(logfile)) {
				out.print(logger.toString());
			}
			
			git.add().addFilepattern(subdir).call();
			
			git.commit().setMessage("from STEP").call();
			git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call();

			
		} catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			System.out.println(e.getMessage());
		} 
//		} else {
//			System.out.println("private.key not found, cannot upload results to git.");
//		}
		
	}

	
	public void exit() {
		System.out.println("[Concur]: Test End");
		driver.close();
	}
}
