package com.github.cbuschka.workspace_mechanic;

import com.github.cbuschka.workspace_mechanic.internal.cli.CommandDispatcher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
@PropertySource(value = "${mechanic.config:none}:", ignoreResourceNotFound = true)
public class Main
{
	public static void main(String[] args)
	{
		SpringApplication application = new SpringApplication(Main.class);
		ConfigurableApplicationContext appContext = application.run(args);
		int result = run(args, appContext);
		appContext.close();

		System.exit(result);
	}

	private static int run(String[] args, ConfigurableApplicationContext appContext)
	{
		CommandDispatcher commandDispatcher = appContext.getBean(CommandDispatcher.class);
		return commandDispatcher.run(args);
	}
}
