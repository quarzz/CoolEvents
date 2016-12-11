package by.bsu.fpmi.dao;

public interface AuthDao {
    int authenticate(String login, String Password);
    public void addToken(int userID, String token, int stage);
    public void deleteToken(String token);
}
