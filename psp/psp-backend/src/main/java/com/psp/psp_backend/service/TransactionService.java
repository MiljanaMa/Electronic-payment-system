package com.psp.psp_backend.service;

import com.psp.psp_backend.dto.MerchantTransactionDto;
import com.psp.psp_backend.model.Client;
import com.psp.psp_backend.model.Transaction;
import com.psp.psp_backend.repository.ClientRepository;
import com.psp.psp_backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    ClientRepository clientRepository;
    public String save(MerchantTransactionDto merchantTransactionDto) throws Exception {
        Client client = clientRepository.findByMerchantIdAndPass(merchantTransactionDto.getMerchantId(), merchantTransactionDto.getMerchantPass()).get();
        if(client == null)
            throw new Exception("Client is not found");
        Transaction transaction = new Transaction(merchantTransactionDto, client);
        return transactionRepository.save(transaction).getId();
    }


}
