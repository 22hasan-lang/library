package library.model;

public class Member {
    private int member_id;
    private String member_name;
    private String member_surname;
    private String phone_number;
    private String email;
    private String identity_id;
    private int credit;

    public Member() {
    }

    public Member(int member_id, String member_name, String member_surname, String phone_number, String email, String identity_id, int credit) {
        this.member_id = member_id;
        this.member_name = member_name;
        this.member_surname = member_surname;
        this.phone_number = phone_number;
        this.email = email;
        this.identity_id = identity_id;
        this.credit = credit;
    }

    public int getMemberId() {
        return member_id;
    }

    public void setMemberId(int member_id) {
        this.member_id = member_id;
    }

    public String getMemberName() {
        return member_name;
    }

    public void setMemberName(String member_name) {
        this.member_name = member_name;
    }

    public String getMemberSurname() {
        return member_surname;
    }

    public void setMemberSurname(String member_surname) {
        this.member_surname = member_surname;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdentityId() {
        return identity_id;
    }

    public void setIdentityId(String identity_id) {
        this.identity_id = identity_id;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    // Member.java dosyasının içine, en alta ekle:
    @Override
    public String toString() {
        // Ekranda "Ahmet Yılmaz (TC: 123...)" şeklinde görünür
        return member_name + " " + member_surname;
    }
}
