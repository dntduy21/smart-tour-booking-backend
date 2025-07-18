package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.Post;
import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.model.request.PostRequestDTO;
import com.dinhngoctranduy.model.response.PostResponseDTO;
import com.dinhngoctranduy.repository.PostRepository;
import com.dinhngoctranduy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private PostResponseDTO toResponseDTO(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .author(PostResponseDTO.AuthorDTO.builder()
                        .id(post.getAuthor().getId())
                        .username(post.getAuthor().getUsername())
                        .build())
                .build();
    }

    public PostResponseDTO createPost(PostRequestDTO request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + request.getAuthorId()));

        Post newPost = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .author(author)
                .build();

        Post savedPost = postRepository.save(newPost);
        return toResponseDTO(savedPost);
    }

    public PostResponseDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài viết với ID: " + id));
        return toResponseDTO(post);
    }

    public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(this::toResponseDTO);
    }

    public Page<PostResponseDTO> searchPostsByTitle(String title, Pageable pageable) {
        return postRepository.findByTitleContainingIgnoreCase(title, pageable).map(this::toResponseDTO);
    }

    @Transactional
    public PostResponseDTO updatePost(Long id, PostRequestDTO request) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài viết với ID: " + id));

        // Không cho phép thay đổi tác giả của bài viết
        existingPost.setTitle(request.getTitle());
        existingPost.setContent(request.getContent());
        existingPost.setImageUrl(request.getImageUrl());

        Post updatedPost = postRepository.save(existingPost);
        return toResponseDTO(updatedPost);
    }

    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy bài viết với ID: " + id);
        }
        postRepository.deleteById(id);
    }
}
