package com.scheduled.job;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import com.scheduled.model.User;
import com.scheduled.service.EmailService;
import com.scheduled.service.UserService;

@Component
public class BirthdayJob implements InitializingBean {

	Log log = LogFactory.getLog(BirthdayJob.class);

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserService userService;

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler(); // single threaded by default
	}

	private AtomicInteger count = new AtomicInteger(0);

	@Scheduled(cron = "0 0 0 * * ?")
	public void executeBirthdayJob() {

		log.info("Spring Scheduler starts.." + count.incrementAndGet());

		try {
			List<User> users = userService.getUserBornToday();

			for (User user : users) {
				emailService.sendEmail(user);
			}

		} catch (MessagingException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (emailService == null)
			throw new Exception("Email Service not initiliazed");
		if (userService == null)
			throw new Exception("User Service not initialized");
	}

}
