package com.spirit.Citizen.services.user;

import com.spirit.Citizen.model.Demande;
import com.spirit.Citizen.model.Signalement;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${email.username}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String username) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Bienvenue chez Citizen");

            String htmlMsg = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<div style='text-align: center; margin-bottom: 20px;'>" +
                    "<img src='https://i.pinimg.com/736x/74/c5/7d/74c57d1e425149ca1fb9839dc6c99595.jpg' alt='Company Logo' style='max-width: 150px;' />" +
                    "</div>" +
                    "<h2 style='color: #4CAF50;'>Bienvenue, " + username + " !</h2>" +
                    "<p>Nous sommes ravis de vous accueillir dans notre communauté.</p>" +
                    "<p style='margin-top: 20px;'>Pour commencer, nous vous encourageons à explorer votre compte.</p>" +

                    "<p>Si vous avez des questions, n'hésitez pas à nous contacter.</p>" +
                    "<p>Cordialement,<br> </p>" +
                    "<div style='text-align: center; margin-top: 30px; font-size: 12px; color: #777;'>" +
                    "<p>&copy; 2024 . Tous droits réservés.</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlMsg, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email de bienvenue à {}: {}", toEmail, e.getMessage());
            throw new IllegalStateException("Erreur lors de l'envoi de l'email de bienvenue.", e);
        }
    }


    ////////////////////////////////////// Signalement ////////////////////////////////
    public void sendSignalementReceptionEmail(String toEmail, Signalement signalement) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Confirmation de Réception de Votre Signalement");

            String htmlMsg = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<h2 style='color: #4CAF50;'>Votre signalement a été reçu !</h2>" +
                    "<img src='https://i.pinimg.com/736x/74/c5/7d/74c57d1e425149ca1fb9839dc6c99595.jpg' alt='Company Logo' style='max-width: 150px;' />" +
                    "<p>Bonjour, " + signalement.getUser().getProfil().getPseudo() +"</p>" +
                    "<p>Nous vous remercions d'avoir soumis le signalement, dont les détails suivent:</p>" +
                    "<p><strong>Description :</strong> " + signalement.getDescription() + "</p>" +
                    "<p><strong>Type :</strong> " + signalement.getCategories() + "</p>" +
                    "<p><strong>Localisation :</strong> " + signalement.getLocalisation() + "</p>" +
                    "<p>Votre signalement sera traité dans les plus brefs délais.</p>" +
                    "<p>Si vous avez des questions, n'hésitez pas à nous contacter.</p>" +
                    "<p>Cordialement,<br> equipe de CITIZEN </p>" +
                    "<div style='text-align: center; margin-top: 30px; font-size: 12px; color: #777;'>" +
                    "<p>&copy; 2024 . Tous droits réservés.</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlMsg, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email de confirmation pour le signalement à {}: {}", toEmail, e.getMessage());
            throw new IllegalStateException("Erreur lors de l'envoi de l'email de confirmation.", e);
        }
    }
    public void sendSignalementTermineEmail(String toEmail, Signalement signalement) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Traitement de Votre Signalement Terminé");

            String htmlMsg = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<h2 style='color: #4CAF50;'>Le traitement de votre signalement est terminé.</h2>" +
                    "<p>Bonjour, " + signalement.getUser().getProfil().getPseudo() +"</p>" +
                    "<p>Nous vous informons que le traitement de votre signalement est maintenant terminé. Voici les détails :</p>" +
                    "<p><strong>Description :</strong> " + signalement.getDescription() + "</p>" +
                    "<p><strong>Type :</strong> " + signalement.getCategories() + "</p>" +
                    "<p><strong>Localisation :</strong> " + signalement.getLocalisation() + "</p>" +
                    "<p>Merci de votre patience et de votre compréhension.</p>" +
                    "<p>Cordialement,<br> </p>" +
                    "<div style='text-align: center; margin-top: 30px; font-size: 12px; color: #777;'>" +
                    "<p>&copy; 2024 . Tous droits réservés.</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlMsg, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email de fin de traitement pour le signalement à {}: {}", toEmail, e.getMessage());
            throw new IllegalStateException("Erreur lors de l'envoi de l'email de fin de traitement.", e);
        }
    }
    public void sendSignalementEnTraitementEmail(String toEmail, Signalement signalement) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Votre Signalement est en Traitement");

            String htmlMsg = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<h2 style='color: #FFA500;'>Votre signalement est maintenant en traitement.</h2>" +
                    "<p>Bonjour, " + signalement.getUser().getProfil().getPseudo() +"</p>" +
                    "<p>Nous vous informons que votre signalement a été pris en charge. Voici les détails :</p>" +
                    "<p><strong>Description :</strong> " + signalement.getDescription() + "</p>" +
                    "<p><strong>Type :</strong> " + signalement.getCategories() + "</p>" +
                    "<p><strong>Localisation :</strong> " + signalement.getLocalisation() + "</p>" +
                    "<p>Nous vous tiendrons informé de l'avancement du traitement.</p>" +
                    "<p>Cordialement,<br> </p>" +
                    "<div style='text-align: center; margin-top: 30px; font-size: 12px; color: #777;'>" +
                    "<p>&copy; 2024 . Tous droits réservés.</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlMsg, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email de traitement pour le signalement à {}: {}", toEmail, e.getMessage());
            throw new IllegalStateException("Erreur lors de l'envoi de l'email de traitement.", e);
        }
    }


    ///////////////////////////////////////// Demande ////////////////////////////////

    public void sendDemandeReceptionEmail(String toEmail, Demande demande) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Confirmation de Réception de Votre Signalement");

            String htmlMsg = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<h2 style='color: #4CAF50;'>Votre signalement a été reçu !</h2>" +
                    "<img src='https://i.pinimg.com/736x/74/c5/7d/74c57d1e425149ca1fb9839dc6c99595.jpg' alt='Company Logo' style='max-width: 150px;' />" +
                    "<p>Bonjour, " + demande.getUser().getProfil().getPseudo() +"</p>" +
                    "<p>Nous vous remercions d'avoir pour votre demande, dont le sujet suit:</p>" +
                    "<p><strong>Sujet :</strong> " + demande.getSujet() + "</p>" +
                    "<p>Votre demande sera etudié dans les plus brefs délais.</p>" +
                    "<p>Si vous avez des questions, n'hésitez pas à nous contacter.</p>" +
                    "<p>Cordialement,<br> equipe de CITIZEN </p>" +
                    "<div style='text-align: center; margin-top: 30px; font-size: 12px; color: #777;'>" +
                    "<p>&copy; 2024 . Tous droits réservés.</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlMsg, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email de confirmation pour le signalement à {}: {}", toEmail, e.getMessage());
            throw new IllegalStateException("Erreur lors de l'envoi de l'email de confirmation.", e);
        }
    }
    public void sendDemandeAppouverEmail(String toEmail, Demande demande) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Traitement de Votre Signalement Terminé");

            String htmlMsg = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<h2 style='color: #4CAF50;'>Le traitement de votre signalement est terminé.</h2>" +
                    "<p>Bonjour, " + demande.getUser().getProfil().getPseudo() +"</p>" +
                    "<p>Nous vous informons que votre demande portant sur le sujet :</p>" +
                    "<p><strong>Sujet :</strong> " + demande.getSujet() + " a ete approuvee. Nous vous tiendrons informer des future evenement lié à cette demande</p>" +
                    "<p>Merci de votre patience et de votre compréhension.</p>" +
                    "<p>Cordialement,<br> </p>" +
                    "<div style='text-align: center; margin-top: 30px; font-size: 12px; color: #777;'>" +
                    "<p>&copy; 2024 . Tous droits réservés.</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlMsg, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email de fin de traitement pour le signalement à {}: {}", toEmail, e.getMessage());
            throw new IllegalStateException("Erreur lors de l'envoi de l'email de fin de traitement.", e);
        }
    }
    public void sendDemandeRejetEmail(String toEmail, Demande demande) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Votre Signalement est en Traitement");

            String htmlMsg = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<h2 style='color: #FFA500;'>Votre signalement est maintenant en traitement.</h2>" +
                    "<p>Bonjour, " + demande.getUser().getProfil().getPseudo() +"</p>" +
                    "<p>Nous soummes au regret de vous informer du rejet de votre demande :</p>" +
                    "<p><strong>Sujet :</strong> " + demande.getSujet()+ "</p>" +
                    "<p>Nous vous tiendrons informé des prochain evenement lié a cette demande.</p>" +
                    "<p>Cordialement,<br> </p>" +
                    "<div style='text-align: center; margin-top: 30px; font-size: 12px; color: #777;'>" +
                    "<p>&copy; 2024 . Tous droits réservés.</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlMsg, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email de traitement pour le signalement à {}: {}", toEmail, e.getMessage());
            throw new IllegalStateException("Erreur lors de l'envoi de l'email de traitement.", e);
        }
    }

}
