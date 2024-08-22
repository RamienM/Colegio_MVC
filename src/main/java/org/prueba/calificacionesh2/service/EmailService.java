package org.prueba.calificacionesh2.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.context.Context;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailService {

    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String nombreDestinatario, String cuerpoMensaje, String nombreRemitente){
        try {
            // Crear un MimeMessage
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            // Configurar destinatario, asunto y remitente
            helper.setTo(to);
            helper.setSubject(subject);

            // Crear contexto de Thymeleaf
            Context context = new Context();
            context.setVariable("nombreDestinatario", nombreDestinatario);
            context.setVariable("cuerpoMensaje", cuerpoMensaje);
            context.setVariable("nombreRemitente", nombreRemitente);

            // Procesar la plantilla Thymeleaf
            String htmlContent = templateEngine.process("/email/email.html", context);
            helper.setText(htmlContent, true); // true indica que es HTML

            // Enviar el correo
            mailSender.send(mimeMessage);
        }catch (MessagingException e){
            System.out.println("Imposible mandar correo, se ha producido un error. Es posible que el correo o contraseña " +
                    "de aplicación no estén bien añadidos o no este en uso");
        }

    }
}
