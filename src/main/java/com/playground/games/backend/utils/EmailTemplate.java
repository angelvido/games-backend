package com.playground.games.backend.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EmailTemplate {
    public static String getConfirmationEmailTemplate(String token, String confirmationUrl) throws IOException {
        // Cargar el contenido del archivo HTML de la plantilla
        String templatePath = "com/playground/games/backend/utils/templates/EmailTemplate.html"; // Ruta al archivo de plantilla HTML
        ClassPathResource resource = new ClassPathResource(templatePath);
        byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
        String templateContent = new String(bytes, StandardCharsets.UTF_8);

        // Reemplazar la URL de confirmación en la plantilla
        return templateContent.replace("{{confirmation_url}}", confirmationUrl + token);
    }
}
