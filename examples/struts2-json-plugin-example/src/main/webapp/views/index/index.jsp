<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />

<title>Struts 2 JSON Plugin - Example</title>

</head>

<body>
	<h2>Struts 2 JSON Plugin - Example</h2>

    <h3>Examples:</h3>
	<ul>
		<li><a href="${pageContext.request.contextPath}/list">List&lt;String&gt;</a></li>
		<li><a href="${pageContext.request.contextPath}/map">Map&lt;Integer, String&gt;</a></li>
		<li><a href="${pageContext.request.contextPath}/bar">Bar()</a></li>
		<li><a href="${pageContext.request.contextPath}/bar-excludes">Bar() - @Json(exclude = { "description", "pubDate", "status" })</a></li>
		<li><a href="${pageContext.request.contextPath}/foo">Foo()</a></li>
		<%-- <li><a href="${pageContext.request.contextPath}/foo-excludes">Foo() - @Json(exclude = { "id", "bar.title", "bar.status" })</a></li> --%>
	</ul>

</body>
</html>
