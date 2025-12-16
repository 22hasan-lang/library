package library.service;

public interface IAuthService {
    boolean login(String username, String password);
    boolean changePassword(String username, String oldPass, String newPass);
    boolean addAdmin(String username, String password);
}