package com.example.socialcoffee.repository.postgres.feature;

import com.example.socialcoffee.domain.feature.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
