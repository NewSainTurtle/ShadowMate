package com.newsainturtle.shadowmate.social.repository;

import com.newsainturtle.shadowmate.social.entity.Social;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocialRepository extends JpaRepository<Social, Long> {

    @Query(value = "SELECT s FROM Social s WHERE s.deleteTime is null")
    List<Social> findAllByDeleteTime();
}
