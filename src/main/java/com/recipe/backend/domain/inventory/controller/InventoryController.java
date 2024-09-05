package com.recipe.backend.domain.inventory.controller;

import com.recipe.backend.domain.inventory.dto.IngredientRequest;
import com.recipe.backend.domain.inventory.dto.IngredientResponseDTO;
import com.recipe.backend.domain.inventory.dto.IngredientsRequest;
import com.recipe.backend.domain.inventory.service.InventoryService;
import com.recipe.backend.global.config.auth.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public String getUserInventory(@RequestParam("token") String token, Model model) {
        if (token == null || !jwtUtil.validateJwtToken(token)) {
            return "redirect:/users/login?error=true";
        }
        String username = jwtUtil.getUserNameFromJwtToken(token);
        List<IngredientResponseDTO> inventory = inventoryService.getUserInventoryIngredients(username);
        model.addAttribute("ingredients", inventory);
        model.addAttribute("token", token);
        return "inventory";  // 냉장고 관리 페이지 렌더링
    }

    @PostMapping("/add")
    public String addIngredient(@ModelAttribute IngredientRequest ingredientRequest, @RequestParam("token") String token) {
        String username = jwtUtil.getUserNameFromJwtToken(token);

        IngredientsRequest ingredientsRequest = new IngredientsRequest();
        ingredientsRequest.setIngredients(List.of(ingredientRequest));

        inventoryService.addIngredientsToInventory(ingredientsRequest, username);
        return "redirect:/inventory?token=" + token;  // 추가 후 다시 목록 페이지로 리다이렉트
    }

    @PostMapping("/delete")
    public String deleteIngredient(@RequestParam("name") String name, @RequestParam("token") String token) {
        String username = jwtUtil.getUserNameFromJwtToken(token);
        inventoryService.deleteIngredientFromUserInventory(name, username);
        return "redirect:/inventory?token=" + token;  // 삭제 후 다시 목록 페이지로 리다이렉트
    }

}
