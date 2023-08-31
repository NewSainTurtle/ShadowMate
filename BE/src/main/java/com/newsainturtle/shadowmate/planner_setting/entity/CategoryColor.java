package com.newsainturtle.shadowmate.planner_setting.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category_color")
@Getter
public class CategoryColor {

    @Id
    @Column(name = "category_color_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryColorId;

    @Column(name = "category_color_name", length = 7, nullable = false)
    private String categoryColorCode;

}
