package library.service;

import library.model.Member;
import java.util.List;

public interface IMemberService {
    boolean addMember(Member member);
    boolean updateMember(Member member);
    boolean deleteMember(int memberId);
    Member getMemberById(int memberId);
    List<Member> getAllMembers();
    List<Member> searchMembers(String keyword);
}
