package com.easydb.easydb.environments;

import java.util.Optional;
import joptsimple.OptionParser;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.JOptCommandLinePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import static com.google.common.collect.Lists.newArrayList;

public class ApplicationEnvironmentPreparedListener implements
        ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    private final static String ACTIVE_ENVIRONMENT = "application.environment";
    private final static String SERVER_PORT = "server.port";
    private final static String CMD_ARGS = "commandLineArgs";

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();

        MutablePropertySources propertySources = environment.getPropertySources();
        PropertySource<?> cmdLinePropertySource = cmdLinePropertySource(event.getArgs());
        propertySources.addFirst(cmdLinePropertySource);

        Optional<String> profile = Optional.ofNullable(environment.getProperty(ACTIVE_ENVIRONMENT));
        environment.addActiveProfile(profile.orElse("local"));
    }

    private PropertySource<?> cmdLinePropertySource(String[] args) {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(newArrayList(ACTIVE_ENVIRONMENT, "environment")).withOptionalArg();
        parser.acceptsAll(newArrayList(SERVER_PORT, "port")).withRequiredArg();
        parser.allowsUnrecognizedOptions();
        return new JOptCommandLinePropertySource(CMD_ARGS, parser.parse(args));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
