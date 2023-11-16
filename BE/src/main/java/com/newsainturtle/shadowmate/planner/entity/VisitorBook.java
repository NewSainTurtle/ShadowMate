package com.newsainturtle.shadowmate.planner.entity;

import com.newsainturtle.shadowmate.common.entity.CommonEntity;
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "visitor_book")
@AttributeOverride(name = "id", column = @Column(name = "visitor_book_id"))
public class VisitorBook extends CommonEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitor_id")
    private User visitor;

    @Column(name = "visitor_book_content", length = 60)
    private String visitorBookContent;
}
