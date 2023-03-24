package it.polimi.tiw.documents.utils;

public class Paths {
	private static final String contextPath = "/DocumentManagerPureHTML";
	private static final String folderPath = "/WEB-INF";
	
	public static final String TO_LOGIN_PAGE_RELATIVE = "/";
	public static final String TO_HOME_PAGE_RELATIVE = "/GoToHomePage";
	public static final String TO_SUBFOLDER_PAGE_RELATIVE = "/GetSubfolderContent";
	public static final String TO_DOCUMENT_PAGE_RELATIVE = "/GetDocumentDetails";
	public static final String TO_CREATE_CONTENT_PAGE_RELATIVE = "/GoToCreateContentPage";
	public static final String CHECK_LOGIN_RELATIVE = "/CheckLogin";
	public static final String REGISTER_USER_RELATIVE = "/RegisterUser";
	public static final String CREATE_CONTENT_RELATIVE = "/CreateContent";
	public static final String MOVE_DOCUMENT_RELATIVE = "/MoveDocument";

	public static final String TO_LOGIN_PAGE = contextPath + TO_LOGIN_PAGE_RELATIVE;
	public static final String TO_HOME_PAGE = contextPath + TO_HOME_PAGE_RELATIVE;
	public static final String TO_SUBFOLDER_PAGE = contextPath + TO_SUBFOLDER_PAGE_RELATIVE;
	public static final String TO_DOCUMENT_PAGE = contextPath + TO_DOCUMENT_PAGE_RELATIVE;
	public static final String TO_CREATE_CONTENT_PAGE = contextPath + TO_CREATE_CONTENT_PAGE_RELATIVE;

	public static final String LOGIN_PAGE = folderPath + "/login.html";
	public static final String HOME_PAGE = folderPath + "/home.html";
	public static final String SUBFOLDER_PAGE = folderPath + "/subfolder.html";
	public static final String DOCUMENT_PAGE = folderPath + "/document.html";
	public static final String CREATE_CONTENT_PAGE = folderPath + "/createContent.html";
}
