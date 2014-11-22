<%--
  ==============================================================================

  Default head script.

  Draws the HTML head with some default content:
  - includes the WCML init script
  - includes the head libs script
  - includes the favicons
  - sets the HTML title
  - sets some meta data

  ==============================================================================

--%>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@include file="/libs/foundation/global.jsp" %>

<head>
   	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="description" content="">
	<meta name="viewport" content="width=device-width, user-scalable=no">
	<cq:include script="/libs/wcm/core/components/init/init.jsp"/>
	<cq:include script="headlibs.jsp"/>
  <!--[if lte IE 8]> <script src="/etc/designs/foxtel/common/clientlibs/js/vendor/respondjs/respond.js"></script> <![endif]-->
</head>
