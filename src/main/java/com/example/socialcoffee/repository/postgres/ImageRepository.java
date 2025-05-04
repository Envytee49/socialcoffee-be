package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
