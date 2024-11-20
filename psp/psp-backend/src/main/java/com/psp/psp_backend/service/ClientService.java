package com.psp.psp_backend.service;

import com.psp.psp_backend.dto.ClientDto;
import com.psp.psp_backend.mapper.DtoUtils;
import com.psp.psp_backend.model.Client;
import com.psp.psp_backend.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    @Autowired
    ClientRepository clientRepository;
    public ClientDto getClient(String username) throws Exception{
        Client client = clientRepository.findByUsername(username).get();
        if(client == null) throw new IllegalArgumentException("Client does not exist.");
        return (ClientDto) new DtoUtils().convertToDto(client, new ClientDto());
    }
}
