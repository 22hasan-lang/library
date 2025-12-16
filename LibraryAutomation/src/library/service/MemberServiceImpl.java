package library.service;

import library.model.Member;
import library.database.DBCon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberServiceImpl implements IMemberService {

    @Override
    public boolean addMember(Member member) {
        // identity_id veritabanında VARCHAR olmalı, bu yüzden String gönderiyoruz.
        String sql = "INSERT INTO members (member_name, member_surname, phone_number, email, identity_id, credit) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBCon.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getMemberName());
            ps.setString(2, member.getMemberSurname());
            ps.setString(3, member.getPhoneNumber());
            ps.setString(4, member.getEmail());

            // DÜZELTME: setInt yerine setString yaptık (TC No için)
            ps.setString(5, member.getIdentityId());

            ps.setInt(6, member.getCredit());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Üye ekleme hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateMember(Member member) {
        String sql = "UPDATE members SET member_name=?, member_surname=?, phone_number=?, email=?, identity_id=?, credit=? WHERE member_id=?";

        try (Connection conn = DBCon.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getMemberName());
            ps.setString(2, member.getMemberSurname());
            ps.setString(3, member.getPhoneNumber());
            ps.setString(4, member.getEmail());

            // DÜZELTME: TC No String olarak güncelleniyor
            ps.setString(5, member.getIdentityId());

            ps.setInt(6, member.getCredit());
            ps.setInt(7, member.getMemberId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Üye güncelleme hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteMember(int memberId) {
        String sql = "DELETE FROM members WHERE member_id=?";
        try (Connection conn = DBCon.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Üye silme hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Member getMemberById(int memberId) {
        String sql = "SELECT * FROM members WHERE member_id=?";
        try (Connection conn = DBCon.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Member(
                        rs.getInt("member_id"),
                        rs.getString("member_name"),
                        rs.getString("member_surname"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("identity_id"), // DÜZELTME: Veritabanından String olarak çekiyoruz
                        rs.getInt("credit")
                );
            }

        } catch (SQLException e) {
            System.out.println("ID ile üye getirme hatası: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";

        try (Connection conn = DBCon.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                members.add(new Member(
                        rs.getInt("member_id"),
                        rs.getString("member_name"),
                        rs.getString("member_surname"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("identity_id"), // DÜZELTME: getInt yerine getString
                        rs.getInt("credit")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Üyeleri listeleme hatası: " + e.getMessage());
        }

        return members;
    }

    @Override
    public List<Member> searchMembers(String keyword) {
        List<Member> members = new ArrayList<>();
        // TC Kimlik ile de arama yapabilmek için sorguya ekleme yaptım
        String sql = "SELECT * FROM members WHERE member_name LIKE ? OR member_surname LIKE ? OR email LIKE ? OR identity_id LIKE ?";

        try (Connection conn = DBCon.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern); // TC araması için 4. parametre

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                members.add(new Member(
                        rs.getInt("member_id"),
                        rs.getString("member_name"),
                        rs.getString("member_surname"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("identity_id"), // DÜZELTME: getString
                        rs.getInt("credit")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Arama hatası: " + e.getMessage());
        }

        return members;
    }
}