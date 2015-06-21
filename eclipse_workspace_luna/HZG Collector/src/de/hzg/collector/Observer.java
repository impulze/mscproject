package de.hzg.collector;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
		urlPatterns = { "/log" }
)
public class Observer extends HttpServlet {
	private static final long serialVersionUID = 8691259019278629167L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final OutputStream responseStream = response.getOutputStream();
		final Object webLogHandlerObject = request.getServletContext().getAttribute("webLogHandler");
		final WebLogHandler webLogHandler = (WebLogHandler)webLogHandlerObject;
		final WebLogStream webLogStream = webLogHandler.getWebLogStream();

		webLogHandler.flush();
		webLogStream.writeTo(responseStream);
		webLogStream.reset();
	}
}