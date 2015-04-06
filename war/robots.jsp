<%
	String domain = request.getServerName().replace("www.", "");

	if("cpcgifts.fr".equals(domain)) {

%>
User-Agent: *    
Disallow: /private/
<%

	} else {
	
%>
User-agent: *
Disallow: /
<%

	}
	
%>