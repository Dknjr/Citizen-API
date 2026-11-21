package com.spirit.Citizen;

import com.spirit.Citizen.services.user.GoogleCloudStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CitizenApplicationTests {

	// Remplacé par un faux pour ne pas avoir besoin d'identifiants Google Cloud en test
	@MockBean
	private GoogleCloudStorageService googleCloudStorageService;

	@Test
	void contextLoads() {
	}

}
