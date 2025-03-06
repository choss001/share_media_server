package com.media.share.controller;

import com.media.share.model.Board;
import com.media.share.model.User;
import com.media.share.repository.BoardRepository;
import com.media.share.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tiptap")
public class BoardController {
    Logger logger = LoggerFactory.getLogger(MediaController.class);

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/content")
    public ResponseEntity<String> insertData(@RequestBody TempModel tempModel){
        logger.info("contents : " + tempModel.contents);
        logger.info("userToken : " + tempModel.userToken);

        String principal = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null & authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)){
            principal = authentication.getPrincipal().toString();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("not authentication");
        }

        Optional<User> user = userRepository.findByUsername(principal);


        logger.info(user.get().toString());

        //update
        if (tempModel.getId() != 0){
            Board getBoardFromId = boardRepository.getReferenceById(tempModel.getId());
            getBoardFromId.setContents(tempModel.getContents());
            getBoardFromId.setTitle(tempModel.getTitle());
            getBoardFromId.setUser(user.get());
            boardRepository.save(getBoardFromId);
        //create
        }else{
            Board board = new Board();
            board.setContents(tempModel.getContents());
            board.setTitle(tempModel.getTitle());
            board.setUser(user.get());
            boardRepository.save(board);
        }
        return ResponseEntity.ok("ok SUCCESS!!!");
    }
    @GetMapping("/board")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<TempModel>> getData(){
        List<Board> all = boardRepository.findAll();

        List<TempModel> collect = all.stream().map(board -> {
            TempModel tempModel = new TempModel();
            tempModel.setId(board.getBoard_id());
            tempModel.setContents(board.getContents());
            tempModel.setTitle(board.getTitle());
            tempModel.setCreatedAt(board.getCreatedAt());
            tempModel.setUpdateAt(board.getUpdateAt());
            Optional<User> byUserId = userRepository.findById(board.getUser().getId());
            tempModel.setUserName(byUserId.get().getUsername());
            tempModel.setHits(board.getHits());
            return tempModel;
        }).collect(Collectors.toList());
        logger.info(collect.toString());
        return ResponseEntity.ok().body(collect);

    }
    @PatchMapping("/board/hits/{id}")
    public ResponseEntity<String> updateHits(@PathVariable("id") Long id){
        boardRepository.incrementHits(id);
        return ResponseEntity.ok().body("OK");
    }

    @Value("${file.path}")
    private String filePathBase;

    @PostMapping("/image")
    public ResponseEntity<TempModel> image(@RequestParam("file") MultipartFile file) throws IOException {

        String fileName = "thumbnail" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(filePathBase,"thumbnail", fileName);
        Files.createDirectories(filePath.getParent()); // Ensure directory exists
        file.transferTo(filePath.toFile());
        TempModel tempModel = new TempModel();
        tempModel.setContents(fileName);

        return ResponseEntity.ok().body(tempModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TempModel> getTopic(@PathVariable("id") Long id){
        Optional<Board> byIdWithUser = boardRepository.findByIdWithUser(id);
        if (byIdWithUser.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(convertToTempModel(byIdWithUser.get()));
    }

    private TempModel convertToTempModel(Board board){
        return new TempModel( board.getBoard_id(), board.getTitle(), null, board.getContents(), board.getUser().getUsername(),
                board.getCreatedAt(), board.getUpdateAt(), board.getHits());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class TempModel{
        private Long id;
        private String title;
        private String userToken;
        private String contents;
        private String userName;
        private Instant createdAt;
        private Instant updateAt;
        private Long hits;

    }

}
