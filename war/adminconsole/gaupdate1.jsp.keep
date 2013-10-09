<%@page import="com.google.appengine.api.taskqueue.TaskOptions"%>
<%@page import="com.google.appengine.api.taskqueue.QueueFactory"%>
<%@page import="com.google.appengine.api.taskqueue.Queue"%>
<%

	Queue queue = QueueFactory.getQueue("datastore-update");
	queue.add(TaskOptions.Builder.withUrl("/task/gaupdate1"));
	
	response.getWriter().print("Task added");

%>