package com.media.share.repository;

import com.media.share.model.Board;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Board b SET b.hits = b.hits + 1 WHERE b.id = :boardId")
    void incrementHits(@Param("boardId") Long boardId);

    @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.board_id = :id")
    Optional<Board> findByIdWithUser(@Param("id") Long id);
}
