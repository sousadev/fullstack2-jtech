package br.com.jtech.tasklist.config.infra.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    private String secret;
    private Long expiration;
    private Long refreshExpiration;
}
