package com.newsainturtle.shadowmate.planner_setting.repository;

import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryColorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryColorRepositoryTest {

    @Autowired
    private CategoryColorRepository categoryColorRepository;

    @Test
    void 카테고리색상목록조회() {
        //given

        //when
        final List<CategoryColor> result = categoryColorRepository.findAll();

        //then
        assertThat(result).isNotNull().hasSize(16);
    }

}
