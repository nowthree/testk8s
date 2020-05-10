package com.sap.start.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.lib.Repository;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.google.gson.Gson;
import com.sap.start.run.bean.Step;
import com.sap.start.run.bean.TaBean;
import com.sap.start.salesorder.selenium.webdriver.MultipleWebDriverManager;

public class RunScript {


	public static void run(File script) throws Exception {


		System.out.println("Run script: " + script.getAbsolutePath());

		Map<Integer, WebDriver> browsermap = new HashMap<Integer, WebDriver>();
		List<File> files = new ArrayList<File>();
		String name = null;
		String stepName = null;
		String fileName = null;
		int i = 0;
		StringBuffer logger = new StringBuffer();
		StringBuffer detailLogger = new StringBuffer();

		try {

			int current_browser_num = 0;
			boolean compare_flag = false;
			WebDriver browser = null;

			Gson gson = new Gson();
			TaBean bean = (TaBean) gson.fromJson(new FileReader(script), TaBean.class);
			Map<String, String> variables = bean.getTechVarables();
			Map<String, String> value_pool = new HashMap<String, String>();
			List<Step> steps = bean.getSteps();

			System.out.println("Start to run test case: " + bean.getName());
			logger.append("Start to run test case: " + bean.getName());
			logger.append(System.lineSeparator());

			name = bean.getName();

			TaAction taaction = new TaAction();

			try {

				for (; i < steps.size(); i++) {

					Step step = steps.get(i);

					String action = step.getAction();
					String techUISelector = step.getTechUISelector();
					String parameter = step.getParameter();
					String subSelector = step.getSubSelector();
					String techIdletime = step.getTechIdletime();
					String parameterType = step.getParameterType();
					String optional = step.getOptional();

					stepName = step.getStepName();

					fileName = stepName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

					if (browsermap.get(0) == null) {
						browser = new MultipleWebDriverManager().getWebDriver();
						browsermap.put(0, browser);
					}

					if (step.getBrowserSession() != current_browser_num) {
						current_browser_num = step.getBrowserSession();

						if (browsermap.get(current_browser_num) == null) {
							browser = new MultipleWebDriverManager().getWebDriver();
							browsermap.put(current_browser_num, browser);
						} else {
							browser = browsermap.get(current_browser_num);
						}

					}

					if ("Variable".equals(step.getParameterType()) || "Credential".equals(step.getParameterType())) {
						parameter = variables.get(parameter);
					}

					String method = null;
					//assign a default method
					if ("JumpIfTrue".equals(step.getAction())) {
						parameter = (Integer.valueOf(step.getParameter()).intValue() - 1) + "";
						if (compare_flag) {
							i = Integer.valueOf(step.getParameter());
						}
						continue;
					} else if ("JumpIfFalse".equals(step.getAction())) {
						parameter = (Integer.valueOf(step.getParameter()).intValue() - 1) + "";
						if (!compare_flag) {
							i = Integer.valueOf(step.getParameter());
						}
						continue;
					} else if ("CompareText".equals(step.getAction())) {
						method = "CompareText";
					} else if ("Checked".equals(step.getAction())) {
						method = "CompareBool";
					} else if ("Enter".equals(step.getAction())) {
						method = "OpenURL";
					} else if ("Click".equals(step.getAction())) {
						method = "ClickButton";
					} else if ("Input".equals(step.getAction())) {
						method = "FindInputBox";
					} else if ("Read".equals(step.getAction())) {
						method = "ReadValueAndSave";
					} else if ("Assert".equals(step.getAction())) {
						method = "ReadValueAssert";
					}
					
					//assign the specified method
					if (step.getTechUIMethod() != null && !"".equals(step.getTechUIMethod())) {
						method = step.getTechUIMethod();
					}

					HashMap<String, String> param = new HashMap<String, String>();
					param.put("name", step.getStepName());
					param.put("action", action);
					param.put("techUISelector", techUISelector);
					param.put("techUISelector", techUISelector);
					param.put("parameter", parameter);
					param.put("subSelector", subSelector);
					param.put("techIdletime", techIdletime);
					param.put("optional", optional);

					Method methodToCall = TaAction.class.getMethod(method, Map.class,
							org.openqa.selenium.WebDriver.class);

					if (!"Internal Variable".equals(parameterType)) {
						parameterType = "Others";
					}
					if (!"ReadValueAndSave".contentEquals(method) && !"RandomID".contentEquals(method) && !"CompareBool".contentEquals(method)
							&& !"CompareText".contentEquals(method)) {
						method = "Others";
					}

					String parameterTypeMethod = parameterType + " + " + method;

					try {
						if ("Internal Variable + ReadValueAndSave".equals(parameterTypeMethod) || 
								"Internal Variable + RandomID".equals(parameterTypeMethod)) {
							value_pool.put(parameter, methodToCall.invoke(taaction, param, browser).toString());
						} else if ("Internal Variable + CompareBool".equals(parameterTypeMethod)
								|| "Internal Variable + CompareText".equals(parameterTypeMethod)) {
							parameter = value_pool.get(parameter);
							param.put("parameter", parameter);
							compare_flag = (boolean) methodToCall.invoke(taaction, param, browser);
						} else if ("Internal Variable + Others".equals(parameterTypeMethod)) {
							parameter = value_pool.get(parameter);
							param.put("parameter", parameter);
							methodToCall.invoke(taaction, param, browser);
						} else if ("Others + CompareBool".equals(parameterTypeMethod)
								|| "Others + CompareText".equals(parameterTypeMethod)) {
							compare_flag = (boolean) methodToCall.invoke(taaction, param, browser);
						}
						if ("Others + Others".equals(parameterTypeMethod)) {
							methodToCall.invoke(taaction, param, browser);
						}
					} catch (Exception e) {
						if (ExceptionUtils.getStackTrace(e).contains("RuntimeException: SKIP")) {
							System.out.println("Run test step: " + i + " ... Skipped");
							logger.append("Run test step: " + i + "_" + fileName + " ... Skipped");
							logger.append(System.lineSeparator());
							continue;
						} else {
							detailLogger.append(ExceptionUtils.getStackTrace(e));
							throw e;
						}
					}

					System.out.println("Run test step: " + i + " ... Successful");
					logger.append("Run test step: " + i + "_" + fileName + " ... Successful");
					logger.append(System.lineSeparator());
				}
				File scrFile;
				File f;
				for (Entry<Integer, WebDriver> entry : browsermap.entrySet()) {
					scrFile = ((TakesScreenshot) entry.getValue()).getScreenshotAs(OutputType.FILE);
					f = new File("run_last_" + i + "_" + fileName + "_succssful_page" + entry.getKey().toString() + ".png");
					f.deleteOnExit();
					FileUtils.copyFile(scrFile, f);
					files.add(f);
				}
			} catch (Exception e) {
				System.out.println("Run test step: " + i + " ... Failed");
				logger.append("Run test step: " + i + "_" + fileName + " ... Failed");
				logger.append(System.lineSeparator());
				
				File scrFile;
				File f;
				for (Entry<Integer, WebDriver> entry : browsermap.entrySet()) {
					scrFile = ((TakesScreenshot) entry.getValue()).getScreenshotAs(OutputType.FILE);
					f = new File("run_last_" + i + "_" + fileName + "_failed_page" + entry.getKey().toString() + ".png");
					f.deleteOnExit();
					FileUtils.copyFile(scrFile, f);
					files.add(f);
				}
				throw e;
			}

		} catch (Exception e) {
			throw e;
		} finally {
			Platform currentPlatform = Platform.getCurrent().family();
			switch (currentPlatform) {
			case WINDOWS:
				sendmail("jim.zheng01@sap.com", "noreply@exchange.sap.corp", "run script \"" + name + "\" finished.",
						detailLogger.toString(), files);
				break;
			case UNIX:
				sendmail("jim.zheng01@sap.com", "noreply@exchange.sap.corp", "run script \"" + name + "\" finished.",
						detailLogger.toString(), files);
	            sendmail("justin.cao@sap.com", "noreply@exchange.sap.corp", "run script \"" + name + "\" finished.",
						logger.toString(), files);
	            sendmail("joyce.zhang@sap.com", "noreply@exchange.sap.corp", "run script \"" + name + "\" finished.",
	            		logger.toString(), files);
				commitToGitRepo(files, name, detailLogger);
				break;
			default:
				break;
			}
						
			for (Entry<Integer, WebDriver> entry : browsermap.entrySet()) {
				entry.getValue().close();
			}
		}

	}

	private static void sendmail(String to, String from, String subject, String text, List<File> files) {
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

	public static void commitToGitRepo(List<File> files, String reportName, StringBuffer logger) {
		String myReopName = reportName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
		SimpleDateFormat myformat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		File localPath = new File(System.getProperty("java.io.tmpdir") + "/gittest");

		File privatekey = new File(System.getProperty("user.dir") + "/key", "private.key");

		System.out.println(privatekey.getAbsolutePath());
		
		if (privatekey.exists()) {
			
			String url = null;
			

			TransportConfigCallback transportConfigCallback = new SshTransportConfigCallback();

			try {
				Git git = Git.open(new File(System.getProperty("user.dir")));
				System.out.println("Opening repository: " + git.getRepository().getDirectory());
				url = git.getRepository().getConfig().getString("remote", "origin", "url");
				System.out.println("http url: " + url);
				url = url.replace("https://github.wdf.sap.corp/", "git@github.wdf.sap.corp:");
				System.out.println("ssh url: " + url);
				
				git = Git.cloneRepository().setURI(url).setTransportConfigCallback(transportConfigCallback).setDirectory(localPath).call();
				
				System.out.println("Having repository: " + git.getRepository().getDirectory());

				System.out.println("clone url: " + git.getRepository().getConfig().getString("remote", "origin", "url"));

				Repository repo = git.getRepository();

				String subdir = "result/" + myReopName + "_" + myformat.format(System.currentTimeMillis());

				String newdirstr = repo.getDirectory().getParent() + '/' + subdir;

				File newdir = new File(newdirstr);

				newdir.mkdir();

				for (File file : files) {

					File targetfile = new File(newdir, file.getName());
					FileUtils.copyFile(file, targetfile);

				}

				File logfile = new File(newdir, "result.txt");
				logfile.createNewFile();
				try (PrintWriter out = new PrintWriter(logfile)) {
					out.print(logger.toString());
				}

				git.add().addFilepattern(subdir).call();

				git.commit().setCommitter("tuscrum2", "dl_5df9eaabd2a050027f2d67de@global.corp.sap").setMessage("from STEP").call();
				git.push().setTransportConfigCallback(transportConfigCallback).call();

			} catch (NoFilepatternException e) {
				e.printStackTrace();
			} catch (GitAPIException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("private.key not found, cannot upload results to git.");
		}

	}

}
