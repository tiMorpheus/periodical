package com.tolochko.periodicals.model.domain.user;

import org.apache.log4j.Logger;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1232131888L;
    private static final Logger logger = Logger.getLogger(User.class);

    private long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String passwordHash;
    private Role role;
    private Status status;

    public enum Status {
        ACTIVE, BLOCKED;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public enum Role {
        ADMIN, SUBSCRIBER;


        @Override
        public String toString() {
            return name().toLowerCase();
        }

    }

    public static class Builder {
        private User user;

        public Builder() {
            this.user = new User();
        }

        public Builder setId(long id) {
            user.setId(id);
            return this;
        }

        public Builder setUsername(String username) {
            user.setUsername(username);
            return this;
        }

        public Builder setFirstName(String firstName) {
            user.setFirstName(firstName);
            return this;
        }

        public Builder setLastName(String lastName) {
            user.setLastName(lastName);
            return this;
        }

        public Builder setEmail(String email) {
            user.setEmail(email);
            return this;
        }

        public Builder setAddress(String address) {
            user.setAddress(address);
            return this;
        }

        public Builder setRole(Role role) {
            user.setRole(role);
            return this;
        }

        public Builder setPassword(String password) {
            user.setPassword(password);
            return this;
        }

        public Builder setStatus(Status status) {
            user.setStatus(status);
            return this;
        }

        public User build() {
            return user;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean hasRole(Role roles) {
        if (roles == null) {
            logger.debug("checking not null role: " + roles);
            throw new NullPointerException();
        }

        return role.equals(roles);
    }

    public boolean hasRole(String role) {
        return hasRole(Role.valueOf(role.toUpperCase()));
    }

    public String getPassword() {
        return passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = password;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("User{id=%d,username='%s' firstName='%s', lastName='%s', " +
                        "email='%s', address='%s', status=%s, role=%s}",
                id, username, firstName, lastName,
                email, address, status, role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (id != user.id) {
            return false;
        }
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) {
            return false;
        }
        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) {
            return false;
        }
        return true;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        return result;
    }


}
