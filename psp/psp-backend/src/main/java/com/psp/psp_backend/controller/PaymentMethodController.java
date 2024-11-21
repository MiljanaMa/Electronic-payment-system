package com.psp.psp_backend.controller;
import com.psp.psp_backend.dto.PaymentMethodInfoDto;
import com.psp.psp_backend.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Set;
@Controller
@RequestMapping(value = "/api/paymentMethod")
public class PaymentMethodController {
    @Autowired
    private PaymentMethodService paymentMethodService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("")
    public ResponseEntity<Set<PaymentMethodInfoDto>> getAll(){
        Set<PaymentMethodInfoDto> paymentMethodsDto = paymentMethodService.getAll();
        return ResponseEntity.ok(paymentMethodsDto);
    }
}