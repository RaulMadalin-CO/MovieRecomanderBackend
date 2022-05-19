package com.movierecommender.backend.comments;

import com.movierecommender.backend.advice.BusinessException;
import com.movierecommender.backend.identity.IdentityService;
import com.movierecommender.backend.security.facades.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/comments")
public class CommentController {
    private final CommentRepository commentRepository;
    private final IdentityService identityService;

    @Autowired
    public CommentController(CommentRepository commentRepository, IdentityService identityService) {
        this.commentRepository = commentRepository;
        this.identityService = identityService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public ResponseEntity<List<Comment>> get() {
        return ResponseEntity.ok(commentRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @ResponseStatus(code = HttpStatus.CREATED, reason = "CREATED")
    public void post(@RequestBody Comment comment) {
        commentRepository.save(comment);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public ResponseEntity<Comment> read(@PathVariable("id") Long id) {
        var foundComment = commentRepository.findById(id);
        if (foundComment.isEmpty()) {
            throw new BusinessException("Comment not found", "Invalid data", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(foundComment.get());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "UPDATED")
    public void update(@PathVariable("id") Long id, @RequestBody Comment comment) {
        var foundComment = commentRepository.findById(id);
        if (foundComment.isEmpty()) {
            throw new BusinessException("Comment not found", "Invalid data", HttpStatus.NOT_FOUND);
        }

        var currentUser = this.identityService.getLoggedInUser();
        if (currentUser.isEmpty() || !currentUser.get().equals(foundComment.get().getAppUser())) {
            throw new BusinessException("User can only update his own comment", "Invalid permission", HttpStatus.FORBIDDEN);
        }

        foundComment.get().update(comment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "DELETED")
    public void delete(@PathVariable("id") Long id) {
        var foundComment = commentRepository.findById(id);
        if (foundComment.isEmpty()) {
            throw new BusinessException("Comment not found", "Invalid data", HttpStatus.NOT_FOUND);
        }

        var currentUser = this.identityService.getLoggedInUser();
        if (currentUser.isEmpty() || !currentUser.get().equals(foundComment.get().getAppUser())) {
            throw new BusinessException("User can only delete his own comment", "Invalid permission", HttpStatus.FORBIDDEN);
        }

        commentRepository.delete(foundComment.get());
    }
}
