<!-- Book Component -->

<%@include file="/libs/foundation/global.jsp"%>
<%@taglib prefix="neba" uri="http://neba.io/1.0"%>
<%@page session="false" %>

<neba:defineObjects/>

<c:if test="${empty m.isbn}">
	Please edit the component and enter an ISBN
</c:if>

<div class="col-md-12">
    <div class="row">
        <div class="col-sm-4">
            <img class="img-responsive" src="${ m.imagePath }">
            <cq:include path="sidepar" resourceType="foundation/components/parsys" />
        </div>
        <div class="col-sm-8">
            <strong>${ m.title }</strong><br>
			<small>by ${ m.author }</small><br>
			<small>${ m.genre }</small><br>
			<br>
			<p>${ m.description }<br>
			<cq:include path="par" resourceType="foundation/components/parsys" />
        </div>
    </div>
</div>

