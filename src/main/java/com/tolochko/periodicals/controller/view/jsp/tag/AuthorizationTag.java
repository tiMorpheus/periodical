package com.tolochko.periodicals.controller.view.jsp.tag;

import com.tolochko.periodicals.model.domain.user.User;
import org.apache.log4j.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import static java.util.Objects.nonNull;

/**
 * Allows specifying two sets of roles that a user must have and must not have in order to
 * see the content of this tag.
 */
public class AuthorizationTag extends TagSupport {
    private static final Logger logger = Logger.getLogger(AuthorizationTag.class);

    private String mustHaveRole;

    private User user;

    @Override
    public int doStartTag() throws JspException {
        user = getUserFromSession();
        logger.debug(user);

        if (nonNull(user) && hasUserLegitRole()) {

            return Tag.EVAL_BODY_INCLUDE;
        }

        return Tag.SKIP_BODY;
    }

    private User getUserFromSession() {
        return (User) pageContext.getSession().getAttribute("currentUser");
    }

    private boolean hasUserLegitRole() {
        if ("*".equals(mustHaveRole)) {
            return true;

        } else {
            User.Role legitRole = parseLegitRole();
            User.Role userRole = user.getRole();

            return legitRole != null && legitRole.equals(userRole);
        }
    }

    private User.Role parseLegitRole() {
        return nonNull(mustHaveRole) ? User.Role.valueOf(mustHaveRole.toUpperCase()) : null;
    }

    public String getMustHaveRole() {
        return mustHaveRole;
    }

    public void setMustHaveRole(String mustHaveRole) {
        this.mustHaveRole = mustHaveRole;
    }
}
