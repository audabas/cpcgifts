<%@page import="com.google.appengine.api.taskqueue.TaskOptions"%>
<%@page import="com.google.appengine.api.taskqueue.QueueFactory"%>
<%@page import="com.google.appengine.api.taskqueue.Queue"%>
<%

	Queue queue = QueueFactory.getQueue("datastore-update");
	queue.add(TaskOptions.Builder.withUrl("/task/userupdate2"));
	
	response.getWriter().println("User task added<br />");
	
	queue.add(TaskOptions.Builder.withUrl("/task/gaupdate2"));
	
	response.getWriter().println("GA task added");

%>