<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="custom" uri="http://tolochko.com/javaee/jsp-tags-0.1" %>
<%@ taglib prefix="o" uri="http://java.sun.com/jsp/jstl/core" %>

<fmt:setBundle basename="i18n.general.general" var="langGeneral" />

<c:set var="language"
       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"
       scope="session"/>
<fmt:setLocale value="${language}"/>


<!doctype html>
<html>
<head>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title><fmt:message key="htmlHead.title" bundle="${langGeneral}" /></title>
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">

    <link rel="stylesheet" href="/css/bootstrap.min.css">
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>

<div class="container">
    <div id="header" class="row">

        <div class="col-xs-6 col-md-2 col-md-push-6">
            <form>
                <select class="form-control" id="language" name="language" onchange="submit()">
                    <option value="en_EN" ${language == 'en_EN' ? 'selected' : ''}>English</option>
                    <option value="ru_RU" ${language == 'ru_RU' ? 'selected' : ''}>Русский</option>
                </select>
            </form>
        </div>

        <div class="col-xs-6 col-md-4 col-md-push-6 text-right">

            <c:choose>

                <c:when test="${currentUser != null}">
                    <%@include file="/WEB-INF/includes/topUserInfo.jsp" %>
                </c:when>

                <c:otherwise>
                    <c:if test="${pageContext.request.requestURI != '/login.jsp'}">
                        <p><a href="/login.jsp"><fmt:message key="signIn.label" bundle="${langGeneral}"/></a></p>
                    </c:if>
                </c:otherwise>
            </c:choose>

        </div>

        <div class="col-xs-12 col-md-6 col-md-pull-6">
            <c:if test="${currentUser != null}">
                <%@include file="/WEB-INF/includes/topMenu.jsp" %>
            </c:if>
        </div>
    </div>

    <c:if test="${(not empty messages) && (not empty messages['topMessages'])}">
        <%@include file="/WEB-INF/includes/topMessagesBlock.jsp" %>
    </c:if>