package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TransferService {
    private String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();
    private String AUTH_TOKEN = "";

    public TransferService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public User[] retrieveUsers() {
        User[] users = null;
        users = restTemplate.exchange(baseUrl + "users", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
        return users;
    }

    public Transfer[] retrieveTransferList(int userId) throws TransferException {
        Transfer[] transfers = null;
        try {
            transfers = restTemplate.exchange(baseUrl + "users/" + userId + "/transfers", HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
        } catch (RestClientResponseException ex) {
            throw new TransferException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }
        return transfers;
    }

    public void makeTransfer(Transfer transfer) throws TransferException {
        try {
            Transfer transfer1 = restTemplate.exchange(baseUrl + "transfers",
                    HttpMethod.POST, makeEntity(transfer), Transfer.class).getBody();
        } catch (RestClientResponseException ex) {
            throw new TransferException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }

    }

    public void setAUTH_TOKEN(String AUTH_TOKEN) {
        this.AUTH_TOKEN = AUTH_TOKEN;
    }

    private HttpEntity<Transfer> makeEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

}
