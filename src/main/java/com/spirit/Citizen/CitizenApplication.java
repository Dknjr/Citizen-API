package com.spirit.Citizen;

import com.spirit.Citizen.model.User;
import com.spirit.Citizen.model.UserRole;
import com.spirit.Citizen.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootApplication
public class CitizenApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(CitizenApplication.class);

	@Autowired
	private UserRepository userRepository;

	// Compte administrateur initial, lu dans ADMIN_EMAIL et ADMIN_PASSWORD.
	// S'ils ne sont pas définis, aucun compte n'est créé (plus de compte admin/admin par défaut).
	@Value("${app.admin.email:}")
	private String adminEmail;

	@Value("${app.admin.password:}")
	private String adminPassword;

	public static void main(String[] args) {
		SpringApplication.run(CitizenApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<User> adminAccount = userRepository.findByUserRole(UserRole.ADMIN);
		if (!adminAccount.isEmpty()) {
			return;
		}
		if (adminEmail.isBlank() || adminPassword.isBlank()) {
			log.warn("Aucun compte administrateur : définissez ADMIN_EMAIL et ADMIN_PASSWORD pour en créer un au démarrage.");
			return;
		}
		User user = new User();
		user.setEmail(adminEmail);
		user.setUserRole(UserRole.ADMIN);
		user.setPassword(new BCryptPasswordEncoder().encode(adminPassword));
		userRepository.save(user);
		log.info("Compte administrateur créé pour {}", adminEmail);
	}

}
