package test.firebase.post;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import test.firebase.auth.Login;
import test.firebase.member.Member;

@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;

    @PostMapping("/post/new")
    public Post createPost(@RequestBody Post post, @Login Member member) {
        return postService.createPost(post, member);
    }

    @GetMapping("/post/{postId}")
    public Post getPost(Long postId, @Login Member member) {
        return postService.getPost(postId);
    }
}
