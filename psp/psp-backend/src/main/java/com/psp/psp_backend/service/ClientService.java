package com.psp.psp_backend.service;

import com.psp.psp_backend.dto.ClientDto;
import com.psp.psp_backend.dto.PaymentMethodInfoDto;
import com.psp.psp_backend.mapper.DtoUtils;
import com.psp.psp_backend.model.Client;
import com.psp.psp_backend.model.PaymentMethod;
import com.psp.psp_backend.repository.ClientRepository;
import com.psp.psp_backend.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientService {
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    public ClientDto getClient(String username) throws Exception{
        Client client = clientRepository.findByUsername(username).get();
        if(client == null) throw new IllegalArgumentException("Client does not exist.");
        return (ClientDto) new DtoUtils().convertToDto(client, new ClientDto());
    }
    public ClientDto changeSubscription(List<String> paymentMethodsIds, String username) throws Exception{
        Client client = clientRepository.findByUsername(username).get();
        if(client == null) throw new IllegalArgumentException("Client does not exist.");
        List<UUID> uuids = paymentMethodsIds.stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());

        Set<PaymentMethod> dbPaymentMethods = new HashSet<>(paymentMethodRepository.findAllById(uuids));
        client.setPaymentMethods(dbPaymentMethods);
        Client savedClient = clientRepository.save(client);
        return (ClientDto) new DtoUtils().convertToDto(savedClient, new ClientDto());
    }
}
