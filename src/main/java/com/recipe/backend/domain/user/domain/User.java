package com.recipe.backend.domain.user.domain;

import com.recipe.backend.domain.inventory.domain.Inventory;
import com.recipe.backend.domain.rating.domain.RecipeRating;
import com.recipe.backend.global.TimeStamp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(nullable = false,unique = true, length = 30)
    private String username;

    @Column(nullable = false,unique = true, length = 30)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Inventory inventory;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeRating> ratings = new ArrayList<>();
}
