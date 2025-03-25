package com.example.socialcoffee.repository;

import com.example.socialcoffee.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
