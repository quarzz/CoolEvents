package by.bsu.fpmi.dao;

public interface AuthDao {
    int authenticate(String login, String Password);
    int authenticate2(String token, int pin);
    void addToken(int userID, String token, int stage);
    void deleteToken(String token);
    boolean hasToken(String token, int stage);
    void setPin(String token, int pin);
    int getPin(String token);

    int getUserIdByToken(String token, int stage);
    int getStage(String token);
    void setStage(String token, int stage);
}
