package org.example.stockradar.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.security.auth.login.LoginException;

/**
 * @author Hyun7en
 */

@Configuration
public class JdaConfig {

    @Value("${discord.bot.token}")
    private String botToken;

    @Bean
    public JDA jda() throws LoginException, InterruptedException {
        return JDABuilder.createDefault(botToken)
                .build()
                .awaitReady();
    }
}