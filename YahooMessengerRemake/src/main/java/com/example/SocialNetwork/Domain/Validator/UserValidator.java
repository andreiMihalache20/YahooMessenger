package com.example.SocialNetwork.Domain.Validator;

import java.util.Objects;
import com.example.SocialNetwork.Domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User utilizator) throws ValidationException {
        String mesajEroare = "";

        if (Objects.equals(utilizator.getFirstName(), "")) {
            mesajEroare += "The first name cannot be empty.\n";
        }

        if (Objects.equals(utilizator.getLastName(), "")) {
            mesajEroare += "The last name cannot be empty\n";
        }

        if (utilizator.getFirstName().matches(".*\\d.*") || utilizator.getLastName().matches(".*\\d.*")) {
            mesajEroare += "The first name and last name cannot contain digits.\n";
        }

        if (!mesajEroare.isEmpty()) {
            throw new ValidationException(mesajEroare);
        }
    }
}
