package it.polimi.tiw.documents.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.documents.utils.Paths;

@WebFilter(
	urlPatterns = {
		Paths.TO_LOGIN_PAGE_RELATIVE,
		Paths.CHECK_LOGIN_RELATIVE,
		Paths.REGISTER_USER_RELATIVE})
public class ForwardLoggedUser extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest HTTPRequest = (HttpServletRequest) request;
		HttpServletResponse HTTPResponse = (HttpServletResponse) response;
		HttpSession session = HTTPRequest.getSession();

		if (session.getAttribute("user") != null) {
			HTTPResponse.sendRedirect(Paths.TO_HOME_PAGE);
			return;
		}

		chain.doFilter(request, response);
	}
}
