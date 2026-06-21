package com.lbg0146.crew_service.domain.enums;

public enum SubCategory {
    // 스포츠
    FOOTBALL(Category.SPORTS, "축구"),
    BASEBALL(Category.SPORTS, "야구"),
    BASKETBALL(Category.SPORTS, "농구"),
    TENNIS(Category.SPORTS, "테니스"),
    GOLF(Category.SPORTS, "골프"),
    RUNNING(Category.SPORTS, "러닝"),
    HIKING(Category.SPORTS, "등산"),

    // 문화
    MUSICAL(Category.CULTURE, "뮤지컬"),
    DANCE(Category.CULTURE, "댄스"),
    MUSIC(Category.CULTURE, "음악"),
    MOVIE(Category.CULTURE, "영화"),

    // 게임
    RPG(Category.GAMING, "롤플레잉"),
    FPS(Category.GAMING, "슈팅"),
    ADVENTURE(Category.GAMING, "모험"),
    RACING(Category.GAMING, "레이싱"),
    MOBILE(Category.GAMING, "모바일"),

    // 건강
    GYM(Category.HEALTH, "피트니스"),
    CROSSFIT(Category.HEALTH, "크로스핏"),
    YOGA(Category.HEALTH, "요가"),
    PILATES(Category.HEALTH, "필라테스"),
    DIET(Category.HEALTH, "다이어트"),

    // 펫
    DOG(Category.PET, "강아지"),
    CAT(Category.PET, "고양이"),
    HAMSTER(Category.PET, "햄스터"),
    ;

    private final Category category;
    private final String  description;

    SubCategory(Category category, String description) {
        this.category = category;
        this.description = description;
    }

    public Category getCategory(){
        return category;
    }
    public String getDescription() {
        return description;
    }
}
