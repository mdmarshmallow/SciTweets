Home.jsp updated version

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>SciTweets</title>
<link href="webjars/bootstrap/3.3.6/css/bootstrap.min.css"
	rel="stylesheet">
<style>
.columnscroll {
	overflow-y: scroll;
	padding: 20px;
}
</style>
</head>
<body>
	<div style="background-color: #000000" class="container-fluid">

		<h1 style="font: monaco; font-size: 20pt; color: #FFFFFF">
			<center> SciTweets: Keep Current With The Latest Advancements In The Scientific World </center> </h1>
		<h5 style="font: monaco; font-size: 13pt; color: #add8e6">
			<center> Click on a Twitter User or Organization and Read About the Latest Science News </center></h5>
		<!--  <img src="${pageContext.servletContext.contextPath}/images/Logo.png" alt="SciTweets"
			style="position: absolute; top: 6px; left: 706px; width: 285px; height: 123px;"/> -->

		<center>
			<h1 style="font: monaco; font-size: 20pt; color: #FFFFFF">
				SciTweets: Keep Current With The Latest Advancements In The
				Scientific World</h1>
		</center>
		<center>
			<h5 style="font: monaco; font-size: 13pt; color: #add8e6">Click
				on a Twitter User or Organization and Read About the Latest Science
				News</h5>
		</center>
		<div class="row">
			<div class="col-sm-3 columnscroll” id="left"
				style="background-color: #000000; height: 100vh">
				<c:forEach items="${categories}" var="category">
					<p>
					<div class="dropdown">
						<button class="btn btn-primary dropdown-toggle" type="button"
							data-toggle="dropdown">
							${category.categoryName} <span class="caret"></span>
						</button>
						<ul class="dropdown-menu">
							<c:forEach items="${category.accounts}" var="account">
								<li
									onclick="post('/home', {user: '${account.username}'}); loadingMessage();">${account.name}</li>
							</c:forEach>
						</ul>
					</div>
					</p>
				</c:forEach>
			</div>
			<div class="col-sm-9 columnscroll" id="right"
				style="background-color: #000000; height: 100vh">
				<p id="loading"></p>
					<c:forEach items="${tweets}" var="tweet">
						<h2 style="font: monaco; font-size: 15pt; color: #add8e6">
							<a href="${tweet.url}" target="_blank"> ${tweet.url}</a>
						</h2>
						<h3 style="font: monaco; font-size: 12pt; color: #FFFFFF">${tweet.description}</h3>
					</c:forEach>
			</div>
		</div>
		<div style="position: relative">
			<h4
				style="font-size: 8pt; font: monaco; position: fixed; bottom: 0; width: 100%; text-align: center; color: #FFFFFF">
				<a href="mailto:scitweets.filter@gmail.com"> Contact Us</a>
				<a href="about.html"> About Us</a>
			</h4>
		</div>
	</div>
	<script>
		function post(path, params) {
			var form = document.createElement("form");
			form.setAttribute("method", "post");
			form.setAttribute("action", path);
			for ( var key in params) {
				if (params.hasOwnProperty(key)) {
					var hiddenField = document.createElement("input");
					hiddenField.setAttribute("type", "hidden");
					hiddenField.setAttribute("name", key);
					hiddenField.setAttribute("value", params[key]);
					form.appendChild(hiddenField);
				}
			}
			document.body.appendChild(form);
			form.submit();
		}
	</script>
	<script>
		function loadingMessage() {
			document.getElementById('loading').innerHTML = 'Please wait while the page loads. It might take a few seconds...';
			document.getElementById('loading').setAttribute("class", "alert alert-info");
			document.getElementById('loading').setAttribute("style", "font-weight: bold;")
		}
	</script>
	<script src="webjars/jquery/1.9.1/jquery.min.js"></script>
	<script src="webjars/bootstrap/3.3.6/js/bootstrap.min.js"></script>
</body>
</html>