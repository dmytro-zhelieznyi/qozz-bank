package io.qozz.qozzbank.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class JwtConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${supabase.anon-key}")
    private String anonKey;

    @Bean
    public JwtDecoder jwtDecoder() {
        // 1. Создаем RestTemplate для доступа к защищенному JWKS
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            // Supabase требует anon-key (publishable key) для доступа к этому эндпоинту
            request.getHeaders().add("apikey", anonKey);
            return execution.execute(request, body);
        });

        // 2. Настраиваем декодер на правильный URL и алгоритм ES256
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .restOperations(restTemplate)
                .jwsAlgorithm(SignatureAlgorithm.ES256) // Для твоего ECC P-256
                .build();

        // 3. Валидатор издателя (Issuer)
        // ВАЖНО: Проверь в jwt.io, чтобы в токене поле 'iss' СОВПАДАЛО с issuerUri
        jwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuerUri));

        return jwtDecoder;
    }
}