package org.example.antispamgorbushka.enums;

public enum UserRole {

    BLOCKED("Заблокирован"),
    UNREGISTERED("Незарегистрирован"),
    EMPLOYEE("Пользователь"),
    ADMIN("Администратор");

    private String title;

    UserRole(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
