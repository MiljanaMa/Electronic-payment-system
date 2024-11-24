package com.psp.psp_backend.controller;

import com.psp.psp_backend.dto.ClientDto;
import com.psp.psp_backend.dto.PaymentMethodInfoDto;
import com.psp.psp_backend.dto.RegistrationDto;
import com.psp.psp_backend.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

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
    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("updatePaymentMethods")
    public ResponseEntity<ClientDto> changeSubscription(@RequestBody List<String> paymentMethodIds, Principal user){
        try {
            ClientDto clientDto = clientService.changeSubscription(paymentMethodIds, user.getName());
            return ResponseEntity.ok(clientDto);
        }catch(Exception e){
            return (ResponseEntity<ClientDto>) ResponseEntity.notFound();
        }
    }

}
