package com.example.socialcoffee;

import com.example.socialcoffee.domain.AuthProvider;
import com.example.socialcoffee.domain.Role;
import com.example.socialcoffee.enums.AuthProviderEnum;
import com.example.socialcoffee.enums.RoleEnum;
import com.example.socialcoffee.repository.AuthProviderRepository;
import com.example.socialcoffee.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@RequiredArgsConstructor
public class SocialcoffeeApplication {

    private final RoleRepository roleRepository;
    private final AuthProviderRepository authProviderRepository;

	public static void main(String[] args) {
		SpringApplication.run(SocialcoffeeApplication.class, args);
	}

    @PostConstruct
    public void init() {
        // role
        if (!roleRepository.existsByName(RoleEnum.USER.getValue())) {
            roleRepository.save(new Role(RoleEnum.USER.getValue()));
        }
        if (!roleRepository.existsByName(RoleEnum.ADMIN.getValue())) {
            roleRepository.save(new Role(RoleEnum.ADMIN.getValue()));
        }

        // auth provider
        if (!authProviderRepository.existsByName(AuthProviderEnum.GOOGLE.getValue())) {
            authProviderRepository.save(new AuthProvider(AuthProviderEnum.GOOGLE.getValue()));
        }
        if (!authProviderRepository.existsByName(AuthProviderEnum.FACEBOOK.getValue())) {
            authProviderRepository.save(new AuthProvider(AuthProviderEnum.FACEBOOK.getValue()));
        }
    }

}
