package com.psp.psp_backend.controller;

import com.psp.psp_backend.dto.ClientDto;
import com.psp.psp_backend.dto.PaymentMethodInfoDto;
import com.psp.psp_backend.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/client")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("")
    public ResponseEntity<ClientDto> getClient(Principal user){
        try {
            ClientDto clientDto = clientService.getClient(user.getName());
            return ResponseEntity.ok(clientDto);
        }catch(Exception e){
            return (ResponseEntity<ClientDto>) ResponseEntity.notFound();
        }
    }

}
