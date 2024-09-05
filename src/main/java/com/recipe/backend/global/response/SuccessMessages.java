package com.recipe.backend.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessMessages {
    SIGNUP_SUCCESS(HttpStatus.CREATED,"회원가입 성공"),
    LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공"),
    ADD_INGRE_SUCCESS(HttpStatus.OK, "재료 추가 성공"),
    LOAD_INVENTORY(HttpStatus.OK, "재료 불러오기 성공"),
    DELETE_ING_SUCCESS(HttpStatus.OK, "재료 삭제 성공"),
    RECOM_SUCCESS(HttpStatus.OK, "레시피 추천 성공"),
    LOAD_RECIPE_SUCCESS(HttpStatus.OK, "레시피 전체조회 성공"),
    LOAD_RECIPE_DETAIL_SUCCESS(HttpStatus.OK, "레시피 상세조회 성공"),
    EMPTY_INVENTORY(HttpStatus.OK,"냉장고에 재료가 없습니다."),
    CREATE_RATING_SUCCESS(HttpStatus.OK, "평가 추가 완료"),
    LOAD_RATINGS_SUCCESS(HttpStatus.OK, "평가 불러오기 성공");

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode(){
        return this.httpStatus.value();
    }
}
