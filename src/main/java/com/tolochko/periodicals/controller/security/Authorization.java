package com.tolochko.periodicals.controller.security;

import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.model.domain.user.User;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Authorization {
    private static Authorization authorization = new Authorization();
    private static final Map<String, User.Role> permissionMapping = new HashMap<>();

    static {
        User.Role admin = User.Role.ADMIN;

        permissionMapping.put("GET:/app/users/?", admin);
        permissionMapping.put("GET:/app/periodicals/createNew/?", admin);
        permissionMapping.put("GET:/app/periodicals/\\d+/update/?", admin);
        permissionMapping.put("POST:/app/periodicals/?", admin);
        permissionMapping.put("POST:/app/periodicals/discarded/?", admin);
        permissionMapping.put("GET:/app/adminPanel/?", admin);
        permissionMapping.put("POST:/app/users/changeStatus/\\d+/?", admin);
    }

    private Authorization() {
    }

    public static Authorization getInstance() {
        return authorization;
    }

    /**
     * Checks whether a current user has enough permissions to access a requested uri
     * using a current http method.
     *
     * @param request a current http request
     * @return {@code true} - if a current user has enough permissions to perform such a kind of requests,
     * and {@code false} otherwise
     */
    public boolean checkPermissions(HttpServletRequest request) {
        if (!isUserIdInUriValid(request)) {
            return false;
        }

        Optional<Map.Entry<String, User.Role>> accessRestriction = getPermissionMapping(request);

        if (accessRestriction.isPresent()) {
            return isPermissionGranted(accessRestriction.get(), request);
        }

        return true;
    }


    private boolean isUserIdInUriValid(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        Matcher matcher = Pattern.compile("/app/users/\\d+").matcher(requestUri);
        boolean isValid = true;

        if (matcher.find()) {
            long userIdFromUri = HttpUtil.getFirstIdFromUri(requestUri);
            long userIdFromSession = HttpUtil.getUserIdFromSession(request);


            if (userIdFromSession == userIdFromUri) {
                isValid = true;
            } else {
                isValid = false;
            }
        }

        return isValid;
    }


    private Optional<Map.Entry<String, User.Role>> getPermissionMapping(HttpServletRequest request) {
        return permissionMapping.entrySet()
                .stream()
                .filter(entry -> HttpUtil.filterRequestByHttpMethod(request, entry.getKey()))
                .filter(entry -> HttpUtil.filterRequestByUri(request, entry.getKey()))
                .findFirst();
    }

    private boolean isPermissionGranted(Map.Entry<String, User.Role> permissionMapping,
                                        HttpServletRequest request) {

        User.Role userRole = getUserFromSession(request).getRole();
        User.Role legitRole = permissionMapping.getValue();

        return userRole.equals(legitRole);
    }

    private User getUserFromSession(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("currentUser");
    }

}
