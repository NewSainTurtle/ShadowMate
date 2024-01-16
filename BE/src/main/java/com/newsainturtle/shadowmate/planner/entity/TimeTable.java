package com.newsainturtle.shadowmate.planner.entity;

import com.newsainturtle.shadowmate.common.entity.CommonEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

@SuperBuilder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "time_table")
@AttributeOverride(name = "id", column = @Column(name = "time_table_id"))
public class TimeTable extends CommonEntity {

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    public void setTodo(Todo todo) {
        if (this.todo != null) {
            this.todo.getTimeTables().remove(this);
        }

        this.todo = todo;
        if (todo != null) {
            todo.getTimeTables().add(this);
        }
    }
}
