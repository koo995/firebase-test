package test.firebase.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import test.firebase.member.Member;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    public Post createPost(Post post, Member member) {
        log.info("post: {}", post);
        post.setMember(member);
        return postRepository.save(post);
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }
}
