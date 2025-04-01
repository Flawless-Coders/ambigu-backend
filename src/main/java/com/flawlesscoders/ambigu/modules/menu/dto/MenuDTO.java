package com.flawlesscoders.ambigu.modules.menu.dto;

import java.util.List;

import com.flawlesscoders.ambigu.modules.category.Category;
import com.flawlesscoders.ambigu.modules.dish.Dish;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class MenuDTO {
    private Category category;
    private List<Dish> dishes;
}
