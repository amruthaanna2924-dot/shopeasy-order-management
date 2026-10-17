package com.shopeasy.ordermanagement.service;


import com.shopeasy.ordermanagement.dto.request.RegisterRequestDTO;
import com.shopeasy.ordermanagement.dto.response.AuthResponseDTO;

public interface AuthService {

    AuthResponseDTO register(RegisterRequestDTO request);
}
