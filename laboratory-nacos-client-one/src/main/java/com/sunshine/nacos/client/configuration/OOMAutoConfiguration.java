package com.sunshine.nacos.client.configuration;

import com.sunshine.nacos.client.event.RefreshEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author <sunshine> mysunshinedreams@163.com
 * @date 2018-12-13 17:06
 */
@RefreshScope
@Configuration
public class OOMAutoConfiguration {

	@Value("${jdbc.url}")
	private String jdbcUrl;
	@Value("${jdbc.username}")
	private String jdbcUsername;
	@Value("${jdbc.password}")
	private String jdbcPassword;

	@Autowired
	private ApplicationContext applicationContext;

	private static final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setName("sunshine-refresh");
			t.setDaemon(true);
			return t;
		}
	});

	{
		this.executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					publish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 5, 1, TimeUnit.SECONDS);
	}

	private void publish() {
		RefreshEvent refreshEvent = new RefreshEvent(this, this.jdbcUrl, this.jdbcUsername, this.jdbcPassword);
		applicationContext.publishEvent(refreshEvent);
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public String getJdbcUsername() {
		return jdbcUsername;
	}

	public String getJdbcPassword() {
		return jdbcPassword;
	}
}
