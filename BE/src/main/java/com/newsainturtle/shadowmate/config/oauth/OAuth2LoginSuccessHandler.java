package com.newsainturtle.shadowmate.config.oauth;

import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //super.handle(request, response, authentication);

        String tagetUrl = request.getServletPath();
        System.out.println("tagetUrl = " + tagetUrl);

//        RequestDispatcher requestDispatcher = request.getRequestDispatcher(tagetUrl);

        request.setCharacterEncoding("UTF-8");
        request.setAttribute("name","test");

        System.out.println("response = " + response.getHeader("Authorization"));
        response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());

        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/api/auth/google");
        requestDispatcher.forward(request,response);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("request = " + request);
        System.out.println("response = " + response);
        System.out.println("authentication = " + authentication.getDetails());
        System.out.println("authentication = " + authentication.getPrincipal());
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("principalDetails.getUser().getEmail() = " + principalDetails.getUser().getEmail());


        String jwtToken = jwtProvider.createToken(principalDetails);
        jwtProvider.addTokenHeader(response, jwtToken);
        System.out.println("ADDTK_response = " + response.getHeader("Authorization"));

        //RedirectAttributes redirect = null;
        //Map<String, String> map = new HashMap<>();
        //map.put("Authorization", "test");
        //redirect = redirect.addFlashAttribute("map", map);
        //getRedirectStrategy().sendRedirect(request,response,"/api/auth/google");

        //this.handle(request,response,authentication);
        //super.clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request,response,determineTargetUrl(request,response,authentication));
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String tartget = "/api/auth/google";
        return UriComponentsBuilder.fromUriString(tartget)
                .queryParam("token", "1")
                .build().toUriString();
    }
}
