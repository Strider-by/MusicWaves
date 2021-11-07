package by.musicwaves.controller.command.xhr;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface XHRCommand
{
    String execute(HttpServletRequest request, HttpServletResponse response);
}
