package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {
}
