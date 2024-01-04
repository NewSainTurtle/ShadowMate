package com.newsainturtle.shadowmate.planner_setting.entity;

import com.newsainturtle.shadowmate.common.entity.CommonEntity;
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "category")
@AttributeOverride(name = "id", column = @Column(name = "category_id"))
public class Category extends CommonEntity {

    @Column(length = 10, nullable = false)
    private String categoryTitle;

    @Column
    private Boolean categoryRemove;

    @Column(length = 2)
    private String categoryEmoticon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_color_id")
    private CategoryColor categoryColor;

    public void updateCategoryTitleAndEmoticonAndColor(final String categoryTitle,
                                                       final String categoryEmoticon,
                                                       final CategoryColor categoryColor) {
        this.categoryTitle = categoryTitle;
        this.categoryEmoticon = categoryEmoticon;
        this.categoryColor = categoryColor;
    }

    public void deleteCategory() {
        super.updateDeleteTime();
        this.categoryRemove = true;
    }

}
