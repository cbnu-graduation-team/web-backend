package com.recipe.backend.domain.inventory.service;// InventoryService.java
import com.recipe.backend.domain.ingredient.domain.Ingredient;
import com.recipe.backend.domain.ingredient.repository.IngredientRepository;
import com.recipe.backend.domain.inventory.domain.Inventory;
import com.recipe.backend.domain.inventory.domain.InventoryIngredient;
import com.recipe.backend.domain.inventory.dto.IngredientRequest;
import com.recipe.backend.domain.inventory.dto.IngredientResponseDTO;
import com.recipe.backend.domain.inventory.dto.IngredientsRequest;
import com.recipe.backend.domain.inventory.repository.InventoryIngredientRepository;
import com.recipe.backend.domain.inventory.repository.InventoryRepository;
import com.recipe.backend.domain.user.domain.User;
import com.recipe.backend.domain.user.repository.UserRepository;
import com.recipe.backend.global.response.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final InventoryIngredientRepository inventoryIngredientRepository;

    public void addIngredientsToInventory(IngredientsRequest ingredientsRequest, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        Inventory inventory = inventoryRepository.findByUser(user).orElse(new Inventory());
        inventory.setUser(user);

        List<String> existingIngredientNames = inventory.getInventoryIngredients().stream()
                .map(ii -> ii.getIngredient().getName())
                .collect(Collectors.toList());

        for (IngredientRequest ingredientReq : ingredientsRequest.getIngredients()) {
            String ingredientName = ingredientReq.getName();
            if(!existingIngredientNames.contains(ingredientName)) {
                Ingredient ingredient = ingredientRepository.findByName(ingredientName)
                        .orElseGet(() -> {
                            Ingredient newIngredient = new Ingredient(ingredientName);
                            ingredientRepository.save(newIngredient);
                            return newIngredient;
                        });

                // 재료와 인벤토리를 연결하는 InventoryIngredient 생성 및 설정
                InventoryIngredient inventoryIngredient = new InventoryIngredient();
                inventoryIngredient.setInventory(inventory);
                inventoryIngredient.setIngredient(ingredient);
                inventoryIngredient.setExpiryDate(ingredientReq.getExpiryDate());
                inventory.getInventoryIngredients().add(inventoryIngredient);
            }
        }

        inventoryRepository.save(inventory);
    }


    public List<IngredientResponseDTO> getUserInventoryIngredients(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        Inventory inventory = inventoryRepository.findByUser(user)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        return inventory.getInventoryIngredients().stream()
                .map(ii -> new IngredientResponseDTO(ii.getIngredient().getName(),ii.getExpiryDate()))
                .collect(Collectors.toList());
    }


    public void deleteIngredientFromUserInventory(String ingredientName, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Inventory inventory = user.getInventory();

        InventoryIngredient ingredientToRemove = inventory.getInventoryIngredients().stream()
                .filter(ingredient -> ingredient.getIngredient().getName().equals(ingredientName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found in inventory"));

        // 데이터베이스에서 해당 재료 삭제
        inventoryIngredientRepository.delete(ingredientToRemove);

        // 인메모리에서 인벤토리 재료 목록에서도 제거
        inventory.getInventoryIngredients().remove(ingredientToRemove);
    }
}
