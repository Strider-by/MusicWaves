package by.musicwaves.service;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.UserDao;
import by.musicwaves.dao.factory.UserDaoFactory;
import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;
import by.musicwaves.util.PasswordWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
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
}
