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
		Paths.HOME_PAGE_RELATIVE,
		Paths.TO_HOME_PAGE_RELATIVE,
		Paths.TO_DOCUMENT_PAGE_RELATIVE,
		Paths.MOVE_DOCUMENT_RELATIVE,
		Paths.CREATE_CONTENT_RELATIVE,
		Paths.DELETE_CONTENT_RELATIVE})
public class BlockUnknownUser extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest HTTPRequest = (HttpServletRequest) request;
		HttpServletResponse HTTPResponse = (HttpServletResponse) response;
		HttpSession session = HTTPRequest.getSession();

		if (session.isNew() || session.getAttribute("user") == null) {
			HTTPResponse.sendRedirect(Paths.LOGIN_PAGE);
			return;
		}

		chain.doFilter(request, response);
	}
}
