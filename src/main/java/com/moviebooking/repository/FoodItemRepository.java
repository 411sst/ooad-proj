package com.moviebooking.repository;

import com.moviebooking.entity.FoodItem;
import com.moviebooking.entity.enums.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    List<FoodItem> findByCategory(FoodCategory category);

    List<FoodItem> findByIsAvailableTrue();

    List<FoodItem> findByCategoryAndIsAvailableTrue(FoodCategory category);
}
