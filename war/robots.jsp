<%
	String domain = request.getServerName().replace("www.", "");

	if("cpcgifts.ovh".equals(domain)) {

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