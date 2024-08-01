package test.firebase.auth;


import com.google.firebase.auth.FirebaseToken;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import test.firebase.member.Member;
import test.firebase.member.MemberRepository;

import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;

//    @Autowired
//    private AuthService self;

//    @Transactional
//    public Member joinAndLogin(FirebaseToken decodedToken) {
//        return self.internalJoinAndLogin(decodedToken);
//    }

    private final ReentrantLock lock = new ReentrantLock();

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Member joinAndLogin(FirebaseToken decodedToken) {
        lock.lock();
        String uid = decodedToken.getUid();
        Member member = memberRepository.findByUid(uid)
                .orElseGet(Member.builder()
                        .username(decodedToken.getName())
                        .email(decodedToken.getEmail())
                        .uid(uid)::build);
        memberRepository.save(member);
        lock.unlock();
        return member;
    }
}
