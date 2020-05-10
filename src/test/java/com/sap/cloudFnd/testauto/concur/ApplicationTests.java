package com.sap.cloudFnd.testauto.concur;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void concur1M1Test() {
		System.out.println("[Concur]: 1M1 scope item auto-test start");
		
		List<File> files = new ArrayList<File>();
		String name = "ScopeItem_1M1";
		StringBuffer logger = new StringBuffer();

		try {

			Steps step = new Steps();
//			step.loginConcur("kavana.wang_CC8100@test.com", "Welcome1!");
//			String reportName = step.creatingExpenseReport();
//			step.loginS4("CONF_EXPERT_BUS_NET_INT", "Welcome1!");
//			step.finanicalPosting();
			step.loginS4("AP_ACCOUNTANT", "Welcome1!");
//			step.payTheInvoice();
//			step.loginS4("CONF_EXPERT_BUS_NET_INT", "Welcome1!");
//			step.sendingPaymentStatusToConcur();
//			step.loginConcur("kavana.wang_CC8100@test.com", "Welcome1!");
//			step.checkStatus(reportName);
//			step.editingExpense(reportName);
			step.exit();
		} catch (Exception e) {
			
		} finally {
//			Steps.sendmail("jim.zheng01@sap.com", "noreply@exchange.sap.corp", "run script \"" + name + "\" finished.",
//					logger.toString(), files);
			// Steps.sendmail("joyce.zhang@sap.com", "noreply@exchange.sap.corp", "run
			// script \"" + name + "\" finished.",
			// logger.toString(), files);

			// Steps.commitToGitRepo(url, files, name, logger, "tuscrum2", "Welcome2");

			//Steps.commitToGitRepo(files, name, logger, "tuscrum2", "Welcome2");
		}

	}

}
