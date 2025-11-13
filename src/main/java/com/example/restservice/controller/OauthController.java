package com.example.restservice.controller;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class OauthController {
  
  private final ClientRegistrationRepository clientRegistrationRepository;
  
  public OauthController(ClientRegistrationRepository clientRegistrationRepository) {
    this.clientRegistrationRepository = clientRegistrationRepository;
  }
  
  @PostMapping("/github/exchange")
  public Map<String, Object> exchangeGitHubCode(@RequestBody Map<String, String> request) {
    String code = request.get("code");
    
    if (code == null || code.isEmpty()) {
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", "Code is required");
      return error;
    }
    
    try {
      ClientRegistration github = clientRegistrationRepository.findByRegistrationId("github");
      String clientId = github.getClientId();
      String clientSecret = github.getClientSecret();
      
    //   Get access token
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders tokenHeaders = new HttpHeaders();
      tokenHeaders.setContentType(MediaType.APPLICATION_JSON);
      tokenHeaders.set("Accept", "application/json");
      
      Map<String, String> tokenRequest = new HashMap<>();
      tokenRequest.put("client_id", clientId);
      tokenRequest.put("client_secret", clientSecret);
      tokenRequest.put("code", code);
      
      HttpEntity<Map<String, String>> tokenEntity = new HttpEntity<>(tokenRequest, tokenHeaders);
      
      ResponseEntity<Map> tokenResponse = restTemplate.exchange(
        "https://github.com/login/oauth/access_token",
        HttpMethod.POST,
        tokenEntity,
        Map.class
      );
      
      Map<String, Object> tokenBody = tokenResponse.getBody();
      if (tokenBody == null || !tokenBody.containsKey("access_token")) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Failed to get access token from GitHub");
        return error;
      }
      
      String accessToken = (String) tokenBody.get("access_token");
      
    //   Get user data
      HttpHeaders userHeaders = new HttpHeaders();
      userHeaders.setBearerAuth(accessToken);
      HttpEntity<String> userEntity = new HttpEntity<>(userHeaders);
      
      ResponseEntity<Map> userResponse = restTemplate.exchange(
        "https://api.github.com/user",
        HttpMethod.GET,
        userEntity,
        Map.class
      );
      
      Map<String, Object> userData = userResponse.getBody();
      String email = userData != null ? (String) userData.get("email") : null;
      
    //   Get email through backdoor (straight vibe code)
      if (email == null) {
        ResponseEntity<List> emailsResponse = restTemplate.exchange(
          "https://api.github.com/user/emails",
          HttpMethod.GET,
          userEntity,
          List.class
        );
        
        List<Map<String, Object>> emails = emailsResponse.getBody();
        
        if (emails != null && !emails.isEmpty()) {
          // Find primary email
          for (Map<String, Object> emailObj : emails) {
            Boolean primary = (Boolean) emailObj.get("primary");
            if (primary != null && primary) {
              email = (String) emailObj.get("email");
              break;
            }
          }
          
          // If no primary, find verified email
          if (email == null) {
            for (Map<String, Object> emailObj : emails) {
              Boolean verified = (Boolean) emailObj.get("verified");
              if (verified != null && verified) {
                email = (String) emailObj.get("email");
                break;
              }
            }
          }
          
          // Last resort: first email
          if (email == null) {
            email = (String) emails.get(0).get("email");
          }
        }
      }
      
    //   Return user data
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("email", email);
      response.put("githubUsername", userData.get("login"));
      response.put("name", userData.get("name"));
      response.put("avatarUrl", userData.get("avatar_url"));
      
      return response;
      
    } catch (Exception e) {
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", "Failed to exchange code: " + e.getMessage());
      e.printStackTrace();
      return error;
    }
  }

  @PostMapping("/google/exchange")
  public Map<String, Object> exchangeGoogleCode(@RequestBody Map<String, String> request) {
    String code = request.get("code");
    String redirectUri = request.get("redirectUri");
    String platform = request.get("platform");
    
    if (redirectUri == null || redirectUri.isEmpty()) {
      redirectUri = "http://localhost:8080/dev/callback";
    }
    
    if (platform == null || platform.isEmpty()) {
      platform = "web";
    }
    
    if (code == null || code.isEmpty()) {
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", "Code is required");
      return error;
    }
    
    try {
      String registrationId;
      switch (platform.toLowerCase()) {
        case "ios":
          registrationId = "google-ios";
          break;
        case "android":
          registrationId = "google-android";
          break;
        case "web":
        default:
          registrationId = "google-web";
          break;
      }
      
      ClientRegistration google = clientRegistrationRepository.findByRegistrationId(registrationId);
      String clientId = google.getClientId();
      String clientSecret = google.getClientSecret();
      
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders tokenHeaders = new HttpHeaders();
      tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      
      String tokenRequestBody;
      if (clientSecret != null && !clientSecret.isEmpty()) {
        tokenRequestBody = String.format(
          "code=%s&client_id=%s&client_secret=%s&redirect_uri=%s&grant_type=authorization_code",
          code, clientId, clientSecret, redirectUri
        );
      } else {
        tokenRequestBody = String.format(
          "code=%s&client_id=%s&redirect_uri=%s&grant_type=authorization_code",
          code, clientId, redirectUri
        );
      }
      
      HttpEntity<String> tokenEntity = new HttpEntity<>(tokenRequestBody, tokenHeaders);
      
      ResponseEntity<Map> tokenResponse = restTemplate.exchange(
        "https://oauth2.googleapis.com/token",
        HttpMethod.POST,
        tokenEntity,
        Map.class
      );
      
      Map<String, Object> tokenBody = tokenResponse.getBody();
      if (tokenBody == null || !tokenBody.containsKey("access_token")) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Failed to get access token from Google");
        error.put("details", tokenBody);
        return error;
      }
      
      String accessToken = (String) tokenBody.get("access_token");
      
      HttpHeaders userHeaders = new HttpHeaders();
      userHeaders.setBearerAuth(accessToken);
      HttpEntity<String> userEntity = new HttpEntity<>(userHeaders);
      
      ResponseEntity<Map> userResponse = restTemplate.exchange(
        "https://www.googleapis.com/oauth2/v2/userinfo",
        HttpMethod.GET,
        userEntity,
        Map.class
      );
      
      Map<String, Object> userData = userResponse.getBody();
      
      if (userData == null) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Failed to get user info from Google");
        return error;
      }
      
      String email = (String) userData.get("email");
      String name = (String) userData.get("name");
      String googleId = (String) userData.get("id");
      String picture = (String) userData.get("picture");
      
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("email", email);
      response.put("googleUsername", email);
      response.put("name", name);
      response.put("avatarUrl", picture);
      response.put("googleId", googleId);
      
      System.out.println("Mobile OAuth (Google " + platform + ") - User email: " + email);
      
      return response;
      
    } catch (Exception e) {
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", "Failed to exchange code: " + e.getMessage());
      e.printStackTrace();
      return error;
    }
  }
}