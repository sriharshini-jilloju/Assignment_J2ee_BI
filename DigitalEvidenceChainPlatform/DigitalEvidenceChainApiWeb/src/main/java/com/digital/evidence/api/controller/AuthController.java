package com.digital.evidence.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digital.evidence.api.dto.AuthRequestDTO;
import com.digital.evidence.api.dto.AuthResponseDTO;
import com.digital.evidence.auth.keycloack.KeycloakTokenRequestModel;
import com.digital.evidence.auth.keycloack.KeycloakTokenResponseModel;
import com.digital.evidence.auth.keycloack.service.KeycloakAuthService;

@RestController
@RequestMapping("/authenticate")
public class AuthController {
	
	@Autowired
    private KeycloakAuthService keycloakAuthService;

    @PostMapping("/token")
    public AuthResponseDTO getToken(@RequestBody AuthRequestDTO authRequest) {
        KeycloakTokenRequestModel requestModel = new KeycloakTokenRequestModel();
        requestModel.setUsername(authRequest.getUsername());
        requestModel.setPassword(authRequest.getPassword());
        requestModel.setClient_id(authRequest.getClientId());
        requestModel.setClient_secret(authRequest.getClientSecret());
        requestModel.setGrant_type(authRequest.getGrantType());

        KeycloakTokenResponseModel tokenResponse = keycloakAuthService.getToken(requestModel);

        AuthResponseDTO response = new AuthResponseDTO();
        response.setAccessToken(tokenResponse.getAccessToken());
        response.setTokenType(tokenResponse.getTokenType());
        response.setExpiresIn(tokenResponse.getExpiresIn());

        return response;
    }
}
