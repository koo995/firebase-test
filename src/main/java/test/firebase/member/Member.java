package test.firebase.member;

import jakarta.persistence.*;
import lombok.*;
import test.firebase.post.Post;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(unique = true)
    private String uid;


    @OneToMany(mappedBy = "member")
    private List<Post> posts = new ArrayList<>();

    @Builder
    public Member(String username, String email, String uid) {
        this.username = username;
        this.email = email;
        this.uid = uid;
    }

    public void addPost(Post post) {
        this.posts.add(post);
    }
}