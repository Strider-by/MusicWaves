package by.musicwaves.service;

import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.util.Pair;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public interface UserService {

    ServiceResponse<Integer> registerUser(String login, char[] password1, char[] password2, String inviteCode, Language language) throws ServiceException;

    ServiceResponse<User> login(String login, char[] password, Locale locale) throws ServiceException;

    ServiceResponse<Language> changeLanguage(User user, int languageId) throws ServiceException;

    ServiceResponse<String> changePassword(User user, char[] oldPassword, char[] newPassword1, char[] newPassword2) throws ServiceException;

    ServiceResponse<String> changeLogin(User user, char[] password, String newLogin) throws ServiceException;

    ServiceResponse<?> deleteUserAccount(User user, char[] password) throws ServiceException;

    ServiceResponse<Boolean> deleteUserAccountByAdministration(int userId, Locale locale) throws ServiceException;

    ServiceResponse<Pair<Integer, List<User>>> findUsers(
            Integer id, String login, int loginSearchTypeId,
            Integer roleId, LocalDate registerDate, int registerDateCompareTypeId,
            int fieldIdToBeSortedBy, int sortOrderId, int pageNumber, int recordsPerPage, Locale locale) throws ServiceException;

    ServiceResponse<?> changeUserRole(int userId, int roleId, Locale locale) throws ServiceException;

    ServiceResponse<Boolean> checkIfLoginIsAvailable(String login, Locale locale) throws ServiceException;
}
