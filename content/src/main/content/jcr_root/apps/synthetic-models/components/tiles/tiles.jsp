<!-- Tiles Component -->

<%@include file="/libs/foundation/global.jsp"%>
<%@taglib prefix="neba" uri="http://neba.io/1.0"%>
<%@page session="false" %>

<neba:defineObjects/>

<div id="grid">
	<div id="posts">
		<c:forEach var="book" items="${ m.books }">
			<div class="post">
				<img class="img-responsive" src="${ book.imagePath }"><br>
				<br>
				<strong>${ book.title }</strong><br>
				<small>by ${ book.author }</small><br>
				<small>${ book.genre }</small><br>
				<a href="${ book.link }">View Details</a>
			</div>
		</c:forEach>
	</div>
</div>