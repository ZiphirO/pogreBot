package com.ziphiro.podBot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bot")
public record BotTokenConfig(String token) {

    @Override
    public String token() {
        return "7488437736:AAHOL83XFUsl_RJeI4ql9iby8uXbhyPop5M";
    }
}
