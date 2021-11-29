package by.musicwaves.service;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.UserDao;
import by.musicwaves.dao.factory.UserDaoFactory;
import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;
import by.musicwaves.util.Pair;
import by.musicwaves.util.PasswordWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class UserService {

    private static final UserService userService = new UserService();
    private static final UserDao userDao = UserDaoFactory.getInstance();
    private final static Logger LOGGER = LogManager.getLogger(UserService.class);

    private UserService() {
    }

    public static UserService getInstance() {
        return userService;
    }

    public ServiceResponse<Integer> registerUser(String login, char[] password1, char[] password2, String inviteCode, Language language) throws ServiceException {

        ServiceResponse<Integer> response = new ServiceResponse<>();
        boolean inviteCodeIsValid = checkInviteCode(inviteCode);
        boolean loginIsValid = checkLogin(login);
        boolean passwordsAreEqual = checkPasswordsEquality(password1, password2);
        boolean passwordIsValid = checkPassword(password1);

        Locale locale = language.getLocale();

        if (!(inviteCodeIsValid && loginIsValid && passwordsAreEqual && passwordIsValid)) {
            if (!inviteCodeIsValid) response.addErrorOccurrence(ServiceErrorEnum.INVALID_INVITE_CODE, locale);
            if (!loginIsValid) response.addErrorOccurrence(ServiceErrorEnum.LOGIN_DOES_NOT_FIT_LIMITATIONS, locale);
            if (!passwordsAreEqual) response.addErrorOccurrence(ServiceErrorEnum.PASSWORDS_ARE_NOT_EQUAL, locale);
            if (!passwordIsValid) response.addErrorOccurrence(ServiceErrorEnum.PASSWORD_DOES_NOT_FIT_LIMITATIONS, locale);

            return response;
        }

        String hashedPassword = PasswordWorker.processPasswordHashing(password1);

        LOGGER.debug("register new User service method");
        LOGGER.debug("invite code: " + inviteCode);
        LOGGER.debug("login: " + login);
        LOGGER.debug("passwords: " + new String(password1) + " | " + new String(password2));


        User user = new User();
        user.setLogin(login);
        user.setHashedPassword(hashedPassword);
        user.setRole(Role.USER);
        user.setLanguage(language);

        int userId;
        try {
            userId = userDao.create(user);
        } catch(DaoException ex) {
            throw new ServiceException(ex);
        }

        if(userId == -1) {
            response.addErrorOccurrence(ServiceErrorEnum.LOGIN_ALREADY_IN_USE, locale);
        } else {
            response.setStoredValue(userId);
        }

        return response;
    }

    public ServiceResponse<User> login(String login, CharSequence password, Locale locale) throws ServiceException {
        ServiceResponse<User> response = new ServiceResponse<>();
        String hashedPassword = PasswordWorker.processPasswordHashing(password);
        User user;

        try {
            user = userDao.findByLoginAndPassword(login, hashedPassword);
        } catch(DaoException ex) {
            throw new ServiceException(ex);
        }

        if (user == null) {
            response.addErrorOccurrence(ServiceErrorEnum.INVALID_LOGIN_CREDENTIALS, locale);
        } else {
            response.setStoredValue(user);
        }

        return response;
    }

    public ServiceResponse<Language> changeLanguage(User user, int languageId) throws ServiceException {
        ServiceResponse<Language> serviceResponse = new ServiceResponse<>();

        Language language = Language.getByDatabaseId(languageId);
        Locale locale = Optional.ofNullable(user)
                .map(User::getLanguage)
                .map(Language::getLocale)
                .orElse(Language.UNKNOWN.getLocale());


        if (language == Language.UNKNOWN) {
            serviceResponse.addErrorOccurrence(ServiceErrorEnum.UNKNOWN_LANGUAGE_ID, locale);
        } else {
            user.setLanguage(language);
            try {
                userDao.update(user);
            } catch(DaoException ex) {
                throw new ServiceException(ex);
            }
            serviceResponse.setStoredValue(language);
        }

        return serviceResponse;
    }

    public ServiceResponse<String> changePassword(User user, char[] oldPassword, char[] newPassword1, char[] newPassword2) throws ServiceException {
        String oldPasswordHashed = PasswordWorker.processPasswordHashing(oldPassword);
        ServiceResponse<String> response = new ServiceResponse<>();
        Locale locale = user.getLanguage().getLocale();

        boolean passwordsAreEqual = checkPasswordsEquality(newPassword1, newPassword2);
        boolean newPasswordFitsLimitations = checkPassword(newPassword1);
        boolean oldPasswordIsValid = user.getHashedPassword().equals(oldPasswordHashed);

        if (!passwordsAreEqual || !newPasswordFitsLimitations || !oldPasswordIsValid) {
            if (!oldPasswordIsValid) response.addErrorOccurrence(ServiceErrorEnum.INVALID_PASSWORD, locale);
            if (!passwordsAreEqual) response.addErrorOccurrence(ServiceErrorEnum.PASSWORDS_ARE_NOT_EQUAL, locale);
            if (!newPasswordFitsLimitations) response.addErrorOccurrence(ServiceErrorEnum.PASSWORD_DOES_NOT_FIT_LIMITATIONS, locale);

            return response;
        }

        // all passed parameters are valid, running actual password update
        String newPasswordHashed = PasswordWorker.processPasswordHashing(newPassword1);
        user.setHashedPassword(newPasswordHashed);
        try {
            userDao.update(user);
        } catch(DaoException ex) {
            // setting back old password value
            user.setHashedPassword(oldPasswordHashed);
            throw new ServiceException(ex);
        }

        response.setStoredValue(newPasswordHashed);
        return response;
    }

    public ServiceResponse<String> changeLogin(User user, char[] password, String newLogin) throws ServiceException {
        String passwordHashed = PasswordWorker.processPasswordHashing(password);
        ServiceResponse<String> response = new ServiceResponse<>();
        Locale locale = user.getLanguage().getLocale();

        boolean newLoginFitsLimitations = checkLogin(newLogin);
        boolean oldPasswordIsValid = user.getHashedPassword().equals(passwordHashed);

        if (!newLoginFitsLimitations || !oldPasswordIsValid) {
            if (!newLoginFitsLimitations) response.addErrorOccurrence(ServiceErrorEnum.LOGIN_DOES_NOT_FIT_LIMITATIONS, locale);
            if (!oldPasswordIsValid) response.addErrorOccurrence(ServiceErrorEnum.INVALID_PASSWORD, locale);

            return response;
        }

        // all passed parameters are valid, running actual login update
        boolean loginHasBeenChanged;
        try {
            loginHasBeenChanged = userDao.updateUserLogin(newLogin, user.getId());
            LOGGER.debug("Login has been changed: " + loginHasBeenChanged);
        } catch(DaoException ex) {
            throw new ServiceException(ex);
        }

        if (!loginHasBeenChanged) {
            response.addErrorOccurrence(ServiceErrorEnum.LOGIN_ALREADY_IN_USE, locale);
            return response;
        }

        response.setStoredValue(newLogin);
        return response;
    }

    public ServiceResponse<Boolean> deleteUserAccount(User user, char[] password) throws ServiceException {
        ServiceResponse<Boolean> serviceResponse = new ServiceResponse<>();
        Locale locale = user.getLanguage().getLocale();

        // check if provided with current command request password is correct
        boolean passwordIsCorrect = checkIfPasswordMatchesGivenUser(user, password);
        if (!passwordIsCorrect) {
            serviceResponse.addErrorOccurrence(ServiceErrorEnum.INVALID_PASSWORD, locale);
            return serviceResponse;
        }

        //  password is correct, we can delete account now
        boolean deleted;
        try {
            deleted = userDao.delete(user);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        serviceResponse.setStoredValue(deleted);
        return serviceResponse;
    }

    public ServiceResponse<Boolean> deleteUserAccountByAdministration(int userId, Locale locale) throws ServiceException {

        ServiceResponse<Boolean> serviceResponse = new ServiceResponse<>();
        boolean deleted;
        try {
            deleted = userDao.deleteById(userId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        serviceResponse.setStoredValue(deleted);
        return serviceResponse;
    }

    public ServiceResponse<Pair<Integer, List<User>>> findUsers(
            Integer id, String login, int loginSearchTypeId,
            Integer roleId, LocalDate registerDate, int registerDateCompareTypeId,
            int fieldIdToBeSortedBy, int sortOrderId, int pageNumber, int recordsPerPage) throws ServiceException {

        ServiceResponse<Pair<Integer, List<User>>> serviceResponse = new ServiceResponse<>();

        Pair<Integer, List<User>> daoResponse;

        try {
            daoResponse = userDao.findUsers(id, login, loginSearchTypeId,
                    roleId, registerDate, registerDateCompareTypeId,
                    fieldIdToBeSortedBy, sortOrderId, pageNumber, recordsPerPage);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        serviceResponse.setStoredValue(daoResponse);
        return serviceResponse;
    }

    public ServiceResponse<?> changeUserRole(int userId, int roleId, Locale locale) throws ServiceException {

        ServiceResponse<?> serviceResponse = new ServiceResponse<>();
        Role newRole = Role.getByDatabaseId(roleId);

        // if we cannot get proper Role value, return error and interrupt service execution
        if (newRole == Role.UNKNOWN) {
            serviceResponse.addErrorOccurrence(ServiceErrorEnum.INVALID_ROLE_PARAMETER_VALUE, locale);
            return serviceResponse;
        }

        try {
            userDao.updateUserRole(userId, roleId);
        } catch(DaoException ex) {
            throw new ServiceException(ex);
        }

        return serviceResponse;
    }




    private boolean checkInviteCode(String code) {
        // dummy
        return true;
    }

    private boolean checkPasswordsEquality(char[] password1, char[] password2) {
        return Arrays.equals(password1, password2);
    }

    private boolean checkPassword(char[] password) {
        // dummy
        return true;
    }

    private boolean checkLogin(String login) {
        // dummy
        return true;
    }

    private boolean checkIfPasswordMatchesGivenUser(User user, char[] password) {
        String userHashedPassword = user.getHashedPassword();
        String passwordToCheck = PasswordWorker.processPasswordHashing(password);
        return userHashedPassword.equals(passwordToCheck);
    }
}
