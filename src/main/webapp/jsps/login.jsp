<%--
  Created by IntelliJ IDEA.
  User: burgxun
  Date: 2020/6/1
  Time: 14:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录页面</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/test/index.do" method="post">
    <label>用户名：</label><input type="text" value="${param.usename}" name="username">
    <br/>
    <label>密码：</label> <input type="password" value="${param.pwd}" name="password">
    <br/>
    <input type="submit" value="登录"/>
</form>
</body>
</html>
